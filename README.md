# Theme Switch Demo

一个展示双主题架构（Material Design 3 + MIUI 风格）和多语言切换的 Android 演示应用。

## ✨ 功能特性

- **双主题切换** — 运行时在 Material Design 3 卡片式布局和 MIUI 风格之间无缝切换
- **多语言支持** — 支持中文（简/繁）、英语、日语、韩语、法语、德语、西班牙语、俄语共 10 种语言
- **动态取色** — 支持 Android 12+ 的 Material You 动态取色
- **暗色模式** — 支持跟随系统、浅色、深色三种主题模式
- **数据持久化** — 使用 DataStore 保存用户偏好设置

## 🏗️ 架构设计

灵感来自 [KernelSU](https://github.com/tiann/KernelSU) 的双主题架构：

```
UiMode 枚举 (Material / Miuix)
    ↓
LocalUiMode (CompositionLocal)
    ↓
AppTheme Composable (分发到对应主题)
    ↓
MaterialAppTheme / MiuixAppTheme
    ↓
每个 Screen 独立实现 (*Material.kt / *Miuix.kt)
```

### 项目结构

```
app/src/main/java/com/demo/themeswitch/
├── MainActivity.kt
├── data/
│   ├── AppPreferences.kt
│   └── LocaleHelper.kt
└── ui/
    ├── UiMode.kt
    ├── theme/
    │   ├── AppTheme.kt
    │   ├── MaterialAppTheme.kt
    │   └── MiuixTheme.kt
    └── screen/
        ├── MainScreen.kt
        ├── HomeMaterial.kt
        ├── HomeMiuix.kt
        ├── SettingsMaterial.kt
        ├── SettingsMiuix.kt
        └── AboutScreen.kt
```

## 🛠️ 技术栈

- **Kotlin** — 主要语言
- **Jetpack Compose** — 声明式 UI
- **Material 3** — Material Design 3 组件
- **DataStore Preferences** — 轻量级数据持久化
- **Navigation Compose** — 导航框架

## 🚀 构建与运行

```bash
git clone https://github.com/xioo0317/ThemeSwitchDemo.git
cd ThemeSwitchDemo
./gradlew assembleDebug
```

## 📄 许可证

MIT License