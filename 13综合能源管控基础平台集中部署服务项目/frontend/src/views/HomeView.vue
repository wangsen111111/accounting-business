<template>
  <div class="page">
    <div class="page-head">
      <div>
        <h2 class="page-title">首页驾驶舱</h2>
        <p class="page-sub">
          样本表《2025年1月1日_累计能耗样本》按同时刻映射到今天 ·
          <b class="mono">{{ displayTime }}</b>
          <template v-if="metaMode === 'clock'"> · 1:1 实时对齐</template>
          <template v-else> · 倍速回放 · 每 {{ refreshSeconds }} 秒刷新</template>
        </p>
      </div>
      <div class="page-actions">
        <span class="refresh-hint">
          <span class="pulse"></span>
          {{ countdown }}s 后刷新
        </span>
        <button class="btn btn-primary" :disabled="loading" @click="refresh">
          <NavIcon name="refresh" :size="15" /> 立即刷新
        </button>
      </div>
    </div>

    <div v-if="error" class="error-bar">{{ error }}（请确认后端已在 8080 端口启动）</div>

    <div class="grid grid-kpi">
      <StatCard label="今日综合能耗" :value="kpi.totalEnergyTce" unit="tce" icon="carbon" hint="按 GB/T 2589 折标煤" />
      <StatCard label="今日碳排放" :value="kpi.totalCarbon" unit="tCO₂e" icon="leaf" color="#d97742" tint="#fdf0e6" hint="六类能源介质合计" />
      <StatCard label="单位产品碳排放" :value="kpi.carbonIntensity" unit="tCO₂e/t" :digits="3" icon="target" hint="产量按日 32 t 折算" />
      <StatCard label="实时电功率" :value="kpi.powerNow" unit="kW" icon="pulse" color="#3f8fc9" tint="#e9f3fa" :hint="`绿电占比 ${kpi.greenRatio}%`" />
    </div>

    <div class="grid grid-8-4 home-main">
      <PanelCard title="功能导航">
        <template #extra>
          <span class="tag tag-gray">工信部 12 项核心功能</span>
        </template>
        <div class="honeycomb">
          <div
            v-for="(row, ri) in honeycombRows"
            :key="ri"
            class="honey-row"
            :class="{ offset: ri % 2 === 1 }"
          >
            <router-link
              v-for="item in row"
              :key="item.label + item.path"
              :to="item.path"
              class="hex"
              :class="item.tone"
            >
              {{ item.label }}
            </router-link>
          </div>
        </div>
      </PanelCard>

      <div class="side-col">
        <PanelCard title="采集设备状态">
          <template #extra>
            <span class="tag">{{ onlineCount }}/{{ devices.length }} 在线</span>
          </template>
          <ul class="device-list">
            <li v-for="d in devices" :key="d.deviceId" class="device">
              <span class="device-dot" :class="{ off: !d.online }"></span>
              <div class="device-main">
                <b>{{ d.name }}</b>
                <span class="device-id">{{ d.deviceId }} · {{ d.lastTime }}</span>
              </div>
              <span class="device-val">{{ d.today?.toLocaleString('zh-CN') }}<i>{{ d.unit }}</i></span>
            </li>
          </ul>
        </PanelCard>

        <PanelCard title="实时预警">
          <template #extra>
            <span class="tag" :class="alerts.length ? 'tag-red' : 'tag-gray'">{{ alerts.length }} 条</span>
          </template>
          <ul v-if="alerts.length" class="alert-list">
            <li v-for="(a, i) in alerts.slice(0, 3)" :key="i" class="alert" :class="a.level">
              <span class="alert-time">{{ a.time }}</span>
              <div>
                <b>{{ a.title }}</b>
                <p>{{ a.content }}</p>
              </div>
            </li>
          </ul>
          <div v-else class="empty">当前时段无碳排放超标预警</div>
        </PanelCard>
      </div>
    </div>

    <div class="grid grid-7-5 home-charts">
      <PanelCard title="今日分时能耗趋势">
        <template #extra>
          <span class="tag tag-gray">单位：电 kWh / 气 m³ / 水 m³</span>
        </template>
        <EChart v-if="data" :option="trendOption" height="286px" />
        <div v-else class="chart-skeleton"><div class="skeleton" style="width: 100%; height: 240px"></div></div>
      </PanelCard>

      <PanelCard title="能源消费结构（折标煤）">
        <template #extra>
          <span class="tag tag-gray">合计 {{ kpi.totalEnergyTce }} tce</span>
        </template>
        <EChart v-if="data" :option="mixOption" height="286px" />
        <div v-else class="chart-skeleton"><div class="skeleton" style="width: 100%; height: 240px"></div></div>
      </PanelCard>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import PanelCard from '@/components/PanelCard.vue'
import StatCard from '@/components/StatCard.vue'
import EChart from '@/components/EChart.vue'
import NavIcon from '@/components/NavIcon.vue'
import { honeycombRows } from '@/config/menu'
import { usePolling } from '@/composables/usePolling'
import { useClock } from '@/composables/useClock'
import { getDashboard, getMeta } from '@/api'

const { data, loading, error, countdown, refresh } = usePolling(getDashboard, { interval: 20000 })

const kpi = computed(
  () =>
    data.value?.kpi || {
      totalEnergyTce: 0,
      totalCarbon: 0,
      carbonIntensity: 0,
      powerNow: 0,
      greenRatio: 0
    }
)
const devices = computed(() => data.value?.devices || [])
const alerts = computed(() => data.value?.alerts || [])
const onlineCount = computed(() => devices.value.filter((d) => d.online).length)
const refreshSeconds = ref(20)
const metaMode = ref('clock')

// clock 模式下副标题显示每秒跳动的真实时间；replay 模式显示后端回放时刻
const { dateTime } = useClock()
const displayTime = computed(() =>
  metaMode.value === 'clock' ? dateTime.value : data.value?.simulatedTime || '—'
)

onMounted(async () => {
  try {
    const meta = await getMeta()
    refreshSeconds.value = Math.round((meta?.refresh?.tickMillis || 20000) / 1000)
    metaMode.value = meta?.refresh?.mode || 'clock'
  } catch (e) {
    /* 使用默认 20 秒 / clock 模式 */
  }
})

/* ---------- 图表配置 ---------- */
const palette = ['#2F9E63', '#F2A93B', '#3F8FC9']
const areaStyle = (color) => ({
  color: {
    type: 'linear',
    x: 0,
    y: 0,
    x2: 0,
    y2: 1,
    colorStops: [
      { offset: 0, color: `${color}33` },
      { offset: 1, color: `${color}00` }
    ]
  }
})

const trendOption = computed(() => {
  const trend = data.value?.energyTrend
  if (!trend) return {}
  return {
    grid: { left: 8, right: 8, top: 40, bottom: 4, containLabel: true },
    tooltip: { trigger: 'axis', backgroundColor: 'rgba(255,255,255,.96)', borderColor: '#e4ede8', textStyle: { color: '#1b2a24', fontSize: 12 } },
    legend: { top: 0, right: 0, icon: 'roundRect', itemWidth: 10, itemHeight: 10, textStyle: { color: '#75877e', fontSize: 11 } },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: trend.labels,
      axisLine: { lineStyle: { color: '#e4ede8' } },
      axisTick: { show: false },
      axisLabel: { color: '#a2b0a9', fontSize: 11 }
    },
    yAxis: [
      {
        type: 'value',
        name: '电 kWh',
        nameTextStyle: { color: '#a2b0a9', fontSize: 10, padding: [0, 0, 0, -6] },
        splitLine: { lineStyle: { color: '#f0f5f2' } },
        axisLabel: { color: '#a2b0a9', fontSize: 10 }
      },
      {
        type: 'value',
        show: false,
        splitLine: { show: false }
      }
    ],
    series: trend.series.map((s, i) => ({
      name: s.name,
      type: 'line',
      smooth: true,
      symbol: 'none',
      yAxisIndex: i === 0 ? 0 : 1,
      lineStyle: { width: 2, color: palette[i % palette.length] },
      itemStyle: { color: palette[i % palette.length] },
      areaStyle: areaStyle(palette[i % palette.length]),
      data: s.data
    }))
  }
})

const mixOption = computed(() => {
  const mix = data.value?.energyMix || []
  return {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255,255,255,.96)',
      borderColor: '#e4ede8',
      textStyle: { color: '#1b2a24', fontSize: 12 },
      formatter: (p) => `${p.name}<br/>${p.value} tce（${(p.percent || 0).toFixed(1)}%）`
    },
    legend: {
      orient: 'vertical',
      right: 0,
      top: 'center',
      icon: 'circle',
      itemWidth: 8,
      itemHeight: 8,
      itemGap: 12,
      textStyle: { color: '#5c6f66', fontSize: 11 },
      formatter: (name) => {
        const item = mix.find((m) => m.name === name)
        return item ? `${name}  ${(item.ratio * 100).toFixed(1)}%` : name
      }
    },
    series: [
      {
        type: 'pie',
        radius: ['52%', '74%'],
        center: ['34%', '52%'],
        avoidLabelOverlap: true,
        itemStyle: { borderColor: '#fff', borderWidth: 3 },
        label: {
          show: true,
          position: 'center',
          formatter: () => `{a|${kpi.value.totalEnergyTce}}\n{b|tce 合计}`,
          rich: {
            a: { fontSize: 20, fontWeight: 700, color: '#1b2a24', lineHeight: 26 },
            b: { fontSize: 11, color: '#75877e' }
          }
        },
        emphasis: { label: { show: true } },
        data: mix.map((m) => ({ name: m.name, value: Number(m.tce.toFixed(3)), itemStyle: { color: m.color } }))
      }
    ]
  }
})
</script>

<style scoped>
.home-main {
  margin-top: 16px;
}

.home-charts {
  margin-top: 16px;
}

.refresh-hint {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--muted);
  font-variant-numeric: tabular-nums;
}

.pulse {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--gf-500);
  box-shadow: 0 0 0 0 rgba(47, 158, 99, 0.5);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(47, 158, 99, 0.45);
  }
  70% {
    box-shadow: 0 0 0 6px rgba(47, 158, 99, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(47, 158, 99, 0);
  }
}

.mono {
  font-family: var(--mono);
  font-weight: 600;
  color: var(--ink-2);
}

.error-bar {
  margin-bottom: 14px;
  padding: 10px 14px;
  border-radius: 10px;
  background: var(--red-soft);
  color: var(--red);
  font-size: 13px;
}

/* ---------- 蜂窝导航 ---------- */
.honeycomb {
  --hex-w: clamp(84px, 9.4vw, 132px);
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 14px 0 6px;
}

.honey-row {
  display: flex;
  gap: 7px;
}

.honey-row + .honey-row {
  margin-top: calc(var(--hex-w) * -0.055);
}

.honey-row.offset {
  margin-left: calc(var(--hex-w) / 2 + 3.5px);
}

.hex {
  width: var(--hex-w);
  height: calc(var(--hex-w) * 0.92);
  clip-path: polygon(50% 0%, 100% 25%, 100% 75%, 50% 100%, 0% 75%, 0% 25%);
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 0 14%;
  font-size: clamp(9.5px, 0.78vw, 12px);
  line-height: 1.3;
  color: #fff;
  transition: transform 0.16s ease, filter 0.16s ease;
  position: relative;
}

.hex:hover {
  transform: scale(1.05);
  filter: brightness(1.06);
  z-index: 2;
}

.hex.green {
  background: linear-gradient(150deg, #3aa971 0%, #2f9e63 55%, #26824f 100%);
}

.hex.orange {
  background: linear-gradient(150deg, #f7bf62 0%, #f2a93b 55%, #dd9126 100%);
  color: #4b3005;
}

.hex.dark {
  background: linear-gradient(150deg, #2b7f59 0%, #1e6b4b 100%);
  font-weight: 600;
}

/* ---------- 设备 / 预警 ---------- */
.side-col {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

.device-list,
.alert-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.device {
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 8px 10px;
  border-radius: 9px;
  background: var(--gf-50);
}

.device-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #3fbf78;
  flex: none;
  box-shadow: 0 0 0 3px rgba(63, 191, 120, 0.16);
}

.device-dot.off {
  background: #c9d3cd;
  box-shadow: none;
}

.device-main {
  display: flex;
  flex-direction: column;
  line-height: 1.35;
  min-width: 0;
}

.device-main b {
  font-size: 12.5px;
  font-weight: 600;
}

.device-id {
  font-size: 10.5px;
  color: var(--muted);
}

.device-val {
  margin-left: auto;
  font-size: 13px;
  font-weight: 600;
  font-variant-numeric: tabular-nums;
  white-space: nowrap;
}

.device-val i {
  font-style: normal;
  font-size: 10px;
  color: var(--muted);
  margin-left: 2px;
}

.alert {
  display: flex;
  gap: 10px;
  padding: 10px;
  border-radius: 9px;
  background: var(--amber-soft);
  border-left: 3px solid var(--amber);
}

.alert.danger {
  background: var(--red-soft);
  border-left-color: var(--red);
}

.alert-time {
  font-family: var(--mono);
  font-size: 12px;
  font-weight: 700;
  color: #a5710f;
  flex: none;
  padding-top: 1px;
}

.alert.danger .alert-time {
  color: var(--red);
}

.alert b {
  font-size: 12.5px;
  display: block;
  margin-bottom: 3px;
}

.alert p {
  font-size: 11.5px;
  color: var(--ink-2);
  line-height: 1.5;
}

.chart-skeleton {
  padding-top: 8px;
}

@media (max-width: 1199px) {
  .honeycomb {
    --hex-w: clamp(84px, 12vw, 118px);
  }
}

@media (max-width: 767px) {
  .honeycomb {
    --hex-w: clamp(58px, 17.4vw, 76px);
    align-items: flex-start;
    padding-left: 2px;
  }
  .honey-row {
    gap: 5px;
  }
  .honey-row.offset {
    margin-left: calc(var(--hex-w) / 2 + 3px);
  }
  .hex {
    font-size: 9px;
    padding: 0 10%;
    line-height: 1.25;
  }
}
</style>
