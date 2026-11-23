<template>
  <div class="page">
    <PageHeader eyebrow="人事管理" title="员工资料"
                subtitle="只读查看员工档案信息，便于人事日常查询">
      <template #actions>
        <el-button :icon="Refresh" :loading="loading" @click="loadData">刷新</el-button>
      </template>
    </PageHeader>

    <section class="section-card filter-card">
      <div class="filter-head">
        <div>
          <span class="filter-title">筛选条件</span>
          <span v-if="activeFilterCount" class="filter-hint">已启用 {{ activeFilterCount }} 项</span>
        </div>
        <el-button link @click="resetSearch">清空筛选</el-button>
      </div>
      <el-form :inline="true" :model="query" class="filter-form">
        <el-form-item label="姓名">
          <el-input v-model="query.name" placeholder="员工姓名" clearable style="width: 142px"
                    @keyup.enter="search"/>
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="query.departmentId" placeholder="全部部门" clearable style="width: 150px">
            <el-option v-for="d in dicts.departments" :key="d.id" :label="d.name" :value="d.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="职位">
          <el-select v-model="query.posId" placeholder="全部职位" clearable style="width: 140px">
            <el-option v-for="p in dicts.positions" :key="p.id" :label="p.name" :value="p.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="职称">
          <el-select v-model="query.jobLevelId" placeholder="全部职称" clearable style="width: 140px">
            <el-option v-for="j in dicts.joblevels" :key="j.id" :label="j.name" :value="j.id"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="search">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="section-card table-card">
      <div class="table-head">
        <div>
          <strong>员工列表</strong>
          <span class="table-count">共 {{ total }} 人</span>
        </div>
        <span class="readonly-hint">只读视图</span>
      </div>
      <EmployeeTable :data="employees" :loading="loading" show-detail
                     @detail="openDetail"/>
      <div class="pager">
        <el-pagination background v-model:current-page="page" v-model:page-size="size"
                       :page-sizes="[5, 10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
                       :total="total" @change="paginationChange"/>
      </div>
    </section>

    <EmployeeDetailDrawer v-model="detailVisible" :employee="detailEmployee" :loading="detailLoading"
                          :editable="false" :show-advanced="false"/>
  </div>
</template>

<script setup>
import {computed, onMounted, reactive, ref, toRefs} from 'vue';
import {Refresh, Search} from '@element-plus/icons-vue';
import PageHeader from '@/components/common/PageHeader.vue';
import EmployeeTable from '@/components/employee/EmployeeTable.vue';
import EmployeeDetailDrawer from '@/components/employee/EmployeeDetailDrawer.vue';
import {getEmployeeById, loadDicts, loadEmployees} from '@/api/employee/employee.js';

const data = reactive({
  employees: [],
  total: 0,
  page: 1,
  size: 10,
  loading: false,
  query: {name: '', departmentId: '', posId: '', jobLevelId: ''},
  dicts: {departments: [], positions: [], joblevels: [], nations: [], politics: []},
  detailVisible: false,
  detailLoading: false,
  detailEmployee: null
});
const {
  employees, total, page, size, loading, query, dicts,
  detailVisible, detailLoading, detailEmployee
} = toRefs(data);

const activeFilterCount = computed(() =>
    Object.values(query.value).filter(value => value !== '' && value !== null && value !== undefined).length
);

function buildQuery() {
  const params = {page: page.value, size: size.value};
  if (query.value.name) params.name = query.value.name;
  if (query.value.departmentId) params.departmentId = query.value.departmentId;
  if (query.value.posId) params.posId = query.value.posId;
  if (query.value.jobLevelId) params.jobLevelId = query.value.jobLevelId;
  return params;
}

function loadData() {
  loading.value = true;
  loadEmployees(buildQuery()).then(res => {
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
  Object.assign(query.value, {name: '', departmentId: '', posId: '', jobLevelId: ''});
  search();
}

function paginationChange(newPage, newSize) {
  page.value = newPage;
  size.value = newSize;
  loadData();
}

function openDetail(row) {
  detailVisible.value = true;
  detailLoading.value = true;
  detailEmployee.value = null;
  getEmployeeById(row.id).then(res => {
    detailEmployee.value = res.data;
  }).finally(() => {
    detailLoading.value = false;
  });
}

onMounted(() => {
  loadDicts().then(d => Object.assign(dicts.value, d));
  loadData();
});
</script>

<style scoped>
.readonly-hint {
  padding: 3px 10px;
  color: #64748b;
  background: #f1f5f9;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
}
</style>
