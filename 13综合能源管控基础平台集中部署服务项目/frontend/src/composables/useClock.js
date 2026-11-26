import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { formatDateTime, formatTime } from '@/utils/datetime'

/**
 * 每秒跳动的真实时钟。
 * 用于 clock（1:1 对齐）模式下展示"当前时间"，而不是等接口 20 秒刷新一次。
 */
export function useClock() {
  const now = ref(new Date())
  let timer = null

  onMounted(() => {
    timer = setInterval(() => {
      now.value = new Date()
    }, 1000)
  })

  onBeforeUnmount(() => {
    clearInterval(timer)
    timer = null
  })

  const dateTime = computed(() => formatDateTime(now.value))
  const time = computed(() => formatTime(now.value))

  return { now, dateTime, time }
}
