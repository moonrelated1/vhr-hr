import {ElMessage} from 'element-plus';

const columns = [
  ['工号', row => row.workId],
  ['姓名', row => row.name],
  ['性别', row => row.gender],
  ['出生日期', row => row.birthday],
  ['身份证号', row => row.idCard],
  ['婚姻状况', row => row.wedlock],
  ['民族', row => row.nationName],
  ['籍贯', row => row.nativePlace],
  ['政治面貌', row => row.politicName],
  ['邮箱', row => row.email],
  ['手机号', row => row.phone],
  ['联系地址', row => row.address],
  ['所属部门', row => row.departmentName],
  ['职位', row => row.posName],
  ['职称', row => row.jobLevelName],
  ['聘用形式', row => row.engageForm],
  ['最高学历', row => row.tipTopDegree],
  ['专业', row => row.specialty],
  ['毕业院校', row => row.school],
  ['入职日期', row => row.beginDate],
  ['在职状态', row => row.workState],
  ['合同期限', row => row.contractTerm],
  ['转正日期', row => row.conversionTime],
  ['离职日期', row => row.notWorkDate],
  ['合同起始', row => row.beginContract],
  ['合同终止', row => row.endContract],
  ['工作年限', row => row.workAge]
];

function buildRows(employees) {
  return employees.map(employee => {
    const row = {};
    columns.forEach(([label, getter]) => {
      const value = getter(employee);
      row[label] = value === null || value === undefined ? '' : value;
    });
    return row;
  });
}

/**
 * 将当前筛选出的员工导出为 Excel。
 * 动态加载 xlsx，避免未使用导出功能时拖慢首屏。
 */
export async function exportEmployeesToExcel(employees, filenamePrefix = '员工档案') {
  if (!employees || employees.length === 0) {
    ElMessage.warning('当前筛选条件下没有可导出的员工数据');
    return false;
  }

  const module = await import('xlsx');
  const XLSX = module.default || module;
  const worksheet = XLSX.utils.json_to_sheet(
      buildRows(employees),
      {header: columns.map(([label]) => label)}
  );

  worksheet['!cols'] = columns.map(([label]) => ({
    wch: Math.min(28, Math.max(10, label.length * 2 + 4))
  }));

  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, worksheet, '员工档案');

  const now = new Date();
  const date = [
    now.getFullYear(),
    String(now.getMonth() + 1).padStart(2, '0'),
    String(now.getDate()).padStart(2, '0')
  ].join('-');

  XLSX.writeFile(workbook, `${filenamePrefix}_${date}.xlsx`);
  ElMessage.success(`已导出 ${employees.length} 条员工数据`);
  return true;
}
