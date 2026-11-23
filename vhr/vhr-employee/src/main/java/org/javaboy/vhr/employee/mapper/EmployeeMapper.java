package org.javaboy.vhr.employee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.javaboy.vhr.employee.entity.Employee;

/**
 * 员工档案 Mapper。
 */
public interface EmployeeMapper extends BaseMapper<Employee> {

    /**
     * empsalary 表也有员工外键，删除员工前需要一并检查。
     */
    @Select("select count(*) from empsalary where eid = #{eid}")
    long countEmpSalaryByEid(@Param("eid") Integer eid);
}
