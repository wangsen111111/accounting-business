<template>
  <div class="page">
    <div class="page-head">
      <div>
        <h2 class="page-title">能耗历史追溯</h2>
        <p class="page-sub">样本表 10 秒一个采样点，可按小时 / 10 分钟聚合查看消耗量与累计读数</p>
      </div>
      <div class="page-actions">
        <span class="refresh-hint"><span class="pulse"></span>{{ countdown }}s 后自动刷新</span>
        <button class="btn btn-primary" :disabled="loading" @click="refresh">
          <NavIcon name="refresh" :size="15" /> 刷新
        </button>
      </div>
    </div>

    <PanelCard title="查询条件">
      <div class="chips">
        <button
          v-for="t in energyTypes"
          :key="t.code"
          class="chip"
          :class="{ active: type === t.code }"
          @click="changeType(t.code)"
        >
          <span class="chip-dot" :style="{ background: t.color }"></span>
          {{ t.name }}
        </button>
      </div>
      <div class="granularity">
        <span class="field-label">聚合粒度</span>
        <div class="seg">
          <button :class="{ active: granularity === 'hour' }" @click="changeGranularity('hour')">按小时</button>
          <button :class="{ active: granularity === 'minute10' }" @click="changeGranularity('minute10')">按 10 分钟</button>
        </div>
      </div>
    </PanelCard>

    <div class="grid grid-kpi stats">
      <StatCard label="今日累计消耗" :value="summary.total" :unit="unit" icon="carbon" :digits="2" />
      <StatCard label="峰值时段消耗" :value="summary.peakValue" :unit="unit" icon="pulse" :digits="2" :hint="`出现在 ${summary.peakLabel}`" color="#d97742" tint="#fdf0e6" />
      <StatCard label="平均每时段消耗" :value="summary.avg" :unit="unit" icon="target" :digits="2" />
      <StatCard label="折算碳排放" :value="summary.carbon" unit="tCO₂e" icon="leaf" :digits="3" :hint="`因子 ${summary.factor} kgCO₂/${unit}`" color="#3f8fc9" tint="#e9f3fa" />
    </div>

    <PanelCard title="消耗量分布" class="mt">
      <template #extra><span class="tag tag-gray">单位：{{ unit }}</span></template>
      <EChart v-if="data" :option="barOption" height="260px" />
      <div v-else class="skeleton" style="height: 240px"></div>
    </PanelCard>

    <PanelCard title="累计读数曲线" class="mt">
      <template #extra><span class="tag tag-gray">表计累计值（只增不减）</span></template>
      <EChart v-if="data" :option="lineOption" height="260px" />
      <div v-else class="skeleton" style="height: 240px"></div>
    </PanelCard>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import PanelCard from '@/components/PanelCard.vue'
import StatCard from '@/components/StatCard.vue'
import EChart from '@/components/EChart.vue'
import NavIcon from '@/components/NavIcon.vue'
import { usePolling } from '@/composables/usePolling'
import { getEnergySeries, getMeta } from '@/api'

const type = ref('electricity')
const granularity = ref('hour')
const energyTypes = ref([])

const { data, loading, countdown, refresh } = usePolling(
  () => getEnergySeries({ type: type.value, granularity: granularity.value }),
  { interval: 20000 }
)

const unit = computed(() => energyTypes.value.find((t) => t.code === type.value)?.unit || '')
const summary = computed(() => {
  const consumption = data.value?.consumption || []
  const labels = data.value?.labels || []
  const total = consumption.reduce((a, b) => a + b, 0)
  let peakValue = 0
  let peakLabel = '—'
  consumption.forEach((v, i) => {
    if (v > peakValue) {
      peakValue = v
      peakLabel = labels[i] || '—'
    }
  })
  const factor = energyTypes.value.find((t) => t.code === type.value)?.carbonFactor || 0
  return {
    total,
    peakValue,
    peakLabel,
    avg: consumption.length ? total / consumption.length : 0,
    carbon: (total * factor) / 1000,
    factor
  }
})

onMounted(async () => {
  try {
    const meta = await getMeta()
    energyTypes.value = meta.energyTypes
  } catch (e) {
    energyTypes.value = [{ code: 'electricity', name: '电', unit: 'kWh', color: '#2F9E63', carbonFactor: 0.5703 }]
  }
})

function changeType(code) {
  type.value = code
  refresh()
}

function changeGranularity(g) {
  granularity.value = g
  refresh()
}

const axisBase = {
  axisLine: { lineStyle: { color: '#e4ede8' } },
  axisTick: { show: false },
  axisLabel: { color: '#a2b0a9', fontSize: 11 }
}

const barOption = computed(() => ({
  grid: { left: 10, right: 12, top: 24, bottom: 4, containLabel: true },
  tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
  xAxis: { type: 'category', data: data.value?.labels || [], ...axisBase },
  yAxis: {
    type: 'value',
    name: unit.value,
    nameTextStyle: { color: '#a2b0a9', fontSize: 10 },
    splitLine: { lineStyle: { color: '#f0f5f2' } },
    axisLabel: { color: '#a2b0a9', fontSize: 10 }
  },
  series: [
    {
      type: 'bar',
      barMaxWidth: 18,
      itemStyle: {
        borderRadius: [4, 4, 0, 0],
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: '#42b177' },
            { offset: 1, color: '#2f9e63' }
          ]
        }
      },
      data: data.value?.consumption || []
    }
  ]
}))

const lineOption = computed(() => ({
  grid: { left: 10, right: 14, top: 24, bottom: 4, containLabel: true },
  tooltip: { trigger: 'axis' },
  xAxis: { type: 'category', boundaryGap: false, data: data.value?.labels || [], ...axisBase },
  yAxis: {
    type: 'value',
    scale: true,
    splitLine: { lineStyle: { color: '#f0f5f2' } },
    axisLabel: { color: '#a2b0a9', fontSize: 10 }
  },
  series: [
    {
      type: 'line',
      smooth: true,
      symbol: 'none',
      lineStyle: { width: 2.2, color: '#2f9e63' },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(47,158,99,.22)' },
            { offset: 1, color: 'rgba(47,158,99,0)' }
          ]
        }
      },
      data: data.value?.cumulative || []
    }
  ]
}))
</script>

<style scoped>
.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 32px;
  padding: 0 13px;
  border: 1px solid var(--line);
  border-radius: 20px;
  background: #fff;
  color: var(--ink-2);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.15s ease;
}

.chip:hover {
  border-color: var(--gf-400);
}

.chip.active {
  background: var(--gf-50);
  border-color: var(--gf-500);
  color: var(--gf-700);
  font-weight: 600;
}

.chip-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
}

.granularity {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 14px;
}

.seg {
  display: inline-flex;
  padding: 3px;
  background: var(--gf-50);
  border-radius: 9px;
}

.seg button {
  height: 28px;
  padding: 0 12px;
  border: 0;
  border-radius: 7px;
  background: transparent;
  color: var(--ink-2);
  font-size: 12.5px;
  cursor: pointer;
}

.seg button.active {
  background: #fff;
  color: var(--gf-700);
  font-weight: 600;
  box-shadow: var(--shadow-sm);
}

.stats {
  margin-top: 16px;
}

.mt {
  margin-top: 16px;
}

.refresh-hint {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--muted);
}

.pulse {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--gf-500);
}
</style>
