/*在这个文件中我们对请求和响应进行封装*/
import axios from "axios";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'

const service = axios.create({
    timeout: 10000
})

// 请求拦截器：如果使用 JWT 等令牌登录，可以在这里统一添加令牌。
service.interceptors.request.use();
service.interceptors.response.use(success => {
    // 获取服务端返回的状态码，如果服务端没有设置状态码，默认就是 200。
    const code = success.data && success.data.status ? success.data.status : 200;
    if (code == 200) {
        if (success.data && success.data.message) {
            ElMessage.success(success.data.message)
        }
        // 返回服务端返回的 JSON（分页接口是裸 RespPageBean，没有 status）。
        return success.data;
    } else {
        const message = (success.data && success.data.message) || '请求失败';
        ElMessage.error(message)
        return Promise.reject(message);
    }
}, error => {
    if (error.response && error.response.status == 401) {
        router.replace('/');
    }
    // HTTP 状态码不是 200，会进入这里；优先展示后端 RespBean 中的 message。
    const data = error.response && error.response.data;
    const message = (data && (data.message || data.error)) || error.message || '网络请求失败';
    ElMessage.error(message);
    return Promise.reject(error);
})

export default service;
