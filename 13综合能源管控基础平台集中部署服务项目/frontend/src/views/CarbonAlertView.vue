<template>
  <div class="page">
    <div class="page-head">
      <div>
        <h2 class="page-title">碳排放趋势预警</h2>
        <p class="page-sub">按小时对比历史均值，超出阈值 12% 触发预警 · 数据随模拟采集每 20 秒刷新</p>
      </div>
      <div class="page-actions">
        <span class="refresh-hint"><span class="pulse"></span>{{ countdown }}s 后刷新</span>
        <button class="btn btn-primary" :disabled="loading" @click="refresh">
          <NavIcon name="refresh" :size="15" /> 立即检测
        </button>
      </div>
    </div>

    <div class="grid grid-kpi">
      <StatCard label="历史小时均值" :value="data?.mean || 0" unit="tCO₂e" icon="chart" :digits="3" />
      <StatCard label="预警阈值" :value="data?.threshold || 0" unit="tCO₂e" icon="alert" :digits="3" color="#d97742" tint="#fdf0e6" />
      <StatCard label="超阈值时段" :value="items.length" unit="个" icon="pulse" :digits="0" :hint="`共统计 ${data?.labels?.length || 0} 个时段`" />
      <StatCard
        label="最大偏差"
        :value="maxDeviation"
        unit="%"
        icon="target"
        :digits="1"
        :hint="items.length ? '建议排查该时段高耗能设备' : '全部时段运行正常'"
      />
    </div>

    <PanelCard title="小时碳排放与警戒线" class="mt">
      <template #extra>
        <span class="tag tag-gray">实线：实际排放</span>
        <span class="tag tag-amber">虚线：预警阈值</span>
      </template>
      <EChart v-if="data" :option="option" height="300px" />
      <div v-else class="skeleton" style="height: 260px"></div>
    </PanelCard>

    <PanelCard title="预警记录" class="mt">
      <template #extra>
        <span class="tag" :class="items.length ? 'tag-red' : 'tag-gray'">{{ items.length }} 条</span>
      </template>
      <ul v-if="items.length" class="alert-list">
        <li v-for="(item, i) in items" :key="i" class="alert-item" :class="item.level">
          <div class="alert-time">{{ item.time }}</div>
          <div class="alert-body">
            <div class="alert-title">
              <b>{{ item.title }}</b>
              <span class="tag" :class="item.level === 'danger' ? 'tag-red' : 'tag-amber'">
                偏差 +{{ item.deviation }}%
              </span>
            </div>
            <p>{{ item.content }}</p>
          </div>
          <div class="alert-value">
            <b>{{ item.value }}</b>
            <span>tCO₂e</span>
          </div>
        </li>
      </ul>
      <div v-else class="empty">当前所有时段碳排放均在阈值范围内</div>
    </PanelCard>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import PanelCard from '@/components/PanelCard.vue'
import StatCard from '@/components/StatCard.vue'
import EChart from '@/components/EChart.vue'
import NavIcon from '@/components/NavIcon.vue'
import { usePolling } from '@/composables/usePolling'
import { getCarbonAlerts } from '@/api'

const { data, loading, countdown, refresh } = usePolling(getCarbonAlerts, { interval: 20000 })

const items = computed(() => data.value?.items || [])
const maxDeviation = computed(() =>
  items.value.length ? Math.max(...items.value.map((i) => i.deviation)) : 0
)

const option = computed(() => ({
  grid: { left: 12, right: 16, top: 30, bottom: 6, containLabel: true },
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: data.value?.labels || [],
    axisLine: { lineStyle: { color: '#e4ede8' } },
    axisTick: { show: false },
    axisLabel: { color: '#a2b0a9', fontSize: 11 }
  },
  yAxis: {
    type: 'value',
    name: 'tCO₂e',
    nameTextStyle: { color: '#a2b0a9', fontSize: 10 },
    splitLine: { lineStyle: { color: '#f0f5f2' } },
    axisLabel: { color: '#a2b0a9', fontSize: 10 }
  },
  series: [
    {
      name: '实际排放',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 5,
      lineStyle: { width: 2.4, color: '#2f9e63' },
      itemStyle: { color: '#2f9e63' },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(47,158,99,.2)' },
            { offset: 1, color: 'rgba(47,158,99,0)' }
          ]
        }
      },
      data: data.value?.emissions || []
    },
    {
      name: '历史均值',
      type: 'line',
      smooth: true,
      symbol: 'none',
      lineStyle: { width: 1.6, type: 'dashed', color: '#c8a15a' },
      data: data.value?.baseline || []
    },
    {
      name: '预警阈值',
      type: 'line',
      symbol: 'none',
      lineStyle: { width: 1.4, type: 'dashed', color: '#d9534f' },
      data: (data.value?.labels || []).map(() => data.value?.threshold || 0),
      markPoint: { show: false }
    }
  ]
}))
</script>

<style scoped>
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

.alert-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.alert-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 13px 16px;
  border-radius: 11px;
  background: var(--amber-soft);
  border-left: 3px solid var(--amber);
}

.alert-item.danger {
  background: var(--red-soft);
  border-left-color: var(--red);
}

.alert-time {
  font-family: var(--mono);
  font-size: 13px;
  font-weight: 700;
  color: #a5710f;
  width: 52px;
  flex: none;
}

.alert-item.danger .alert-time {
  color: var(--red);
}

.alert-body {
  flex: 1;
  min-width: 0;
}

.alert-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.alert-title b {
  font-size: 13px;
}

.alert-body p {
  margin-top: 4px;
  font-size: 12px;
  color: var(--ink-2);
  line-height: 1.5;
}

.alert-value {
  text-align: right;
  flex: none;
}

.alert-value b {
  font-size: 16px;
  font-variant-numeric: tabular-nums;
}

.alert-value span {
  display: block;
  font-size: 10px;
  color: var(--muted);
}

@media (max-width: 767px) {
  .alert-item {
    align-items: flex-start;
    flex-wrap: wrap;
    gap: 8px;
  }
  .alert-value {
    width: 100%;
    text-align: left;
  }
}
</style>
