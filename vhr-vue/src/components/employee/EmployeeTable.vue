<template>
  <el-table :data="data" v-loading="loading" style="width: 100%" row-key="id"
            :highlight-current-row="highlightCurrentRow" empty-text="暂无员工数据"
            @row-click="handleRowClick">
    <el-table-column label="员工" min-width="160">
      <template #default="scope">
        <div class="employee-cell">
          <EmployeeAvatar :name="scope.row.name" :seed="scope.row.id" :size="36"/>
          <div class="employee-copy">
            <span class="employee-name">{{ scope.row.name || '-' }}</span>
            <span class="employee-sub">工号 {{ scope.row.workId || '-' }}</span>
          </div>
        </div>
      </template>
    </el-table-column>
    <el-table-column prop="gender" label="性别" width="50" align="center"/>
    <el-table-column prop="birthday" label="出生日期" width="104"/>
    <el-table-column prop="departmentName" label="所属部门" min-width="84" show-overflow-tooltip/>
    <el-table-column prop="posName" label="职位" min-width="110" show-overflow-tooltip/>
    <el-table-column prop="jobLevelName" label="职称" min-width="104" show-overflow-tooltip/>
    <el-table-column prop="engageForm" label="聘用形式" width="86"/>
    <el-table-column prop="beginDate" label="入职日期" width="98"/>
    <el-table-column label="在职状态" width="74" align="center">
      <template #default="scope">
        <el-tag :type="scope.row.workState === '在职' ? 'success' : 'danger'" effect="light" round>
          {{ scope.row.workState || '-' }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column v-if="showActions || showDetail" label="操作" :width="actionWidth" fixed="right">
      <template #default="scope">
        <div class="table-actions">
          <el-button v-if="showDetail" link type="primary" @click.stop="emit('detail', scope.row)">详情</el-button>
          <el-button v-if="showActions" link type="primary" @click.stop="emit('edit', scope.row)">编辑</el-button>
          <el-button v-if="showActions" link type="danger" @click.stop="emit('delete', scope.row)">删除</el-button>
        </div>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import {computed} from 'vue';
import EmployeeAvatar from '@/components/employee/EmployeeAvatar.vue';

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  showActions: {
    type: Boolean,
    default: false
  },
  showDetail: {
    type: Boolean,
    default: false
  },
  highlightCurrentRow: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['edit', 'delete', 'detail', 'row-click']);

const actionWidth = computed(() => {
  if (props.showActions && props.showDetail) return 148;
  if (props.showActions || props.showDetail) return 84;
  return 0;
});

function handleRowClick(row) {
  emit('row-click', row);
}
</script>

<style scoped>
.employee-cell {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.employee-copy {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 2px;
}

.employee-name {
  overflow: hidden;
  color: #28344a;
  font-size: 13px;
  font-weight: 600;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.employee-sub {
  color: #a0a9b8;
  font-size: 10px;
  letter-spacing: 0.02em;
}

.table-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
}

.table-actions :deep(.el-button + .el-button) {
  margin-left: 0;
}
</style>
