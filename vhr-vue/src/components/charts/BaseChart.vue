<template>
  <div ref="chartRef" class="base-chart" :style="{height}"></div>
</template>

<script setup>
import {onBeforeUnmount, onMounted, ref, watch} from 'vue';
import * as echarts from 'echarts/core';
import {BarChart, LineChart, PieChart} from 'echarts/charts';
import {GridComponent, LegendComponent, TitleComponent, TooltipComponent} from 'echarts/components';
import {CanvasRenderer} from 'echarts/renderers';

echarts.use([BarChart, LineChart, PieChart, GridComponent, LegendComponent, TitleComponent, TooltipComponent, CanvasRenderer]);

const props = defineProps({
  option: {type: Object, required: true},
  height: {type: String, default: '300px'}
});

const chartRef = ref();
let chart = null;
let observer = null;

function render() {
  if (!chartRef.value) return;
  if (!chart) {
    chart = echarts.init(chartRef.value);
  }
  chart.setOption(props.option || {}, true);
}

onMounted(() => {
  render();
  observer = new ResizeObserver(() => chart && chart.resize());
  observer.observe(chartRef.value);
});

watch(() => props.option, render, {deep: true});

onBeforeUnmount(() => {
  observer && observer.disconnect();
  chart && chart.dispose();
  chart = null;
});
</script>

<style scoped>
.base-chart {
  width: 100%;
  min-height: 220px;
}
</style>
