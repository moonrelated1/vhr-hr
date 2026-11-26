<template>
  <aside class="app-sidebar" :class="{collapsed}">
    <div class="brand">
      <div class="brand-mark">Z</div>
      <div v-show="!collapsed" class="brand-copy">
        <strong>智汇 HRMS</strong>
        <span>人力资源管理平台</span>
      </div>
    </div>

    <el-scrollbar class="menu-scroll">
      <el-menu
          :default-active="activePath"
          :default-openeds="openedMenus"
          :collapse="collapsed"
          :collapse-transition="false"
          class="nav-menu"
          router
      >
        <el-menu-item index="/home" class="home-item">
          <el-icon><Odometer/></el-icon>
          <template #title>工作台</template>
        </el-menu-item>

        <el-sub-menu v-for="menu in menus" :key="menu.id || menu.name" :index="'group-' + (menu.id || menu.name)">
          <template #title>
            <el-icon>
              <component :is="menuIcon(menu)"/>
            </el-icon>
            <span>{{ menu.name }}</span>
          </template>
          <el-menu-item v-for="child in menu.children || []" :key="child.path" :index="child.path">
            {{ child.name }}
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-scrollbar>

    <div class="sidebar-footer">
      <button type="button" class="collapse-btn" @click="$emit('toggle')">
        <el-icon><component :is="collapsed ? Expand : Fold"/></el-icon>
        <span v-show="!collapsed">收起菜单</span>
      </button>
      <div v-show="!collapsed" class="sidebar-version">员工管理模块 · v2.0</div>
    </div>
  </aside>
</template>

<script setup>
import {computed} from 'vue';
import {useRoute} from 'vue-router';
import {Expand, Files, Fold, Management, Odometer, UserFilled} from '@element-plus/icons-vue';

const props = defineProps({
  menus: {type: Array, default: () => []},
  collapsed: {type: Boolean, default: false}
});
defineEmits(['toggle']);

const route = useRoute();
const activePath = computed(() => route.path || '/home');
const openedMenus = computed(() => props.menus.map(menu => 'group-' + (menu.id || menu.name)));

function menuIcon(menu) {
  if (menu.name === '员工资料') return UserFilled;
  if (menu.name === '人事管理') return Management;
  return Files;
}
</script>

<style scoped>
.app-sidebar {
  position: relative;
  z-index: 20;
  display: flex;
  flex: 0 0 248px;
  flex-direction: column;
  width: 248px;
  height: 100vh;
  overflow: hidden;
  background:
      radial-gradient(circle at 20% 0%, rgba(79, 110, 247, 0.24), transparent 34%),
      linear-gradient(180deg, #111c33 0%, #0f172a 58%, #0b1324 100%);
  box-shadow: 12px 0 30px rgba(15, 23, 42, 0.08);
  transition: width 0.2s ease, flex-basis 0.2s ease;
}

.app-sidebar.collapsed {
  flex-basis: 76px;
  width: 76px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  height: 76px;
  padding: 0 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.07);
}

.collapsed .brand {
  justify-content: center;
  padding: 0;
}

.brand-mark {
  display: flex;
  flex: 0 0 auto;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  color: #fff;
  background: linear-gradient(135deg, #5b7cfa, #3f5bd6);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(79, 110, 247, 0.35);
  font-size: 20px;
  font-weight: 800;
  letter-spacing: -0.06em;
}

.brand-copy {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 3px;
  overflow: hidden;
  white-space: nowrap;
}

.brand-copy strong {
  color: #f8faff;
  font-size: 17px;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.brand-copy span {
  color: #8794ae;
  font-size: 10px;
  letter-spacing: 0.08em;
}

.menu-scroll {
  flex: 1;
  min-height: 0;
  padding: 16px 10px;
}

.nav-menu {
  border-right: 0;
  background: transparent;
}

.nav-menu :deep(.el-menu-item),
.nav-menu :deep(.el-sub-menu__title) {
  height: 44px;
  margin-bottom: 5px;
  color: #9da9bf;
  border-radius: 9px;
  font-size: 13px;
}

.nav-menu :deep(.el-menu-item:hover),
.nav-menu :deep(.el-sub-menu__title:hover) {
  color: #fff;
  background: rgba(255, 255, 255, 0.055);
}

.nav-menu :deep(.el-menu-item.is-active) {
  color: #fff;
  background: linear-gradient(90deg, rgba(79, 110, 247, 0.95), rgba(79, 110, 247, 0.62));
  box-shadow: 0 8px 18px rgba(79, 110, 247, 0.22);
}

.nav-menu :deep(.el-sub-menu .el-menu) {
  background: transparent;
}

.nav-menu :deep(.el-sub-menu .el-menu-item) {
  min-width: 0;
  height: 38px;
  padding-left: 50px !important;
  color: #8190aa;
  font-size: 12px;
}

.nav-menu :deep(.el-sub-menu .el-menu-item.is-active) {
  color: #fff;
  background: rgba(79, 110, 247, 0.24);
  box-shadow: none;
}

.nav-menu :deep(.el-icon) {
  font-size: 17px;
}

.home-item {
  margin-bottom: 12px !important;
}

.sidebar-footer {
  padding: 14px 16px 18px;
  border-top: 1px solid rgba(255, 255, 255, 0.07);
}

.collapse-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  height: 34px;
  color: #8d9bb5;
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  cursor: pointer;
  font: inherit;
  font-size: 12px;
  transition: 0.18s;
}

.collapse-btn:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.06);
}

.sidebar-version {
  margin-top: 12px;
  color: #5f6d86;
  font-size: 10px;
  text-align: center;
  letter-spacing: 0.04em;
}

@media (max-width: 900px) {
  .app-sidebar {
    flex-basis: 76px;
    width: 76px;
  }

  .brand {
    justify-content: center;
    padding: 0;
  }

  .brand-copy,
  .sidebar-version,
  .collapse-btn span {
    display: none;
  }
}
</style>
