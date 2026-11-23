<template>
  <header class="app-header">
    <div class="header-left">
      <button type="button" class="icon-btn" title="切换菜单" @click="$emit('toggle')">
        <el-icon :size="18"><component :is="collapsed ? Expand : Fold"/></el-icon>
      </button>
      <el-breadcrumb :separator-icon="ArrowRight" class="breadcrumb">
        <el-breadcrumb-item :to="{path: '/home'}">工作台</el-breadcrumb-item>
        <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">{{ item.title }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="header-right">
      <span class="today">{{ todayText }}</span>
      <span class="header-divider"></span>
      <el-dropdown trigger="click" @command="$emit('command', $event)">
        <button type="button" class="user-chip">
          <EmployeeAvatar :name="displayName" :size="34"/>
          <span class="user-copy">
            <strong>{{ displayName }}</strong>
            <small>{{ user.roleName || '系统管理员' }}</small>
          </span>
          <el-icon class="chevron"><ArrowDown/></el-icon>
        </button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人信息</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<script setup>
import {computed} from 'vue';
import {useRoute} from 'vue-router';
import {ArrowDown, ArrowRight, Expand, Fold} from '@element-plus/icons-vue';
import EmployeeAvatar from '@/components/employee/EmployeeAvatar.vue';

const props = defineProps({
  collapsed: {type: Boolean, default: false},
  user: {type: Object, default: () => ({})}
});
defineEmits(['toggle', 'command']);

const route = useRoute();

const displayName = computed(() => props.user.name || props.user.username || '管理员');

const breadcrumbs = computed(() => {
  const seen = new Set();
  return route.matched
      .filter(record => record.meta && record.meta.title)
      .map(record => ({title: record.meta.title, path: record.path}))
      .filter(item => {
        if (item.title === '工作台' || item.title === '首页' || seen.has(item.title)) return false;
        seen.add(item.title);
        return true;
      });
});

const todayText = computed(() => {
  const now = new Date();
  const weekdays = ['日', '一', '二', '三', '四', '五', '六'];
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 · 星期${weekdays[now.getDay()]}`;
});
</script>

<style scoped>
.app-header {
  position: relative;
  z-index: 10;
  display: flex;
  flex: 0 0 72px;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  height: 72px;
  padding: 0 26px 0 18px;
  background: rgba(255, 255, 255, 0.94);
  border-bottom: 1px solid #e9edf5;
  backdrop-filter: blur(12px);
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  color: #66738a;
  background: #f6f8fc;
  border: 1px solid #e8ecf5;
  border-radius: 9px;
  cursor: pointer;
  transition: 0.18s;
}

.icon-btn:hover {
  color: #4f6ef7;
  background: #eef2ff;
  border-color: #d9e1ff;
}

.breadcrumb {
  margin-left: 4px;
}

.breadcrumb :deep(.el-breadcrumb__inner) {
  color: #9aa4b5;
  font-size: 12px;
  font-weight: 500;
}

.breadcrumb :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #33405a;
}

.today {
  color: #8f9aac;
  font-size: 12px;
}

.header-divider {
  width: 1px;
  height: 22px;
  background: #edf0f6;
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 6px 4px 4px;
  background: transparent;
  border: 0;
  border-radius: 10px;
  cursor: pointer;
  font: inherit;
  transition: 0.18s;
}

.user-chip:hover {
  background: #f6f8fc;
}

.user-copy {
  display: flex;
  min-width: 70px;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}

.user-copy strong {
  color: #28344a;
  font-size: 13px;
  font-weight: 650;
}

.user-copy small {
  color: #98a2b3;
  font-size: 10px;
}

.chevron {
  color: #a2abbb;
  font-size: 12px;
}

@media (max-width: 900px) {
  .today,
  .header-divider {
    display: none;
  }

  .app-header {
    padding-right: 16px;
  }
}
</style>
