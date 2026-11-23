<template>
  <div class="page">
    <PageHeader eyebrow="员工资料" title="基本资料"
                subtitle="集中维护员工档案、岗位、合同与在职状态">
      <template #actions>
        <el-button :icon="Refresh" :loading="loading" @click="loadData">刷新</el-button>
        <el-button :icon="Download" :loading="exporting" @click="handleExport">导出 Excel</el-button>
        <el-button type="primary" :icon="Plus" @click="openAdd">新增员工</el-button>
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
        <el-form-item label="聘用形式">
          <el-select v-model="query.engageForm" placeholder="全部" clearable style="width: 120px">
            <el-option v-for="e in dicts.engageForms" :key="e" :label="e" :value="e"/>
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
        <el-button link :icon="Refresh" @click="loadData">刷新列表</el-button>
      </div>
      <EmployeeTable :data="employees" :loading="loading" show-actions show-detail
                     @edit="openEdit" @delete="handleDelete" @detail="openDetail"/>
      <div class="pager">
        <el-pagination background v-model:current-page="page" v-model:page-size="size"
                       :page-sizes="[5, 10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
                       :total="total" @change="paginationChange"/>
      </div>
    </section>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑员工档案' : '新增员工档案'"
               width="980px" top="4vh" :close-on-click-modal="false">
      <div class="dialog-tip">带 <b>*</b> 为必填项；历史数据中的工号、手机号等字段仅在内容变化时校验。</div>
      <el-scrollbar max-height="66vh">
        <el-form :model="form" :rules="rules" ref="formRef" label-width="94px" class="employee-form">
          <div class="form-section">
            <div class="form-section-title">基本信息</div>
            <el-row :gutter="18">
              <el-col :span="12">
                <el-form-item label="姓名" prop="name">
                  <el-input v-model="form.name" maxlength="10" placeholder="请输入姓名"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="工号" prop="workId">
                  <el-input v-model="form.workId" maxlength="8" placeholder="8 位数字"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="性别" prop="gender">
                  <el-select v-model="form.gender" style="width: 100%">
                    <el-option v-for="g in dicts.genders" :key="g" :label="g" :value="g"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="出生日期" prop="birthday">
                  <el-date-picker v-model="form.birthday" type="date" value-format="YYYY-MM-DD"
                                  placeholder="选择日期" style="width: 100%"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="身份证号" prop="idCard">
                  <el-input v-model="form.idCard" maxlength="18" placeholder="18 位身份证号"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="婚姻状况" prop="wedlock">
                  <el-select v-model="form.wedlock" style="width: 100%" clearable placeholder="请选择">
                    <el-option v-for="w in dicts.wedlocks" :key="w" :label="w" :value="w"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="民族" prop="nationId">
                  <el-select v-model="form.nationId" style="width: 100%" clearable filterable placeholder="请选择">
                    <el-option v-for="n in dicts.nations" :key="n.id" :label="n.name" :value="n.id"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="籍贯" prop="nativePlace">
                  <el-input v-model="form.nativePlace" maxlength="20" placeholder="如：河北石家庄"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="政治面貌" prop="politicId">
                  <el-select v-model="form.politicId" style="width: 100%" clearable placeholder="请选择">
                    <el-option v-for="p in dicts.politics" :key="p.id" :label="p.name" :value="p.id"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="form.email" maxlength="20" placeholder="name@zhihui.cn"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="form.phone" maxlength="11" placeholder="11 位手机号"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="联系地址" prop="address">
                  <el-input v-model="form.address" maxlength="64" placeholder="请输入联系地址"/>
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <div class="form-section">
            <div class="form-section-title">岗位与在职</div>
            <el-row :gutter="18">
              <el-col :span="12">
                <el-form-item label="所属部门" prop="departmentId">
                  <el-select v-model="form.departmentId" style="width: 100%" placeholder="请选择部门">
                    <el-option v-for="d in dicts.departments" :key="d.id" :label="d.name" :value="d.id"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="职称" prop="jobLevelId">
                  <el-select v-model="form.jobLevelId" style="width: 100%" placeholder="请选择职称">
                    <el-option v-for="j in dicts.joblevels" :key="j.id" :label="j.name" :value="j.id"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="职位" prop="posId">
                  <el-select v-model="form.posId" style="width: 100%" placeholder="请选择职位">
                    <el-option v-for="p in dicts.positions" :key="p.id" :label="p.name" :value="p.id"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="聘用形式" prop="engageForm">
                  <el-select v-model="form.engageForm" style="width: 100%" placeholder="请选择">
                    <el-option v-for="e in dicts.engageForms" :key="e" :label="e" :value="e"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="入职日期" prop="beginDate">
                  <el-date-picker v-model="form.beginDate" type="date" value-format="YYYY-MM-DD"
                                  placeholder="选择日期" style="width: 100%"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="在职状态" prop="workState">
                  <el-select v-model="form.workState" style="width: 100%">
                    <el-option v-for="s in dicts.workStates" :key="s" :label="s" :value="s"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="最高学历" prop="tipTopDegree">
                  <el-select v-model="form.tipTopDegree" style="width: 100%" clearable placeholder="请选择">
                    <el-option v-for="d in dicts.degrees" :key="d" :label="d" :value="d"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="专业" prop="specialty">
                  <el-input v-model="form.specialty" maxlength="32" placeholder="如：计算机科学与技术"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="毕业院校" prop="school">
                  <el-input v-model="form.school" maxlength="32" placeholder="请输入毕业院校"/>
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <div class="form-section">
            <div class="form-section-title">合同信息</div>
            <el-row :gutter="18">
              <el-col :span="12">
                <el-form-item label="合同期限" prop="contractTerm">
                  <el-input-number v-model="form.contractTerm" :min="0" :precision="1"
                                   style="width: 100%" placeholder="年"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="转正日期" prop="conversionTime">
                  <el-date-picker v-model="form.conversionTime" type="date" value-format="YYYY-MM-DD"
                                  placeholder="选择日期" style="width: 100%"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="合同起始" prop="beginContract">
                  <el-date-picker v-model="form.beginContract" type="date" value-format="YYYY-MM-DD"
                                  placeholder="选择日期" style="width: 100%"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="合同终止" prop="endContract">
                  <el-date-picker v-model="form.endContract" type="date" value-format="YYYY-MM-DD"
                                  placeholder="选择日期" style="width: 100%"/>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </el-form>
      </el-scrollbar>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存档案</el-button>
      </template>
    </el-dialog>

    <EmployeeDetailDrawer v-model="detailVisible" :employee="detailEmployee" :loading="detailLoading"
                          editable show-advanced @edit="handleDetailEdit" @advanced="goAdvanced"/>
  </div>
</template>

<script setup>
import {computed, nextTick, onMounted, reactive, ref, toRefs} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {ElMessage, ElMessageBox} from 'element-plus';
import {Download, Plus, Refresh, Search} from '@element-plus/icons-vue';
import PageHeader from '@/components/common/PageHeader.vue';
import EmployeeTable from '@/components/employee/EmployeeTable.vue';
import EmployeeDetailDrawer from '@/components/employee/EmployeeDetailDrawer.vue';
import {
  addEmployee,
  deleteEmployeeById,
  getEmployeeById,
  loadAllEmployees,
  loadDicts,
  loadEmployees,
  updateEmployee
} from '@/api/employee/employee.js';
import {exportEmployeesToExcel} from '@/utils/exportExcel.js';

const route = useRoute();
const router = useRouter();

const data = reactive({
  employees: [],
  total: 0,
  page: 1,
  size: 10,
  loading: false,
  exporting: false,
  query: {name: '', departmentId: '', posId: '', jobLevelId: '', engageForm: ''},
  dicts: {
    departments: [], positions: [], joblevels: [], nations: [], politics: [],
    genders: [], wedlocks: [], engageForms: [], degrees: [], workStates: []
  },
  dialogVisible: false,
  saving: false,
  detailVisible: false,
  detailLoading: false,
  detailEmployee: null
});
const {
  employees, total, page, size, loading, exporting, query, dicts,
  dialogVisible, saving, detailVisible, detailLoading, detailEmployee
} = toRefs(data);

const activeFilterCount = computed(() =>
    Object.values(query.value).filter(value => value !== '' && value !== null && value !== undefined).length
);

const formRef = ref();
const form = reactive(emptyForm());
// 编辑时保存原值：历史数据里工号、手机号等存在不合规值，
// 与后端保持一致，只有字段真的被改过才做格式校验。
let originalEmployee = null;

function emptyForm() {
  return {
    id: null, name: '', gender: '男', birthday: '', idCard: '', wedlock: '',
    nationId: null, nativePlace: '', politicId: null, email: '', phone: '',
    address: '', departmentId: null, jobLevelId: null, posId: null,
    engageForm: '', tipTopDegree: '', specialty: '', school: '', beginDate: '',
    workState: '在职', workId: '', contractTerm: null, conversionTime: '',
    notWorkDate: '', beginContract: '', endContract: '', workAge: null
  };
}

function unchanged(field) {
  return originalEmployee !== null && form[field] === originalEmployee[field];
}

function requiredRule(message, trigger = 'blur') {
  return {required: true, message: message, trigger: trigger};
}

const rules = {
  name: [requiredRule('请输入姓名')],
  workId: [requiredRule('请输入工号'), {
    validator(rule, value, callback) {
      if (!value || unchanged('workId')) return callback();
      if (!/^\d{8}$/.test(value)) return callback(new Error('工号必须为8位数字'));
      callback();
    },
    trigger: 'blur'
  }],
  gender: [requiredRule('请选择性别', 'change')],
  birthday: [requiredRule('请选择出生日期', 'change')],
  idCard: [requiredRule('请输入身份证号'), {
    validator(rule, value, callback) {
      if (!value || unchanged('idCard')) return callback();
      if (!/^\d{17}[\dXx]$/.test(value)) return callback(new Error('身份证号格式不正确'));
      callback();
    },
    trigger: 'blur'
  }],
  phone: [{
    validator(rule, value, callback) {
      if (!value || unchanged('phone')) return callback();
      if (!/^\d{11}$/.test(value)) return callback(new Error('手机号必须为11位数字'));
      callback();
    },
    trigger: 'blur'
  }],
  email: [{
    validator(rule, value, callback) {
      if (!value || unchanged('email')) return callback();
      if (value.length > 20) return callback(new Error('邮箱长度不能超过20个字符'));
      if (!/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(value)) {
        return callback(new Error('邮箱格式不正确'));
      }
      callback();
    },
    trigger: 'blur'
  }],
  departmentId: [requiredRule('请选择所属部门', 'change')],
  jobLevelId: [requiredRule('请选择职称', 'change')],
  posId: [requiredRule('请选择职位', 'change')],
  engageForm: [requiredRule('请选择聘用形式', 'change')],
  beginDate: [requiredRule('请选择入职日期', 'change'), {
    validator(rule, value, callback) {
      if (!value) return callback();
      const today = new Date();
      const todayStr = today.getFullYear() + '-' +
          String(today.getMonth() + 1).padStart(2, '0') + '-' +
          String(today.getDate()).padStart(2, '0');
      if (value > todayStr) return callback(new Error('入职日期不能晚于今天'));
      callback();
    },
    trigger: 'change'
  }],
  workState: [requiredRule('请选择在职状态', 'change')],
  endContract: [{
    validator(rule, value, callback) {
      if (!value || !form.beginContract) return callback();
      if (unchanged('endContract') && form.beginContract === originalEmployee.beginContract) {
        return callback();
      }
      if (value < form.beginContract) return callback(new Error('合同终止日期不能早于合同起始日期'));
      callback();
    },
    trigger: 'change'
  }]
};

function buildQuery() {
  const params = {page: page.value, size: size.value};
  if (query.value.name) params.name = query.value.name;
  if (query.value.departmentId) params.departmentId = query.value.departmentId;
  if (query.value.posId) params.posId = query.value.posId;
  if (query.value.jobLevelId) params.jobLevelId = query.value.jobLevelId;
  if (query.value.engageForm) params.engageForm = query.value.engageForm;
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
  Object.assign(query.value, {name: '', departmentId: '', posId: '', jobLevelId: '', engageForm: ''});
  search();
}

function paginationChange(newPage, newSize) {
  page.value = newPage;
  size.value = newSize;
  loadData();
}

function openAdd() {
  originalEmployee = null;
  Object.assign(form, emptyForm());
  dialogVisible.value = true;
  nextTick(() => formRef.value && formRef.value.clearValidate());
}

function openEdit(row) {
  getEmployeeById(row.id).then(res => {
    const emp = res.data;
    originalEmployee = JSON.parse(JSON.stringify(emp));
    Object.assign(form, emptyForm(), emp);
    dialogVisible.value = true;
    nextTick(() => formRef.value && formRef.value.clearValidate());
  });
}

/**
 * 可选字段留空时发送 null，避免空字符串写入 enum/date 列导致数据库报错。
 */
function buildPayload() {
  const payload = {...form};
  [
    'wedlock', 'tipTopDegree', 'nativePlace', 'email', 'phone', 'address',
    'specialty', 'school', 'notWorkDate', 'conversionTime', 'beginContract', 'endContract'
  ].forEach(key => {
    if (payload[key] === '') payload[key] = null;
  });
  if (payload.contractTerm === '') payload.contractTerm = null;
  return payload;
}

function submit() {
  formRef.value.validate(valid => {
    if (!valid) return;
    saving.value = true;
    const payload = buildPayload();
    const request = form.id ? updateEmployee(payload) : addEmployee(payload);
    request.then(() => {
      dialogVisible.value = false;
      loadData();
    }).finally(() => {
      saving.value = false;
    });
  });
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

function handleDetailEdit(employee) {
  detailVisible.value = false;
  openEdit(employee);
}

function goAdvanced(employee) {
  detailVisible.value = false;
  router.push({path: '/emp/adv', query: {empId: employee.id}});
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除员工【${row.name}】吗？`, '提示', {type: 'warning'})
      .then(() => deleteEmployeeById(row.id))
      .then(() => loadData())
      .catch(() => {
      });
}

async function handleExport() {
  exporting.value = true;
  try {
    const rows = await loadAllEmployees(buildQuery());
    await exportEmployeesToExcel(rows, '员工档案');
  } catch {
    ElMessage.error('导出失败，请稍后重试');
  } finally {
    exporting.value = false;
  }
}

onMounted(() => {
  loadDicts().then(d => Object.assign(dicts.value, d));
  loadData();
  if (route.query.action === 'add') {
    openAdd();
    router.replace({path: '/emp/basic'});
  }
});
</script>

<style scoped>
.dialog-tip {
  margin-bottom: 18px;
  padding: 10px 12px;
  color: #7d889b;
  background: #f8faff;
  border: 1px solid #edf1f9;
  border-radius: 8px;
  font-size: 11px;
}

.dialog-tip b {
  color: #ef4444;
}

.employee-form {
  padding-right: 12px;
}

@media (max-width: 1000px) {
  .employee-form :deep(.el-col-12) {
    max-width: 100%;
    flex: 0 0 100%;
  }
}
</style>
