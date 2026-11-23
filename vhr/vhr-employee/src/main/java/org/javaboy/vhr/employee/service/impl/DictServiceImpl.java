package org.javaboy.vhr.employee.service.impl;

import org.javaboy.vhr.employee.entity.Employee;
import org.javaboy.vhr.employee.entity.Nation;
import org.javaboy.vhr.employee.entity.Politicsstatus;
import org.javaboy.vhr.employee.mapper.EmployeeMapper;
import org.javaboy.vhr.employee.mapper.NationMapper;
import org.javaboy.vhr.employee.mapper.PoliticsstatusMapper;
import org.javaboy.vhr.employee.service.IDictService;
import org.javaboy.vhr.system.entity.Department;
import org.javaboy.vhr.system.entity.Joblevel;
import org.javaboy.vhr.system.entity.Position;
import org.javaboy.vhr.system.mapper.DepartmentMapper;
import org.javaboy.vhr.system.mapper.JoblevelMapper;
import org.javaboy.vhr.system.mapper.PositionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典实现：每次请求读一次字典表，再在内存中做 ID -> 名称映射。
 * 字典表很小（最多 56 行），不需要缓存。
 */
@Service
public class DictServiceImpl implements IDictService {

    @Autowired
    DepartmentMapper departmentMapper;
    @Autowired
    PositionMapper positionMapper;
    @Autowired
    JoblevelMapper joblevelMapper;
    @Autowired
    NationMapper nationMapper;
    @Autowired
    PoliticsstatusMapper politicsstatusMapper;
    @Autowired
    EmployeeMapper employeeMapper;

    @Override
    public Map<String, Object> getAllDicts() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("departments", departmentMapper.selectList(null));
        result.put("positions", positionMapper.selectList(null));
        result.put("joblevels", joblevelMapper.selectList(null));
        result.put("nations", nationMapper.selectList(null));
        result.put("politics", politicsstatusMapper.selectList(null));
        result.put("genders", List.of("男", "女"));
        result.put("wedlocks", List.of("已婚", "未婚", "离异"));
        result.put("engageForms", List.of("劳动合同", "劳务合同"));
        result.put("degrees", List.of("博士", "硕士", "本科", "大专", "高中", "初中", "小学", "其他"));
        result.put("workStates", List.of("在职", "离职"));
        return result;
    }

    @Override
    public Map<Integer, String> departmentNames() {
        Map<Integer, String> map = new HashMap<>();
        for (Department d : departmentMapper.selectList(null)) {
            map.put(d.getId(), d.getName());
        }
        return map;
    }

    @Override
    public Map<Integer, String> positionNames() {
        Map<Integer, String> map = new HashMap<>();
        for (Position p : positionMapper.selectList(null)) {
            map.put(p.getId(), p.getName());
        }
        return map;
    }

    @Override
    public Map<Integer, String> joblevelNames() {
        Map<Integer, String> map = new HashMap<>();
        for (Joblevel j : joblevelMapper.selectList(null)) {
            map.put(j.getId(), j.getName());
        }
        return map;
    }

    @Override
    public Map<Integer, String> nationNames() {
        Map<Integer, String> map = new HashMap<>();
        for (Nation n : nationMapper.selectList(null)) {
            map.put(n.getId(), n.getName());
        }
        return map;
    }

    @Override
    public Map<Integer, String> politicNames() {
        Map<Integer, String> map = new HashMap<>();
        for (Politicsstatus p : politicsstatusMapper.selectList(null)) {
            map.put(p.getId(), p.getName());
        }
        return map;
    }

    @Override
    public Map<Integer, String> employeeNames(Collection<Integer> ids) {
        Map<Integer, String> map = new HashMap<>();
        if (ids == null || ids.isEmpty()) {
            return map;
        }
        List<Employee> employees = employeeMapper.selectBatchIds(new ArrayList<>(ids));
        for (Employee e : employees) {
            map.put(e.getId(), e.getName());
        }
        return map;
    }
}
