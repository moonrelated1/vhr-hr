<template>
  <div class="records-page" :class="{'is-embedded': !!eid}">
    <PageHeader v-if="!eid" eyebrow="人事管理" title="调动记录"
                subtitle="记录部门与职位调动，保存后同步更新员工档案">
      <template #actions>
        <el-button :icon="Refresh" :loading="loading" @click="loadData">刷新</el-button>
        <el-button type="primary" :icon="Plus" @click="openAdd">新增调动记录</el-button>
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
          <strong>{{ eid ? '调动记录' : '记录列表' }}</strong>
          <span class="table-count">共 {{ records.length }} 条</span>
        </div>
        <el-button v-if="eid" type="primary" size="small" :icon="Plus" @click="openAdd">新增调动记录</el-button>
      </div>
      <el-table :data="records" v-loading="loading" empty-text="暂无调动记录">
        <el-table-column v-if="!eid" label="员工姓名" width="150">
          <template #default="{row}">
            <div class="emp-cell">
              <EmployeeAvatar :name="row.empName" :seed="row.eid" :size="26"/>
              <span>{{ row.empName || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="removeDate" label="调动日期" width="120"/>
        <el-table-column label="调动后部门" min-width="140">
          <template #default="{row}">
            <el-tag type="info" effect="plain" round>{{ row.afterDepName || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="afterJobName" label="调动后职位" min-width="140">
          <template #default="{row}">{{ row.afterJobName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="reason" label="调动原因" min-width="180" show-overflow-tooltip/>
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

    <el-dialog v-model="dialogVisible" title="新增调动记录" width="540px" :close-on-click-modal="false">
      <el-alert title="保存后将同步更新员工档案中的部门与职位" type="warning"
                :closable="false" show-icon style="margin-bottom: 16px"/>
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
        <el-form-item label="调动后部门" prop="afterDepId">
          <el-select v-model="form.afterDepId" style="width: 100%" placeholder="请选择部门">
            <el-option v-for="d in dicts.departments" :key="d.id" :label="d.name" :value="d.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="调动后职位" prop="afterJobId">
          <el-select v-model="form.afterJobId" style="width: 100%" placeholder="请选择职位">
            <el-option v-for="p in dicts.positions" :key="p.id" :label="p.name" :value="p.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="调动日期" prop="removeDate">
          <el-date-picker v-model="form.removeDate" type="date" value-format="YYYY-MM-DD"
                          placeholder="选择日期" style="width: 100%"/>
        </el-form-item>
        <el-form-item label="调动原因" prop="reason">
          <el-input v-model="form.reason" maxlength="100" placeholder="如：业务线调整"/>
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
import {loadDicts, loadEmployees} from '@/api/employee/employee.js';
import {addMove, deleteMove, loadMoveRecords} from '@/api/employee/record.js';

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
  empLoading: false,
  dicts: {departments: [], positions: []}
});
const {records, loading, query, dialogVisible, saving, employeeOptions, empLoading, dicts} = toRefs(data);

const formRef = ref();
const form = reactive(emptyForm());

function emptyForm() {
  return {id: null, eid: null, afterDepId: null, afterJobId: null, removeDate: '', reason: '', remark: ''};
}

const rules = {
  eid: [{required: true, message: '请选择员工', trigger: 'change'}],
  afterDepId: [{required: true, message: '请选择调动后部门', trigger: 'change'}],
  afterJobId: [{required: true, message: '请选择调动后职位', trigger: 'change'}],
  removeDate: [{required: true, message: '请选择调动日期', trigger: 'change'}],
  reason: [{required: true, message: '请输入调动原因', trigger: 'blur'}]
};

function loadData() {
  loading.value = true;
  const params = {};
  if (props.eid) {
    params.eid = props.eid;
  } else if (query.value.name) {
    params.name = query.value.name;
  }
  loadMoveRecords(params).then(res => {
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
    addMove({...form}).then(() => {
      dialogVisible.value = false;
      loadData();
    }).finally(() => {
      saving.value = false;
    });
  });
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除【${row.empName || '该员工'}】的这条调动记录吗？删除后不会回滚员工档案。`,
      '提示', {type: 'warning'})
      .then(() => deleteMove(row.id))
      .then(() => loadData())
      .catch(() => {
      });
}

watch(() => props.eid, () => {
  dialogVisible.value = false;
  loadData();
});

onMounted(() => {
  loadDicts().then(d => Object.assign(dicts.value, d));
  loadData();
});
</script>

<style scoped>
.emp-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #36435a;
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
