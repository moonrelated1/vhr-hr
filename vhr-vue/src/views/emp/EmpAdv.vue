<template>
  <div class="page">
    <PageHeader eyebrow="员工资料" title="高级资料"
                subtitle="按员工查看奖惩、培训、调动与调薪四类人事记录">
      <template #actions>
        <el-button :icon="Refresh" :loading="loading" @click="loadData">刷新列表</el-button>
      </template>
    </PageHeader>

    <section v-if="selectedEmployee" class="section-card profile-card">
      <EmployeeAvatar :name="selectedEmployee.name" :seed="selectedEmployee.id" :size="58"/>
      <div class="profile-main">
        <div class="profile-title">
          <strong>{{ selectedEmployee.name }}</strong>
          <el-tag :type="selectedEmployee.workState === '在职' ? 'success' : 'danger'"
                  effect="light" round size="small">
            {{ selectedEmployee.workState || '未知状态' }}
          </el-tag>
        </div>
        <p class="profile-sub">
          {{ selectedEmployee.departmentName || '未分配部门' }}
          · {{ selectedEmployee.posName || '未分配职位' }}
          · 工号 {{ selectedEmployee.workId || '-' }}
        </p>
        <p class="profile-meta">
          职称 {{ selectedEmployee.jobLevelName || '-' }}
          <span>入职 {{ selectedEmployee.beginDate || '-' }}</span>
          <span>{{ selectedEmployee.gender || '-' }}</span>
        </p>
      </div>
      <div class="profile-tip">
        <el-icon><User/></el-icon>
        已选中员工
      </div>
    </section>
    <section v-else class="section-card profile-empty">
      <div class="empty-icon"><el-icon><User/></el-icon></div>
      <div>
        <strong>尚未选择员工</strong>
        <p>在下方列表中点击任意一行，即可查看该员工的四类人事记录。</p>
      </div>
    </section>

    <section class="section-card table-card">
      <div class="filter-head">
        <div>
          <span class="filter-title">选择员工</span>
          <span class="filter-hint">共 {{ total }} 人</span>
        </div>
        <el-form :inline="true" :model="query" class="inline-search">
          <el-form-item>
            <el-input v-model="query.name" placeholder="输入员工姓名" clearable :prefix-icon="Search"
                      style="width: 190px" @keyup.enter="search"/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="search">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <EmployeeTable :data="employees" :loading="loading" highlight-current-row
                     @row-click="selectEmployee"/>
      <div class="pager">
        <el-pagination background v-model:current-page="page" v-model:page-size="size"
                       :page-sizes="[5, 10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
                       :total="total" @change="paginationChange"/>
      </div>
    </section>

    <section v-if="selectedEmployee" class="section-card records-card">
      <div class="table-head">
        <div>
          <strong>人事记录</strong>
          <span class="table-count">{{ selectedEmployee.name }} 的奖惩、培训、调动与调薪明细</span>
        </div>
      </div>
      <el-tabs v-model="activeTab" class="records-tabs">
        <el-tab-pane label="奖惩记录" name="ec" lazy>
          <PerEc :eid="selectedEmployee.id" :emp-name="selectedEmployee.name"/>
        </el-tab-pane>
        <el-tab-pane label="培训记录" name="train" lazy>
          <PerTrain :eid="selectedEmployee.id" :emp-name="selectedEmployee.name"/>
        </el-tab-pane>
        <el-tab-pane label="调动记录" name="mv" lazy>
          <PerMv :eid="selectedEmployee.id" :emp-name="selectedEmployee.name"/>
        </el-tab-pane>
        <el-tab-pane label="调薪记录" name="salary" lazy>
          <PerSalary :eid="selectedEmployee.id" :emp-name="selectedEmployee.name"/>
        </el-tab-pane>
      </el-tabs>
    </section>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref, toRefs} from 'vue';
import {useRoute} from 'vue-router';
import {Refresh, Search, User} from '@element-plus/icons-vue';
import PageHeader from '@/components/common/PageHeader.vue';
import EmployeeAvatar from '@/components/employee/EmployeeAvatar.vue';
import EmployeeTable from '@/components/employee/EmployeeTable.vue';
import PerEc from '@/views/per/PerEc.vue';
import PerTrain from '@/views/per/PerTrain.vue';
import PerMv from '@/views/per/PerMv.vue';
import PerSalary from '@/views/per/PerSalary.vue';
import {getEmployeeById, loadEmployees} from '@/api/employee/employee.js';

const route = useRoute();

const data = reactive({
  employees: [],
  total: 0,
  page: 1,
  size: 10,
  loading: false,
  query: {name: ''},
  selectedEmployee: null,
  activeTab: 'ec'
});
const {employees, total, page, size, loading, query, selectedEmployee, activeTab} = toRefs(data);

function loadData() {
  loading.value = true;
  const params = {page: page.value, size: size.value};
  if (query.value.name) params.name = query.value.name;
  loadEmployees(params).then(res => {
    employees.value = res.data || [];
    total.value = res.total || 0;
  }).finally(() => {
    loading.value = false;
  });
}

function search() {
  page.value = 1;
  loadData();
}

function resetSearch() {
  query.value.name = '';
  search();
}

function paginationChange(newPage, newSize) {
  page.value = newPage;
  size.value = newSize;
  loadData();
}

function selectEmployee(row) {
  selectedEmployee.value = row;
}

function loadSelectedFromQuery() {
  const empId = Number(route.query.empId);
  if (!empId) return;
  getEmployeeById(empId).then(res => {
    selectedEmployee.value = res.data;
  }).catch(() => {
  });
}

onMounted(() => {
  loadData();
  loadSelectedFromQuery();
});
</script>

<style scoped>
.profile-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 22px;
  background:
      radial-gradient(600px 140px at 0% 0%, rgba(79, 110, 247, 0.1), transparent 70%),
      #fff;
}

.profile-main {
  flex: 1;
  min-width: 0;
}

.profile-title {
  display: flex;
  align-items: center;
  gap: 9px;
}

.profile-title strong {
  color: #172033;
  font-size: 19px;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.profile-sub {
  margin: 5px 0 4px;
  color: #5d6b82;
  font-size: 12px;
}

.profile-meta {
  display: flex;
  gap: 18px;
  color: #98a2b4;
  font-size: 11px;
}

.profile-tip {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #4f6ef7;
  font-size: 11px;
  font-weight: 600;
}

.profile-empty {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px 22px;
  border-style: dashed;
}

.empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  color: #4f6ef7;
  background: #eef2ff;
  border-radius: 14px;
  font-size: 20px;
}

.profile-empty strong {
  color: #36435a;
  font-size: 13px;
}

.profile-empty p {
  margin: 4px 0 0;
  color: #98a2b4;
  font-size: 11px;
}

.inline-search {
  display: flex;
  align-items: center;
}

.inline-search :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 10px;
}

.records-card {
  padding-bottom: 22px;
}

.records-tabs :deep(.el-tabs__header) {
  margin-bottom: 16px;
}
</style>
