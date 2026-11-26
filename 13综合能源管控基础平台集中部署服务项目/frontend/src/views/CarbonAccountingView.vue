<template>
  <div class="page">
    <div class="page-head">
      <div>
        <h2 class="page-title">碳排放核算</h2>
        <p class="page-sub">{{ data?.summary?.standard || 'GB/T 32150-2015' }} · 排放源明细按当日样本数据折算月度活动水平</p>
      </div>
      <div class="page-actions">
        <select v-model.number="year" class="select" style="width: 120px">
          <option :value="2025">2025 年</option>
          <option :value="2024">2024 年</option>
        </select>
        <button class="btn btn-primary" :disabled="loading" @click="reload">
          <NavIcon name="refresh" :size="15" /> 重新核算
        </button>
      </div>
    </div>

    <div class="grid grid-kpi">
      <StatCard label="年度排放总量" :value="data?.summary?.totalEmission || 0" unit="tCO₂e" icon="carbon" :digits="1" />
      <StatCard label="年度综合能耗" :value="data?.summary?.totalTce || 0" unit="tce" icon="wallet" :digits="1" color="#3f8fc9" tint="#e9f3fa" />
      <StatCard label="排放强度" :value="data?.summary?.intensity || 0" unit="kgCO₂e/tce" icon="target" :digits="2" />
      <StatCard
        label="配额使用率"
        :value="`${data?.summary?.quotaUsage || 0}%`"
        unit=""
        icon="alert"
        :hint="`年度配额 ${(data?.summary?.quota || 0).toLocaleString('zh-CN')} tCO₂e`"
        color="#d97742"
        tint="#fdf0e6"
      />
    </div>

    <PanelCard title="月度核算结果" class="mt">
      <template #extra>
        <span class="tag tag-gray">点击行查看核算过程</span>
      </template>
      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>核算期间</th>
              <th class="num">活动水平 (tce)</th>
              <th class="num">排放量 (tCO₂e)</th>
              <th class="num">排放强度 (kgCO₂e/tce)</th>
              <th class="num">环比</th>
              <th class="num">占比</th>
              <th>状态</th>
              <th style="width: 84px">操作</th>
            </tr>
          </thead>
          <tbody v-if="months.length">
            <tr v-for="m in months" :key="m.month" class="clickable" @click="openDetail(m)">
              <td class="mono">{{ m.month }}</td>
              <td class="num">{{ m.activity.toLocaleString('zh-CN') }}</td>
              <td class="num strong">{{ m.emission.toLocaleString('zh-CN') }}</td>
              <td class="num">{{ m.intensity.toFixed(2) }}</td>
              <td class="num" :class="m.yoy > 0 ? 'up' : 'down'">{{ m.yoy > 0 ? '+' : '' }}{{ m.yoy.toFixed(2) }}%</td>
              <td class="num">{{ (m.ratio * 100).toFixed(1) }}%</td>
              <td><span class="tag" :class="m.status === '偏高' ? 'tag-amber' : ''">{{ m.status }}</span></td>
              <td><button class="btn btn-sm" @click.stop="openDetail(m)">详情</button></td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr><td colspan="8"><div class="empty">{{ error || '核算中…' }}</div></td></tr>
          </tbody>
        </table>
      </div>
    </PanelCard>

    <transition name="modal">
      <div v-if="activeMonth" class="modal-mask" @click.self="activeMonth = null">
        <div class="modal">
          <header class="modal-head">
            <div>
              <h3>{{ activeMonth.month }} 核算明细</h3>
              <p>活动水平 × 排放因子 ÷ 1000 = 排放量（tCO₂e）</p>
            </div>
            <button class="icon-close" @click="activeMonth = null"><NavIcon name="close" :size="17" /></button>
          </header>
          <div class="modal-body table-wrap">
            <table class="table">
              <thead>
                <tr>
                  <th>排放源</th>
                  <th class="num">活动水平</th>
                  <th class="num">排放因子</th>
                  <th class="num">排放量</th>
                  <th>计算过程</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="d in details" :key="d.source">
                  <td>
                    <b>{{ d.source }}</b>
                  </td>
                  <td class="num">{{ d.activity.toLocaleString('zh-CN') }} {{ d.activityUnit }}</td>
                  <td class="num">{{ d.factor }} {{ d.factorUnit }}</td>
                  <td class="num strong">{{ d.emission.toLocaleString('zh-CN') }}</td>
                  <td class="formula">{{ d.formula }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <footer class="modal-foot">
            <span>该月排放合计 <b>{{ activeMonth.emission.toLocaleString('zh-CN') }}</b> tCO₂e</span>
            <button class="btn" @click="activeMonth = null">关闭</button>
          </footer>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import PanelCard from '@/components/PanelCard.vue'
import StatCard from '@/components/StatCard.vue'
import NavIcon from '@/components/NavIcon.vue'
import { usePolling } from '@/composables/usePolling'
import { getCarbonAccounting } from '@/api'

const year = ref(2025)
const activeMonth = ref(null)

const { data, loading, error, refresh } = usePolling(
  () => getCarbonAccounting(year.value),
  { interval: 20000 }
)

const months = computed(() => data.value?.months || [])
const details = computed(() => data.value?.details || [])

watch(year, () => refresh())

function reload() {
  refresh()
}

function openDetail(m) {
  activeMonth.value = m
}
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

.clickable {
  cursor: pointer;
}

.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(12, 30, 22, 0.44);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  z-index: 60;
}

.modal {
  width: min(880px, 100%);
  max-height: 86vh;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 24px 60px rgba(18, 63, 46, 0.28);
}

.modal-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding: 18px 20px 14px;
  border-bottom: 1px solid var(--line);
}

.modal-head h3 {
  font-size: 16px;
}

.modal-head p {
  margin-top: 5px;
  font-size: 12px;
  color: var(--muted);
}

.icon-close {
  border: 0;
  background: transparent;
  color: var(--muted);
  cursor: pointer;
  padding: 4px;
  border-radius: 6px;
}

.icon-close:hover {
  background: var(--gf-50);
  color: var(--gf-700);
}

.modal-body {
  padding: 6px 20px 10px;
  overflow: auto;
}

.formula {
  font-family: var(--mono);
  font-size: 11.5px;
  color: var(--muted);
  white-space: nowrap;
}

.modal-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 20px;
  border-top: 1px solid var(--line);
  font-size: 13px;
  color: var(--ink-2);
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.2s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
