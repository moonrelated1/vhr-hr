<template>
  <div class="app-shell" :class="{'is-collapsed': collapsed}">
    <AppSidebar :menus="menus" :collapsed="collapsed" @toggle="toggleSidebar"/>
    <div class="app-body">
      <AppHeader :collapsed="collapsed" :user="hr" @toggle="toggleSidebar" @command="handleCommand"/>
      <main class="app-main">
        <RouterView v-slot="{Component}">
          <Transition name="page" mode="out-in">
            <component :is="Component"/>
          </Transition>
        </RouterView>
      </main>
    </div>
  </div>
</template>

<script setup>
import {computed, reactive, ref} from 'vue';
import {useRouter, RouterView} from 'vue-router';
import {ElMessage, ElMessageBox} from 'element-plus';
import AppSidebar from '@/components/layout/AppSidebar.vue';
import AppHeader from '@/components/layout/AppHeader.vue';
import {logout} from '@/api/login.js';
import {menusStore} from '@/stores/index.js';

const router = useRouter();
const mStore = menusStore();
const collapsed = ref(false);

const storedUser = window.sessionStorage.getItem('hr');
const hr = reactive(storedUser ? JSON.parse(storedUser) : {});
const menus = computed(() => mStore.menus || []);

function toggleSidebar() {
  collapsed.value = !collapsed.value;
}

function handleCommand(command) {
  if (command === 'logout') {
    confirmLogout();
  } else if (command === 'profile') {
    ElMessage.info('个人信息维护将在后续版本开放');
  }
}

async function confirmLogout() {
  try {
    await ElMessageBox.confirm('此操作将注销登录，是否继续?', '提示', {
      confirmButtonText: '确认退出',
      cancelButtonText: '取消',
      type: 'warning'
    });
  } catch {
    return;
  }
  try {
    await logout();
  } catch {
    // 即使服务端注销失败，也清理本地登录态
  }
  window.sessionStorage.removeItem('hr');
  mStore.clearMenus();
  router.replace('/');
}
</script>

<style scoped>
.app-shell {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  background: #f4f6fb;
}

.app-body {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
}

.app-main {
  flex: 1;
  min-height: 0;
  padding: 24px 26px 32px;
  overflow: auto;
  background:
      radial-gradient(circle at 88% 0%, rgba(79, 110, 247, 0.055), transparent 28%),
      #f4f6fb;
}

@media (max-width: 900px) {
  .app-main {
    padding: 18px 16px 26px;
  }
}
</style>
