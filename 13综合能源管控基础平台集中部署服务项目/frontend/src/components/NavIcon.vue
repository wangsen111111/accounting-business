<template>
  <svg
    class="nav-icon"
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    :stroke-width="stroke"
    stroke-linecap="round"
    stroke-linejoin="round"
    aria-hidden="true"
  >
    <path v-for="(d, i) in paths[normalized]" :key="i" :d="d" />
  </svg>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  name: { type: String, default: 'home' },
  size: { type: [Number, String], default: 18 },
  stroke: { type: [Number, String], default: 1.7 }
})

const paths = {
  home: ['M3 10.6 12 3.4l9 7.2V20a1 1 0 0 1-1 1h-5v-6H9v6H4a1 1 0 0 1-1-1z'],
  list: ['M8 6h13', 'M8 12h13', 'M8 18h13', 'M3.6 6h.01', 'M3.6 12h.01', 'M3.6 18h.01'],
  chart: ['M4 4v16h16', 'M7.5 15.5l3.2-4.6 3 2.8 4.3-6.2'],
  calc: ['M5 3h14a1 1 0 0 1 1 1v16a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1z', 'M8 7h8', 'M8 12h2', 'M14 12h2', 'M8 16h2', 'M14 16h2'],
  bulb: ['M9 18h6', 'M10 21h4', 'M12 3a6 6 0 0 0-3.6 10.8c.6.5 1 1.3 1 2.1V16h5.2v-.1c0-.8.4-1.6 1-2.1A6 6 0 0 0 12 3z'],
  target: ['M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18z', 'M12 16.5a4.5 4.5 0 1 0 0-9 4.5 4.5 0 0 0 0 9z', 'M12 13.5a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3z'],
  flow: ['M4 7h9a3 3 0 0 1 3 3v7', 'M4 12h6a3 3 0 0 1 3 3v1', 'M16 13l-3.5 4L16 21', 'M4 17h3'],
  wallet: ['M4 8.5A2.5 2.5 0 0 1 6.5 6H19a1 1 0 0 1 1 1v11a1 1 0 0 1-1 1H6.5A2.5 2.5 0 0 1 4 16.5z', 'M4 9h16', 'M15.5 13.5h.01'],
  carbon: ['M12 3l7.8 4.5v9L12 21l-7.8-4.5v-9z', 'M12 15.6a3.6 3.6 0 1 0 0-7.2 3.6 3.6 0 0 0 0 7.2z'],
  pulse: ['M3 13h3.5l2.2-6 3.4 12 2.6-7 1.8 3H21'],
  alert: ['M12 4.5 21 19.5H3z', 'M12 10v4', 'M12 16.6h.01'],
  file: ['M6 3h8l5 5v13H6z', 'M14 3v5h5', 'M9 13h6', 'M9 17h4'],
  factor: ['M4 7h10', 'M18 7h2', 'M4 17h4', 'M12 17h8', 'M16 4.8v4.4', 'M10 14.8v4.4'],
  leaf: ['M20 4c-8 0-13 3.6-13 9.5A4.5 4.5 0 0 0 11.5 18C17.4 18 20 12.5 20 4z', 'M4 21c1.5-4.5 4-8 8.5-10.5'],
  chain: ['M10.5 13.5a4 4 0 0 0 5.7 0l2.3-2.3a4 4 0 0 0-5.7-5.7L11.5 6.8', 'M13.5 10.5a4 4 0 0 0-5.7 0l-2.3 2.3a4 4 0 0 0 5.7 5.7l1.3-1.3'],
  asset: ['M12 8.5a6 6 0 1 0 0 12 6 6 0 0 0 0-12z', 'M9 3.5c1.6.7 2.7 1.8 3 3.2', 'M15 3.5c-1.6.7-2.7 1.8-3 3.2'],
  settings: ['M12 15.2a3.2 3.2 0 1 0 0-6.4 3.2 3.2 0 0 0 0 6.4z', 'M4 12h1.8', 'M18.2 12H20', 'M12 4v1.8', 'M12 18.2V20', 'M6.3 6.3l1.3 1.3', 'M16.4 16.4l1.3 1.3', 'M17.7 6.3l-1.3 1.3', 'M7.6 16.4l-1.3 1.3'],
  menu: ['M4 7h16', 'M4 12h16', 'M4 17h16'],
  close: ['M6 6l12 12', 'M18 6L6 18'],
  refresh: ['M20 12a8 8 0 1 1-2.5-5.8', 'M20 4v4h-4'],
  user: ['M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8z', 'M4.5 20c.9-3.4 3.9-5.2 7.5-5.2s6.6 1.8 7.5 5.2'],
  bell: ['M18 16H6l1.4-2.2V10a4.6 4.6 0 0 1 9.2 0v3.8z', 'M10 19a2 2 0 0 0 4 0'],
  search: ['M11 18a7 7 0 1 0 0-14 7 7 0 0 0 0 14z', 'M16.2 16.2 21 21'],
  logout: ['M15 5h4a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1h-4', 'M11 8l-4 4 4 4', 'M3 12h8'],
  download: ['M12 4v11', 'M7.5 11 12 15.5 16.5 11', 'M4 19h16'],
  plus: ['M12 5v14', 'M5 12h14'],
  dot: ['M12 12h.01']
}

const normalized = computed(() => (paths[props.name] ? props.name : 'dot'))
</script>

<style scoped>
.nav-icon {
  flex: none;
  display: block;
}
</style>
