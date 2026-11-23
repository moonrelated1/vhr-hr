import {createRouter, createWebHistory} from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from "@/views/LoginView.vue";
import DashboardView from "@/views/DashboardView.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    scrollBehavior: () => ({top: 0}),
    routes: [
        {
            path: '/',
            name: '登录',
            component: LoginView,
            meta: {title: '登录'},
            hidden: true
        },
        {
            path: '/home',
            name: '首页',
            component: HomeView,
            meta: {title: '工作台'},
            children: [
                {
                    path: '',
                    name: '工作台',
                    component: DashboardView,
                    meta: {title: '工作台'}
                }
            ]
        }
        // 兜底路由在动态菜单注册后由守卫添加，避免 F5 刷新子页面时
        // 未匹配路由先被重定向到工作台。
    ]
})

router.afterEach(to => {
    const title = to.meta && to.meta.title;
    document.title = title ? `${title} | 智汇 HRMS` : '智汇 HRMS';
});

export default router
