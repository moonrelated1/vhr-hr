<template>
  <div>
    <el-form v-if="!eid" :inline="true" :model="query">
      <el-form-item label="员工姓名">
        <el-input v-model="query.name" placeholder="员工姓名" clearable style="width: 140px" @keyup.enter="search"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="toolbar">
      <el-button type="primary" @click="openAdd">新增调薪记录</el-button>
    </div>

    <el-table :data="records" v-loading="loading" border stripe>
      <el-table-column v-if="!eid" prop="empName" label="员工姓名" width="110"/>
      <el-table-column prop="asDate" label="调薪日期" width="120"/>
      <el-table-column label="调薪前薪资" width="120">
        <template #default="scope">{{ money(scope.row.beforeSalary) }}</template>
      </el-table-column>
      <el-table-column label="调薪后薪资" width="120">
        <template #default="scope">{{ money(scope.row.afterSalary) }}</template>
      </el-table-column>
      <el-table-column prop="reason" label="调薪原因" min-width="180"/>
      <el-table-column prop="remark" label="备注" min-width="140"/>
      <el-table-column label="操作" width="90">
        <template #default="scope">
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="新增调薪记录" width="520px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item v-if="!eid" label="员工" prop="eid">
          <el-select v-model="form.eid" filterable remote :remote-method="searchEmployees"
                     :loading="empLoading" placeholder="输入姓名搜索员工" style="width: 100%">
            <el-option v-for="emp in employeeOptions" :key="emp.id"
                       :label="emp.name + '（' + emp.workId + '）'" :value="emp.id"/>
          </el-select>
        </el-form-item>
        <el-form-item v-else label="员工">
          <span>{{ empName || ('员工编号 ' + eid) }}</span>
        </el-form-item>
        <el-form-item label="调薪日期" prop="asDate">
          <el-date-picker v-model="form.asDate" type="date" value-format="YYYY-MM-DD"
                          placeholder="选择日期" style="width: 100%"/>
        </el-form-item>
        <el-form-item label="调薪前薪资" prop="beforeSalary">
          <el-input-number v-model="form.beforeSalary" :min="0" :precision="0" style="width: 100%"/>
        </el-form-item>
        <el-form-item label="调薪后薪资" prop="afterSalary">
          <el-input-number v-model="form.afterSalary" :min="1" :precision="0" style="width: 100%"/>
        </el-form-item>
        <el-form-item label="调薪原因" prop="reason">
          <el-input v-model="form.reason" maxlength="100"/>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" maxlength="200"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {nextTick, onMounted, reactive, ref, toRefs, watch} from "vue";
import {ElMessageBox} from "element-plus";
import {loadEmployees} from "@/api/employee/employee.js";
import {addSalary, deleteSalary, loadSalaryRecords} from "@/api/employee/record.js";

const props = defineProps({
  eid: {type: Number, default: null},
  empName: {type: String, default: ''}
});

const data = reactive({
  records: [],
  loading: false,
  query: {name: ''},
  dialogVisible: false,
  saving: false,
  employeeOptions: [],
  empLoading: false
});
const {records, loading, query, dialogVisible, saving, employeeOptions, empLoading} = toRefs(data);

const formRef = ref();
const form = reactive(emptyForm());

function emptyForm() {
  return {id: null, eid: null, asDate: '', beforeSalary: null, afterSalary: null, reason: '', remark: ''};
}

const rules = {
  eid: [{required: true, message: '请选择员工', trigger: 'change'}],
  asDate: [{required: true, message: '请选择调薪日期', trigger: 'change'}],
  afterSalary: [{required: true, message: '请输入调薪后薪资', trigger: 'change'}],
  reason: [{required: true, message: '请输入调薪原因', trigger: 'blur'}]
};

function money(value) {
  return value === null || value === undefined ? '-' : '¥' + value;
}

function loadData() {
  loading.value = true;
  const params = {};
  if (props.eid) {
    params.eid = props.eid;
  } else if (query.value.name) {
    params.name = query.value.name;
  }
  loadSalaryRecords(params).then(res => {
    records.value = res.data || [];
  }).finally(() => {
    loading.value = false;
  });
}

function search() {
  loadData();
}

function resetSearch() {
  query.value.name = '';
  loadData();
}

function searchEmployees(keyword) {
  empLoading.value = true;
  loadEmployees({page: 1, size: 20, name: keyword || ''}).then(res => {
    employeeOptions.value = res.data || [];
  }).finally(() => {
    empLoading.value = false;
  });
}

function openAdd() {
  Object.assign(form, emptyForm());
  form.eid = props.eid || null;
  dialogVisible.value = true;
  if (!props.eid) {
    searchEmployees('');
  }
  nextTick(() => formRef.value && formRef.value.clearValidate());
}

function submit() {
  formRef.value.validate(valid => {
    if (!valid) return;
    saving.value = true;
    addSalary({...form}).then(() => {
      dialogVisible.value = false;
      loadData();
    }).finally(() => {
      saving.value = false;
    });
  });
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除【${row.empName || '该员工'}】的这条调薪记录吗？`, '提示', {type: 'warning'})
      .then(() => deleteSalary(row.id))
      .then(() => loadData())
      .catch(() => {
      });
}

watch(() => props.eid, () => {
  dialogVisible.value = false;
  loadData();
});

onMounted(loadData);
</script>

<style scoped>
.toolbar {
  margin-bottom: 12px;
}
</style>
