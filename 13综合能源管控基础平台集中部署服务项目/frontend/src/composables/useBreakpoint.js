import { onBeforeUnmount, onMounted, ref } from 'vue'

/**
 * 响应式断点：与 CSS 中的媒体查询保持一致。
 * desktop ≥1200 / tablet 768~1199 / mobile <768
 */
export function useBreakpoint() {
  const width = ref(typeof window === 'undefined' ? 1440 : window.innerWidth)
  const isMobile = ref(width.value < 768)
  const isTablet = ref(width.value >= 768 && width.value < 1200)
  const isDesktop = ref(width.value >= 1200)

  const update = () => {
    width.value = window.innerWidth
    isMobile.value = width.value < 768
    isTablet.value = width.value >= 768 && width.value < 1200
    isDesktop.value = width.value >= 1200
  }

  let timer = null
  const onResize = () => {
    clearTimeout(timer)
    timer = setTimeout(update, 80)
  }

  onMounted(() => window.addEventListener('resize', onResize))
  onBeforeUnmount(() => {
    clearTimeout(timer)
    window.removeEventListener('resize', onResize)
  })

  return { width, isMobile, isTablet, isDesktop }
}
