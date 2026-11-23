package org.javaboy.vhr.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.javaboy.vhr.employee.entity.Adjustsalary;
import org.javaboy.vhr.employee.entity.Employee;
import org.javaboy.vhr.employee.entity.Employeeremove;
import org.javaboy.vhr.employee.entity.Employeetrain;
import org.javaboy.vhr.employee.mapper.AdjustsalaryMapper;
import org.javaboy.vhr.employee.mapper.EmployeeMapper;
import org.javaboy.vhr.employee.mapper.EmployeeremoveMapper;
import org.javaboy.vhr.employee.mapper.EmployeetrainMapper;
import org.javaboy.vhr.employee.service.IDictService;
import org.javaboy.vhr.employee.service.IEmployeeRecordService;
import org.javaboy.vhr.framework.entity.RespBean;
import org.javaboy.vhr.system.entity.Employeeec;
import org.javaboy.vhr.system.mapper.DepartmentMapper;
import org.javaboy.vhr.system.mapper.EmployeeecMapper;
import org.javaboy.vhr.system.mapper.PositionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 员工下属记录服务实现。
 */
@Service
public class EmployeeRecordServiceImpl implements IEmployeeRecordService {

    private static final Set<Integer> EC_TYPES = Set.of(0, 1);

    @Autowired
    EmployeeMapper employeeMapper;
    @Autowired
    EmployeeecMapper employeeecMapper;
    @Autowired
    EmployeetrainMapper employeetrainMapper;
    @Autowired
    EmployeeremoveMapper employeeremoveMapper;
    @Autowired
    AdjustsalaryMapper adjustsalaryMapper;
    @Autowired
    DepartmentMapper departmentMapper;
    @Autowired
    PositionMapper positionMapper;
    @Autowired
    IDictService dictService;

    // ---------------------------------------------------------------- 奖惩

    @Override
    public List<Employeeec> getEcList(Integer eid, String name, Integer ecType) {
        List<Integer> eids = employeeIdsByName(name);
        if (eids != null && eids.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<Employeeec> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(eid != null, Employeeec::getEid, eid)
                .eq(ecType != null, Employeeec::getEcType, ecType)
                .in(eids != null, Employeeec::getEid, eids)
                .orderByDesc(Employeeec::getId);
        List<Employeeec> records = employeeecMapper.selectList(wrapper);
        fillEmpNames(records, Employeeec::getEid, Employeeec::setEmpName);
        return records;
    }

    @Override
    public RespBean addEc(Employeeec ec) {
        if (ec == null) {
            return RespBean.error("奖惩信息不能为空");
        }
        RespBean employeeError = checkEmployee(ec.getEid());
        if (employeeError != null) {
            return employeeError;
        }
        if (ec.getEcDate() == null) {
            return RespBean.error("奖罚日期不能为空");
        }
        if (!StringUtils.hasText(ec.getEcReason())) {
            return RespBean.error("奖罚原因不能为空");
        }
        ec.setEcReason(ec.getEcReason().trim());
        if (ec.getEcReason().length() > 255) {
            return RespBean.error("奖罚原因长度不能超过255个字符");
        }
        if (ec.getEcPoint() == null) {
            return RespBean.error("奖罚分不能为空");
        }
        if (ec.getEcType() == null || !EC_TYPES.contains(ec.getEcType())) {
            return RespBean.error("奖罚类别只能为奖或罚");
        }
        if (length(ec.getRemark()) > 255) {
            return RespBean.error("备注长度不能超过255个字符");
        }
        ec.setId(null);
        return employeeecMapper.insert(ec) == 1 ? RespBean.ok("添加成功") : RespBean.error("添加失败");
    }

    @Override
    public RespBean deleteEc(Integer id) {
        return deleteRecord(id, employeeecMapper::deleteById);
    }

    // ---------------------------------------------------------------- 培训

    @Override
    public List<Employeetrain> getTrainList(Integer eid, String name) {
        List<Integer> eids = employeeIdsByName(name);
        if (eids != null && eids.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<Employeetrain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(eid != null, Employeetrain::getEid, eid)
                .in(eids != null, Employeetrain::getEid, eids)
                .orderByDesc(Employeetrain::getId);
        List<Employeetrain> records = employeetrainMapper.selectList(wrapper);
        fillEmpNames(records, Employeetrain::getEid, Employeetrain::setEmpName);
        return records;
    }

    @Override
    public RespBean addTrain(Employeetrain train) {
        if (train == null) {
            return RespBean.error("培训信息不能为空");
        }
        RespBean employeeError = checkEmployee(train.getEid());
        if (employeeError != null) {
            return employeeError;
        }
        if (train.getTrainDate() == null) {
            return RespBean.error("培训日期不能为空");
        }
        if (!StringUtils.hasText(train.getTrainContent())) {
            return RespBean.error("培训内容不能为空");
        }
        train.setTrainContent(train.getTrainContent().trim());
        if (train.getTrainContent().length() > 255) {
            return RespBean.error("培训内容长度不能超过255个字符");
        }
        if (length(train.getRemark()) > 255) {
            return RespBean.error("备注长度不能超过255个字符");
        }
        train.setId(null);
        return employeetrainMapper.insert(train) == 1 ? RespBean.ok("添加成功") : RespBean.error("添加失败");
    }

    @Override
    public RespBean deleteTrain(Integer id) {
        return deleteRecord(id, employeetrainMapper::deleteById);
    }

    // ---------------------------------------------------------------- 调薪

    @Override
    public List<Adjustsalary> getSalaryList(Integer eid, String name) {
        List<Integer> eids = employeeIdsByName(name);
        if (eids != null && eids.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<Adjustsalary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(eid != null, Adjustsalary::getEid, eid)
                .in(eids != null, Adjustsalary::getEid, eids)
                .orderByDesc(Adjustsalary::getId);
        List<Adjustsalary> records = adjustsalaryMapper.selectList(wrapper);
        fillEmpNames(records, Adjustsalary::getEid, Adjustsalary::setEmpName);
        return records;
    }

    @Override
    public RespBean addSalary(Adjustsalary salary) {
        if (salary == null) {
            return RespBean.error("调薪信息不能为空");
        }
        RespBean employeeError = checkEmployee(salary.getEid());
        if (employeeError != null) {
            return employeeError;
        }
        if (salary.getAsDate() == null) {
            return RespBean.error("调薪日期不能为空");
        }
        if (salary.getAfterSalary() == null || salary.getAfterSalary() <= 0) {
            return RespBean.error("调薪后薪资必须大于0");
        }
        if (length(salary.getReason()) > 255) {
            return RespBean.error("调薪原因长度不能超过255个字符");
        }
        if (length(salary.getRemark()) > 255) {
            return RespBean.error("备注长度不能超过255个字符");
        }
        salary.setId(null);
        return adjustsalaryMapper.insert(salary) == 1 ? RespBean.ok("添加成功") : RespBean.error("添加失败");
    }

    @Override
    public RespBean deleteSalary(Integer id) {
        return deleteRecord(id, adjustsalaryMapper::deleteById);
    }

    // ---------------------------------------------------------------- 调动

    @Override
    public List<Employeeremove> getMoveList(Integer eid, String name) {
        List<Integer> eids = employeeIdsByName(name);
        if (eids != null && eids.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<Employeeremove> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(eid != null, Employeeremove::getEid, eid)
                .in(eids != null, Employeeremove::getEid, eids)
                .orderByDesc(Employeeremove::getId);
        List<Employeeremove> records = employeeremoveMapper.selectList(wrapper);
        fillEmpNames(records, Employeeremove::getEid, Employeeremove::setEmpName);
        Map<Integer, String> departments = dictService.departmentNames();
        Map<Integer, String> positions = dictService.positionNames();
        for (Employeeremove record : records) {
            record.setAfterDepName(departments.get(record.getAfterDepId()));
            record.setAfterJobName(positions.get(record.getAfterJobId()));
        }
        return records;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RespBean addMove(Employeeremove move) {
        if (move == null) {
            return RespBean.error("调动信息不能为空");
        }
        RespBean employeeError = checkEmployee(move.getEid());
        if (employeeError != null) {
            return employeeError;
        }
        if (move.getAfterDepId() == null || departmentMapper.selectById(move.getAfterDepId()) == null) {
            return RespBean.error("请选择有效的调动后部门");
        }
        if (move.getAfterJobId() == null || positionMapper.selectById(move.getAfterJobId()) == null) {
            return RespBean.error("请选择有效的调动后职位");
        }
        if (move.getRemoveDate() == null) {
            return RespBean.error("调动日期不能为空");
        }
        if (!StringUtils.hasText(move.getReason())) {
            return RespBean.error("调动原因不能为空");
        }
        move.setReason(move.getReason().trim());
        if (move.getReason().length() > 255) {
            return RespBean.error("调动原因长度不能超过255个字符");
        }
        if (length(move.getRemark()) > 255) {
            return RespBean.error("备注长度不能超过255个字符");
        }

        move.setId(null);
        if (employeeremoveMapper.insert(move) != 1) {
            throw new RuntimeException("保存调动记录失败");
        }
        Employee employee = employeeMapper.selectById(move.getEid());
        employee.setDepartmentId(move.getAfterDepId());
        employee.setPosId(move.getAfterJobId());
        if (employeeMapper.updateById(employee) != 1) {
            throw new RuntimeException("同步员工档案失败");
        }
        return RespBean.ok("调动成功");
    }

    @Override
    public RespBean deleteMove(Integer id) {
        // 按 FR-MV-03：只删除调动记录，不回滚员工档案
        return deleteRecord(id, employeeremoveMapper::deleteById);
    }

    // ---------------------------------------------------------------- 共用

    /**
     * 按员工姓名模糊查询出 eid 列表；name 为空返回 null（表示不限制）。
     */
    private List<Integer> employeeIdsByName(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return employeeMapper.selectList(new LambdaQueryWrapper<Employee>()
                        .select(Employee::getId)
                        .like(Employee::getName, name.trim()))
                .stream()
                .map(Employee::getId)
                .toList();
    }

    private RespBean checkEmployee(Integer eid) {
        if (eid == null) {
            return RespBean.error("请选择员工");
        }
        if (employeeMapper.selectById(eid) == null) {
            return RespBean.error("员工不存在");
        }
        return null;
    }

    private <T> void fillEmpNames(List<T> records, java.util.function.Function<T, Integer> eidGetter,
                                  java.util.function.BiConsumer<T, String> nameSetter) {
        if (records == null || records.isEmpty()) {
            return;
        }
        Map<Integer, String> names = dictService.employeeNames(
                records.stream().map(eidGetter).filter(Objects::nonNull).collect(Collectors.toSet()));
        for (T record : records) {
            nameSetter.accept(record, names.get(eidGetter.apply(record)));
        }
    }

    private RespBean deleteRecord(Integer id, java.util.function.Function<Integer, Integer> deleter) {
        if (id == null) {
            return RespBean.error("记录编号不能为空");
        }
        return deleter.apply(id) == 1 ? RespBean.ok("删除成功") : RespBean.error("记录不存在，删除失败");
    }

    private int length(String value) {
        return value == null ? 0 : value.length();
    }
}
