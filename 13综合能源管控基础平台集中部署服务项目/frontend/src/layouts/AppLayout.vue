<template>
  <div class="shell">
    <AppTopbar
      :is-mobile="isMobile"
      :simulated-time="simulatedTime"
      :refresh-seconds="refreshSeconds"
      :mode="metaMode"
      @toggle="toggleSidebar"
    />

    <div class="shell-body">
      <AppSidebar
        :collapsed="collapsed"
        :mobile-open="drawerOpen"
        @navigate="drawerOpen = false"
      />

      <transition name="mask">
        <div v-if="isMobile && drawerOpen" class="mask" @click="drawerOpen = false"></div>
      </transition>

      <main class="shell-main">
        <router-view v-slot="{ Component }">
          <transition name="page" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import AppTopbar from '@/components/AppTopbar.vue'
import AppSidebar from '@/components/AppSidebar.vue'
import { useBreakpoint } from '@/composables/useBreakpoint'
import { usePolling } from '@/composables/usePolling'
import { getMeta } from '@/api'

const { isMobile, isTablet } = useBreakpoint()

const drawerOpen = ref(false)
const userCollapsed = ref(false)

const collapsed = computed(() => {
  if (isMobile.value) return false
  // 平板默认收起为图标栏，点一下可展开
  return isTablet.value ? !userCollapsed.value : userCollapsed.value
})

function toggleSidebar() {
  if (isMobile.value) {
    drawerOpen.value = !drawerOpen.value
  } else {
    userCollapsed.value = !userCollapsed.value
  }
}

const { data: meta } = usePolling(getMeta, { interval: 20000 })

const simulatedTime = computed(() => meta.value?.refresh?.simulatedTime || '')
const refreshSeconds = computed(() =>
  meta.value?.refresh?.tickMillis ? Math.round(meta.value.refresh.tickMillis / 1000) : 20
)
const metaMode = computed(() => meta.value?.refresh?.mode || 'clock')

watch(isMobile, (v) => {
  if (!v) drawerOpen.value = false
})

watch(drawerOpen, (v) => {
  document.body.style.overflow = v ? 'hidden' : ''
})
</script>

<style scoped>
.shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg);
}

.shell-body {
  display: flex;
  align-items: flex-start;
  flex: 1;
  min-height: 0;
}

.shell-main {
  flex: 1;
  min-width: 0;
  min-height: calc(100vh - var(--topbar-h));
}

.mask {
  position: fixed;
  inset: var(--topbar-h) 0 0 0;
  background: rgba(12, 30, 22, 0.42);
  z-index: 15;
  backdrop-filter: blur(1px);
}

.mask-enter-active,
.mask-leave-active {
  transition: opacity 0.2s ease;
}

.mask-enter-from,
.mask-leave-to {
  opacity: 0;
}

.page-enter-active,
.page-leave-active {
  transition: opacity 0.16s ease, transform 0.16s ease;
}

.page-enter-from {
  opacity: 0;
  transform: translateY(6px);
}

.page-leave-to {
  opacity: 0;
}
</style>
