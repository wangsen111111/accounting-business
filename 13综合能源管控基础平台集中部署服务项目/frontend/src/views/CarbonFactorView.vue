<template>
  <div class="page">
    <div class="page-head">
      <div>
        <h2 class="page-title">碳因子库设置</h2>
        <p class="page-sub">维护排放源 / 减排量 / 吸收量因子，核算单据按调用时的因子快照计算</p>
      </div>
      <div class="page-actions">
        <input v-model.trim="keyword" class="input" placeholder="搜索因子名称 / 编码" style="width: 200px" />
        <button class="btn btn-primary" @click="openForm"><NavIcon name="plus" :size="15" /> 新增因子</button>
      </div>
    </div>

    <PanelCard title="因子清单">
      <template #extra>
        <span class="tag tag-gray">共 {{ filtered.length }} 条</span>
        <span class="tag tag-amber">演示：新增仅保存在当前页面</span>
      </template>

      <div class="chips">
        <button
          v-for="c in categories"
          :key="c"
          class="chip"
          :class="{ active: category === c }"
          @click="category = c"
        >
          {{ c }}
        </button>
      </div>

      <div class="table-wrap mt">
        <table class="table">
          <thead>
            <tr>
              <th>因子编码</th>
              <th>因子名称</th>
              <th>类别</th>
              <th>参数类型</th>
              <th class="num">默认值</th>
              <th>单位</th>
              <th>数据来源</th>
              <th>更新时间</th>
              <th class="num">版本</th>
            </tr>
          </thead>
          <tbody v-if="filtered.length">
            <tr v-for="f in filtered" :key="f.code">
              <td class="mono">{{ f.code }}</td>
              <td><b>{{ f.name }}</b></td>
              <td><span class="tag" :class="f.category === '排放源' ? '' : f.category === '减排量' ? 'tag-amber' : 'tag-gray'">{{ f.category }}</span></td>
              <td>{{ f.paramType }}</td>
              <td class="num strong">{{ f.value }}</td>
              <td class="muted">{{ f.unit }}</td>
              <td class="muted src">{{ f.source }}</td>
              <td class="mono">{{ f.updatedAt }}</td>
              <td class="num">v{{ f.version }}</td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr><td colspan="9"><div class="empty">{{ error || '没有匹配的因子' }}</div></td></tr>
          </tbody>
        </table>
      </div>
    </PanelCard>

    <transition name="modal">
      <div v-if="showForm" class="modal-mask" @click.self="showForm = false">
        <div class="modal">
          <header class="modal-head">
            <div>
              <h3>新增碳因子</h3>
              <p>保存后可在后续核算单据中选择使用（演示环境不落库）</p>
            </div>
            <button class="icon-close" @click="showForm = false"><NavIcon name="close" :size="17" /></button>
          </header>
          <div class="modal-body">
            <div class="form-grid">
              <label class="field">
                <span class="field-label">因子名称 *</span>
                <input v-model.trim="form.name" class="input" placeholder="如：测试燃料" />
              </label>
              <label class="field">
                <span class="field-label">因子编码 *</span>
                <input v-model.trim="form.code" class="input" placeholder="如：F-TEST-001" />
              </label>
              <label class="field">
                <span class="field-label">类别</span>
                <select v-model="form.category" class="select">
                  <option>排放源</option>
                  <option>减排量</option>
                  <option>吸收量</option>
                </select>
              </label>
              <label class="field">
                <span class="field-label">参数类型</span>
                <select v-model="form.paramType" class="select">
                  <option>排放因子</option>
                  <option>活动水平</option>
                </select>
              </label>
              <label class="field">
                <span class="field-label">默认值 *</span>
                <input v-model.number="form.value" class="input" type="number" step="0.0001" />
              </label>
              <label class="field">
                <span class="field-label">单位 *</span>
                <input v-model.trim="form.unit" class="input" placeholder="如：kgCO₂/kg" />
              </label>
            </div>
            <p v-if="formError" class="form-error">{{ formError }}</p>
          </div>
          <footer class="modal-foot">
            <button class="btn" @click="showForm = false">取消</button>
            <button class="btn btn-primary" @click="submit">保存</button>
          </footer>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import PanelCard from '@/components/PanelCard.vue'
import NavIcon from '@/components/NavIcon.vue'
import { getCarbonFactors } from '@/api'

const factors = ref([])
const error = ref('')
const keyword = ref('')
const category = ref('全部')
const categories = ['全部', '排放源', '减排量', '吸收量']

const showForm = ref(false)
const formError = ref('')
const form = reactive({ name: '', code: '', category: '排放源', paramType: '排放因子', value: 0, unit: '' })

const filtered = computed(() =>
  factors.value.filter((f) => {
    const hitCategory = category.value === '全部' || f.category === category.value
    const hitKeyword =
      !keyword.value ||
      f.name.includes(keyword.value) ||
      f.code.toLowerCase().includes(keyword.value.toLowerCase())
    return hitCategory && hitKeyword
  })
)

onMounted(load)

async function load() {
  try {
    factors.value = await getCarbonFactors()
  } catch (e) {
    error.value = e.message
  }
}

function openForm() {
  formError.value = ''
  Object.assign(form, { name: '', code: '', category: '排放源', paramType: '排放因子', value: 0, unit: '' })
  showForm.value = true
}

function submit() {
  if (!form.name || !form.code || !form.unit) {
    formError.value = '请填写因子名称、编码与单位'
    return
  }
  factors.value = [
    {
      ...form,
      source: '手动录入（演示）',
      updatedAt: new Date().toISOString().slice(0, 10),
      version: 1
    },
    ...factors.value
  ]
  showForm.value = false
}
</script>

<style scoped>
.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip {
  height: 30px;
  padding: 0 14px;
  border: 1px solid var(--line);
  border-radius: 18px;
  background: #fff;
  color: var(--ink-2);
  font-size: 12.5px;
  cursor: pointer;
}

.chip.active {
  background: var(--gf-50);
  border-color: var(--gf-500);
  color: var(--gf-700);
  font-weight: 600;
}

.mt {
  margin-top: 14px;
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

.src {
  max-width: 240px;
  overflow: hidden;
  text-overflow: ellipsis;
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
  width: min(600px, 100%);
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

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  padding: 18px 20px;
}

.form-error {
  margin: 0 20px 14px;
  font-size: 12px;
  color: var(--red);
  background: var(--red-soft);
  padding: 7px 10px;
  border-radius: 6px;
}

.modal-foot {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 14px 20px;
  border-top: 1px solid var(--line);
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.2s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

@media (max-width: 767px) {
  .form-grid {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
