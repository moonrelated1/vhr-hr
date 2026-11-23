import request from "@/utils/request.js";

// ---------------------------------------------------------------- 奖惩

export function loadEcRecords(params) {
    return request({
        url: '/api/employee/basic/ec/list',
        method: 'get',
        params: params
    })
}

export function addEc(data) {
    return request({
        url: '/api/employee/basic/ec/add',
        method: 'post',
        data: data
    })
}

export function deleteEc(id) {
    return request({
        url: '/api/employee/basic/ec/delete/' + id,
        method: 'delete'
    })
}

// ---------------------------------------------------------------- 培训

export function loadTrainRecords(params) {
    return request({
        url: '/api/employee/basic/train/list',
        method: 'get',
        params: params
    })
}

export function addTrain(data) {
    return request({
        url: '/api/employee/basic/train/add',
        method: 'post',
        data: data
    })
}

export function deleteTrain(id) {
    return request({
        url: '/api/employee/basic/train/delete/' + id,
        method: 'delete'
    })
}

// ---------------------------------------------------------------- 调薪

export function loadSalaryRecords(params) {
    return request({
        url: '/api/employee/basic/salary/list',
        method: 'get',
        params: params
    })
}

export function addSalary(data) {
    return request({
        url: '/api/employee/basic/salary/add',
        method: 'post',
        data: data
    })
}

export function deleteSalary(id) {
    return request({
        url: '/api/employee/basic/salary/delete/' + id,
        method: 'delete'
    })
}

// ---------------------------------------------------------------- 调动

export function loadMoveRecords(params) {
    return request({
        url: '/api/employee/basic/remove/list',
        method: 'get',
        params: params
    })
}

export function addMove(data) {
    return request({
        url: '/api/employee/basic/remove/add',
        method: 'post',
        data: data
    })
}

export function deleteMove(id) {
    return request({
        url: '/api/employee/basic/remove/delete/' + id,
        method: 'delete'
    })
}
