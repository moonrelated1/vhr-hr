import request from "@/utils/request.js";

let dictsPromise = null;

export function loadEmployees(params) {
    return request({
        url: '/api/employee/basic/list',
        method: 'get',
        params: params
    })
}

export function getEmployeeById(id) {
    return request({
        url: '/api/employee/basic/get/' + id,
        method: 'get'
    })
}

export function addEmployee(data) {
    return request({
        url: '/api/employee/basic/add',
        method: 'post',
        data: data
    })
}

export function updateEmployee(data) {
    return request({
        url: '/api/employee/basic/update',
        method: 'put',
        data: data
    })
}

export function deleteEmployeeById(id) {
    return request({
        url: '/api/employee/basic/delete/' + id,
        method: 'delete'
    })
}

/**
 * 字典只请求一次，多个页面共享同一个 Promise。
 */
export function loadDicts() {
    if (!dictsPromise) {
        dictsPromise = request({
            url: '/api/employee/basic/dicts',
            method: 'get'
        }).then(res => res.data).catch(err => {
            dictsPromise = null;
            return Promise.reject(err);
        });
    }
    return dictsPromise;
}
