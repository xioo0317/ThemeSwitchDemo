# KernelSU UI 移植报告

- **目标仓库**：[xioo0317/ThemeSwitchDemo](https://github.com/xioo0317/ThemeSwitchDemo)
- **分支**：`port-ksu-ui`
- **基线参考**：[tiann/KernelSU](https://github.com/tiann/KernelSU) `main`（material3 `1.5.0-alpha28`）
- **本轮日期**：2026-09-22

---

## 一、本轮移植内容

### 1. Material 层升级到 M3 Expressive

对齐 KernelSU `ui/theme/MaterialTheme.kt`（`MaterialKernelSUTheme`）。

**改动文件：**

| 文件 | 改动 |
| --- | --- |
| `ui/theme/MaterialAppTheme.kt` | `MaterialTheme(...)` → `MaterialExpressiveTheme(colorScheme, motionScheme = MotionScheme.expressive(), ...)`；传入 `animateAsState()` 后的配色 |
| `ui/theme/ThemeExt.kt` | **新增**。移植 KSU `ColorScheme.animateAsState()`：对 ColorScheme 全部色槽（含 fixed 系列）逐一 `animateColorAsState(spring())` |

**效果：** 主题模式 / 种子色切换时，全 UI 容器色不再瞬间跳变，而是逐色 spring 平滑过渡；组件获得 Expressive 动效方案（容器形变、强调节奏等）。

**版本/开关确认（此前已具备，本轮复用）：**

- Compose BOM `2026.09.00`，`androidx.compose.material3:material3:1.5.0-alpha28`
- 编译参数已 opt-in `ExperimentalMaterial3Api` + `ExperimentalMaterial3ExpressiveApi`

> 与 KSU 的差异：KSU 显式传入自定义 `Typography`；本项目无自定义字体排版，使用 `MaterialExpressiveTheme` 默认 Typography，观感由 Expressive 默认字形规格提供。

### 2. M3 工作卡片按莫奈取色（未工作 / 工作中）

对齐 KernelSU `HomeMaterial.kt#StatusCard` 的取色逻辑。

**改动文件：** `ui/screen/HomeMaterial.kt`（`StatusCard`）

取色链路（与 KSU 一致，**无任何硬编码颜色**）：

```
壁纸 Monet primary（或用户自定义种子色 keyColor）
        ↓ materialkolor
   ColorScheme
        ↓
 工作中：secondaryContainer +  CheckCircle
 未工作：errorContainer + Warning
```

要点：

- **两种状态的容器色都来自当前 ColorScheme**；而 Material 模式的 ColorScheme 由系统壁纸 Monet（keyColor==0 时取 `dynamic{Dark,Light}ColorScheme().primary` 作种子）或自定义种子经 materialkolor 派生，因此未工作态的红色也是 HCT/Monet 派生色，随壁纸/种子联动，并非固定死红。
- 标题改用 Expressive 的 `titleMediumEmphasized`；副标题透明度 0.7；ListItem 各内容色显式绑定 contentColor。

> Miuix 模式首页另有独立实现（`HomeMiuix.kt`），本轮仅涉及 Material 模式。

### 3. 语言选项 System default → 跟随系统

**改动文件：**

| 文件 | 改动 |
| --- | --- |
| `data/LocaleHelper.kt` | system 项展示名不再硬编码英文，置空占位 |
| `ui/screen/SettingsMaterial.kt` | 语言列表映射：`system` → `stringResource(R.string.lang_system)` |
| `ui/screen/SettingsMiuix.kt` | 同上 |

复用仓库既有字符串资源 `lang_system`（此前未被使用），故为**全语言本地化**：

- `values-zh-rCN`：**跟随系统**
- `values-zh-rTW`：跟隨系統
- `values`：Follow System
- ja：システムに従う / ko：시스템 따르기 / de：System folgen / es：Seguir al sistema / fr：Suivre le système / ru：Следовать системе

语言代码值仍为 `"system"`，`LocaleHelper` 存取与 `wrapContext` 逻辑不变，无行为回归。

---

## 二、验证方式

- 编译验证：`.github/workflows/build.yml` 已扩展为在 `port-ksu-ui` 同步触发，runner 以 JDK 21 + Gradle 9.7.1 执行 `gradle assembleDebug`。
- 本报告记录的是**推送时的代码状态**；最终编译结论以该分支最新 CI 运行结果为准。
- 本仓库未包含 Gradle Wrapper 脚本（仅有 `gradle-wrapper.properties`），本地构建需 Gradle 9.7.1 + JDK 21 + Android SDK Platform 37.0 + Build-Tools 37。

---

## 三、后续可继续移植项（建议，按优先级）

1. **Expressive 组件底座全面切换** — 首页 `Scaffold` → `ExpressiveScaffold` + `expressiveTopAppBarColors()`；复用项目已有 `component/material/ExpressiveScaffold.kt`。
2. **TonalCard / WarningCard 体系** — 移植 KSU `TonalCard` 与三级 `WarningLevel`（Error=`errorContainer` / Notice=`tertiaryContainer`）。
3. **StatusTag 状态标签** — LKM/GKI、安全模式、Jailbreak 等标签。
4. **Expressive 按钮/进度组件** — `Button`、`SplitButton`、`LoadingIndicator` 等替换。
5. **Miuix 侧版本跟进** — 跟随 KSU 升级 miuix 时核对 blur/preference/nav API 变化。
6. **预测性返回细节** — 已引入 navigationevent 底座，对齐各屏返回拦截与转场。

---

## 四、风险与回滚

- 第 1、2 项为 **Material 全 UI 观感变更**，仅在 Material 模式生效；默认 UI 为 Miuix，默认路径不受影响。
- 所有改动集中在 `port-ksu-ui` 分支，合入 main 前独立验证；回滚整体丢弃该分支新增提交即可，不涉及偏好键变更。
