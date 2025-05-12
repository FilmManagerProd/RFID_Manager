# RFID Manager

## Overview
RFID Manager is an Android application designed to manage and scan RFID tags. It provides functionalities for inventory management, receiving and dispatching operations using RFID technology, and is backed by Cloud Storage using Firebase Realtime Database Storage.

## Features
- **Locale Management**: Supports English and Chinese language modes
- **RFID Scanning**: Efficiently scan and process RFID tags.
- **User Interface**: Intuitive UI for managing scanning operations.

## Installation
- On your Laptop or PC, go to [https://developer.android.com/tools/releases/platform-tools](https://developer.android.com/tools/releases/platform-tools)
- Install SDK Platform-Tools for your Operating System (OS). E.g Windows, MacOS or Linux.
- After installation is complete, open the Command Prompt or Terminal on your Laptop or PC.
- For Windows, press `Windows + R` and type `cmd`. Open the Command Prompt.
- For MacOS, press `Command + SPACE` to launch Spotlight. Type `Terminal` and open Terminal.
- For Linux, press `Ctrl + Alt + T` to open the Terminal.
- In the Command Prompt or Terminal, type the following command: `adb --version` and press `ENTER`
- You should see something like this:
```bash
Joshua@Joshuas-MacBook-Pro ~ % adb --version
Android Debug Bridge version 1.0.41
Version 35.0.2-12147458
Installed as /Users/joshua/Library/Android/sdk/platform-tools/adb
Running on Darwin 24.3.0 (arm64)
```
- If you do not see a version, please retry the installation from [https://developer.android.com/tools/releases/platform-tools](https://developer.android.com/tools/releases/platform-tools)
- On your Android Handheld PDA Scanner Device, go to `Settings > About Phone` and tap the `Build Number` 7 times to enable Developer Options.
- Go to `Settings > System > Developer Options` and enable USB Debugging.
- Connect the Android Handheld PDA Scanner Device to your Laptop or PC via a wired connection. Ensure you use a cable that supports Data Transfer.
- If you see a prompt on the device asking to trust the computer, tap `Allow`
- Go back to your Command Prompt or Terminal and type the following command: `adb devices` and press `ENTER`
- You should see the device listed.
- Ensure you have downloaded the APK file, and its located in your `Downloads` folder on your Laptop or PC.
- Go back to your Command Prompt or Terminal and navigate to the `Downloads` folder. E.g `cd Downloads`
- Run this command: `adb install -r RFID_Manager_ReleaseProdV1.0.apk`
- You should see this:
```bash
Performing Streamed Install
Success
```
- The APP should be successfully installed onto your Android Handheld PDA Scanner Device with the name `RFID`

## Usage
- Launch the application.
- Use the main interface to perform scanning operations.
- Click "Start" to start scanning for RFID Tags, and "Stop" to stop scanning for RFID Tags
- Click "Clear" to clear all scanning records.
- Click "Receive" to add the scanned tags to the Database
- Click "Dispatch" to dispatch the scanned tags from the Database
- To write data to tags, ensure only 1 item is scanned and click the "Write" button
- Key in the value to write to the tag, ensure the tag is in range of the scanner and confirm
- Ensure that other tags do not interfere with the tag you are writing to
- Switch languages using the language button.
- If any errors occur, please ensure that the device is connected to the internet, and try restarting it and launching the app again.

## License
This project is licensed under the GNU General Public License v3.0. See the LICENSE file for more details.

---

# RFID 管理器

## 概述
RFID 管理器是一款 Android 应用，旨在管理和扫描 RFID 标签。它提供库存管理、入库和出库操作功能，并使用 Firebase 实时数据库进行云存储支持。

## 功能
- **语言管理**：支持英文和简体中文模式
- **RFID 扫描**：高效扫描并处理 RFID 标签
- **用户界面**：直观的界面用于管理扫描操作

## 安装指南

- 在你的笔记本或电脑上，访问 [https://developer.android.com/tools/releases/platform-tools](https://developer.android.com/tools/releases/platform-tools)
- 根据你的操作系统（例如 Windows、MacOS 或 Linux）安装 SDK Platform-Tools。
- 安装完成后，在你的笔记本或电脑上打开命令提示符或终端。
- 对于 Windows，按下 `Windows + R` 并输入 `cmd`，打开命令提示符。
- 对于 MacOS，按下 `Command + SPACE` 打开 Spotlight，输入 Terminal 并打开终端。
- 对于 Linux，按下 `Ctrl + Alt + T` 打开终端。
- 在命令提示符或终端中输入以下命令：`adb --version`，然后按下 `ENTER` 键。
- 你应该会看到类似以下的输出：

```bash
Joshua@Joshuas-MacBook-Pro ~ % adb --version
Android Debug Bridge version 1.0.41
Version 35.0.2-12147458
Installed as /Users/joshua/Library/Android/sdk/platform-tools/adb
Running on Darwin 24.3.0 (arm64)
```
- 如果你没有看到版本信息，请重新从 [https://developer.android.com/tools/releases/platform-tools](https://developer.android.com/tools/releases/platform-tools) 下载安装。
- 在你的安卓手持 PDA 扫描设备上，进入`设置 > 关于手机`，连续点击 7 次`版本号`以启用开发者选项。
- 进入`设置 > 系统 > 开发者选项`，启用 USB 调试。
- 使用有数据传输功能的线缆，将安卓手持 PDA 扫描设备通过有线方式连接到你的笔记本或电脑。
- 如果设备上弹出信任电脑的提示，请点击“允许”。
- 回到命令提示符或终端，输入以下命令：`adb devices`，然后按下 `ENTER` 键。
- 你应该能看到设备已列出。
- 请确保你已经下载了 APK 文件，并且该文件位于笔记本或电脑的 Downloads 文件夹中。
- 回到命令提示符或终端，进入 `Downloads` 文件夹，例如：`cd Downloads`
- 运行以下命令：`adb install -r RFID_Manager_ReleaseProdV1.0.apk`
- 你应该会看到以下输出：
```bash
Performing Streamed Install
Success
```
- 该 APP 应该已经成功安装到你的安卓手持 PDA 扫描设备上，名称为 RFID。



## 使用方法
- 启动应用程序。
- 使用主界面执行扫描操作。
- 点击 “开始” 开始扫描 RFID 标签，点击 “停止” 停止扫描。
- 点击 “清除” 清空所有扫描记录。
- 点击 “入库” 将扫描到的标签添加到数据库。
- 点击 “出库” 将扫描到的标签从数据库中移除。
- 要将数据写入标签，请确保仅扫描 1 个物品，然后点击“写入”按钮。
- 输入要写入标签的值，确保标签在扫描范围内并确认。
- 确保其他标签不会干扰您正在写入的标签。
- 使用语言切换按钮在英文和中文之间切换。
- 如遇错误，请确保设备已连接互联网，然后重启设备并重新启动应用。

## 许可证
本项目基于 GNU 通用公共许可证 v3.0 发布。详情请参阅 LICENSE 文件。

---

Documentation written by [Lincoln](https://github.com/lincoln0623) and [Joshua](https://github.com/Sadliquid)

文档由 [Lincoln](https://github.com/lincoln0623) 和 [Joshua](https://github.com/Sadliquid) 撰写.
