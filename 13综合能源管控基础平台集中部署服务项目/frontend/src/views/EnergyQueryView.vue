<template>
  <div class="page">
    <div class="page-head">
      <div>
        <h2 class="page-title">能耗数据查询</h2>
        <p class="page-sub">
          按能源介质查询采集明细（每 {{ query.bucketMinutes }} 分钟聚合一条）· 可导出 CSV 交由财务/核查使用
        </p>
      </div>
      <div class="page-actions">
        <span class="refresh-hint"><span class="pulse"></span>{{ countdown }}s 后自动刷新</span>
        <button class="btn" @click="exportCsv"><NavIcon name="download" :size="15" /> 导出 CSV</button>
      </div>
    </div>

    <PanelCard title="筛选条件">
      <form class="filter-bar" @submit.prevent="refresh">
        <label class="field">
          <span class="field-label">能源类型</span>
          <select v-model="query.type" class="select">
            <option v-for="t in energyTypes" :key="t.code" :value="t.code">{{ t.name }}（{{ t.unit }}）</option>
          </select>
        </label>
        <label class="field">
          <span class="field-label">聚合粒度</span>
          <select v-model.number="query.bucketMinutes" class="select">
            <option :value="1">1 分钟</option>
            <option :value="5">5 分钟</option>
            <option :value="10">10 分钟</option>
            <option :value="30">30 分钟</option>
          </select>
        </label>
        <label class="field grow">
          <span class="field-label">关键字</span>
          <input v-model.trim="query.keyword" class="input" placeholder="按时间 / 介质 / 设备名筛选，如 08:30" />
        </label>
        <div class="filter-actions">
          <button class="btn btn-primary" type="submit" :disabled="loading">查询</button>
          <button class="btn" type="button" @click="reset">重置</button>
        </div>
      </form>
    </PanelCard>

    <PanelCard title="采集明细" class="mt">
      <template #extra>
        <span class="tag tag-gray">本页合计 {{ pageTotal.toLocaleString('zh-CN') }} {{ currentUnit }}</span>
      </template>

      <div v-if="error" class="empty">{{ error }}</div>

      <div v-else class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th style="width: 64px">序号</th>
              <th>采集时间</th>
              <th>能源类型</th>
              <th>采集设备</th>
              <th class="num">用量</th>
              <th style="width: 66px">单位</th>
              <th class="num">累计读数</th>
              <th class="num">碳排放 (kgCO₂e)</th>
            </tr>
          </thead>
          <tbody v-if="rows.length">
            <tr v-for="row in rows" :key="row.seq">
              <td>{{ row.seq }}</td>
              <td class="mono">{{ row.time }}</td>
              <td><span class="tag">{{ row.energyName }}</span></td>
              <td>{{ row.device }}</td>
              <td class="num strong">{{ row.value.toLocaleString('zh-CN', { minimumFractionDigits: 2 }) }}</td>
              <td class="muted">{{ row.unit }}</td>
              <td class="num muted">{{ row.cumulative.toLocaleString('zh-CN', { minimumFractionDigits: 2 }) }}</td>
              <td class="num">{{ row.carbon.toFixed(3) }}</td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr>
              <td colspan="8">
                <div v-if="loading" class="loading-rows">
                  <div v-for="n in 5" :key="n" class="skeleton" style="height: 14px"></div>
                </div>
                <div v-else class="empty">没有匹配的数据，试试调整筛选条件</div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <Pager
        :page="data?.page || 1"
        :pages="data?.pages || 1"
        :total="data?.total || 0"
        @change="onPage"
      />
    </PanelCard>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import PanelCard from '@/components/PanelCard.vue'
import Pager from '@/components/Pager.vue'
import NavIcon from '@/components/NavIcon.vue'
import { usePolling } from '@/composables/usePolling'
import { getEnergyRecords, getMeta } from '@/api'

const energyTypes = ref([])
const query = reactive({ type: 'electricity', bucketMinutes: 5, keyword: '', page: 1, size: 10 })

const { data, loading, error, countdown, refresh } = usePolling(
  () => getEnergyRecords({ ...query }),
  { interval: 20000 }
)

const rows = computed(() => data.value?.records || [])
const pageTotal = computed(() => rows.value.reduce((sum, r) => sum + (r.value || 0), 0))
const currentUnit = computed(() => energyTypes.value.find((t) => t.code === query.type)?.unit || '')

onMounted(async () => {
  try {
    const meta = await getMeta()
    energyTypes.value = meta.energyTypes
  } catch (e) {
    energyTypes.value = [
      { code: 'electricity', name: '电', unit: 'kWh' },
      { code: 'water', name: '水', unit: 'm³' },
      { code: 'naturalGas', name: '天然气', unit: 'm³' }
    ]
  }
})

watch(
  () => [query.type, query.bucketMinutes, query.keyword],
  () => {
    query.page = 1
    refresh()
  }
)

function onPage(p) {
  query.page = p
  refresh()
}

function reset() {
  query.type = 'electricity'
  query.bucketMinutes = 5
  query.keyword = ''
  query.page = 1
  refresh()
}

function exportCsv() {
  if (!rows.value.length) return
  const header = ['序号', '采集时间', '能源类型', '采集设备', '用量', '单位', '累计读数', '碳排放kgCO2e']
  const lines = rows.value.map((r) =>
    [r.seq, r.time, r.energyName, r.device, r.value, r.unit, r.cumulative, r.carbon].join(',')
  )
  const csv = '\uFEFF' + [header.join(','), ...lines].join('\r\n')
  const url = URL.createObjectURL(new Blob([csv], { type: 'text/csv;charset=utf-8' }))
  const a = document.createElement('a')
  a.href = url
  a.download = `能耗明细_${query.type}_${new Date().toISOString().slice(0, 10)}.csv`
  a.click()
  URL.revokeObjectURL(url)
}
</script>

<style scoped>
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  align-items: flex-end;
}

.field {
  width: 168px;
}

.field.grow {
  flex: 1;
  min-width: 180px;
}

.filter-actions {
  display: flex;
  gap: 8px;
  padding-bottom: 1px;
}

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

.muted {
  color: var(--muted);
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

.loading-rows {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 8px 0;
}

@media (max-width: 767px) {
  .field,
  .field.grow {
    width: 100%;
  }
  .filter-actions {
    width: 100%;
  }
  .filter-actions .btn {
    flex: 1;
  }
}
</style>
