# Developer Documentation for RFID Manager

## Introduction
This document provides information for developers who wish to contribute to the RFID Manager. It includes setup instructions and code structure

## Project Setup
1. Clone the repository to your local machine.
2. Open the project in Android Studio.
3. Ensure you have the necessary SDKs and tools located in /libs
4. Ensure you have the required .env variables in local.properties

## Code Structure
- **MainActivity.java**: Contains the main logic for the application, including UI interactions and RFID scanning events.
- **LocaleHelper.java**: Manages locale settings and language switching.
- **Adapter and Entity Classes**: Located in the `adapter` and `entity` packages, these classes handle data representation and manipulation.
- **Utilities**: Located in the `utils` folder, these classes handle the abstraction of low-level methods from the SDK and as well as basic power ON and OFF of the RFID Scanner
- **Layouts**: Base layout is structured in AndroidManifest.xml, and the Homepage layout is structured in /res/layout/scan_activity.xml & /res/layout/tag_view.xml

---

# RFID 管理器 开发者文档

## 介绍
本文件为希望为 RFID 管理器 做出贡献的开发者提供信息，包括环境搭建说明和代码结构概览。

## 项目搭建
1. 将仓库克隆到本地。
2. 在 Android Studio 中打开项目。
3. 确保 `/libs` 文件夹中已包含所需的 SDK 和工具。
4. 在 `local.properties` 中配置所需的 `.env` 变量。

## 代码结构
- **MainActivity.java**：包含应用的主要逻辑，包括 UI 交互和 RFID 扫描事件处理。
- **LocaleHelper.java**：负责语言环境设置和语言切换。
- **Adapter 和 Entity 类**：位于 `adapter` 和 `entity` 包中，负责数据表示和操作。
- **工具类（Utilities）**：位于 `utils` 文件夹，封装 SDK 的底层方法以及 RFID 扫描器的基本开/关机操作。
- **布局文件（Layouts）**：
  - 基础配置在 `AndroidManifest.xml` 中定义。
  - 首页布局位于 `/res/layout/scan_activity.xml` 和 `/res/layout/tag_view.xml`。