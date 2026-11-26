-- =====================================================================
-- 智汇 HRMS v2 演示数据脚本
-- 适用数据库：vhr2024
-- 作用：
--   1. 美化 employee 表 617 条员工数据（姓名/工号/联系方式/部门/日期等）；
--   2. 为部分员工补充奖惩、培训、调动、调薪四类人事记录；
--   3. 脚本可重复执行，记录以 remark = '演示数据' 作为清理标记。
-- 注意：
--   - 不修改表结构，不修改 hr / menu / menu_role；
--   - 执行前请先备份数据库；
--   - 仅用于课程演示。
-- =====================================================================

SET NAMES utf8mb4;
SET SQL_SAFE_UPDATES = 0;
USE vhr2024;

START TRANSACTION;

-- ---------------------------------------------------------------- 字典名称规范化
-- 原始演示库里的职级/职位字典带有「教授」「中级工程师66」等占位值，
-- 统一改为企业中常见的职级名称，便于演示与导出。
UPDATE joblevel SET name = '技术专家'   WHERE id = 9  AND name = '教授';
UPDATE joblevel SET name = '高级专家'   WHERE id = 10 AND name = '副教授';
UPDATE joblevel SET name = '助理工程师' WHERE id = 12 AND name = '助教';
UPDATE joblevel SET name = '工程师'     WHERE id = 13 AND name = '讲师';
UPDATE joblevel SET name = '中级工程师' WHERE id = 15 AND name = '中级工程师66';
UPDATE joblevel SET name = '资深专家'   WHERE id = 17 AND name = '骨灰级工程师';

UPDATE position SET name = '综合管理总监' WHERE id = 30 AND name = '运营总监';
UPDATE position SET enabled = 1 WHERE id = 33 AND name = '研发工程师';

-- ---------------------------------------------------------------- 员工主表
UPDATE employee e
JOIN (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM employee
) t ON e.id = t.id
SET
    e.name = CONCAT(
        ELT(MOD(t.rn - 1, 26) + 1,
            '王', '李', '张', '刘', '陈', '杨', '赵', '黄', '周', '吴', '徐', '孙', '马',
            '朱', '胡', '郭', '何', '高', '林', '罗', '郑', '梁', '谢', '宋', '唐', '许'),
        ELT(MOD(FLOOR((t.rn - 1) / 26) + MOD(t.rn - 1, 26), 24) + 1,
            '伟', '强', '磊', '军', '洋', '勇', '杰', '涛', '明', '超', '宇轩', '浩然',
            '静', '敏', '丽', '娜', '芳', '燕', '婷', '雪', '欣怡', '雨桐', '思涵', '佳琪')
    ),
    e.gender = IF(MOD(FLOOR((t.rn - 1) / 26) + MOD(t.rn - 1, 26), 24) < 12, '男', '女'),

    -- 入职日期：2015 ~ 2025，人数最多的 3 个 id 落在当月，保证「本月新增」有数据
    e.begin_date = CASE t.id
        WHEN 1941 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(2, DAYOFMONTH(CURDATE()) - 1) DAY)
        WHEN 1940 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(9, DAYOFMONTH(CURDATE()) - 1) DAY)
        WHEN 1939 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(16, DAYOFMONTH(CURDATE()) - 1) DAY)
        ELSE DATE_ADD('2015-01-01', INTERVAL MOD(t.rn * 47, 4300) DAY)
    END,

    -- 出生日期：比入职日期早 22 ~ 40 年，符合正常求学与工作经历
    e.birthday = DATE_SUB(
        DATE_SUB(
            CASE t.id
                WHEN 1941 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(2, DAYOFMONTH(CURDATE()) - 1) DAY)
                WHEN 1940 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(9, DAYOFMONTH(CURDATE()) - 1) DAY)
                WHEN 1939 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(16, DAYOFMONTH(CURDATE()) - 1) DAY)
                ELSE DATE_ADD('2015-01-01', INTERVAL MOD(t.rn * 47, 4300) DAY)
            END,
            INTERVAL (22 + MOD(t.rn, 18)) YEAR
        ),
        INTERVAL MOD(t.rn * 13, 365) DAY
    ),

    e.id_card = CONCAT(
        ELT(MOD(t.rn, 10) + 1,
            '110105', '130102', '130104', '120101', '140105',
            '610113', '610102', '420106', '420111', '310104'),
        DATE_FORMAT(
            DATE_SUB(
                DATE_SUB(
                    CASE t.id
                        WHEN 1941 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(2, DAYOFMONTH(CURDATE()) - 1) DAY)
                        WHEN 1940 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(9, DAYOFMONTH(CURDATE()) - 1) DAY)
                        WHEN 1939 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(16, DAYOFMONTH(CURDATE()) - 1) DAY)
                        ELSE DATE_ADD('2015-01-01', INTERVAL MOD(t.rn * 47, 4300) DAY)
                    END,
                    INTERVAL (22 + MOD(t.rn, 18)) YEAR
                ),
                INTERVAL MOD(t.rn * 13, 365) DAY
            ),
            '%Y%m%d'
        ),
        LPAD(MOD(t.rn * 7919, 1000), 3, '0'),
        'X'
    ),

    e.wedlock = CASE
        WHEN MOD(t.rn, 20) = 0 THEN '离异'
        WHEN MOD(t.rn, 3) <> 0 THEN '已婚'
        ELSE '未婚'
    END,
    e.nation_id = IF(MOD(t.rn, 7) = 0,
                     ELT(MOD(t.rn, 6) + 2, 2, 3, 4, 5, 6, 11),
                     1),
    e.native_place = ELT(MOD(t.rn, 20) + 1,
                         '河北石家庄', '河北保定', '河北唐山', '北京', '天津',
                         '山西太原', '山东济南', '河南郑州', '陕西西安', '湖北武汉',
                         '湖南长沙', '江苏南京', '浙江杭州', '安徽合肥', '四川成都',
                         '重庆', '广东广州', '福建福州', '辽宁沈阳', '吉林长春'),
    e.politic_id = CASE
        WHEN MOD(t.rn, 10) = 0 THEN 1
        WHEN MOD(t.rn, 3) = 0 THEN 3
        ELSE 13
    END,
    e.email = CONCAT('zh', LPAD(t.rn, 4, '0'), '@zhihui.cn'),
    e.phone = CONCAT('139', LPAD(t.rn, 8, '0')),
    e.address = CONCAT(
        ELT(MOD(t.rn, 20) + 1,
            '河北石家庄', '河北保定', '河北唐山', '北京', '天津',
            '山西太原', '山东济南', '河南郑州', '陕西西安', '湖北武汉',
            '湖南长沙', '江苏南京', '浙江杭州', '安徽合肥', '四川成都',
            '重庆', '广东广州', '福建福州', '辽宁沈阳', '吉林长春'),
        ELT(MOD(t.rn, 12) + 1,
            '长安区', '桥西区', '新华区', '朝阳区', '海淀区', '和平区',
            '历下区', '金水区', '雁塔区', '江汉区', '岳麓区', '玄武区'),
        ELT(MOD(t.rn, 6) + 1, '建设', '和平', '中山', '人民', '解放', '胜利'),
        '路', MOD(t.rn * 13, 200) + 1, '号'
    ),

    e.department_id = ELT(MOD(t.rn, 18) + 1,
                          91, 91, 91, 92, 92, 93, 94, 78, 78,
                          81, 81, 82, 85, 86, 87, 89, 8, 5),
    e.job_level_id = ELT(MOD(t.rn, 10) + 1, 14, 14, 14, 15, 15, 15, 15, 16, 16, 17),
    e.engage_form = IF(MOD(t.rn, 9) = 0, '劳务合同', '劳动合同'),
    e.tip_top_degree = CASE
        WHEN MOD(t.rn, 20) = 0 THEN '博士'
        WHEN MOD(t.rn, 20) <= 6 THEN '硕士'
        WHEN MOD(t.rn, 20) <= 15 THEN '本科'
        WHEN MOD(t.rn, 20) <= 17 THEN '大专'
        ELSE '高中'
    END,
    e.specialty = ELT(MOD(t.rn, 12) + 1,
                       '计算机科学与技术', '软件工程', '信息管理与信息系统', '市场营销',
                       '工商管理', '财务管理', '人力资源管理', '汉语言文学',
                       '电子信息工程', '网络工程', '统计学', '电子商务'),
    e.school = ELT(MOD(t.rn, 18) + 1,
                   '河北科技大学', '河北经贸大学', '河北师范大学', '燕山大学', '河北大学',
                   '北京理工大学', '北京邮电大学', '天津大学', '南开大学',
                   '西安电子科技大学', '西北工业大学', '华中科技大学', '武汉大学',
                   '四川大学', '电子科技大学', '南京大学', '浙江大学', '山东大学'),

    e.work_state = IF(t.id >= 1939, '在职', IF(MOD(t.rn, 20) = 0, '离职', '在职')),
    e.work_id = CONCAT('20', LPAD(t.id, 6, '0')),
    e.contract_term = ELT(MOD(t.rn, 5) + 1, 1, 2, 3, 3.5, 5),
    e.not_work_date = IF(
        t.id >= 1939 OR MOD(t.rn, 20) <> 0,
        NULL,
        LEAST(CURDATE(), DATE_ADD(
            CASE t.id
                WHEN 1941 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(2, DAYOFMONTH(CURDATE()) - 1) DAY)
                WHEN 1940 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(9, DAYOFMONTH(CURDATE()) - 1) DAY)
                WHEN 1939 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(16, DAYOFMONTH(CURDATE()) - 1) DAY)
                ELSE DATE_ADD('2015-01-01', INTERVAL MOD(t.rn * 47, 4300) DAY)
            END,
            INTERVAL 120 + MOD(t.rn, 500) DAY
        ))
    ),
    e.conversion_time = DATE_ADD(
        CASE t.id
            WHEN 1941 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(2, DAYOFMONTH(CURDATE()) - 1) DAY)
            WHEN 1940 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(9, DAYOFMONTH(CURDATE()) - 1) DAY)
            WHEN 1939 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(16, DAYOFMONTH(CURDATE()) - 1) DAY)
            ELSE DATE_ADD('2015-01-01', INTERVAL MOD(t.rn * 47, 4300) DAY)
        END,
        INTERVAL 3 MONTH
    ),
    e.begin_contract = CASE t.id
        WHEN 1941 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(2, DAYOFMONTH(CURDATE()) - 1) DAY)
        WHEN 1940 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(9, DAYOFMONTH(CURDATE()) - 1) DAY)
        WHEN 1939 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(16, DAYOFMONTH(CURDATE()) - 1) DAY)
        ELSE DATE_ADD('2015-01-01', INTERVAL MOD(t.rn * 47, 4300) DAY)
    END,
    e.end_contract = DATE_ADD(
        CASE t.id
            WHEN 1941 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(2, DAYOFMONTH(CURDATE()) - 1) DAY)
            WHEN 1940 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(9, DAYOFMONTH(CURDATE()) - 1) DAY)
            WHEN 1939 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(16, DAYOFMONTH(CURDATE()) - 1) DAY)
            ELSE DATE_ADD('2015-01-01', INTERVAL MOD(t.rn * 47, 4300) DAY)
        END,
        INTERVAL FLOOR(ELT(MOD(t.rn, 5) + 1, 1, 2, 3, 3.5, 5) * 365) DAY
    ),
    e.work_age = TIMESTAMPDIFF(
        YEAR,
        CASE t.id
            WHEN 1941 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(2, DAYOFMONTH(CURDATE()) - 1) DAY)
            WHEN 1940 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(9, DAYOFMONTH(CURDATE()) - 1) DAY)
            WHEN 1939 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(16, DAYOFMONTH(CURDATE()) - 1) DAY)
            ELSE DATE_ADD('2015-01-01', INTERVAL MOD(t.rn * 47, 4300) DAY)
        END,
        IF(
            t.id >= 1939 OR MOD(t.rn, 20) <> 0,
            CURDATE(),
            LEAST(CURDATE(), DATE_ADD(
                CASE t.id
                    WHEN 1941 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(2, DAYOFMONTH(CURDATE()) - 1) DAY)
                    WHEN 1940 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(9, DAYOFMONTH(CURDATE()) - 1) DAY)
                    WHEN 1939 THEN DATE_SUB(CURDATE(), INTERVAL LEAST(16, DAYOFMONTH(CURDATE()) - 1) DAY)
                    ELSE DATE_ADD('2015-01-01', INTERVAL MOD(t.rn * 47, 4300) DAY)
                END,
                INTERVAL 120 + MOD(t.rn, 500) DAY
            ))
        )
    );

-- 职位与部门保持业务一致
UPDATE employee e
SET e.pos_id = CASE
    WHEN e.department_id = 91 THEN
        IF(MOD(e.id, 8) = 0, 29, IF(MOD(e.id, 4) = 0, 36, 33))
    WHEN e.department_id IN (92, 93, 94) THEN
        IF(MOD(e.id, 6) = 0, 29, IF(MOD(e.id, 3) = 0, 36, 34))
    WHEN e.department_id IN (78, 81, 82, 85, 86, 87, 89) THEN
        IF(MOD(e.id, 5) = 0, 30, 31)
    WHEN e.department_id IN (5, 8) THEN
        IF(MOD(e.id, 6) = 0, 29, 30)
    ELSE 34
END;

-- 职级与岗位匹配：管理岗为专家序列，研发/运维为工程师序列
UPDATE employee e
SET e.job_level_id = CASE
    WHEN e.pos_id IN (29, 30, 31) THEN IF(MOD(e.id, 3) = 0, 9, 17)
    WHEN e.pos_id = 36 THEN IF(MOD(e.id, 3) = 0, 16, 15)
    WHEN e.pos_id IN (33, 34) THEN ELT(MOD(e.id, 4) + 1, 14, 15, 16, 16)
    ELSE 15
END;

-- ---------------------------------------------------------------- 清理旧演示记录
DELETE FROM employeeec WHERE remark = '演示数据';
DELETE FROM employeetrain WHERE remark = '演示数据';
DELETE FROM employeeremove WHERE remark = '演示数据';
DELETE FROM adjustsalary WHERE remark = '演示数据';

-- ---------------------------------------------------------------- 奖惩记录
-- 每位入选员工至少一条奖励
INSERT INTO employeeec (eid, ec_date, ec_reason, ec_point, ec_type, remark)
SELECT e.id,
       LEAST(CURDATE(), GREATEST(
           DATE_ADD(e.begin_date, INTERVAL 45 DAY),
           DATE_SUB(CURDATE(), INTERVAL MOD(e.id * 7, 420) + 30 DAY)
       )),
       ELT(MOD(e.id, 5) + 1,
           '季度绩效优秀', '项目交付突出贡献', '年度优秀员工',
           '客户满意度突出', '内部知识分享贡献'),
       3 + MOD(e.id, 8),
       0,
       '演示数据'
FROM employee e
WHERE MOD(e.id, 3) = 0 OR e.id >= 1939;

-- 部分员工补充一条处罚记录
INSERT INTO employeeec (eid, ec_date, ec_reason, ec_point, ec_type, remark)
SELECT e.id,
       LEAST(CURDATE(), GREATEST(
           DATE_ADD(e.begin_date, INTERVAL 90 DAY),
           DATE_SUB(CURDATE(), INTERVAL MOD(e.id * 11, 300) + 20 DAY)
       )),
       ELT(MOD(e.id, 4) + 1, '考勤异常', '项目交付延期', '流程执行不规范', '文档提交不及时'),
       1 + MOD(e.id, 4),
       1,
       '演示数据'
FROM employee e
WHERE (MOD(e.id, 3) = 0 OR e.id >= 1939)
  AND MOD(e.id, 5) = 0;

-- ---------------------------------------------------------------- 培训记录
INSERT INTO employeetrain (eid, train_date, train_content, remark)
SELECT e.id,
       LEAST(CURDATE(), DATE_ADD(e.begin_date, INTERVAL 14 DAY)),
       '新员工入职培训：企业文化与制度规范',
       '演示数据'
FROM employee e
WHERE MOD(e.id, 3) = 0 OR e.id >= 1939;

INSERT INTO employeetrain (eid, train_date, train_content, remark)
SELECT e.id,
       LEAST(CURDATE(), GREATEST(
           DATE_ADD(e.begin_date, INTERVAL 90 DAY),
           DATE_SUB(CURDATE(), INTERVAL MOD(e.id * 13, 500) + 60 DAY)
       )),
       ELT(MOD(e.id, 5) + 1,
           '年度专业技能提升培训', '项目管理实战工作坊', '信息安全与合规培训',
           '客户沟通与服务技巧', '数据分析基础培训'),
       '演示数据'
FROM employee e
WHERE MOD(e.id, 3) = 0 OR e.id >= 1939;

-- ---------------------------------------------------------------- 调动记录
INSERT INTO employeeremove (eid, after_dep_id, after_job_id, remove_date, reason, remark)
SELECT e.id,
       ELT(MOD(e.id, 13) + 1, 91, 92, 93, 94, 78, 81, 82, 85, 86, 87, 89, 8, 5),
       CASE
           WHEN ELT(MOD(e.id, 13) + 1, 91, 92, 93, 94, 78, 81, 82, 85, 86, 87, 89, 8, 5) IN (92, 93, 94)
               THEN IF(MOD(e.id, 6) = 0, 29, IF(MOD(e.id, 3) = 0, 36, 34))
           WHEN ELT(MOD(e.id, 13) + 1, 91, 92, 93, 94, 78, 81, 82, 85, 86, 87, 89, 8, 5) = 91
               THEN IF(MOD(e.id, 8) = 0, 29, IF(MOD(e.id, 4) = 0, 36, 33))
           WHEN ELT(MOD(e.id, 13) + 1, 91, 92, 93, 94, 78, 81, 82, 85, 86, 87, 89, 8, 5) IN (78, 81, 82, 85, 86, 87, 89)
               THEN IF(MOD(e.id, 5) = 0, 30, 31)
           ELSE 30
       END,
       LEAST(CURDATE(), GREATEST(
           DATE_ADD(e.begin_date, INTERVAL 180 DAY),
           DATE_SUB(CURDATE(), INTERVAL MOD(e.id * 17, 700) + 40 DAY)
       )),
       ELT(MOD(e.id, 4) + 1, '业务线调整', '岗位轮换', '组织架构优化', '个人发展需要'),
       '演示数据'
FROM employee e
WHERE MOD(e.id, 3) = 0 OR e.id >= 1939;

-- ---------------------------------------------------------------- 调薪记录
INSERT INTO adjustsalary (eid, as_date, before_salary, after_salary, reason, remark)
SELECT e.id,
       LEAST(CURDATE(), GREATEST(
           DATE_ADD(e.begin_date, INTERVAL 365 DAY),
           DATE_SUB(CURDATE(), INTERVAL MOD(e.id * 19, 900) + 30 DAY)
       )),
       7000 + MOD(e.id * 37, 13000),
       7000 + MOD(e.id * 37, 13000) + 1000 + MOD(e.id * 53, 5000),
       ELT(MOD(e.id, 4) + 1, '年度绩效调薪', '晋升调薪', '市场薪酬对标', '项目奖金转固定薪资'),
       '演示数据'
FROM employee e
WHERE MOD(e.id, 3) = 0 OR e.id >= 1939;

COMMIT;

-- ---------------------------------------------------------------- 校验输出
SELECT '员工总数' AS item, COUNT(*) AS value FROM employee
UNION ALL
SELECT '唯一工号数', COUNT(DISTINCT work_id) FROM employee
UNION ALL
SELECT '重复工号组数', COUNT(*) FROM (
    SELECT work_id FROM employee GROUP BY work_id HAVING COUNT(*) > 1
) dup
UNION ALL
SELECT '在职人数', COUNT(*) FROM employee WHERE work_state = '在职'
UNION ALL
SELECT '离职人数', COUNT(*) FROM employee WHERE work_state = '离职'
UNION ALL
SELECT '奖惩记录', COUNT(*) FROM employeeec WHERE remark = '演示数据'
UNION ALL
SELECT '培训记录', COUNT(*) FROM employeetrain WHERE remark = '演示数据'
UNION ALL
SELECT '调动记录', COUNT(*) FROM employeeremove WHERE remark = '演示数据'
UNION ALL
SELECT '调薪记录', COUNT(*) FROM adjustsalary WHERE remark = '演示数据';

SELECT id, name, gender, work_id, phone, email, department_id, pos_id, job_level_id,
       begin_date, work_state, tip_top_degree
FROM employee
ORDER BY id DESC
LIMIT 5;

SELECT department_id, COUNT(*) AS people
FROM employee
GROUP BY department_id
ORDER BY people DESC;
