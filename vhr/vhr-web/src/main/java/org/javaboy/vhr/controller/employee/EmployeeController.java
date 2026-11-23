package org.javaboy.vhr.controller.employee;

import org.javaboy.vhr.employee.entity.Employee;
import org.javaboy.vhr.employee.service.IDictService;
import org.javaboy.vhr.employee.service.IEmployeeService;
import org.javaboy.vhr.framework.entity.RespBean;
import org.javaboy.vhr.framework.entity.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 员工档案接口。
 * URL 前缀必须落在菜单 7 的 /employee/basic/** 范围内。
 */
@RestController
@RequestMapping("/employee/basic")
public class EmployeeController {

    @Autowired
    IEmployeeService employeeService;
    @Autowired
    IDictService dictService;

    @GetMapping("/list")
    public RespPageBean getEmployeesByPage(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) Integer departmentId,
                                           @RequestParam(required = false) Integer posId,
                                           @RequestParam(required = false) Integer jobLevelId,
                                           @RequestParam(required = false) String engageForm) {
        return employeeService.getEmployeesByPage(page, size, name, departmentId, posId, jobLevelId, engageForm);
    }

    @GetMapping("/get/{id}")
    public RespBean getEmployeeById(@PathVariable Integer id) {
        Employee employee = employeeService.getEmployeeById(id);
        return employee == null ? RespBean.error("员工不存在") : RespBean.ok(null, employee);
    }

    @PostMapping("/add")
    public RespBean addEmployee(@RequestBody Employee employee) {
        return employeeService.addEmployee(employee);
    }

    @PutMapping("/update")
    public RespBean updateEmployee(@RequestBody Employee employee) {
        return employeeService.updateEmployee(employee);
    }

    @DeleteMapping("/delete/{id}")
    public RespBean deleteEmployeeById(@PathVariable Integer id) {
        return employeeService.deleteEmployeeById(id);
    }

    @GetMapping("/dicts")
    public RespBean getDicts() {
        return RespBean.ok(null, dictService.getAllDicts());
    }
}
