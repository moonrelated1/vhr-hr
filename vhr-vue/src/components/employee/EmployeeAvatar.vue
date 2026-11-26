<template>
  <span class="employee-avatar" :style="avatarStyle" :title="name">
    {{ initial }}
  </span>
</template>

<script setup>
import {computed} from 'vue';

const props = defineProps({
  name: {type: String, default: ''},
  seed: {type: [String, Number], default: ''},
  size: {type: Number, default: 36}
});

const palettes = [
  ['#4f6ef7', '#7c93ff'],
  ['#0ea5e9', '#38bdf8'],
  ['#10b981', '#34d399'],
  ['#f59e0b', '#fbbf24'],
  ['#ec4899', '#f472b6'],
  ['#8b5cf6', '#a78bfa'],
  ['#14b8a6', '#2dd4bf'],
  ['#f97316', '#fb923c']
];

const initial = computed(() => {
  const value = (props.name || '?').trim();
  return value ? value.charAt(0).toUpperCase() : '?';
});

const avatarStyle = computed(() => {
  const key = String(props.seed ?? props.name ?? '');
  let hash = 0;
  for (let i = 0; i < key.length; i++) {
    hash = (hash * 31 + key.charCodeAt(i)) >>> 0;
  }
  const [start, end] = palettes[hash % palettes.length];
  return {
    width: `${props.size}px`,
    height: `${props.size}px`,
    fontSize: `${Math.max(12, Math.round(props.size * 0.38))}px`,
    background: `linear-gradient(135deg, ${start}, ${end})`
  };
});
</script>

<style scoped>
.employee-avatar {
  display: inline-flex;
  flex: 0 0 auto;
  align-items: center;
  justify-content: center;
  color: #fff;
  border-radius: 50%;
  font-weight: 700;
  letter-spacing: -0.02em;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.12);
  user-select: none;
}
</style>
