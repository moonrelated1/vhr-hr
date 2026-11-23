package org.javaboy.vhr.employee.service;

import org.javaboy.vhr.employee.entity.Employee;
import org.javaboy.vhr.framework.entity.RespBean;
import org.javaboy.vhr.framework.entity.RespPageBean;

/**
 * 员工档案服务。
 */
public interface IEmployeeService {

    RespPageBean getEmployeesByPage(Integer page, Integer size, String name,
                                    Integer departmentId, Integer posId,
                                    Integer jobLevelId, String engageForm);

    Employee getEmployeeById(Integer id);

    RespBean addEmployee(Employee employee);

    RespBean updateEmployee(Employee employee);

    RespBean deleteEmployeeById(Integer id);
}
