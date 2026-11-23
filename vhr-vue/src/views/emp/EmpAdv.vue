<template>
  <div>
    <el-form :inline="true" :model="query">
      <el-form-item label="姓名">
        <el-input v-model="query.name" placeholder="员工姓名" clearable style="width: 160px" @keyup.enter="search"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <EmployeeTable :data="employees" :loading="loading" highlight-current-row @row-click="selectEmployee"/>

    <div class="pager">
      <el-pagination background v-model:current-page="page" v-model:page-size="size"
                     :page-sizes="[5, 10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
                     :total="total" @change="paginationChange"/>
    </div>

    <el-divider/>

    <template v-if="selectedEmployee">
      <div class="current">
        当前员工：<b>{{ selectedEmployee.name }}</b>（工号 {{ selectedEmployee.workId }}，{{ selectedEmployee.departmentName }}）
      </div>
      <el-tabs v-model="activeTab" type="border-card">
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
    </template>
    <el-empty v-else description="请在上方员工列表中点击选择一名员工，查看其高级资料"/>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref, toRefs} from "vue";
import EmployeeTable from "@/components/employee/EmployeeTable.vue";
import PerEc from "@/views/per/PerEc.vue";
import PerTrain from "@/views/per/PerTrain.vue";
import PerMv from "@/views/per/PerMv.vue";
import PerSalary from "@/views/per/PerSalary.vue";
import {loadEmployees} from "@/api/employee/employee.js";

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

onMounted(loadData);
</script>

<style scoped>
.pager {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.current {
  margin-bottom: 12px;
  font-size: 14px;
  color: #303133;
}
</style>
