-- 员工管理模块 v1：给系统管理员（role id = 6）补「基本资料」「高级资料」菜单授权。
-- 背景：vhr2024 种子数据中 admin 只有 menu 9-13，缺少 menu 7/8，
-- 导致 admin 登录后看不到这两个页面，且 /employee/basic/** 接口会返回 403。
-- 本脚本可重复执行。

INSERT INTO menu_role (mid, rid)
SELECT 7, 6 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu_role WHERE mid = 7 AND rid = 6);

INSERT INTO menu_role (mid, rid)
SELECT 8, 6 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu_role WHERE mid = 8 AND rid = 6);

-- 验证：应返回 2 行
SELECT mid, rid FROM menu_role WHERE rid = 6 AND mid IN (7, 8);
