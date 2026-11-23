package org.javaboy.vhr.employee.service;

import java.util.Collection;
import java.util.Map;

/**
 * 字典查询与 ID -> 名称翻译。
 */
public interface IDictService {

    /**
     * 返回前端下拉框所需的全部字典。
     */
    Map<String, Object> getAllDicts();

    Map<Integer, String> departmentNames();

    Map<Integer, String> positionNames();

    Map<Integer, String> joblevelNames();

    Map<Integer, String> nationNames();

    Map<Integer, String> politicNames();

    /**
     * 批量查询员工姓名。
     */
    Map<Integer, String> employeeNames(Collection<Integer> ids);
}
