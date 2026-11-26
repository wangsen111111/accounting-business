<template>
  <div ref="el" class="echart" :style="{ height }"></div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref, shallowRef, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  option: { type: Object, required: true },
  height: { type: String, default: '280px' }
})

const el = ref(null)
const chart = shallowRef(null)
let observer = null

function render() {
  if (!chart.value || !props.option) return
  chart.value.setOption(props.option, true)
}

onMounted(async () => {
  await nextTick()
  chart.value = echarts.init(el.value, null, { renderer: 'canvas' })
  render()
  observer = new ResizeObserver(() => chart.value && chart.value.resize())
  observer.observe(el.value)
})

watch(
  () => props.option,
  () => render(),
  { deep: true }
)

onBeforeUnmount(() => {
  observer && observer.disconnect()
  chart.value && chart.value.dispose()
  chart.value = null
})
</script>

<style scoped>
.echart {
  width: 100%;
  min-height: 160px;
}
</style>
