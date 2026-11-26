import { onBeforeUnmount, onMounted, ref } from 'vue'

/**
 * 轮询数据：默认 20 秒一次，与后端刷新周期保持一致。
 * 返回 data / loading / error / countdown / lastUpdated / refresh
 */
export function usePolling(fetcher, options = {}) {
  const { interval = 20000, immediate = true, autoStart = true, transform = (v) => v } = options

  const data = ref(null)
  const loading = ref(false)
  const error = ref('')
  const lastUpdated = ref(null)
  const countdown = ref(Math.round(interval / 1000))

  let timer = null
  let inFlight = false

  async function load(showLoading = false) {
    if (inFlight) return
    inFlight = true
    if (showLoading || !data.value) loading.value = true
    try {
      const result = await fetcher()
      data.value = transform(result)
      error.value = ''
      lastUpdated.value = new Date()
    } catch (e) {
      error.value = e.message || '数据加载失败'
    } finally {
      loading.value = false
      inFlight = false
    }
  }

  function tick() {
    countdown.value -= 1
    if (countdown.value <= 0) {
      countdown.value = Math.round(interval / 1000)
      load()
    }
  }

  function start() {
    if (timer) return
    timer = setInterval(tick, 1000)
  }

  function stop() {
    clearInterval(timer)
    timer = null
  }

  async function refresh() {
    countdown.value = Math.round(interval / 1000)
    await load(true)
  }

  onMounted(() => {
    if (autoStart) {
      if (immediate) load(true)
      start()
    }
  })

  onBeforeUnmount(stop)

  return { data, loading, error, countdown, lastUpdated, refresh, start, stop, load }
}
