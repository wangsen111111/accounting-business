<template>
  <section class="card panel">
    <header v-if="title || $slots.extra" class="card-head">
      <h3 class="card-title">{{ title }}</h3>
      <div class="panel-extra">
        <slot name="extra" />
      </div>
    </header>
    <div class="card-body" :class="{ 'no-pad': noPadding }">
      <slot />
    </div>
  </section>
</template>

<script setup>
defineProps({
  title: { type: String, default: '' },
  noPadding: { type: Boolean, default: false }
})
</script>

<style scoped>
.panel {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.panel-extra {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.card-body.no-pad {
  padding: 12px 0 0;
}
</style>
