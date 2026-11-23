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
      <el-form-item label="聘用形式">
        <el-select v-model="query.engageForm" placeholder="全部" clearable style="width: 120px">
          <el-option v-for="e in dicts.engageForms" :key="e" :label="e" :value="e"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="toolbar">
      <el-button type="primary" @click="openAdd">新增员工</el-button>
    </div>

    <EmployeeTable :data="employees" :loading="loading" show-actions
                   @edit="openEdit" @delete="handleDelete"/>

    <div class="pager">
      <el-pagination background v-model:current-page="page" v-model:page-size="size"
                     :page-sizes="[5, 10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
                     :total="total" @change="paginationChange"/>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑员工' : '新增员工'" width="900px" top="4vh">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" maxlength="10"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工号" prop="workId">
              <el-input v-model="form.workId" maxlength="8" placeholder="8位数字"/>
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
              <el-input v-model="form.idCard" maxlength="18"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="婚姻状况" prop="wedlock">
              <el-select v-model="form.wedlock" style="width: 100%" clearable>
                <el-option v-for="w in dicts.wedlocks" :key="w" :label="w" :value="w"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="民族" prop="nationId">
              <el-select v-model="form.nationId" style="width: 100%" clearable>
                <el-option v-for="n in dicts.nations" :key="n.id" :label="n.name" :value="n.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="籍贯" prop="nativePlace">
              <el-input v-model="form.nativePlace" maxlength="20"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="政治面貌" prop="politicId">
              <el-select v-model="form.politicId" style="width: 100%" clearable>
                <el-option v-for="p in dicts.politics" :key="p.id" :label="p.name" :value="p.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" maxlength="20"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" maxlength="11"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系地址" prop="address">
              <el-input v-model="form.address" maxlength="64"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属部门" prop="departmentId">
              <el-select v-model="form.departmentId" style="width: 100%">
                <el-option v-for="d in dicts.departments" :key="d.id" :label="d.name" :value="d.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职称" prop="jobLevelId">
              <el-select v-model="form.jobLevelId" style="width: 100%">
                <el-option v-for="j in dicts.joblevels" :key="j.id" :label="j.name" :value="j.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位" prop="posId">
              <el-select v-model="form.posId" style="width: 100%">
                <el-option v-for="p in dicts.positions" :key="p.id" :label="p.name" :value="p.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="聘用形式" prop="engageForm">
              <el-select v-model="form.engageForm" style="width: 100%">
                <el-option v-for="e in dicts.engageForms" :key="e" :label="e" :value="e"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最高学历" prop="tipTopDegree">
              <el-select v-model="form.tipTopDegree" style="width: 100%" clearable>
                <el-option v-for="d in dicts.degrees" :key="d" :label="d" :value="d"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="专业" prop="specialty">
              <el-input v-model="form.specialty" maxlength="32"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="毕业院校" prop="school">
              <el-input v-model="form.school" maxlength="32"/>
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
            <el-form-item label="合同期限" prop="contractTerm">
              <el-input-number v-model="form.contractTerm" :min="0" :precision="1" style="width: 100%"/>
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
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {nextTick, onMounted, reactive, ref, toRefs} from "vue";
import {ElMessageBox} from "element-plus";
import EmployeeTable from "@/components/employee/EmployeeTable.vue";
import {
  addEmployee,
  deleteEmployeeById,
  getEmployeeById,
  loadDicts,
  loadEmployees,
  updateEmployee
} from "@/api/employee/employee.js";

const data = reactive({
  employees: [],
  total: 0,
  page: 1,
  size: 10,
  loading: false,
  query: {name: '', departmentId: '', posId: '', jobLevelId: '', engageForm: ''},
  dicts: {
    departments: [], positions: [], joblevels: [], nations: [], politics: [],
    genders: [], wedlocks: [], engageForms: [], degrees: [], workStates: []
  },
  dialogVisible: false,
  saving: false
});
const {employees, total, page, size, loading, query, dicts, dialogVisible, saving} = toRefs(data);

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

function submit() {
  formRef.value.validate(valid => {
    if (!valid) return;
    saving.value = true;
    const request = form.id ? updateEmployee({...form}) : addEmployee({...form});
    request.then(() => {
      dialogVisible.value = false;
      loadData();
    }).finally(() => {
      saving.value = false;
    });
  });
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除员工【${row.name}】吗？`, '提示', {type: 'warning'})
      .then(() => deleteEmployeeById(row.id))
      .then(() => loadData())
      .catch(() => {
      });
}

onMounted(() => {
  loadDicts().then(d => Object.assign(dicts.value, d));
  loadData();
});
</script>

<style scoped>
.toolbar {
  margin-bottom: 12px;
}

.pager {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>
