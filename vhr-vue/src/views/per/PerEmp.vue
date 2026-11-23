<template>
  <div>
    <el-form :inline="true" :model="query">
      <el-form-item label="姓名">
        <el-input v-model="query.name" placeholder="员工姓名" clearable style="width: 140px" @keyup.enter="search"/>
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
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <EmployeeTable :data="employees" :loading="loading"/>

    <div class="pager">
      <el-pagination background v-model:current-page="page" v-model:page-size="size"
                     :page-sizes="[5, 10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
                     :total="total" @change="paginationChange"/>
    </div>
  </div>
</template>

<script setup>
import {onMounted, reactive, toRefs} from "vue";
import EmployeeTable from "@/components/employee/EmployeeTable.vue";
import {loadDicts, loadEmployees} from "@/api/employee/employee.js";

const data = reactive({
  employees: [],
  total: 0,
  page: 1,
  size: 10,
  loading: false,
  query: {name: '', departmentId: '', posId: '', jobLevelId: ''},
  dicts: {departments: [], positions: [], joblevels: []}
});
const {employees, total, page, size, loading, query, dicts} = toRefs(data);

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

onMounted(() => {
  loadDicts().then(d => Object.assign(dicts.value, d));
  loadData();
});
</script>

<style scoped>
.pager {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>
