# 智汇人力资源管理系统（ZhiHui HRMS）

| | |
|---|---|
| **项目仓库** | https://github.com/moonrelated1/vhr-hr |
| **课程** | 软件工程实践（2026 秋） |
| **项目成员** | 杨宇恒（[@moonrelated1](https://github.com/moonrelated1)）、肖克锋（[@ZeroniAC](https://github.com/ZeroniAC)） |
| **协作规范** | 见 [CONTRIBUTING.md](CONTRIBUTING.md) |

> ### ⚠️ 关于本项目来源的说明（请先读这段）
>
> 本项目为**《软件工程实践》课程作业**，基于以下开源脚手架进行**二次开发**：
>
> | 项目 | 说明 |
> |---|---|
> | 原项目 | [lenve/vhr2.0](https://github.com/lenve/vhr2.0) |
> | 原作者 | 江南一点雨（GitHub: [@lenve](https://github.com/lenve)） |
> | 原项目定位 | **脚手架（Scaffold）**——作者明确说明其业务功能不完整，供二次开发使用 |
> | 原项目协议 | 仓库中**未附带 LICENSE 文件**（版权默认归原作者） |
>
> **我们明确承认**：项目骨架、前后端基础设施、登录鉴权、权限体系、系统管理（菜单 / 部门 / 职位）等能力来自上述开源项目，**并非我们原创**。
>
> **我们的原创贡献**集中在 `vhr-employee`（员工管理）与 `vhr-salary`（薪资管理）两个模块——这两个模块在原项目中是**空目录**，作者只创建了模块文件夹，未编写任何 Java 代码。详见 [我们的工作](#我们的工作) 一节。

---

## 项目简介

**智汇人力资源管理系统（ZhiHui HRMS）** 是一个基于 Spring Boot 3 + Vue 3 的前后端分离人力资源管理系统，面向企业 HR 日常业务，覆盖组织架构、员工全生命周期、薪资核算等场景。

> 项目代号 `vhr-hr`（`vhr` 取自底层脚手架 lenve/vhr2.0 的命名）。

## 技术栈

**后端**

| 技术 | 版本 | 说明 |
|---|---|---|
| Spring Boot | 3.2.1 | 主框架 |
| Java | 21 | 需 JDK 17+ |
| MyBatis-Plus | 3.5.5 | ORM |
| Spring Security | — | 认证与授权 |
| MySQL | 8.0 | 数据库 |
| Maven | 3.9 | 构建工具 |

**前端**

| 技术 | 版本 | 说明 |
|---|---|---|
| Vue | 3.3 | 前端框架 |
| Vite | 5.0 | 构建工具，需 Node 18+ |
| Element Plus | 2.4 | UI 组件库 |
| Pinia | 2.1 | 状态管理 |
| Vue Router | 4.2 | 路由 |
| Axios | 1.6 | HTTP 客户端 |

> 说明：本项目**不依赖 Redis 与 RabbitMQ**，仅需 MySQL 即可运行。

## 环境要求

- JDK 17 或以上（**开发验证环境为 JDK 21**）
- Maven 3.6+
- Node.js 18+（**开发验证环境为 Node 22**）
- MySQL 8.0+

> ⚠️ 若使用 JDK 25 需自行验证兼容性；本项目在 JDK 21 上验证通过。

## 快速开始

### 1. 准备数据库

```bash
mysql -u root -p -e "CREATE DATABASE vhr2024 DEFAULT CHARACTER SET utf8mb4;"
mysql -u root -p vhr2024 < vhr/vhr2024_2024-01-09.sql
```

### 2. 配置数据库连接

数据库地址默认为 `localhost:3306/vhr2024`。**密码不入库**，请用以下任一方式提供：

**方式一（推荐）：新建 `vhr/vhr-web/src/main/resources/application-local.yaml`**

该文件已被 `.gitignore` 排除，适合放本机密码：

```yaml
spring:
  datasource:
    password: "你的密码"
```

**方式二：通过环境变量传入**

```bash
export DB_USERNAME=root          # 可选，默认 root
export DB_PASSWORD=你的密码
export DB_URL="jdbc:mysql://localhost:3306/vhr2024?serverTimezone=Asia/Shanghai"   # 可选
```

### 3. 启动后端（端口 8080）

```bash
cd vhr
mvn clean install -DskipTests
mvn spring-boot:run -pl vhr-web
```

### 4. 启动前端（端口 5173）

```bash
cd vhr-vue
npm install
npm run dev
```

浏览器访问 http://localhost:5173/ ，默认账号：

| 账号 | 密码 | 角色 |
|---|---|---|
| `admin` | `123` | 系统管理员 |

## 模块结构

```
vhr-hr/
├── vhr/                          # 后端（Maven 多模块）
│   ├── vhr-framework/            # 框架基础设施：安全配置、工具类、异常处理  【脚手架提供】
│   ├── vhr-system/               # 系统管理：菜单、部门、职位、权限      【脚手架提供】
│   ├── vhr-employee/             # 员工管理                            【本项目实现】
│   ├── vhr-salary/               # 薪资管理                            【本项目实现】
│   ├── vhr-web/                  # 启动模块与 Controller 入口
│   └── vhr2024_2024-01-09.sql    # 数据库脚本（22 张表）
└── vhr-vue/                      # 前端（Vue 3 + Vite）
```

## 我们的工作

> 本节记录本项目相对原脚手架的**增量**。原项目中 `vhr-employee` 与 `vhr-salary` 为空模块。

### 员工管理（`vhr-employee`）

<!-- 开发过程中逐步补充：实现的功能点、对应数据库表、关键设计决策 -->

| 功能 | 涉及表 | 状态 |
|---|---|---|
| _待补充_ | `employee` | ⬜ |

### 薪资管理（`vhr-salary`）

<!-- 开发过程中逐步补充 -->

| 功能 | 涉及表 | 状态 |
|---|---|---|
| _待补充_ | `salary` / `empsalary` / `adjustsalary` | ⬜ |

## 数据库表概览

原脚手架附带 22 张表，本项目主要涉及：

| 表名 | 用途 |
|---|---|
| `employee` | 员工基本信息 |
| `employeeremove` | 员工调动记录 |
| `employeetrain` | 员工培训记录 |
| `employeeec` | 员工奖惩记录 |
| `salary` | 薪资标准 |
| `empsalary` | 员工月度薪资 |
| `adjustsalary` | 调薪记录 |
| `department` / `position` / `joblevel` | 组织架构（脚手架已用） |
| `hr` / `role` / `menu` | 用户与权限（脚手架已用） |

## 参考资料

- 原项目：[lenve/vhr2.0](https://github.com/lenve/vhr2.0)
- 早期版本：[lenve/vhr](https://github.com/lenve/Vhr)
- 作者站点：https://www.javaboy.org

## 致谢

感谢原作者 [@lenve](https://github.com/lenve)（江南一点雨）开源本项目脚手架，为本课程作业提供了坚实的学习与实践基础。
