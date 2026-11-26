import request from "@/utils/request.js";

let dictsPromise = null;

export function loadEmployees(params) {
    return request({
        url: '/api/employee/basic/list',
        method: 'get',
        params: params
    })
}

/**
 * 后端分页接口将 size 上限限制为 100。看板和导出需要全量员工时，
 * 先取第一页拿到 total，再并行拉取剩余页并合并，避免修改后端接口。
 */
export async function loadAllEmployees(params = {}) {
    const pageSize = 100;
    const first = await loadEmployees({...params, page: 1, size: pageSize});
    const rows = [...(first.data || [])];
    const total = first.total || 0;
    if (rows.length >= total) {
        return rows;
    }
    const pages = Math.ceil(total / pageSize);
    const requests = [];
    for (let page = 2; page <= pages; page++) {
        requests.push(loadEmployees({...params, page, size: pageSize}));
    }
    const rest = await Promise.all(requests);
    rest.forEach(result => {
        rows.push(...(result.data || []));
    });
    return rows;
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
