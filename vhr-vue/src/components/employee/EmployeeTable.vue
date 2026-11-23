<template>
  <el-table :data="data" v-loading="loading" border stripe style="width: 100%"
            :highlight-current-row="highlightCurrentRow" @row-click="handleRowClick">
    <el-table-column prop="workId" label="工号" width="100"/>
    <el-table-column prop="name" label="姓名" width="100"/>
    <el-table-column prop="gender" label="性别" width="60"/>
    <el-table-column prop="birthday" label="出生日期" width="110"/>
    <el-table-column prop="departmentName" label="所属部门" min-width="100"/>
    <el-table-column prop="posName" label="职位" min-width="110"/>
    <el-table-column prop="jobLevelName" label="职称" min-width="100"/>
    <el-table-column prop="engageForm" label="聘用形式" width="100"/>
    <el-table-column prop="beginDate" label="入职日期" width="110"/>
    <el-table-column label="在职状态" width="90">
      <template #default="scope">
        <el-tag :type="scope.row.workState === '在职' ? 'success' : 'danger'">
          {{ scope.row.workState }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column v-if="showActions" label="操作" width="150" fixed="right">
      <template #default="scope">
        <el-button size="small" @click.stop="emit('edit', scope.row)">编辑</el-button>
        <el-button size="small" type="danger" @click.stop="emit('delete', scope.row)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
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
  highlightCurrentRow: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['edit', 'delete', 'row-click']);

function handleRowClick(row) {
  emit('row-click', row);
}
</script>

<style scoped>
</style>
