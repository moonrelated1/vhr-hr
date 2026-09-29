# 协作规范

本项目为 2 人小组课程作业。本文档约定协作方式，**目的是让每个人的工作量都能在提交记录中被清楚看到**。

## 一、分支策略（GitHub Flow）

`main` 分支保持可运行，**任何人不得直接向 `main` 提交**。所有改动走"分支 → PR → Review → 合并"。

```
main
 ├─ feat/employee-crud        新功能
 ├─ fix/salary-query-npe      缺陷修复
 ├─ docs/requirements-v1      文档
 └─ test/employee-cases       测试用例
```

**分支命名规范：**

| 前缀 | 用途 | 示例 |
|---|---|---|
| `feat/` | 新功能 | `feat/employee-archive` |
| `fix/` | 缺陷修复 | `fix/dept-tree-null` |
| `docs/` | 文档 | `docs/db-design` |
| `test/` | 测试 | `test/salary-cases` |
| `refactor/` | 重构 | `refactor/employee-service` |
| `chore/` | 构建、配置 | `chore/upgrade-mysql-driver` |

## 二、提交信息规范（Conventional Commits）

格式：`<类型>(<范围>): <描述>`

```
feat(employee): 新增员工档案分页查询接口
fix(salary): 修复月度薪资统计金额计算错误
docs(readme): 补充数据库表说明
test(employee): 补充员工入职流程测试用例
```

**常用类型：** `feat` `fix` `docs` `test` `refactor` `chore` `style` `perf`

> ⚠️ 请**不要**写 `update`、`修改`、`提交` 这类无意义信息。

## 三、Pull Request 规范（课程硬性要求）

> **提交作业时需要提供 GitHub 仓库链接，且每个 PR 都必须标注项目组成员姓名。**
> 因此本项目**所有改动一律走 PR**，`main` 分支不允许出现直接推送的提交。

### 3.1 PR 标题必须带姓名

格式：`【姓名】<类型>(<范围>): <描述>`

```
【杨宇恒】feat(employee): 新增员工档案分页查询接口
【B同学】docs: 编写需求规格说明书 v1
【杨宇恒 / B同学】docs: 补充第二版数据库设计
```

姓名放在**最前面**，这样老师在 PR 列表里一眼就能看到谁做了什么。

### 3.2 PR 正文必须填「项目组成员」表

模板 `.github/PULL_REQUEST_TEMPLATE.md` 顶部已有该表格，**两组都要填**：

- **提交人（Author）**：谁写的这个 PR
- **评审人（Reviewer）**：谁 review 并通过的

> ⚠️ 评审人不能和提交人是同一人——否则体现不出协作。两人互为对方的 Reviewer。

### 3.3 一个 PR 只做一件事

不要把「员工模块」全部塞进一个 PR。按功能点拆，比如：

| PR | 标题 | 内容 |
|---|---|---|
| #1 | 【杨宇恒】feat(employee): 员工实体与 Mapper | 建表映射 |
| #2 | 【杨宇恒】feat(employee): 员工档案增删改查接口 | Service + Controller |
| #3 | 【B同学】test(employee): 员工档案测试用例与执行记录 | 测试报告 |

拆分后 PR 数量多、颗粒度小，**提交记录的「持续更新」和「多人协作」两个要求都能同时满足**。

## 四、标准工作流程

```bash
# 1. 从最新 main 拉分支
git checkout main
git pull origin main
git checkout -b feat/employee-archive

# 2. 开发，小步提交（一次提交只做一件事）
git add <具体文件>          # 避免 git add . 混入无关改动
git commit -m "feat(employee): 新增员工档案实体类"

# 3. 推送到远程
git push origin feat/employee-archive

# 4. 在 GitHub 上发起 Pull Request
#    - 标题写成 【你的姓名】feat(范围): 描述   ← 姓名必须带，见第三节
#    - 填写 PR 模板中的「项目组成员」表格（提交人 + 评审人）
#    - 关联对应 Issue（Closes #编号）
#    - 指定另一人作为 Reviewer

# 5. 对方 Review 通过后合并
git checkout main
git pull origin main
```

## 五、两人分工

### A 同学（后端与前端开发）

- 负责 `vhr-employee`、`vhr-salary` 模块的后端与前端实现
- 每个功能点拆成多次提交（实体 → Mapper → Service → Controller → 前端 → 联调）
- 发起 PR 并等待 Review

### B 同学（文档、测试与质量保障）

不写代码同样有大量**真实且必要**的产出，每一项都对应规范的提交：

| 工作内容 | 产出物 | 提交示例 |
|---|---|---|
| 需求分析 | `docs/requirement.md` | `docs: 编写需求规格说明书 v1` |
| 数据库设计 | `docs/database-design.md`（ER 图） | `docs: 补充员工模块 ER 图` |
| 功能测试 | GitHub Issue（用「缺陷报告」模板） | — |
| 测试用例 | `docs/test-cases.md` | `test: 补充员工管理测试用例 20 条` |
| 测试执行记录 | `docs/test-report.md` + 截图 | `test: 完成员工模块第一轮回归测试` |
| 用户手册 | `docs/user-manual.md` | `docs: 编写员工管理操作手册` |
| 界面素材 | `vhr-vue/src/assets/` | `chore: 替换系统 logo` |
| 演示材料 | `PPT` | — |

**B 同学的 Review 职责：** 对每个 PR 进行功能验收，在 PR 中留下 Review 意见（通过 / 打回并说明原因）。**Review 记录本身就是宝贵的协作证据。**

## 六、红线

- ❌ 不要把数据库密码、密钥提交进仓库（本地配置请放 `application-local.yaml`，已在 `.gitignore` 中排除）
- ❌ 不要直接向 `main` 提交（**所有改动必须走 PR**，见第三节）
- ❌ 不要发**不带姓名**的 PR（课程明确要求 PR 标注组员姓名）
- ❌ 不要积攒大量改动后一次性提交（课程要求持续的、多人的提交记录）
- ✅ 提交前确认身份：`git config user.name` / `git config user.email` 应为本人

## 七、Issue 使用

所有工作**先建 Issue 再动手**。Issue 不只是任务清单，也是你工作量的直接证明。

- 新功能 → 用「功能任务」模板
- 发现缺陷 → 用「缺陷报告」模板
- PR 中写 `Closes #12`，合并后会自动关闭对应 Issue
