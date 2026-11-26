<template>
  <div class="page">
    <div class="page-head">
      <div>
        <h2 class="page-title">产品碳足迹核算</h2>
        <p class="page-sub">覆盖原材料、生产、运输、销售、使用、回收六大环节 · ISO 14067 口径</p>
      </div>
      <div class="page-actions">
        <span class="tag tag-gray">{{ data?.specification || '—' }}</span>
        <button class="btn" :disabled="!data"><NavIcon name="download" :size="15" /> 导出核算报告</button>
      </div>
    </div>

    <div class="grid grid-kpi">
      <StatCard label="产品碳足迹总量" :value="data?.total || 0" :unit="data?.unit || 'tCO₂e/t'" icon="leaf" :digits="3" />
      <StatCard label="绿电/绿证抵扣" :value="data?.greenOffset || 0" :unit="data?.unit || 'tCO₂e/t'" icon="carbon" :digits="2" color="#3f8fc9" tint="#e9f3fa" hint="已核销绿证对应电量" />
      <StatCard label="净碳足迹" :value="data?.netTotal || 0" :unit="data?.unit || 'tCO₂e/t'" icon="target" :digits="3" />
      <StatCard
        label="最大排放环节"
        :value="maxStage?.name || '—'"
        unit=""
        icon="alert"
        color="#d97742"
        tint="#fdf0e6"
        :hint="maxStage ? `占比 ${(maxStage.ratio * 100).toFixed(1)}%` : ''"
      />
    </div>

    <div class="grid grid-7-5 mt">
      <PanelCard title="全生命周期阶段占比">
        <EChart v-if="data" :option="option" height="300px" />
        <div v-else class="skeleton" style="height: 260px"></div>
      </PanelCard>

      <PanelCard title="核算边界与说明">
        <ul class="notes">
          <li><b>产品</b>{{ data?.product || '—' }}</li>
          <li><b>声明单位</b>1 吨产品（{{ data?.unit || 'tCO₂e/t' }}）</li>
          <li><b>系统边界</b>从摇篮到大门 + 使用与回收阶段</li>
          <li><b>数据质量</b>上游因子取 CLCD 数据库，生产环节取企业实测值</li>
          <li><b>绿电抵扣</b>按绿证核销电量 × 电网因子抵扣 {{ data?.greenOffset || 0 }} tCO₂e/t</li>
        </ul>
        <div class="total-box">
          <span>净碳足迹</span>
          <b>{{ data?.netTotal || 0 }}</b>
          <i>{{ data?.unit || 'tCO₂e/t' }}</i>
        </div>
      </PanelCard>
    </div>

    <PanelCard title="分阶段核算明细" class="mt">
      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>生命周期阶段</th>
              <th>说明</th>
              <th class="num">排放量 (tCO₂e/t)</th>
              <th class="num">占比</th>
              <th>因子来源</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="s in data?.stages || []" :key="s.name">
              <td>
                <span class="stage-dot" :style="{ background: s.color }"></span>
                <b>{{ s.name }}</b>
              </td>
              <td class="muted">{{ s.description }}</td>
              <td class="num strong">{{ s.emission }}</td>
              <td class="num">{{ (s.ratio * 100).toFixed(1) }}%</td>
              <td class="muted">{{ s.factorSource }}</td>
            </tr>
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
import { getFootprint } from '@/api'

const { data } = usePolling(getFootprint, { interval: 60000 })

const maxStage = computed(() => {
  const stages = data.value?.stages || []
  return stages.length ? stages.reduce((a, b) => (a.emission >= b.emission ? a : b)) : null
})

const option = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}<br/>{c} tCO₂e/t（{d}%）' },
  legend: {
    orient: 'vertical',
    right: 6,
    top: 'center',
    icon: 'circle',
    itemWidth: 8,
    itemHeight: 8,
    itemGap: 11,
    textStyle: { color: '#5c6f66', fontSize: 11 }
  },
  series: [
    {
      type: 'pie',
      radius: ['46%', '72%'],
      center: ['36%', '50%'],
      itemStyle: { borderColor: '#fff', borderWidth: 3 },
      label: {
        show: true,
        position: 'center',
        formatter: () => `{a|${data.value?.total || 0}}\n{b|tCO₂e / t 产品}`,
        rich: {
          a: { fontSize: 19, fontWeight: 700, color: '#1b2a24', lineHeight: 25 },
          b: { fontSize: 10.5, color: '#75877e' }
        }
      },
      emphasis: { label: { show: true } },
      data: (data.value?.stages || []).map((s) => ({
        name: s.name,
        value: s.emission,
        itemStyle: { color: s.color }
      }))
    }
  ]
}))
</script>

<style scoped>
.mt {
  margin-top: 16px;
}

.notes {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 9px;
  font-size: 12.5px;
  color: var(--ink-2);
}

.notes li {
  display: flex;
  gap: 8px;
  line-height: 1.6;
}

.notes b {
  color: var(--muted);
  font-weight: 400;
  flex: none;
  width: 64px;
}

.total-box {
  margin-top: 16px;
  display: flex;
  align-items: baseline;
  gap: 6px;
  padding: 14px 16px;
  border-radius: 12px;
  background: var(--gf-50);
}

.total-box span {
  font-size: 12px;
  color: var(--muted);
}

.total-box b {
  font-size: 24px;
  color: var(--gf-700);
  font-variant-numeric: tabular-nums;
}

.total-box i {
  font-style: normal;
  font-size: 11px;
  color: var(--muted);
}

.stage-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 3px;
  margin-right: 7px;
}

.strong {
  font-weight: 600;
}

.muted {
  color: var(--muted);
}
</style>
