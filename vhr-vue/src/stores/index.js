import {ref, computed} from 'vue'
import {defineStore} from 'pinia'
import {loadMenus} from "@/api/menus.js";
import HomeView from "@/views/HomeView.vue";

const modules = import.meta.glob('@/views/**/*.vue');

// 本次交付聚焦员工管理模块；薪资/统计/系统管理尚未实现，不注册也不展示。
const VISIBLE_GROUP_NAMES = ['员工资料', '人事管理'];

export const useCounterStore = defineStore('counter', () => {
    const count = ref(0)
    const doubleCount = computed(() => count.value * 2)

    function increment() {
        count.value++
    }

    return {count, doubleCount, increment}
})

export const menusStore = defineStore('menus', {
    state: () => (
        {
            // 这个变量用来保存服务端返回的菜单数据。页面跳转时如果 menus 有值，
            // 说明是普通点击跳转；为空则可能是用户按 F5 刷新。
            menus: []
        }
    ),
    actions: {
        clearMenus() {
            this.menus = [];
        },
        initMenus() {
            return new Promise(resolve => {
                loadMenus().then(res => {
                    let roots = res.data || [];
                    // 兼容后端可能返回一个「所有」根节点的情况。
                    if (roots.length === 1 && roots[0].name === '所有' && Array.isArray(roots[0].children)) {
                        roots = roots[0].children;
                    }
                    const visible = roots.filter(menu => VISIBLE_GROUP_NAMES.includes(menu.name));
                    this.menus = visible;
                    resolve(formatMenus(visible));
                }).catch(() => {
                    resolve([]);
                })
            })
        },
    }
})

function formatMenus(menus) {
    let result = [];
    menus.forEach(menu => {
        let {path, name, children, component} = menu;
        if (children && children instanceof Array) {
            children = formatMenus(children);
        }
        let formatM = {
            path: path,
            // 菜单名可能重复（例如「员工资料」既是目录又是页面），
            // 路由名必须唯一，否则 Vue Router 会把同名路由顶掉。
            name: menu.id ? name + '_' + menu.id : name,
            meta: {title: name},
            children: children,
            component: loadView(component)
        }
        result.push(formatM);
    })
    return result;
}

function loadView(viewPath) {
    if (viewPath == '/src/views/HomeView.vue') {
        return HomeView;
    } else {
        return modules[viewPath];
    }
}
