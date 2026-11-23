// 这个就是这个前端工程的入口文件，类似我们 Java 中的 main 方法
import {createApp} from 'vue'
import {createPinia} from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './styles/theme.css'

import App from './App.vue'
import router from './router'
import {menusStore} from "@/stores/index.js";
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

// 将 App 组件展示在 index.html 中一个 id 为 app 的 div 中
// 即项目启动成功之后，看到的内容都是 App 中的内容
const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus, {locale: zhCn})

const mStore = menusStore();

/**
 * 路由导航守卫：监听页面之间的跳转。
 * from 表示原页面，to 表示目标页面。
 */
router.beforeEach((to, from, next) => {
    if (to.path == '/') {
        next();
        return;
    }
    if (window.sessionStorage.getItem("hr")) {
        // 已登录：内存中有菜单说明是普通跳转；否则是 F5 刷新，需要先恢复菜单和动态路由。
        if (mStore.menus && mStore.menus.length != 0) {
            next();
        } else {
            mStore.initMenus().then(res => {
                res.forEach(r => {
                    router.addRoute(r);
                })
                // 动态路由注册完成后才添加兜底路由：已存在的页面正常匹配，
                // 真正不存在的地址再回到工作台。
                if (!router.hasRoute('NotFound')) {
                    router.addRoute({
                        path: '/:pathMatch(.*)*',
                        name: 'NotFound',
                        redirect: '/home'
                    });
                }
                next({...to});
            });
        }
    } else {
        next({path: '/', query: {redirect: to.path}});
    }
})

app.mount('#app')
