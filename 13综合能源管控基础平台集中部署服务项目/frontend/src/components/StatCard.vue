<template>
  <div class="stat card">
    <div class="stat-top">
      <span class="stat-label">{{ label }}</span>
      <span class="stat-icon" :style="{ background: tint, color: color }">
        <NavIcon :name="icon" :size="15" />
      </span>
    </div>
    <div class="stat-value">
      <span class="num">{{ formatted }}</span>
      <span class="unit">{{ unit }}</span>
    </div>
    <div v-if="hint" class="stat-hint">
      <span :class="['trend', trend]">{{ hint }}</span>
    </div>
    <div v-else class="stat-hint">
      <span class="muted">—</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import NavIcon from './NavIcon.vue'

const props = defineProps({
  label: { type: String, required: true },
  value: { type: [Number, String], default: 0 },
  unit: { type: String, default: '' },
  hint: { type: String, default: '' },
  trend: { type: String, default: 'flat' },
  icon: { type: String, default: 'pulse' },
  color: { type: String, default: '#2f9e63' },
  tint: { type: String, default: '#e7f3ec' },
  digits: { type: Number, default: 2 }
})

const formatted = computed(() => {
  const v = props.value
  if (typeof v === 'string') return v
  if (v === null || v === undefined || Number.isNaN(v)) return '—'
  return v.toLocaleString('zh-CN', {
    minimumFractionDigits: props.digits,
    maximumFractionDigits: props.digits
  })
})
</script>

<style scoped>
.stat {
  padding: 16px 18px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
  position: relative;
  overflow: hidden;
}

.stat::after {
  content: '';
  position: absolute;
  inset: auto -30px -40px auto;
  width: 90px;
  height: 90px;
  border-radius: 50%;
  background: var(--gf-50);
  opacity: 0.7;
}

.stat-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.stat-label {
  font-size: 13px;
  color: var(--muted);
}

.stat-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.stat-value {
  display: flex;
  align-items: baseline;
  gap: 4px;
  z-index: 1;
}

.stat-value .num {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.4px;
  font-variant-numeric: tabular-nums;
}

.stat-value .unit {
  font-size: 12px;
  color: var(--muted);
}

.stat-hint {
  font-size: 12px;
  color: var(--muted);
  z-index: 1;
}

.trend.up {
  color: var(--red);
}

.trend.down {
  color: var(--gf-600);
}

.muted {
  color: var(--muted);
}

@media (max-width: 767px) {
  .stat {
    padding: 13px 14px;
  }
  .stat-value .num {
    font-size: 20px;
  }
}
</style>
