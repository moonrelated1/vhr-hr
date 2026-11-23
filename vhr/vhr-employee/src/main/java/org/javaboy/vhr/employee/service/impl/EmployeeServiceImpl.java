package org.javaboy.vhr.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.javaboy.vhr.employee.entity.Adjustsalary;
import org.javaboy.vhr.employee.entity.Employee;
import org.javaboy.vhr.employee.mapper.AdjustsalaryMapper;
import org.javaboy.vhr.employee.mapper.EmployeeMapper;
import org.javaboy.vhr.employee.mapper.EmployeeremoveMapper;
import org.javaboy.vhr.employee.mapper.EmployeetrainMapper;
import org.javaboy.vhr.employee.service.IDictService;
import org.javaboy.vhr.employee.service.IEmployeeService;
import org.javaboy.vhr.employee.entity.Employeeremove;
import org.javaboy.vhr.employee.entity.Employeetrain;
import org.javaboy.vhr.framework.entity.RespBean;
import org.javaboy.vhr.framework.entity.RespPageBean;
import org.javaboy.vhr.system.entity.Employeeec;
import org.javaboy.vhr.system.mapper.EmployeeecMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 员工档案服务实现。
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {

    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^\\d{17}[\\dXx]$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{11}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern WORK_ID_PATTERN = Pattern.compile("^\\d{8}$");
    private static final Set<String> GENDERS = Set.of("男", "女");
    private static final Set<String> WORK_STATES = Set.of("在职", "离职");
    private static final Set<String> ENGAGE_FORMS = Set.of("劳动合同", "劳务合同");

    @Autowired
    IDictService dictService;
    @Autowired
    EmployeeecMapper employeeecMapper;
    @Autowired
    EmployeetrainMapper employeetrainMapper;
    @Autowired
    EmployeeremoveMapper employeeremoveMapper;
    @Autowired
    AdjustsalaryMapper adjustsalaryMapper;

    @Override
    public RespPageBean getEmployeesByPage(Integer page, Integer size, String name,
                                           Integer departmentId, Integer posId,
                                           Integer jobLevelId, String engageForm) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? 10 : Math.min(size, 100);
        String searchName = name == null ? null : name.trim();

        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(searchName), Employee::getName, searchName)
                .eq(departmentId != null, Employee::getDepartmentId, departmentId)
                .eq(posId != null, Employee::getPosId, posId)
                .eq(jobLevelId != null, Employee::getJobLevelId, jobLevelId)
                .eq(StringUtils.hasText(engageForm), Employee::getEngageForm, engageForm)
                .orderByDesc(Employee::getId);

        Page<Employee> result = page(new Page<>(currentPage, pageSize), wrapper);
        enrich(result.getRecords());
        return new RespPageBean(result.getTotal(), result.getRecords());
    }

    @Override
    public Employee getEmployeeById(Integer id) {
        if (id == null) {
            return null;
        }
        Employee employee = getById(id);
        if (employee != null) {
            enrich(List.of(employee));
        }
        return employee;
    }

    @Override
    public RespBean addEmployee(Employee employee) {
        if (employee == null) {
            return RespBean.error("员工信息不能为空");
        }
        employee.setId(null);
        if (!StringUtils.hasText(employee.getWorkState())) {
            employee.setWorkState("在职");
        }
        String error = validate(employee, null);
        if (error != null) {
            return RespBean.error(error);
        }
        return save(employee) ? RespBean.ok("添加成功") : RespBean.error("添加失败");
    }

    @Override
    public RespBean updateEmployee(Employee employee) {
        if (employee == null || employee.getId() == null) {
            return RespBean.error("员工编号不能为空");
        }
        Employee old = getById(employee.getId());
        if (old == null) {
            return RespBean.error("员工不存在");
        }
        String error = validate(employee, old);
        if (error != null) {
            return RespBean.error(error);
        }
        return updateById(employee) ? RespBean.ok("修改成功") : RespBean.error("修改失败");
    }

    @Override
    public RespBean deleteEmployeeById(Integer id) {
        Employee employee = id == null ? null : getById(id);
        if (employee == null) {
            return RespBean.error("员工不存在");
        }
        long childCount = employeeecMapper.selectCount(
                new LambdaQueryWrapper<Employeeec>().eq(Employeeec::getEid, id));
        childCount += employeetrainMapper.selectCount(
                new LambdaQueryWrapper<Employeetrain>().eq(Employeetrain::getEid, id));
        childCount += employeeremoveMapper.selectCount(
                new LambdaQueryWrapper<Employeeremove>().eq(Employeeremove::getEid, id));
        childCount += adjustsalaryMapper.selectCount(
                new LambdaQueryWrapper<Adjustsalary>().eq(Adjustsalary::getEid, id));
        childCount += baseMapper.countEmpSalaryByEid(id);
        if (childCount > 0) {
            return RespBean.error("该员工【" + employee.getName() + "】存在关联记录，请先处理该员工的关联记录");
        }
        return removeById(id) ? RespBean.ok("删除成功") : RespBean.error("删除失败");
    }

    /**
     * 回填部门/职位/职称/民族/政治面貌名称。
     */
    private void enrich(List<Employee> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        Map<Integer, String> departments = dictService.departmentNames();
        Map<Integer, String> positions = dictService.positionNames();
        Map<Integer, String> joblevels = dictService.joblevelNames();
        Map<Integer, String> nations = dictService.nationNames();
        Map<Integer, String> politics = dictService.politicNames();
        for (Employee e : records) {
            e.setDepartmentName(departments.get(e.getDepartmentId()));
            e.setPosName(positions.get(e.getPosId()));
            e.setJobLevelName(joblevels.get(e.getJobLevelId()));
            e.setNationName(nations.get(e.getNationId()));
            e.setPoliticName(politics.get(e.getPoliticId()));
        }
    }

    /**
     * @param old 修改时的数据库旧值；新增时为 null
     * @return 错误提示；null 表示校验通过
     */
    private String validate(Employee employee, Employee old) {
        if (!StringUtils.hasText(employee.getName())) {
            return "姓名不能为空";
        }
        employee.setName(employee.getName().trim());
        if (employee.getName().length() > 10) {
            return "姓名长度不能超过10个字符";
        }
        if (!StringUtils.hasText(employee.getGender()) || !GENDERS.contains(employee.getGender())) {
            return "性别只能为男或女";
        }
        if (employee.getBirthday() == null) {
            return "出生日期不能为空";
        }
        if (!StringUtils.hasText(employee.getIdCard())) {
            return "身份证号不能为空";
        }
        employee.setIdCard(employee.getIdCard().trim());
        boolean idCardChanged = old == null || !Objects.equals(old.getIdCard(), employee.getIdCard());
        if (idCardChanged && !ID_CARD_PATTERN.matcher(employee.getIdCard()).matches()) {
            return "身份证号必须为18位且格式正确";
        }
        if (employee.getDepartmentId() == null) {
            return "所属部门不能为空";
        }
        if (employee.getJobLevelId() == null) {
            return "职称不能为空";
        }
        if (employee.getPosId() == null) {
            return "职位不能为空";
        }
        if (!StringUtils.hasText(employee.getEngageForm()) || !ENGAGE_FORMS.contains(employee.getEngageForm())) {
            return "聘用形式只能为劳动合同或劳务合同";
        }
        if (employee.getBeginDate() == null) {
            return "入职日期不能为空";
        }
        if (employee.getBeginDate().isAfter(LocalDate.now())) {
            return "入职日期不能晚于今天";
        }
        employee.setWorkState(employee.getWorkState() == null ? null : employee.getWorkState().trim());
        if (!StringUtils.hasText(employee.getWorkState()) || !WORK_STATES.contains(employee.getWorkState())) {
            return "在职状态只能为在职或离职";
        }
        if (!StringUtils.hasText(employee.getWorkId())) {
            return "工号不能为空";
        }
        employee.setWorkId(employee.getWorkId().trim());
        boolean workIdChanged = old == null || !Objects.equals(old.getWorkId(), employee.getWorkId());
        if (workIdChanged) {
            if (!WORK_ID_PATTERN.matcher(employee.getWorkId()).matches()) {
                return "工号必须为8位数字";
            }
            LambdaQueryWrapper<Employee> workIdQuery = new LambdaQueryWrapper<Employee>()
                    .eq(Employee::getWorkId, employee.getWorkId());
            if (old != null) {
                workIdQuery.ne(Employee::getId, old.getId());
            }
            if (count(workIdQuery) > 0) {
                return "该工号已存在";
            }
        }

        if (employee.getPhone() != null) {
            employee.setPhone(employee.getPhone().trim());
        }
        boolean phoneChanged = old == null || !Objects.equals(old.getPhone(), employee.getPhone());
        if (phoneChanged && StringUtils.hasText(employee.getPhone())
                && !PHONE_PATTERN.matcher(employee.getPhone()).matches()) {
            return "手机号必须为11位数字";
        }

        if (employee.getEmail() != null) {
            employee.setEmail(employee.getEmail().trim());
        }
        boolean emailChanged = old == null || !Objects.equals(old.getEmail(), employee.getEmail());
        if (emailChanged && StringUtils.hasText(employee.getEmail())) {
            if (employee.getEmail().length() > 20) {
                return "邮箱长度不能超过20个字符";
            }
            if (!EMAIL_PATTERN.matcher(employee.getEmail()).matches()) {
                return "邮箱格式不正确";
            }
        }
        if (len(employee.getNativePlace()) > 20) {
            return "籍贯长度不能超过20个字符";
        }
        if (len(employee.getAddress()) > 64) {
            return "联系地址长度不能超过64个字符";
        }
        if (len(employee.getSpecialty()) > 32) {
            return "所属专业长度不能超过32个字符";
        }
        if (len(employee.getSchool()) > 32) {
            return "毕业院校长度不能超过32个字符";
        }
        if (employee.getBeginContract() != null && employee.getEndContract() != null) {
            boolean contractChanged = old == null
                    || !Objects.equals(old.getBeginContract(), employee.getBeginContract())
                    || !Objects.equals(old.getEndContract(), employee.getEndContract());
            if (contractChanged && employee.getEndContract().isBefore(employee.getBeginContract())) {
                return "合同终止日期不能早于起始日期";
            }
        }
        return null;
    }

    private int len(String value) {
        return value == null ? 0 : value.length();
    }
}
