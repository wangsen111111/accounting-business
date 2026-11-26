<template>
  <aside class="sidebar" :class="{ collapsed, 'mobile-open': mobileOpen }">
    <div class="side-scroll">
      <div v-for="group in menuGroups" :key="group.title" class="group">
        <p v-if="!collapsed" class="group-title">{{ group.title }}</p>
        <div v-else class="group-line"></div>
        <router-link
          v-for="item in group.items"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: isActive(item.path) }"
          :title="collapsed ? item.label : ''"
          @click="$emit('navigate')"
        >
          <NavIcon :name="item.icon" :size="17" />
          <span v-if="!collapsed" class="nav-label">{{ item.label }}</span>
        </router-link>
      </div>
    </div>

    <div class="side-foot" :class="{ compact: collapsed }">
      <template v-if="!collapsed">
        <span class="dot"></span>
        <div class="foot-text">
          <b> v1.0</b>
          <span>数据源：累计能耗样本表</span>
        </div>
      </template>
      <span v-else class="dot"></span>
    </div>
  </aside>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { menuGroups } from '@/config/menu'
import NavIcon from './NavIcon.vue'

const props = defineProps({
  collapsed: { type: Boolean, default: false },
  mobileOpen: { type: Boolean, default: false }
})
defineEmits(['navigate'])

const route = useRoute()

function isActive(path) {
  return route.path === path
}
</script>

<style scoped>
.sidebar {
  width: var(--sidebar-w);
  flex: none;
  background: linear-gradient(180deg, #17513a 0%, #123f2e 100%);
  color: rgba(255, 255, 255, 0.78);
  display: flex;
  flex-direction: column;
  height: calc(100vh - var(--topbar-h));
  position: sticky;
  top: var(--topbar-h);
  transition: width 0.18s ease, transform 0.22s ease;
  z-index: 20;
}

.sidebar.collapsed {
  width: var(--sidebar-w-md);
}

.side-scroll {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 10px 8px 16px;
  scrollbar-width: thin;
  scrollbar-color: rgba(255, 255, 255, 0.2) transparent;
}

.side-scroll::-webkit-scrollbar {
  width: 5px;
}

.side-scroll::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.16);
  border-radius: 3px;
}

.group + .group {
  margin-top: 6px;
}

.group-title {
  font-size: 11px;
  letter-spacing: 1.2px;
  color: rgba(255, 255, 255, 0.42);
  padding: 10px 12px 6px;
}

.group-line {
  height: 1px;
  margin: 8px 10px;
  background: rgba(255, 255, 255, 0.1);
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 38px;
  padding: 0 12px;
  border-radius: 8px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.76);
  position: relative;
  transition: background 0.14s ease, color 0.14s ease;
  white-space: nowrap;
}

.collapsed .nav-item {
  justify-content: center;
  padding: 0;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.nav-item.active {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
  font-weight: 600;
}

.nav-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 9px;
  bottom: 9px;
  width: 3px;
  border-radius: 0 3px 3px 0;
  background: var(--amber);
}

.nav-label {
  overflow: hidden;
  text-overflow: ellipsis;
}

.side-foot {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 14px 14px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
}

.side-foot.compact {
  justify-content: center;
  padding: 12px 0 14px;
}

.dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #63d18f;
  box-shadow: 0 0 8px rgba(99, 209, 143, 0.8);
  flex: none;
}

.foot-text {
  display: flex;
  flex-direction: column;
  line-height: 1.4;
  min-width: 0;
}

.foot-text b {
  color: rgba(255, 255, 255, 0.78);
  font-weight: 600;
  font-size: 11px;
}

.foot-text span {
  font-size: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 移动端：抽屉模式 */
@media (max-width: 767px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: var(--topbar-h);
    bottom: 0;
    width: 244px;
    height: auto;
    transform: translateX(-102%);
    box-shadow: 12px 0 30px rgba(18, 63, 46, 0.28);
  }

  .sidebar.mobile-open {
    transform: translateX(0);
  }
}
</style>
