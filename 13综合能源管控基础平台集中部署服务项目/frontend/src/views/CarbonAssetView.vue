<template>
  <div class="page">
    <div class="page-head">
      <div>
        <h2 class="page-title">碳资产管理</h2>
        <p class="page-sub">配额 + CCER − 累计排放 = 预计结余 · 行情数据为演示数据</p>
      </div>
      <div class="page-actions">
        <span class="refresh-hint"><span class="pulse"></span>{{ countdown }}s 后刷新</span>
        <button class="btn btn-primary" :disabled="loading" @click="refresh">
          <NavIcon name="refresh" :size="15" /> 刷新
        </button>
      </div>
    </div>

    <div v-if="kpi.riskLevel !== 'normal'" class="risk-bar" :class="kpi.riskLevel">
      <NavIcon name="alert" :size="16" />
      <span>{{ kpi.riskText }}（预计结余 {{ kpi.balance.toLocaleString('zh-CN') }} tCO₂e，安全阈值 2,500 tCO₂e）</span>
    </div>

    <div class="grid grid-kpi">
      <StatCard label="碳配额 CEA" :value="kpi.quota" unit="tCO₂e" icon="carbon" :digits="0" hint="2024 履约周期" />
      <StatCard label="CCER 持有量" :value="kpi.ccer" unit="tCO₂e" icon="leaf" :digits="0" color="#d97742" tint="#fdf0e6" />
      <StatCard label="累计实际排放" :value="kpi.emitted" unit="tCO₂e" icon="pulse" :digits="1" :hint="`占总资产 ${kpi.progress}%`" color="#3f8fc9" tint="#e9f3fa" />
      <StatCard
        label="预计结余"
        :value="kpi.balance"
        unit="tCO₂e"
        icon="asset"
        :digits="1"
        :hint="kpi.riskText"
        :color="kpi.balance < 0 ? '#d9534f' : '#2f9e63'"
        :tint="kpi.balance < 0 ? '#fceded' : '#e7f3ec'"
      />
    </div>

    <div class="grid grid-7-5 mt">
      <PanelCard title="全国碳市场行情（近 30 天）">
        <template #extra>
          <span class="tag">最新 {{ data?.market?.latest || 0 }} 元/吨</span>
          <span class="tag" :class="(data?.market?.change || 0) >= 0 ? 'tag-amber' : 'tag-gray'">
            {{ (data?.market?.change || 0) >= 0 ? '+' : '' }}{{ data?.market?.change || 0 }}
          </span>
        </template>
        <EChart v-if="data" :option="marketOption" height="272px" />
        <div v-else class="skeleton" style="height: 240px"></div>
      </PanelCard>

      <PanelCard title="碳资产构成">
        <EChart v-if="data" :option="ringOption" height="272px" />
        <div v-else class="skeleton" style="height: 240px"></div>
      </PanelCard>
    </div>

    <div class="grid grid-2 mt">
      <PanelCard title="碳资产台账">
        <div class="table-wrap">
          <table class="table" style="min-width: 480px">
            <thead>
              <tr>
                <th>资产类型</th>
                <th>名称</th>
                <th class="num">数量</th>
                <th>履约周期</th>
                <th>状态</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="a in data?.assets || []" :key="a.type">
                <td><span class="tag">{{ a.type }}</span></td>
                <td>{{ a.name }}</td>
                <td class="num strong">{{ a.quantity.toLocaleString('zh-CN') }}</td>
                <td class="muted">{{ a.period }}</td>
                <td><span class="tag" :class="a.status === '已登记' ? '' : 'tag-gray'">{{ a.status }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </PanelCard>

      <PanelCard title="节能规划">
        <div class="table-wrap">
          <table class="table" style="min-width: 480px">
            <thead>
              <tr>
                <th>措施</th>
                <th>责任部门</th>
                <th>完成时限</th>
                <th class="num">预计节碳 (tCO₂e)</th>
                <th>状态</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="p in data?.plans || []" :key="p.name">
                <td>{{ p.name }}</td>
                <td class="muted">{{ p.owner }}</td>
                <td class="mono">{{ p.deadline }}</td>
                <td class="num">{{ p.saving }}</td>
                <td><span class="tag" :class="p.status === '已完成' ? '' : 'tag-amber'">{{ p.status }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </PanelCard>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import PanelCard from '@/components/PanelCard.vue'
import StatCard from '@/components/StatCard.vue'
import EChart from '@/components/EChart.vue'
import NavIcon from '@/components/NavIcon.vue'
import { usePolling } from '@/composables/usePolling'
import { getCarbonAsset } from '@/api'

const { data, loading, countdown, refresh } = usePolling(getCarbonAsset, { interval: 20000 })

const kpi = computed(
  () =>
    data.value?.kpi || {
      quota: 0,
      ccer: 0,
      totalAsset: 0,
      emitted: 0,
      balance: 0,
      progress: 0,
      riskLevel: 'normal',
      riskText: ''
    }
)

const marketOption = computed(() => ({
  grid: { left: 10, right: 12, top: 24, bottom: 4, containLabel: true },
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: data.value?.market?.labels || [],
    axisLine: { lineStyle: { color: '#e4ede8' } },
    axisTick: { show: false },
    axisLabel: { color: '#a2b0a9', fontSize: 10, interval: 4 }
  },
  yAxis: {
    type: 'value',
    scale: true,
    name: '元/吨',
    nameTextStyle: { color: '#a2b0a9', fontSize: 10 },
    splitLine: { lineStyle: { color: '#f0f5f2' } },
    axisLabel: { color: '#a2b0a9', fontSize: 10 }
  },
  series: [
    {
      type: 'line',
      smooth: true,
      symbol: 'none',
      lineStyle: { width: 2.4, color: '#2f9e63' },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(47,158,99,.24)' },
            { offset: 1, color: 'rgba(47,158,99,0)' }
          ]
        }
      },
      data: data.value?.market?.prices || [],
      markLine: {
        silent: true,
        symbol: 'none',
        lineStyle: { type: 'dashed', color: '#c8a15a' },
        label: { color: '#a5710f', fontSize: 10, formatter: '30 日均价' },
        data: [{ type: 'average', name: '均价' }]
      }
    }
  ]
}))

const ringOption = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c} tCO₂e ({d}%)' },
  legend: {
    bottom: 0,
    icon: 'circle',
    itemWidth: 8,
    itemHeight: 8,
    textStyle: { color: '#5c6f66', fontSize: 11 }
  },
  series: [
    {
      type: 'pie',
      radius: ['48%', '70%'],
      center: ['50%', '44%'],
      itemStyle: { borderColor: '#fff', borderWidth: 3 },
      label: { show: false },
      data: (data.value?.ring || []).map((r) => ({
        name: r.name,
        value: Number(r.value.toFixed(1)),
        itemStyle: { color: r.color }
      }))
    }
  ]
}))
</script>

<style scoped>
.mt {
  margin-top: 16px;
}

.mono {
  font-family: var(--mono);
  font-size: 12px;
}

.strong {
  font-weight: 600;
}

.muted {
  color: var(--muted);
}

.risk-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 14px;
  padding: 10px 14px;
  border-radius: 10px;
  font-size: 13px;
  background: var(--amber-soft);
  color: #8a5c0d;
  border-left: 3px solid var(--amber);
}

.risk-bar.danger {
  background: var(--red-soft);
  color: var(--red);
  border-left-color: var(--red);
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
