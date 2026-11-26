<template>
  <header class="topbar">
    <div class="tb-left">
      <button class="icon-btn" :aria-label="'菜单'" @click="$emit('toggle')">
        <NavIcon :name="isMobile ? 'menu' : 'list'" :size="19" />
      </button>
      <router-link to="/home" class="brand">
        <span class="brand-mark">
          <svg viewBox="0 0 32 32" width="22" height="22" aria-hidden="true">
            <path d="M16 2 29 9.5v13L16 30 3 22.5v-13z" fill="rgba(255,255,255,.18)" />
            <path d="M16 7.5 24 12v8l-8 4.5L8 20v-8z" fill="#8FD3AE" />
            <path d="M16 12.5c2.6 0 3.6 2 3.2 4.2-.3 1.6-1.7 3-3.2 3.3-1.5-.3-2.9-1.7-3.2-3.3-.4-2.2.6-4.2 3.2-4.2z" fill="#17513a" />
          </svg>
        </span>
        <span class="brand-text">
          <b>综合能源管控基础平台集中部署服务项目</b>
          <i>Green Factory Energy &amp; Carbon Management</i>
        </span>
      </router-link>
    </div>

    <nav v-if="!isMobile" class="crumbs">
      <span>{{ crumbGroup }}</span>
      <span class="sep">/</span>
      <span class="current">{{ crumbTitle }}</span>
    </nav>

    <div class="tb-right">
      <span v-if="!isMobile && isReplay" class="tb-chip">
        <span class="pulse-dot"></span>
        {{ refreshSeconds }}s 自动刷新
      </span>
      <span class="tb-time" :title="displayTime">{{ displayTime }}</span>

      <div class="user" @click="menuOpen = !menuOpen">
        <span class="avatar">{{ initial }}</span>
        <span v-if="!isMobile" class="user-name">{{ userName }}</span>
        <transition name="fade">
          <div v-if="menuOpen" class="user-menu">
            <div class="um-head">
              <b>{{ userName }}</b>
              <span>{{ role }}</span>
            </div>
            <button class="um-item" @click="logout">
              <NavIcon name="logout" :size="15" /> 退出登录
            </button>
          </div>
        </transition>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavIcon from './NavIcon.vue'
import { useClock } from '@/composables/useClock'

const props = defineProps({
  isMobile: { type: Boolean, default: false },
  simulatedTime: { type: String, default: '' },
  refreshSeconds: { type: Number, default: 20 },
  mode: { type: String, default: 'clock' }
})
defineEmits(['toggle'])

const route = useRoute()
const router = useRouter()
const menuOpen = ref(false)
const user = ref({ displayName: '王工', role: '系统管理员' })

// clock 模式下顶栏时钟每秒跳动，直接显示真实时间；replay 模式显示后端回放时刻
const { dateTime, time } = useClock()
const isReplay = computed(() => props.mode === 'replay')
const displayTime = computed(() => {
  if (isReplay.value) {
    return props.simulatedTime || '—'
  }
  return props.isMobile ? time.value : dateTime.value
})

onMounted(() => {
  try {
    const saved = JSON.parse(localStorage.getItem('gfecmp_user') || '{}')
    if (saved.displayName) user.value = saved
  } catch (e) {
    /* ignore */
  }
  document.addEventListener('click', closeOnOutside)
})
onBeforeUnmount(() => document.removeEventListener('click', closeOnOutside))

function closeOnOutside(e) {
  if (!e.target.closest('.user')) menuOpen.value = false
}

const crumbGroup = computed(() => route.meta.group || '概览')
const crumbTitle = computed(() => route.meta.title || '首页驾驶舱')
const userName = computed(() => user.value.displayName || '王工')
const role = computed(() => user.value.role || '系统管理员')
const initial = computed(() => userName.value.slice(0, 1))

function logout() {
  localStorage.removeItem('gfecmp_token')
  localStorage.removeItem('gfecmp_user')
  router.replace('/login')
}
</script>

<style scoped>
.topbar {
  height: var(--topbar-h);
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 0 16px 0 12px;
  background: linear-gradient(90deg, var(--gf-800) 0%, var(--gf-700) 55%, var(--gf-600) 100%);
  color: #fff;
  position: sticky;
  top: 0;
  z-index: 30;
  box-shadow: 0 2px 10px rgba(18, 63, 46, 0.18);
}

.tb-left {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.icon-btn {
  width: 34px;
  height: 34px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
  cursor: pointer;
  transition: background 0.15s ease;
}

.icon-btn:hover {
  background: rgba(255, 255, 255, 0.18);
}

.brand {
  display: flex;
  align-items: center;
  gap: 9px;
  min-width: 0;
}

.brand-mark {
  width: 32px;
  height: 32px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 9px;
  background: rgba(255, 255, 255, 0.12);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
  min-width: 0;
}

.brand-text b {
  font-size: 15px;
  letter-spacing: 0.4px;
  white-space: nowrap;
}

.brand-text i {
  font-style: normal;
  font-size: 9px;
  letter-spacing: 0.4px;
  opacity: 0.62;
  white-space: nowrap;
}

.crumbs {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.66);
  padding-left: 18px;
  border-left: 1px solid rgba(255, 255, 255, 0.16);
  flex: 1;
  min-width: 0;
}

.crumbs .sep {
  opacity: 0.5;
}

.crumbs .current {
  color: #fff;
  font-weight: 600;
}

.tb-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 12px;
}

.tb-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 26px;
  padding: 0 10px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.12);
  font-size: 12px;
  white-space: nowrap;
}

.pulse-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #8fd3ae;
  box-shadow: 0 0 0 0 rgba(143, 211, 174, 0.7);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(143, 211, 174, 0.6);
  }
  70% {
    box-shadow: 0 0 0 6px rgba(143, 211, 174, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(143, 211, 174, 0);
  }
}

.tb-time {
  font-size: 12px;
  font-variant-numeric: tabular-nums;
  opacity: 0.86;
  white-space: nowrap;
}

.user {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px 4px 4px;
  border-radius: 20px;
  cursor: pointer;
  position: relative;
  transition: background 0.15s ease;
}

.user:hover {
  background: rgba(255, 255, 255, 0.12);
}

.avatar {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: #f2a93b;
  color: #4a2f05;
  font-size: 12px;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.user-name {
  font-size: 12px;
}

.user-menu {
  position: absolute;
  right: 0;
  top: 44px;
  width: 178px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 12px 32px rgba(18, 63, 46, 0.18);
  padding: 10px;
  color: var(--ink);
  z-index: 40;
}

.um-head {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 4px 8px 10px;
  border-bottom: 1px solid var(--line);
  margin-bottom: 6px;
}

.um-head b {
  font-size: 13px;
}

.um-head span {
  font-size: 11px;
  color: var(--muted);
}

.um-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  border: 0;
  border-radius: 8px;
  background: transparent;
  color: var(--ink-2);
  font-size: 13px;
  cursor: pointer;
}

.um-item:hover {
  background: var(--gf-50);
  color: var(--gf-700);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

@media (max-width: 767px) {
  .topbar {
    padding: 0 10px;
    gap: 8px;
  }
  .brand-text i {
    display: none;
  }
  .brand-text b {
    font-size: 13.5px;
  }
}
</style>
