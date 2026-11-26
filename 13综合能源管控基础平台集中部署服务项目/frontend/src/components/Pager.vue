<template>
  <div class="pager">
    <span class="pager-info">共 {{ total }} 条 · 第 {{ page }}/{{ pages || 1 }} 页</span>
    <div class="pager-btns">
      <button class="btn btn-sm" :disabled="page <= 1" @click="go(page - 1)">上一页</button>
      <button
        v-for="p in visiblePages"
        :key="p"
        class="btn btn-sm page-num"
        :class="{ active: p === page }"
        @click="go(p)"
      >
        {{ p }}
      </button>
      <button class="btn btn-sm" :disabled="page >= pages" @click="go(page + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  page: { type: Number, default: 1 },
  pages: { type: Number, default: 1 },
  total: { type: Number, default: 0 }
})
const emit = defineEmits(['change'])

const visiblePages = computed(() => {
  const pages = props.pages || 1
  const start = Math.max(1, Math.min(props.page - 1, pages - 2))
  return Array.from({ length: Math.min(3, pages) }, (_, i) => start + i).filter((p) => p <= pages)
})

function go(p) {
  if (p < 1 || p > (props.pages || 1) || p === props.page) return
  emit('change', p)
}
</script>

<style scoped>
.pager {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 14px 4px 2px;
}

.pager-info {
  font-size: 12px;
  color: var(--muted);
}

.pager-btns {
  display: flex;
  gap: 6px;
}

.page-num {
  min-width: 32px;
  padding: 0 8px;
}

.page-num.active {
  background: var(--gf-500);
  border-color: var(--gf-500);
  color: #fff;
}
</style>
