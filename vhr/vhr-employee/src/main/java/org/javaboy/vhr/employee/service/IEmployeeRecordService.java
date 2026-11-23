package org.javaboy.vhr.employee.service;

import org.javaboy.vhr.employee.entity.Adjustsalary;
import org.javaboy.vhr.employee.entity.Employeeremove;
import org.javaboy.vhr.employee.entity.Employeetrain;
import org.javaboy.vhr.framework.entity.RespBean;
import org.javaboy.vhr.system.entity.Employeeec;

import java.util.List;

/**
 * 员工下属记录：奖惩、培训、调薪、调动。
 */
public interface IEmployeeRecordService {

    List<Employeeec> getEcList(Integer eid, String name, Integer ecType);

    RespBean addEc(Employeeec ec);

    RespBean deleteEc(Integer id);

    List<Employeetrain> getTrainList(Integer eid, String name);

    RespBean addTrain(Employeetrain train);

    RespBean deleteTrain(Integer id);

    List<Adjustsalary> getSalaryList(Integer eid, String name);

    RespBean addSalary(Adjustsalary salary);

    RespBean deleteSalary(Integer id);

    List<Employeeremove> getMoveList(Integer eid, String name);

    RespBean addMove(Employeeremove move);

    RespBean deleteMove(Integer id);
}
