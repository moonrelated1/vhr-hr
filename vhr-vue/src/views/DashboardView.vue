<template>
  <div v-loading="loading" class="dashboard page">
    <section class="dashboard-hero">
      <div class="hero-copy">
        <span class="hero-eyebrow">工作台 · OVERVIEW</span>
        <h1>{{ greeting }}，{{ userName }}</h1>
        <p>今天是 {{ todayText }}。这里是团队的人员概览，愿你今天的工作清晰而从容。</p>
      </div>
      <div class="hero-actions">
        <el-button type="primary" :icon="Plus" @click="goAddEmployee">新增员工</el-button>
        <el-button :icon="User" @click="router.push('/per/emp')">员工查询</el-button>
        <el-button :icon="Download" :loading="exporting" @click="handleExport">导出档案</el-button>
      </div>
    </section>

    <section class="kpi-grid">
      <div v-for="item in kpis" :key="item.label" class="kpi-card">
        <div class="kpi-icon" :style="{background: item.bg, color: item.color}">
          <el-icon :size="20"><component :is="item.icon"/></el-icon>
        </div>
        <div class="kpi-copy">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
          <small>{{ item.hint }}</small>
        </div>
      </div>
    </section>

    <section class="chart-grid">
      <div class="section-card chart-card span-7">
        <div class="card-head">
          <strong>部门人员分布</strong>
          <span class="card-sub">人数 Top 8</span>
        </div>
        <BaseChart :option="departmentOption" height="280px"/>
      </div>
      <div class="section-card chart-card span-5">
        <div class="card-head">
          <strong>性别结构</strong>
          <span class="card-sub">全员占比</span>
        </div>
        <BaseChart :option="genderOption" height="280px"/>
      </div>
      <div class="section-card chart-card span-5">
        <div class="card-head">
          <strong>学历分布</strong>
          <span class="card-sub">最高学历</span>
        </div>
        <BaseChart :option="degreeOption" height="250px"/>
      </div>
      <div class="section-card chart-card span-7">
        <div class="card-head">
          <strong>入职趋势</strong>
          <span class="card-sub">近 7 年</span>
        </div>
        <BaseChart :option="trendOption" height="250px"/>
      </div>
    </section>

    <section class="record-strip">
      <div v-for="item in recordStats" :key="item.label" class="record-chip">
        <span class="record-dot" :style="{background: item.color}"></span>
        <div class="record-copy">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
        </div>
      </div>
    </section>

    <section class="bottom-grid">
      <div class="section-card span-7">
        <div class="card-head">
          <strong>最近入职</strong>
          <span class="card-sub">按入职日期排序</span>
        </div>
        <el-table :data="recentHires" size="small" class="mini-table" empty-text="暂无入职记录">
          <el-table-column label="员工" min-width="160">
            <template #default="scope">
              <div class="mini-person">
                <EmployeeAvatar :name="scope.row.name" :seed="scope.row.id" :size="30"/>
                <div>
                  <b>{{ scope.row.name }}</b>
                  <span>工号 {{ scope.row.workId }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="departmentName" label="部门" min-width="98" show-overflow-tooltip/>
          <el-table-column prop="posName" label="职位" min-width="100" show-overflow-tooltip/>
          <el-table-column prop="beginDate" label="入职日期" width="105"/>
          <el-table-column label="状态" width="76" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.workState === '在职' ? 'success' : 'danger'" size="small" effect="light" round>
                {{ scope.row.workState }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="section-card span-5">
        <div class="card-head">
          <strong>人事动态</strong>
          <span class="card-sub">最新记录</span>
        </div>
        <div v-if="activities.length" class="activity-list">
          <div v-for="item in activities" :key="item.key" class="activity-item">
            <span class="activity-dot" :class="'tone-' + item.tone"></span>
            <div class="activity-copy">
              <div class="activity-line">
                <el-tag :type="item.tone" size="small" effect="light">{{ item.type }}</el-tag>
                <span class="activity-date">{{ item.date }}</span>
              </div>
              <strong>{{ item.title }}</strong>
              <span v-if="item.who" class="activity-who">{{ item.who }}</span>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无人事动态" :image-size="72"/>
      </div>
    </section>
  </div>
</template>

<script setup>
import {computed, onMounted, ref} from 'vue';
import {useRouter} from 'vue-router';
import {ElMessage} from 'element-plus';
import {CircleCheck, Download, OfficeBuilding, Plus, TrendCharts, User, UserFilled} from '@element-plus/icons-vue';
import BaseChart from '@/components/charts/BaseChart.vue';
import EmployeeAvatar from '@/components/employee/EmployeeAvatar.vue';
import {loadAllEmployees} from '@/api/employee/employee.js';
import {loadEcRecords, loadMoveRecords, loadSalaryRecords, loadTrainRecords} from '@/api/employee/record.js';
import {exportEmployeesToExcel} from '@/utils/exportExcel.js';

const router = useRouter();
const storedUser = window.sessionStorage.getItem('hr');
const user = storedUser ? JSON.parse(storedUser) : {};
const userName = computed(() => user.name || user.username || '管理员');

const loading = ref(true);
const exporting = ref(false);
const employees = ref([]);
const ecRecords = ref([]);
const trainRecords = ref([]);
const moveRecords = ref([]);
const salaryRecords = ref([]);

const now = new Date();
const monthKey = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
const todayText = `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日`;

const greeting = computed(() => {
  const hour = new Date().getHours();
  if (hour < 6) return '夜深了';
  if (hour < 12) return '早上好';
  if (hour < 14) return '中午好';
  if (hour < 18) return '下午好';
  return '晚上好';
});

const activeCount = computed(() => employees.value.filter(e => e.workState === '在职').length);
const departmentCount = computed(() => new Set(employees.value.map(e => e.departmentName).filter(Boolean)).size);
const newThisMonth = computed(() => employees.value.filter(e => (e.beginDate || '').startsWith(monthKey)).length);

const kpis = computed(() => [
  {
    label: '员工总数',
    value: employees.value.length,
    hint: '全量员工档案',
    icon: UserFilled,
    bg: '#eef2ff',
    color: '#4f6ef7'
  },
  {
    label: '在职员工',
    value: activeCount.value,
    hint: '当前在岗人数',
    icon: CircleCheck,
    bg: '#e8fbf3',
    color: '#10b981'
  },
  {
    label: '组织部门',
    value: departmentCount.value,
    hint: '已分配部门',
    icon: OfficeBuilding,
    bg: '#fff7e8',
    color: '#f59e0b'
  },
  {
    label: '本月新增',
    value: newThisMonth.value,
    hint: '本月入职员工',
    icon: TrendCharts,
    bg: '#eef7ff',
    color: '#0ea5e9'
  }
]);

const departmentRows = computed(() => {
  const counter = new Map();
  employees.value.forEach(employee => {
    const key = employee.departmentName || '未分配';
    counter.set(key, (counter.get(key) || 0) + 1);
  });
  return [...counter.entries()]
      .map(([name, value]) => ({name, value}))
      .sort((a, b) => b.value - a.value)
      .slice(0, 8);
});

const departmentOption = computed(() => ({
  grid: {left: 4, right: 28, top: 8, bottom: 4, containLabel: true},
  tooltip: {trigger: 'axis', axisPointer: {type: 'shadow'}},
  xAxis: {
    type: 'value',
    axisLine: {show: false},
    axisTick: {show: false},
    axisLabel: {color: '#9aa4b5', fontSize: 10},
    splitLine: {lineStyle: {color: '#eef1f8'}}
  },
  yAxis: {
    type: 'category',
    inverse: true,
    data: departmentRows.value.map(row => row.name),
    axisLine: {show: false},
    axisTick: {show: false},
    axisLabel: {color: '#5f6b81', fontSize: 11}
  },
  series: [{
    type: 'bar',
    data: departmentRows.value.map(row => row.value),
    barWidth: 11,
    itemStyle: {color: '#4f6ef7', borderRadius: [0, 6, 6, 0]},
    label: {show: true, position: 'right', color: '#8c97a9', fontSize: 10}
  }]
}));

const genderRows = computed(() => {
  const male = employees.value.filter(e => e.gender === '男').length;
  const female = employees.value.filter(e => e.gender === '女').length;
  return [
    {name: '男', value: male, itemStyle: {color: '#4f6ef7'}},
    {name: '女', value: female, itemStyle: {color: '#f472b6'}}
  ];
});

const genderOption = computed(() => ({
  tooltip: {trigger: 'item'},
  legend: {bottom: 2, icon: 'circle', itemWidth: 8, itemHeight: 8, textStyle: {color: '#7d889b', fontSize: 11}},
  title: {
    text: String(employees.value.length),
    subtext: '总人数',
    left: 'center',
    top: '32%',
    textAlign: 'center',
    textStyle: {color: '#172033', fontSize: 24, fontWeight: 700},
    subtextStyle: {color: '#9aa4b5', fontSize: 10}
  },
  series: [{
    type: 'pie',
    radius: ['54%', '74%'],
    center: ['50%', '48%'],
    avoidLabelOverlap: true,
    itemStyle: {borderColor: '#fff', borderWidth: 3},
    label: {show: false},
    data: genderRows.value
  }]
}));

const degreeRows = computed(() => {
  const counter = new Map();
  employees.value.forEach(employee => {
    const key = employee.tipTopDegree || '未填写';
    counter.set(key, (counter.get(key) || 0) + 1);
  });
  return [...counter.entries()].map(([name, value]) => ({name, value})).sort((a, b) => b.value - a.value);
});

const degreeOption = computed(() => ({
  color: ['#4f6ef7', '#7c93ff', '#0ea5e9', '#38bdf8', '#10b981', '#f59e0b', '#f472b6', '#94a3b8'],
  tooltip: {trigger: 'item'},
  legend: {
    bottom: 0,
    icon: 'circle',
    itemWidth: 7,
    itemHeight: 7,
    textStyle: {color: '#7d889b', fontSize: 10},
    itemGap: 10
  },
  series: [{
    type: 'pie',
    radius: ['42%', '68%'],
    center: ['50%', '43%'],
    itemStyle: {borderColor: '#fff', borderWidth: 2},
    label: {show: false},
    data: degreeRows.value
  }]
}));

const trendOption = computed(() => {
  const currentYear = new Date().getFullYear();
  const years = Array.from({length: 7}, (_, index) => String(currentYear - 6 + index));
  const counts = years.map(year => employees.value.filter(e => (e.beginDate || '').startsWith(year)).length);
  return {
    grid: {left: 6, right: 16, top: 18, bottom: 6, containLabel: true},
    tooltip: {trigger: 'axis'},
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: years,
      axisLine: {lineStyle: {color: '#e8ecf5'}},
      axisTick: {show: false},
      axisLabel: {color: '#9aa4b5', fontSize: 10}
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLine: {show: false},
      axisTick: {show: false},
      axisLabel: {color: '#9aa4b5', fontSize: 10},
      splitLine: {lineStyle: {color: '#eef1f8'}}
    },
    series: [{
      type: 'line',
      data: counts,
      smooth: true,
      symbol: 'circle',
      symbolSize: 7,
      lineStyle: {width: 3, color: '#4f6ef7'},
      itemStyle: {color: '#4f6ef7', borderColor: '#fff', borderWidth: 2},
      areaStyle: {color: 'rgba(79, 110, 247, 0.12)'}
    }]
  };
});

const recentHires = computed(() => [...employees.value]
    .filter(employee => employee.beginDate)
    .sort((a, b) => String(b.beginDate).localeCompare(String(a.beginDate)))
    .slice(0, 8));

const activities = computed(() => {
  const list = [
    ...ecRecords.value.map(record => ({
      type: record.ecType === 0 ? '奖励' : '处罚',
      tone: record.ecType === 0 ? 'success' : 'danger',
      title: record.ecReason || '奖惩记录',
      who: record.empName || '',
      date: record.ecDate || ''
    })),
    ...trainRecords.value.map(record => ({
      type: '培训',
      tone: 'primary',
      title: record.trainContent || '培训记录',
      who: record.empName || '',
      date: record.trainDate || ''
    })),
    ...moveRecords.value.map(record => ({
      type: '调动',
      tone: 'warning',
      title: record.reason || '调动记录',
      who: record.empName || '',
      date: record.removeDate || ''
    })),
    ...salaryRecords.value.map(record => ({
      type: '调薪',
      tone: 'success',
      title: record.reason || '调薪记录',
      who: record.empName || '',
      date: record.asDate || ''
    }))
  ].filter(item => item.date);

  return list
      .map((item, index) => ({...item, key: item.type + '-' + item.date + '-' + index}))
      .sort((a, b) => b.date.localeCompare(a.date))
      .slice(0, 5);
});

const recordStats = computed(() => [
  {label: '奖惩记录', value: ecRecords.value.length, color: '#4f6ef7'},
  {label: '培训记录', value: trainRecords.value.length, color: '#0ea5e9'},
  {label: '调动记录', value: moveRecords.value.length, color: '#f59e0b'},
  {label: '调薪记录', value: salaryRecords.value.length, color: '#10b981'}
]);

async function loadDashboard() {
  loading.value = true;
  try {
    const [employeeData, ecData, trainData, moveData, salaryData] = await Promise.all([
      loadAllEmployees().catch(() => []),
      loadEcRecords({}).catch(() => ({data: []})),
      loadTrainRecords({}).catch(() => ({data: []})),
      loadMoveRecords({}).catch(() => ({data: []})),
      loadSalaryRecords({}).catch(() => ({data: []}))
    ]);
    employees.value = employeeData || [];
    ecRecords.value = ecData.data || [];
    trainRecords.value = trainData.data || [];
    moveRecords.value = moveData.data || [];
    salaryRecords.value = salaryData.data || [];
  } finally {
    loading.value = false;
  }
}

function goAddEmployee() {
  router.push({path: '/emp/basic', query: {action: 'add'}});
}

async function handleExport() {
  exporting.value = true;
  try {
    await exportEmployeesToExcel(employees.value, '员工档案');
  } catch {
    ElMessage.error('导出失败，请稍后重试');
  } finally {
    exporting.value = false;
  }
}

onMounted(loadDashboard);
</script>

<style scoped>
.dashboard {
  gap: 18px;
}

.dashboard-hero {
  position: relative;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  padding: 28px 30px;
  overflow: hidden;
  color: #fff;
  background:
      radial-gradient(circle at 84% 12%, rgba(122, 145, 255, 0.34), transparent 28%),
      linear-gradient(120deg, #111c33 0%, #172747 58%, #1b2b52 100%);
  border-radius: 18px;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.14);
}

.dashboard-hero::after {
  position: absolute;
  top: -90px;
  right: 90px;
  width: 240px;
  height: 240px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 50%;
  box-shadow: 0 0 0 45px rgba(255, 255, 255, 0.018);
  content: "";
}

.hero-copy {
  position: relative;
  z-index: 1;
}

.hero-eyebrow {
  color: #91a8ff;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.16em;
}

.hero-copy h1 {
  margin: 10px 0 8px;
  color: #f8faff;
  font-size: 27px;
  font-weight: 700;
  letter-spacing: -0.035em;
}

.hero-copy p {
  margin: 0;
  color: #9eabc3;
  font-size: 12px;
}

.hero-actions {
  position: relative;
  z-index: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 9px;
}

.hero-actions :deep(.el-button) {
  color: #dfe6ff;
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.12);
}

.hero-actions :deep(.el-button:hover) {
  color: #fff;
  background: rgba(255, 255, 255, 0.14);
  border-color: rgba(255, 255, 255, 0.2);
}

.hero-actions :deep(.el-button--primary) {
  color: #fff;
  background: #4f6ef7;
  border-color: #4f6ef7;
  box-shadow: 0 6px 16px rgba(79, 110, 247, 0.3);
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.kpi-card {
  display: flex;
  align-items: center;
  gap: 14px;
  min-height: 104px;
  padding: 18px 20px;
  background: #fff;
  border: 1px solid #e8ecf5;
  border-radius: 14px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.035);
}

.kpi-icon {
  display: flex;
  flex: 0 0 auto;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 12px;
}

.kpi-copy {
  display: flex;
  min-width: 0;
  flex-direction: column;
}

.kpi-copy span {
  color: #8d98aa;
  font-size: 11px;
}

.kpi-copy strong {
  margin: 3px 0 2px;
  color: #172033;
  font-size: 25px;
  font-weight: 700;
  letter-spacing: -0.04em;
}

.kpi-copy small {
  color: #a4adbb;
  font-size: 10px;
}

.chart-grid,
.bottom-grid {
  display: grid;
  grid-template-columns: repeat(12, minmax(0, 1fr));
  gap: 16px;
}

.span-7 {
  grid-column: span 7;
}

.span-5 {
  grid-column: span 5;
}

.chart-card {
  min-width: 0;
  padding-bottom: 10px;
}

.card-head {
  padding-bottom: 6px;
}

.card-head strong {
  color: #263248;
}

.card-sub {
  color: #a4adbb;
  font-size: 10px;
}

.record-strip {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.record-chip {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background: #fff;
  border: 1px solid #e8ecf5;
  border-radius: 12px;
}

.record-dot {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  box-shadow: 0 0 0 4px rgba(79, 110, 247, 0.08);
}

.record-copy {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.record-copy span {
  color: #8d98aa;
  font-size: 10px;
}

.record-copy strong {
  color: #263248;
  font-size: 17px;
  font-weight: 700;
}

.mini-table {
  --el-table-header-bg-color: #fbfcff;
}

.mini-person {
  display: flex;
  align-items: center;
  gap: 9px;
}

.mini-person div {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 1px;
}

.mini-person b {
  color: #33405a;
  font-size: 12px;
  font-weight: 600;
}

.mini-person span {
  color: #a0a9b8;
  font-size: 9px;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding-top: 4px;
}

.activity-item {
  display: flex;
  gap: 12px;
  padding: 11px 4px;
  border-bottom: 1px solid #f1f3f9;
}

.activity-item:last-child {
  border-bottom: 0;
}

.activity-dot {
  flex: 0 0 auto;
  width: 8px;
  height: 8px;
  margin-top: 6px;
  border-radius: 50%;
  background: #4f6ef7;
  box-shadow: 0 0 0 4px rgba(79, 110, 247, 0.09);
}

.activity-dot.tone-success {
  background: #10b981;
  box-shadow: 0 0 0 4px rgba(16, 185, 129, 0.09);
}

.activity-dot.tone-danger {
  background: #ef4444;
  box-shadow: 0 0 0 4px rgba(239, 68, 68, 0.09);
}

.activity-dot.tone-warning {
  background: #f59e0b;
  box-shadow: 0 0 0 4px rgba(245, 158, 11, 0.09);
}

.activity-copy {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
  gap: 4px;
}

.activity-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.activity-date {
  color: #a5aebc;
  font-size: 9px;
}

.activity-copy strong {
  overflow: hidden;
  color: #3d4a60;
  font-size: 12px;
  font-weight: 550;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.activity-who {
  color: #a0a9b8;
  font-size: 9px;
}

@media (max-width: 1200px) {
  .kpi-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .span-7,
  .span-5 {
    grid-column: span 12;
  }
}

@media (max-width: 900px) {
  .dashboard-hero {
    align-items: flex-start;
    flex-direction: column;
  }

  .kpi-grid,
  .record-strip {
    grid-template-columns: 1fr;
  }
}
</style>
