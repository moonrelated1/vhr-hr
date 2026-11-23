package org.javaboy.vhr.controller.employee;

import org.javaboy.vhr.employee.entity.Adjustsalary;
import org.javaboy.vhr.employee.entity.Employeeremove;
import org.javaboy.vhr.employee.entity.Employeetrain;
import org.javaboy.vhr.employee.service.IEmployeeRecordService;
import org.javaboy.vhr.framework.entity.RespBean;
import org.javaboy.vhr.system.entity.Employeeec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 员工下属记录接口：奖惩、培训、调薪、调动。
 * 与员工接口共用 /employee/basic 前缀，统一由菜单 7 鉴权。
 */
@RestController
@RequestMapping("/employee/basic")
public class EmployeeRecordController {

    @Autowired
    IEmployeeRecordService recordService;

    // ---------------------------------------------------------------- 奖惩

    @GetMapping("/ec/list")
    public RespBean getEcList(@RequestParam(required = false) Integer eid,
                              @RequestParam(required = false) String name,
                              @RequestParam(required = false) Integer ecType) {
        return RespBean.ok(null, recordService.getEcList(eid, name, ecType));
    }

    @PostMapping("/ec/add")
    public RespBean addEc(@RequestBody Employeeec ec) {
        return recordService.addEc(ec);
    }

    @DeleteMapping("/ec/delete/{id}")
    public RespBean deleteEc(@PathVariable Integer id) {
        return recordService.deleteEc(id);
    }

    // ---------------------------------------------------------------- 培训

    @GetMapping("/train/list")
    public RespBean getTrainList(@RequestParam(required = false) Integer eid,
                                 @RequestParam(required = false) String name) {
        return RespBean.ok(null, recordService.getTrainList(eid, name));
    }

    @PostMapping("/train/add")
    public RespBean addTrain(@RequestBody Employeetrain train) {
        return recordService.addTrain(train);
    }

    @DeleteMapping("/train/delete/{id}")
    public RespBean deleteTrain(@PathVariable Integer id) {
        return recordService.deleteTrain(id);
    }

    // ---------------------------------------------------------------- 调薪

    @GetMapping("/salary/list")
    public RespBean getSalaryList(@RequestParam(required = false) Integer eid,
                                  @RequestParam(required = false) String name) {
        return RespBean.ok(null, recordService.getSalaryList(eid, name));
    }

    @PostMapping("/salary/add")
    public RespBean addSalary(@RequestBody Adjustsalary salary) {
        return recordService.addSalary(salary);
    }

    @DeleteMapping("/salary/delete/{id}")
    public RespBean deleteSalary(@PathVariable Integer id) {
        return recordService.deleteSalary(id);
    }

    // ---------------------------------------------------------------- 调动

    @GetMapping("/remove/list")
    public RespBean getMoveList(@RequestParam(required = false) Integer eid,
                                @RequestParam(required = false) String name) {
        return RespBean.ok(null, recordService.getMoveList(eid, name));
    }

    @PostMapping("/remove/add")
    public RespBean addMove(@RequestBody Employeeremove move) {
        return recordService.addMove(move);
    }

    @DeleteMapping("/remove/delete/{id}")
    public RespBean deleteMove(@PathVariable Integer id) {
        return recordService.deleteMove(id);
    }
}
