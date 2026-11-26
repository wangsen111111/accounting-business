<template>
  <div class="page">
    <div class="page-head">
      <div>
        <h2 class="page-title">{{ meta.title }}</h2>
        <p class="page-sub">{{ meta.group }} · 规划中</p>
      </div>
      <div class="page-actions">
        <router-link to="/home" class="btn">返回驾驶舱</router-link>
      </div>
    </div>

    <div class="card placeholder">
      <div class="ph-art" aria-hidden="true">
        <span v-for="n in 7" :key="n" class="ph-hex" :style="hexStyle(n)"></span>
      </div>
      <div class="ph-content">
        <span class="tag tag-amber">需求已确认 · 本期未实现</span>
        <h3>{{ meta.title }}</h3>
        <p>{{ meta.summary }}</p>
        <ul>
          <li v-for="f in meta.features || []" :key="f">{{ f }}</li>
        </ul>
        <p class="hint">
          当前阶段聚焦「数据采集 → 入库 → 核算 → 可视化」主链路，该模块在需求文档中已定义，
          待主链路验收后按优先级实现。
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const meta = computed(() => route.meta || {})

function hexStyle(n) {
  const row = Math.floor((n - 1) / 3)
  const col = (n - 1) % 3
  return {
    left: `${col * 62 + (row % 2 ? 24 : 0)}px`,
    top: `${row * 46}px`,
    opacity: 0.16 + (n % 3) * 0.08
  }
}
</script>

<style scoped>
.placeholder {
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr);
  gap: 26px;
  padding: 30px;
  align-items: center;
  overflow: hidden;
}

.ph-art {
  position: relative;
  height: 210px;
}

.ph-hex {
  position: absolute;
  width: 60px;
  height: 54px;
  background: var(--gf-500);
  clip-path: polygon(50% 0%, 100% 25%, 100% 75%, 50% 100%, 0% 75%, 0% 25%);
}

.ph-content h3 {
  margin: 12px 0 8px;
  font-size: 18px;
}

.ph-content p {
  font-size: 13px;
  color: var(--ink-2);
  line-height: 1.75;
}

.ph-content ul {
  margin: 14px 0 0;
  padding-left: 18px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px 18px;
  font-size: 12.5px;
  color: var(--muted);
}

.ph-content .hint {
  margin-top: 16px;
  padding: 10px 12px;
  border-radius: 8px;
  background: var(--gf-50);
  font-size: 12px;
  color: var(--muted);
}

@media (max-width: 900px) {
  .placeholder {
    grid-template-columns: minmax(0, 1fr);
  }
  .ph-art {
    display: none;
  }
  .ph-content ul {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
