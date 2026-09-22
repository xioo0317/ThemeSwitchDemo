# Theme Switch Demo

一个展示双主题架构（Material Design 3 **Expressive** + MIUI 风格 Miuix）和多语言切换的 Android 演示应用。UI 架构与 Material 层实现参考 [KernelSU](https://github.com/tiann/KernelSU)。

## ✨ 功能特性

- **双主题切换** — 运行时在 Material Design 3 卡片式布局和 Miuix（MIUI 风格）之间无缝切换
- **M3 Expressive** — Material 层使用 `MaterialExpressiveTheme` + `MotionScheme.expressive()`，配色切换经逐色 spring 动画平滑过渡（material3 1.5.0-alpha28）
- **Monet 动态取色** — Material 工作卡片在"工作中 / 未工作"两种状态下，容器色均由壁纸 Monet（或自定义种子色）经同一条 HCT 路径派生，随壁纸联动
- **多语言支持** — 中文（简/繁）、英语、日语、韩语、法语、德语、西班牙语、俄语；语言项首项为「跟随系统」
- **暗色模式** — 跟随系统 / 浅色 / 深色，支持 AMOLED 纯黑
- **数据持久化** — 使用 DataStore 保存用户偏好设置
- **日志查看** — 内置日志页面，读取应用私有目录 log.txt 并展示

## 🏗️ 架构设计

灵感来自 KernelSU 的双主题架构：

```
UiMode 枚举 (Material / Miuix)
    ↓
LocalUiMode (CompositionLocal)
    ↓
AppTheme Composable (分发到对应主题)
    ↓
MaterialAppTheme (M3 Expressive) / MiuixAppTheme
    ↓
每个 Screen 独立实现 (*Material.kt / *Miuix.kt)
```

### 项目结构

```
app/src/main/java/com/demo/themeswitch/
├── MainActivity.kt
├── data/
│   ├── AppPreferences.kt
│   ├── LogRepository.kt
│   └── LocaleHelper.kt
└── ui/
    ├── UiMode.kt
    ├── component/
    │   ├── material/        # ExpressiveScaffold / TonalCard / SegmentedList ...
    │   └── miuix/
    ├── theme/
    │   ├── AppTheme.kt
    │   ├── MaterialAppTheme.kt   # MaterialExpressiveTheme
    │   ├── ThemeExt.kt           # ColorScheme.animateAsState()
    │   ├── ColorMode.kt
    │   └── MiuixTheme.kt
    └── screen/
        ├── HomeMaterial.kt
        ├── HomeMiuix.kt
        ├── SettingsMaterial.kt
        ├── SettingsMiuix.kt
        └── LogScreen.kt
docs/
└── KSU_PORT_REPORT.md
```

## 🛠️ 技术栈

- **Kotlin** 2.4 — 主要语言
- **Jetpack Compose** — 声明式 UI（Compose BOM 2026.09.00）
- **Material 3** 1.5.0-alpha28 — 含 M3 Expressive
- **Miuix KMP** 0.9.4 — MIUI 风格组件
- **MaterialKolor** 5.0.1 — Monet / HCT 动态取色
- **DataStore Preferences** — 轻量级数据持久化
- **NavigationEvent** — 预测性返回手势底座

## 🚀 构建与运行

需要 **JDK 21**、Gradle 9.7.1、Android SDK Platform 37.0 + Build-Tools 37。

仓库未包含 Gradle Wrapper 脚本，请使用本地 Gradle：

```bash
git clone https://github.com/xioo0317/ThemeSwitchDemo.git
cd ThemeSwitchDemo
gradle :app:assembleDebug
```

推送到 GitHub 后会自动运行 Actions 构建（`main` 分支触发）。

## 📄 移植记录

KernelSU UI 移植过程与细节记录：[docs/KSU_PORT_REPORT.md](docs/KSU_PORT_REPORT.md)

## 📄 许可证

MIT License
