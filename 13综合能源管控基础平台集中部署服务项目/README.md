
前后端分离的项目：**前端 Vue3 + Vite，后端 Java 17 + Spring Boot 3**。

---

## 一、环境与启动

### 1. 已安装的运行环境

| 组件 | 版本 | 安装位置 | 用途 |
| --- | --- | --- | --- |
| Node.js | 22.22.2 | `C:\Users\23847\.workbuddy\binaries\node\versions\22.22.2-6` | 前端构建 |
| npm | 10.9.7 | 随 Node 安装 | 前端依赖 |
| JDK | 17 (Temurin / Microsoft OpenJDK) | `C:\Users\23847\.workbuddy\runtimes\jdk-17*` | 后端编译运行 |
| Maven | 3.9.9 | `C:\Users\23847\.workbuddy\runtimes\apache-maven-3.9.9` | 后端依赖与打包 |
| Vite | 5.x | 前端 `node_modules` | 前端开发服务器 |

> 环境变量（系统级，已写入）：
> `JAVA_HOME`、`MAVEN_HOME`，并把 `%JAVA_HOME%\bin`、`%MAVEN_HOME%\bin` 加入 `Path`。
> 新开一个终端执行 `java -version`、`mvn -v`、`node -v` 均应正常输出。

### 2. 启动后端（端口 8080）

双击项目根目录的 **`start-backend.bat`**（已内置 JDK/Maven 路径），或手动执行：

```bash
cd gfecmp-platform/backend
mvn spring-boot:run
# 或先打包再运行
mvn clean package -DskipTests
java -jar target/gfecmp-backend.jar --server.port=8080
```

> 如果系统里存在 `SERVER_PORT` 环境变量，它会覆盖 `application.yml` 的端口，
> 此时用 `--server.port=8080` 显式指定即可。

`start-backend.bat` 的行为：**优先直接运行已打包的 `target/gfecmp-backend.jar`**（不联网、秒起），
只有 jar 不存在时才回退到 `mvn spring-boot:run`。所以**改过后端代码后**需要重新打包一次：

```bash
cd gfecmp-platform/backend
mvn clean package -DskipTests
```

> 批处理内所有提示都改成了纯英文 + CRLF 行尾。原因是 Windows 控制台默认 GBK，
> 含中文的 UTF-8 批处理会被解析错乱（出现一堆 "?? 不是内部或外部命令"）。
> 如果你要改脚本，请保持纯 ASCII，或另存为 ANSI/GBK 编码。

启动日志会打印样本表解析结果，例如：

```
[数据源] classpath:data/2025年1月1日_累计能耗样本.xlsx 解析完成：8640 个采样点，
        2025-01-01T00:00 ~ 2025-01-01T23:59:50，采样间隔 10s
[模拟引擎] 时钟对齐模式（1:1）：当前时间 2026-11-10T15:24:18，使用样本表 15:24:10 的同刻度数据，刷新周期 20000 ms
```

### 3. 启动前端（端口 5173）

双击 **`start-frontend.bat`**，或手动执行：

```bash
cd gfecmp-platform/frontend
npm install      # 首次执行，已执行过可跳过
npm run dev
```

浏览器打开 <http://localhost:5173>，登录账号 **admin / 123456**（验证码按图输入）。

前端 `vite.config.js` 已配置代理：`/api` → `http://localhost:8080`，因此开发期不存在跨域问题
（后端同时开启了 CORS，方便直连调试）。

### 4. 用开发工具打开

- **VSCode 打开 `gfecmp-platform/frontend`**：推荐安装 `Vue - Official`(Volar) 扩展，
  `.vue` 单文件组件可直接高亮、跳转、格式化；`jsconfig.json` 已配置 `@/` 别名。
- **IntelliJ IDEA 打开 `gfecmp-platform/backend`**：选择 `pom.xml` 以 Maven 项目导入，
  IDEA 会自动识别 `src/main/java`、`src/main/resources` 与 Spring Boot 运行配置。

---

## 二、项目结构

```
gfecmp-platform/
├─ README.md
├─ start-backend.bat                            # 一键启动后端（内置 JDK/Maven 路径）
├─ start-frontend.bat                           # 一键启动前端（自动 npm install）
├─ backend/                                     # Spring Boot 后端
│  ├─ pom.xml                                   # Java 17 / Spring Boot 3.2.5 / Apache POI
│  └─ src/main/
│     ├─ java/com/greenfactory/gfecmp/
│     │  ├─ GfecmpApplication.java              # 启动类（开启 @EnableScheduling）
│     │  ├─ common/
│     │  │  ├─ ApiResult.java                   # 统一响应 {code,message,data,timestamp}
│     │  │  └─ PageResult.java                  # 通用分页
│     │  ├─ config/
│     │  │  ├─ SimulationProperties.java        # gfecmp.simulation.* 配置绑定
│     │  │  └─ WebConfig.java                   # CORS
│     │  ├─ controller/
│     │  │  ├─ AuthController.java              # /api/auth/login（演示鉴权）
│     │  │  ├─ DashboardController.java         # /api/dashboard、/api/meta、/api/health
│     │  │  ├─ EnergyController.java            # /api/energy/**
│     │  │  └─ CarbonController.java            # /api/carbon/**
│     │  ├─ datasource/
│     │  │  └─ EnergySampleDataSource.java      # ★ 解析 xlsx（Apache POI）
│     │  ├─ dto/                                # 出参结构（Java record）
│     │  │  ├─ DashboardView / MetaView / EnergyRow / AnalysisView
│     │  │  └─ FactorView / AccountingView / AlertView / AssetView / FootprintView
│     │  ├─ model/
│     │  │  ├─ EnergyType.java                  # 六类能源 + 排放因子 + 折标煤系数
│     │  │  └─ EnergyPoint.java                 # 单时刻累计读数
│     │  └─ service/
│     │     ├─ EnergyDataService.java           # ★ 20 秒定时推进的模拟采集引擎
│     │     ├─ DashboardService.java            # 驾驶舱聚合
│     │     ├─ EnergyQueryService.java          # 明细查询/历史追溯/强度计算
│     │     ├─ CarbonService.java               # 核算/因子库/预警
│     │     ├─ AssetService.java                # 碳资产履约预测
│     │     └─ FootprintService.java            # 产品碳足迹
│     └─ resources/
│        ├─ application.yml                     # 端口、刷新周期、样本表路径
│        └─ data/2025年1月1日_累计能耗样本.xlsx  # 唯一数据源
└─ frontend/                                    # Vue3 前端
   ├─ package.json / vite.config.js / index.html / jsconfig.json
   ├─ .vscode/extensions.json                   # 推荐的 VSCode 扩展
   └─ src/
      ├─ main.js / App.vue
      ├─ api/
      │  ├─ http.js                             # axios 实例 + 响应解包
      │  └─ index.js                            # 所有接口封装
      ├─ components/
      │  ├─ AppTopbar.vue / AppSidebar.vue      # 顶栏 + 左侧菜单（响应式）
      │  ├─ NavIcon.vue                         # 内联 SVG 图标集
      │  ├─ StatCard.vue / PanelCard.vue        # KPI 卡 / 内容面板
      │  ├─ EChart.vue                          # ECharts 封装（自适应 resize）
      │  └─ Pager.vue
      ├─ composables/
      │  ├─ usePolling.js                       # 20 秒轮询 + 倒计时
      │  └─ useBreakpoint.js                    # PC / 平板 / 手机断点
      ├─ config/menu.js                         # 菜单 + 首页蜂窝导航配置
      ├─ layouts/AppLayout.vue                  # 整体骨架
      ├─ router/index.js                        # 路由 + 登录守卫
      ├─ styles/{tokens.css,global.css}         # 设计变量 / 全局样式
      └─ views/
         ├─ LoginView.vue            # 登录（演示验证码）
         ├─ HomeView.vue             # 首页驾驶舱：KPI + 蜂窝导航 + 设备 + 预警 + 图表
         ├─ EnergyQueryView.vue      # 能耗数据查询（筛选/分页/导出 CSV）
         ├─ EnergyTrendView.vue      # 能耗历史追溯（小时 / 10 分钟聚合）
         ├─ EnergyAnalysisView.vue   # 能源消费量与强度计算（GB/T 2589）
         ├─ CarbonAccountingView.vue # 碳排放核算（月度 + 计算过程弹窗）
         ├─ CarbonAlertView.vue      # 碳排放趋势预警（阈值红线）
         ├─ CarbonFactorView.vue     # 碳因子库（筛选 + 新增）
         ├─ CarbonAssetView.vue      # 碳资产仪表盘（行情/环形/规划）
         ├─ FootprintView.vue        # 产品碳足迹（六阶段占比）
         └─ PlaceholderView.vue      # 规划中模块的统一占位页
```

---

## 三、核心实现说明

### 1. 数据链路

```
2025年1月1日_累计能耗样本.xlsx（8640 行 / 10 秒一条 / 6 类累计表计）
        │  Apache POI 解析（EnergySampleDataSource，启动时一次性载入内存）
        ▼
List<EnergyPoint>  ——  游标 cursor 指向"当前时刻对应的样本点"
        │  clock 模式：cursor = 当前时分秒 / 10 秒（1:1 对齐真实时钟）
        │  replay 模式：@Scheduled 每 20 秒推进 step-points 个采样点（倍速回放）
        ▼
EnergyDataService  ——  今日累计消耗 / 瞬时功率 / 分时曲线 / 折标煤 / 碳排放
        │  DashboardService、EnergyQueryService、CarbonService ...
        ▼
REST API（/api/**）
        │  前端 axios 每 20 秒轮询（usePolling，页面显示倒计时）
        ▼
Vue3 页面（ECharts 渲染）
```

关键参数（`application.yml`）：

```yaml
gfecmp:
  simulation:
    mode: clock               # clock = 1:1 对齐真实时钟（默认）；replay = 倍速回放
    tick-millis: 20000        # 刷新周期：20 秒（只是刷新粒度，不改变流速）
    step-points: 30           # 仅 replay：每周期推进 30 个点 ≈ 15 倍速（2 = 1:1）
    start-offset-minutes: 480 # 仅 replay：启动即快进到 08:00
    data-file: classpath:data/2025年1月1日_累计能耗样本.xlsx
```

- **clock（默认，1:1 实时）**：样本表 `15:24:00` 的数据显示为**今天** `15:24:00`，
  时间流速与真实时钟完全一致（现实过 20 秒，数据前进 20 秒）。
  **现实过 24 点后自动回到样本表 00:00 重新开始**，日志打印 `已跨过零点，样本表从 00:00 重新开始`。
  因为是按"当前时分秒"取点，重启、休眠都不会累计误差，也不需要 start-offset。
- **replay（演示用）**：按 `step-points` 快进，走到表尾进入"次日"（日期 +1 天）继续。
- **月度/年度指标以"整日合计"为基准**（`EnergyDataService.fullDayTce()/fullDayCarbon()`，启动时算一次），
  不随当天进度漂移，跨天时也不会突然掉成 0。
- 想换数据源只需替换 `resources/data` 下的 xlsx（列名保持不变）。

### 2. 计算口径

| 指标 | 公式 | 依据 |
| --- | --- | --- |
| 综合能耗 tce | Σ(介质用量 × 折标煤系数 kgce) ÷ 1000 | GB/T 2589-2020 |
| 碳排放 tCO₂e | Σ(介质用量 × 排放因子 kgCO₂) ÷ 1000 | GB/T 32150-2015 |
| 单位产品能耗 | 综合能耗 ÷ 产量 | GB/T 2589 |
| 碳排放强度 | 碳排放 ÷ 产量 | 企业内部考核口径 |
| 履约结余 | 配额 + CCER − 累计排放 | 全国碳市场履约规则 |

排放因子与折标煤系数集中定义在 `model/EnergyType.java`，修改一处即全站生效：
电力 0.5703 kgCO₂/kWh、天然气 2.162 kgCO₂/m³、汽油 2.925 kgCO₂/L、
柴油 3.096 kgCO₂/L、水 0.344 kgCO₂/m³、折标煤电力 0.1229 kgce/kWh 等。

### 3. 接口清单

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/login` | 登录（admin / 123456） |
| GET | `/api/meta` | 能源类型字典 + 刷新周期 + 当前模拟时刻 |
| GET | `/api/dashboard` | 驾驶舱：KPI、能源/碳结构、分时趋势、预警、设备状态 |
| GET | `/api/energy/records?type=&page=&size=&keyword=&bucketMinutes=` | 能耗分段明细（分页） |
| GET | `/api/energy/series?type=&granularity=hour\|minute10` | 历史追溯曲线 |
| GET | `/api/energy/analysis` | 12 个月能耗与强度（GB/T 2589） |
| GET | `/api/carbon/factors` | 碳因子库 |
| GET | `/api/carbon/accounting?year=2025` | 月度核算 + 排放源计算过程 |
| GET | `/api/carbon/alerts` | 小时排放 + 均值 + 阈值 + 预警记录 |
| GET | `/api/carbon/asset` | 碳资产、行情、构成、节能规划 |
| GET | `/api/carbon/footprint` | 产品碳足迹六阶段 |
| GET | `/api/health` | 健康检查 |

响应统一为 `{ "code": 0, "message": "ok", "data": {...}, "timestamp": 169... }`。

### 4. 界面与响应式

视觉参照《零碳工厂需求文档》中的成品截图还原：深绿顶栏 + 更深绿的左侧菜单、
浅灰内容底 + 白色圆角卡片、绿色主按钮、橙绿相间的六边形蜂窝导航。

| 断点 | 布局 |
| --- | --- |
| ≥ 1200px（PC） | 左侧菜单 232px 常驻，KPI 四列，图表左右分栏 |
| 768–1199px（平板） | 菜单收为 76px 图标栏（可点顶栏按钮展开），KPI 两列，分栏改上下 |
| < 768px（手机） | 菜单变抽屉 + 遮罩，KPI 两列，卡片单列，表格横向滚动 |

顶栏右侧的时间显示：`clock` 模式下是**每秒跳动的真实时间**（只显示 `HH:mm:ss` 于手机端），
不再等待 20 秒接口刷新；`replay` 模式下显示回放时刻，并保留 `20s 自动刷新` 标识。

### 5. 与需求文档的对应

| 需求 | 落地位置 |
| --- | --- |
| FR-001 多源能耗数据采集 | `EnergySampleDataSource` + `EnergyDataService`（xlsx 回放替代 ESP32） |
| FR-002 能源消费量与强度计算 | `/api/energy/analysis` + `EnergyAnalysisView.vue` |
| FR-003 碳因子库配置 | `/api/carbon/factors` + `CarbonFactorView.vue` |
| FR-004 碳排放趋势预警 | `/api/carbon/alerts` + `CarbonAlertView.vue` + 首页预警卡片 |
| FR-005 产品碳足迹核算 | `/api/carbon/footprint` + `FootprintView.vue` |
| FR-006 碳资产履约预测 | `/api/carbon/asset` + `CarbonAssetView.vue` |

---

## 四、后续接入真实 ESP32 的扩展点

当前"假数据"集中在两处，替换即可无缝切换到真实采集：

1. `datasource/EnergySampleDataSource`：改为 MQTT 订阅（topic `greenfactory/esp32/energy`）
   或串口读取，产出同样的 `List<EnergyPoint>`。
2. `service/EnergyDataService#tick`：改为把最新一条采集值追加到时间序列，
   `cursor` 指向最新点，其余业务代码（核算、图表、预警）无需改动。

即：**业务层只依赖 `EnergyPoint` 序列**，不关心数据来自 xlsx 还是 ESP32。

---
