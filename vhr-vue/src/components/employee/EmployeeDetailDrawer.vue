<template>
  <el-drawer :model-value="modelValue" size="520px" :with-header="false" class="employee-drawer"
             @update:model-value="$emit('update:modelValue', $event)">
    <div v-loading="loading" class="drawer-body">
      <template v-if="employee">
        <div class="drawer-head">
          <EmployeeAvatar :name="employee.name" :seed="employee.id" :size="62"/>
          <div class="drawer-identity">
            <h2>{{ employee.name || '-' }}</h2>
            <p>{{ employee.departmentName || '-' }} · {{ employee.posName || '-' }}</p>
            <el-tag :type="employee.workState === '在职' ? 'success' : 'danger'" effect="light" round>
              {{ employee.workState || '未知状态' }}
            </el-tag>
          </div>
          <button type="button" class="drawer-close" @click="$emit('update:modelValue', false)">
            <el-icon><Close/></el-icon>
          </button>
        </div>

        <div class="drawer-section">
          <h3>基本信息</h3>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="工号">{{ value(employee.workId) }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ value(employee.gender) }}</el-descriptions-item>
            <el-descriptions-item label="出生日期">{{ value(employee.birthday) }}</el-descriptions-item>
            <el-descriptions-item label="婚姻状况">{{ value(employee.wedlock) }}</el-descriptions-item>
            <el-descriptions-item label="民族">{{ value(employee.nationName) }}</el-descriptions-item>
            <el-descriptions-item label="政治面貌">{{ value(employee.politicName) }}</el-descriptions-item>
            <el-descriptions-item label="籍贯">{{ value(employee.nativePlace) }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ value(employee.phone) }}</el-descriptions-item>
            <el-descriptions-item label="身份证号" :span="2">{{ value(employee.idCard) }}</el-descriptions-item>
            <el-descriptions-item label="邮箱" :span="2">{{ value(employee.email) }}</el-descriptions-item>
            <el-descriptions-item label="联系地址" :span="2">{{ value(employee.address) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="drawer-section">
          <h3>岗位与任职</h3>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="所属部门">{{ value(employee.departmentName) }}</el-descriptions-item>
            <el-descriptions-item label="职位">{{ value(employee.posName) }}</el-descriptions-item>
            <el-descriptions-item label="职称">{{ value(employee.jobLevelName) }}</el-descriptions-item>
            <el-descriptions-item label="聘用形式">{{ value(employee.engageForm) }}</el-descriptions-item>
            <el-descriptions-item label="入职日期">{{ value(employee.beginDate) }}</el-descriptions-item>
            <el-descriptions-item label="工作年限">{{ employee.workAge == null ? '-' : employee.workAge + ' 年' }}</el-descriptions-item>
            <el-descriptions-item label="转正日期">{{ value(employee.conversionTime) }}</el-descriptions-item>
            <el-descriptions-item label="离职日期">{{ value(employee.notWorkDate) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="drawer-section">
          <h3>教育与合同</h3>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="最高学历">{{ value(employee.tipTopDegree) }}</el-descriptions-item>
            <el-descriptions-item label="专业">{{ value(employee.specialty) }}</el-descriptions-item>
            <el-descriptions-item label="毕业院校" :span="2">{{ value(employee.school) }}</el-descriptions-item>
            <el-descriptions-item label="合同期限">{{ employee.contractTerm == null ? '-' : employee.contractTerm + ' 年' }}</el-descriptions-item>
            <el-descriptions-item label="合同起始">{{ value(employee.beginContract) }}</el-descriptions-item>
            <el-descriptions-item label="合同终止" :span="2">{{ value(employee.endContract) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div v-if="editable || showAdvanced" class="drawer-actions">
          <el-button v-if="editable" type="primary" @click="$emit('edit', employee)">编辑档案</el-button>
          <el-button v-if="showAdvanced" @click="$emit('advanced', employee)">查看高级资料</el-button>
        </div>
      </template>
      <el-empty v-else-if="!loading" description="暂无员工信息"/>
    </div>
  </el-drawer>
</template>

<script setup>
import {Close} from '@element-plus/icons-vue';
import EmployeeAvatar from '@/components/employee/EmployeeAvatar.vue';

defineProps({
  modelValue: {type: Boolean, default: false},
  employee: {type: Object, default: null},
  loading: {type: Boolean, default: false},
  editable: {type: Boolean, default: true},
  showAdvanced: {type: Boolean, default: true}
});

defineEmits(['update:modelValue', 'edit', 'advanced']);

function value(input) {
  return input === null || input === undefined || input === '' ? '-' : input;
}
</script>

<style scoped>
.drawer-body {
  min-height: 300px;
  padding: 26px 26px 30px;
}

.drawer-head {
  position: relative;
  display: flex;
  align-items: center;
  gap: 15px;
  padding-bottom: 24px;
  border-bottom: 1px solid #edf0f7;
}

.drawer-identity h2 {
  margin: 0;
  color: #172033;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: -0.03em;
}

.drawer-identity p {
  margin: 5px 0 8px;
  color: #8d98aa;
  font-size: 12px;
}

.drawer-close {
  position: absolute;
  top: -4px;
  right: -4px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  color: #99a3b4;
  background: #f7f9fd;
  border: 0;
  border-radius: 8px;
  cursor: pointer;
}

.drawer-close:hover {
  color: #4f6ef7;
  background: #eef2ff;
}

.drawer-section {
  margin-top: 24px;
}

.drawer-section h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 12px;
  color: #36435a;
  font-size: 13px;
  font-weight: 650;
}

.drawer-section h3::before {
  width: 4px;
  height: 14px;
  border-radius: 4px;
  background: #4f6ef7;
  content: "";
}

.drawer-section :deep(.el-descriptions__label) {
  width: 86px;
  color: #8d98aa;
  font-weight: 500;
}

.drawer-section :deep(.el-descriptions__content) {
  color: #3d4a60;
}

.drawer-actions {
  display: flex;
  gap: 10px;
  margin-top: 26px;
  padding-top: 20px;
  border-top: 1px solid #edf0f7;
}
</style>
