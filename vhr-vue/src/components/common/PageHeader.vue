<template>
  <div class="page-header">
    <div class="page-heading">
      <div v-if="eyebrow" class="page-eyebrow">{{ eyebrow }}</div>
      <h1>{{ title }}</h1>
      <p v-if="subtitle">{{ subtitle }}</p>
    </div>
    <div v-if="$slots.actions" class="page-actions">
      <slot name="actions"/>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {type: String, required: true},
  subtitle: {type: String, default: ''},
  eyebrow: {type: String, default: ''}
});
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  padding: 2px 2px 4px;
}

.page-heading h1 {
  margin: 0;
  color: #172033;
  font-size: 25px;
  font-weight: 700;
  letter-spacing: -0.03em;
}

.page-heading p {
  margin: 7px 0 0;
  color: #8a95a8;
  font-size: 13px;
}

.page-eyebrow {
  margin-bottom: 6px;
  color: #4f6ef7;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.page-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

@media (max-width: 900px) {
  .page-header {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
