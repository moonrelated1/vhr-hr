<template>
  <div class="records-page" :class="{'is-embedded': !!eid}">
    <PageHeader v-if="!eid" eyebrow="人事管理" title="调薪记录"
                subtitle="跟踪员工薪酬调整前后变化与调薪原因">
      <template #actions>
        <el-button :icon="Refresh" :loading="loading" @click="loadData">刷新</el-button>
        <el-button type="primary" :icon="Plus" @click="openAdd">新增调薪记录</el-button>
      </template>
    </PageHeader>

    <section v-if="!eid" class="section-card filter-card">
      <div class="filter-head">
        <div><span class="filter-title">筛选条件</span></div>
        <el-button link @click="resetSearch">清空筛选</el-button>
      </div>
      <el-form :inline="true" :model="query" class="filter-form">
        <el-form-item label="员工姓名">
          <el-input v-model="query.name" placeholder="员工姓名" clearable style="width: 170px"
                    @keyup.enter="search"/>
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
          <strong>{{ eid ? '调薪记录' : '记录列表' }}</strong>
          <span class="table-count">共 {{ records.length }} 条</span>
        </div>
        <el-button v-if="eid" type="primary" size="small" :icon="Plus" @click="openAdd">新增调薪记录</el-button>
      </div>
      <el-table :data="records" v-loading="loading" empty-text="暂无调薪记录">
        <el-table-column v-if="!eid" label="员工姓名" width="150">
          <template #default="{row}">
            <div class="emp-cell">
              <EmployeeAvatar :name="row.empName" :seed="row.eid" :size="26"/>
              <span>{{ row.empName || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="asDate" label="调薪日期" width="120"/>
        <el-table-column label="调薪前" width="110" align="right">
          <template #default="{row}">
            <span class="money-before">{{ money(row.beforeSalary) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="调薪后" width="120" align="right">
          <template #default="{row}">
            <span class="money-after">{{ money(row.afterSalary) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="变化" width="90" align="center">
          <template #default="{row}">
            <el-tag v-if="growth(row)" :type="growth(row).startsWith('+') ? 'success' : 'danger'"
                    effect="light" round size="small">
              {{ growth(row) }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="调薪原因" min-width="170" show-overflow-tooltip/>
        <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip>
          <template #default="{row}">{{ row.remark || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="right">
          <template #default="scope">
            <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog v-model="dialogVisible" title="新增调薪记录" width="540px" :close-on-click-modal="false">
      <div class="dialog-tip">薪资单位为元/月；调薪后薪资需大于 0。</div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item v-if="!eid" label="员工" prop="eid">
          <el-select v-model="form.eid" filterable remote :remote-method="searchEmployees"
                     :loading="empLoading" placeholder="输入姓名搜索员工" style="width: 100%">
            <el-option v-for="emp in employeeOptions" :key="emp.id"
                       :label="emp.name + '（' + emp.workId + '）'" :value="emp.id"/>
          </el-select>
        </el-form-item>
        <el-form-item v-else label="员工">
          <span class="form-static">{{ empName || ('员工编号 ' + eid) }}</span>
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
          <el-input v-model="form.reason" maxlength="100" placeholder="如：年度绩效调薪"/>
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
import {nextTick, onMounted, reactive, ref, toRefs, watch} from 'vue';
import {ElMessageBox} from 'element-plus';
import {Plus, Refresh, Search} from '@element-plus/icons-vue';
import PageHeader from '@/components/common/PageHeader.vue';
import EmployeeAvatar from '@/components/employee/EmployeeAvatar.vue';
import {loadEmployees} from '@/api/employee/employee.js';
import {addSalary, deleteSalary, loadSalaryRecords} from '@/api/employee/record.js';

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
  return value === null || value === undefined ? '-' : '¥' + Number(value).toLocaleString();
}

function growth(row) {
  const before = Number(row.beforeSalary);
  const after = Number(row.afterSalary);
  if (!before || !after) return '';
  const percent = ((after - before) / before) * 100;
  return (percent >= 0 ? '+' : '') + percent.toFixed(1) + '%';
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
.emp-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #36435a;
}

.money-before {
  color: #98a2b4;
  font-variant-numeric: tabular-nums;
}

.money-after {
  color: #0f172a;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}

.is-embedded {
  padding: 0;
}

.is-embedded .table-card {
  padding: 0;
  background: transparent;
  border: 0;
  box-shadow: none;
}

.form-static {
  color: #36435a;
  font-weight: 600;
}
</style>
