<template>
  <div class="page">
    <div class="page-head">
      <div>
        <h2 class="page-title">能源消费量与强度计算</h2>
        <p class="page-sub">依据 {{ data?.summary?.standard || 'GB/T 2589' }} 折算 · 月度数据为演示数据，日数据来自样本表</p>
      </div>
      <div class="page-actions">
        <span class="tag tag-gray">统计年度 2025</span>
        <button class="btn btn-primary" :disabled="loading" @click="refresh">
          <NavIcon name="refresh" :size="15" /> 重新计算
        </button>
      </div>
    </div>

    <div class="grid grid-kpi">
      <StatCard label="年度综合能耗" :value="data?.summary?.tce || 0" unit="tce" icon="carbon" :digits="1" hint="12 个月累计" />
      <StatCard label="单位产品综合能耗" :value="data?.summary?.intensity || 0" unit="kgce/t" icon="target" :digits="2" hint="目标值 ≤ 100 kgce/t" />
      <StatCard label="单位产值综合能耗" :value="data?.summary?.outputValueIntensity || 0" unit="kgce/万元" icon="wallet" :digits="2" color="#3f8fc9" tint="#e9f3fa" />
      <StatCard label="同比变化" :value="`${data?.summary?.yoy || 0}%`" unit="" icon="pulse" hint="较上年同期" trend="down" />
    </div>

    <PanelCard title="综合能耗与强度趋势" class="mt">
      <template #extra>
        <span class="tag tag-gray">柱：综合能耗 tce</span>
        <span class="tag tag-amber">线：单位产品能耗 kgce/t</span>
      </template>
      <EChart v-if="data" :option="option" height="300px" />
      <div v-else class="skeleton" style="height: 260px"></div>
    </PanelCard>

    <PanelCard title="月度计算结果" class="mt">
      <template #extra><span class="tag tag-gray">共 {{ data?.table?.length || 0 }} 个月</span></template>
      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>月份</th>
              <th class="num">综合能耗 (tce)</th>
              <th class="num">产量 (t)</th>
              <th class="num">单位产品能耗 (kgce/t)</th>
              <th class="num">单位产值能耗 (kgce/万元)</th>
              <th class="num">环比</th>
              <th>评级</th>
            </tr>
          </thead>
          <tbody v-if="data?.table?.length">
            <tr v-for="row in data.table" :key="row.month">
              <td class="mono">{{ row.month }}</td>
              <td class="num strong">{{ row.tce.toLocaleString('zh-CN') }}</td>
              <td class="num">{{ row.output.toLocaleString('zh-CN') }}</td>
              <td class="num">{{ row.intensity.toFixed(2) }}</td>
              <td class="num">{{ row.outputValueIntensity.toFixed(2) }}</td>
              <td class="num" :class="row.yoy > 0 ? 'up' : 'down'">
                {{ row.yoy > 0 ? '+' : '' }}{{ row.yoy.toFixed(2) }}%
              </td>
              <td>
                <span class="tag" :class="levelClass(row.level)">{{ row.level }}</span>
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr><td colspan="7"><div class="empty">{{ error || '暂无数据' }}</div></td></tr>
          </tbody>
        </table>
      </div>
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
import { getEnergyAnalysis } from '@/api'

const { data, loading, error, refresh } = usePolling(getEnergyAnalysis, { interval: 20000 })

function levelClass(level) {
  if (level === '优秀') return ''
  if (level === '达标') return 'tag-gray'
  return 'tag-amber'
}

const option = computed(() => ({
  grid: { left: 12, right: 12, top: 44, bottom: 6, containLabel: true },
  tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
  legend: { top: 0, right: 0, icon: 'roundRect', itemWidth: 10, itemHeight: 10, textStyle: { color: '#75877e', fontSize: 11 } },
  xAxis: {
    type: 'category',
    data: data.value?.months || [],
    axisLine: { lineStyle: { color: '#e4ede8' } },
    axisTick: { show: false },
    axisLabel: { color: '#a2b0a9', fontSize: 11, formatter: (v) => v.slice(5) + '月' }
  },
  yAxis: [
    {
      type: 'value',
      name: 'tce',
      nameTextStyle: { color: '#a2b0a9', fontSize: 10 },
      splitLine: { lineStyle: { color: '#f0f5f2' } },
      axisLabel: { color: '#a2b0a9', fontSize: 10 }
    },
    {
      type: 'value',
      name: 'kgce/t',
      nameTextStyle: { color: '#a2b0a9', fontSize: 10 },
      splitLine: { show: false },
      axisLabel: { color: '#a2b0a9', fontSize: 10 }
    }
  ],
  series: [
    {
      name: '综合能耗',
      type: 'bar',
      barMaxWidth: 22,
      itemStyle: { borderRadius: [4, 4, 0, 0], color: '#7fc9a2' },
      data: data.value?.tceSeries || []
    },
    {
      name: '单位产品能耗',
      type: 'line',
      yAxisIndex: 1,
      smooth: true,
      symbolSize: 6,
      lineStyle: { width: 2.4, color: '#f2a93b' },
      itemStyle: { color: '#f2a93b' },
      data: data.value?.intensitySeries || []
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
  font-size: 12.5px;
}

.strong {
  font-weight: 600;
}

.up {
  color: var(--red);
}

.down {
  color: var(--gf-600);
}
</style>
