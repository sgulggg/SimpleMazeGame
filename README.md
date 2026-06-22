# 🧩 SimpleMazeGame

一款基于 Android Canvas 自定义渲染的触控迷宫探索游戏，支持多关卡生成、无尽模式、排行榜与物理碰撞优化。

---

## 🚀 Version

- V2.0（Current）：完整功能版本（推荐）
- V1.0：基础迷宫 + 多关卡 + 基础统计

---

## 🎮 功能介绍

### 🆕 V2.0 新增功能（核心升级）

---

### 🏁 启动页（StartActivity）

- 主入口界面
- 功能入口：
  - 开始游戏
  - 无尽模式
  - 选择关卡
  - 排行榜
  - 退出游戏

---

### 🗺️ 关卡选择界面（SelectLevelActivity）

- 2列网格缩略图展示关卡
- 实时迷宫预览
- test 关卡支持刷新生成

---

### 🏆 本地排行榜（LeaderboardActivity）

- SharedPreferences + JSON 持久化
- 排序规则：
  - 用时优先
  - 步数辅助排序
- Top 20 记录

---

### 🌱 随机关卡生成系统

#### Prim 迷宫生成算法

- createPrimLevel
- 更均匀路径
- 避免大面积实心块

---

#### 分支型生成算法（改进版）

- createBranchyRandomLevel
- 支持复杂分叉结构

---

### ♾️ 无尽模式（Endless Mode）

- 无限随机生成迷宫
- 每局独立计分
- 成绩写入排行榜
- levelIndex = -1

---

### 🎮 游戏体验优化

- 右上刷新按钮（无尽/测试关）
- UI 对齐优化
- 返回键统一处理
- 动画更流畅

---

### 🛠️ 稳定性修复

- 修复 iconGravity aapt 错误
- 修复 layout / id 问题
- 优化 Activity 生命周期

---

### 🧑‍💻 开发说明

主要修改文件：

- MazeLevel.java
- MainActivity.java
- StartActivity.java
- SelectLevelActivity.java
- LeaderboardActivity.java
- layout / strings 资源文件

---

### 🔮 已知问题 & 后续计划

- 迷宫生成参数优化（分支密度 / 随机种子）
- 建议新增：
  - 显示种子（可复现关卡）
  - 支持关卡分享
- Gradle 问题：
  - 配置本地 JDK 或 org.gradle.java.home

---

## 🎮 V1.0 功能回顾

### 核心玩法

- 触控迷宫探索
- 手指滑动控制小球
- 圆形角色 + 矩形墙体碰撞
- 分离轴移动（X → Y）
- 到达出口通关

---

### 多关卡系统

| 关卡 | 名称 | 尺寸 |
|------|------|------|
| 1 | 初探迷域 | 11×11 |
| 2 | 曲折回廊 | 13×13 |
| 3 | 深渊终局 | 15×15 |

功能：

- 计时（0.1s）
- 移动次数统计（手势级）
- 上一关 / 下一关 / 重开
- 通关弹窗
- 全部通关提示

---

### V1.0 创新点

| 创新点 | 说明 |
|--------|------|
| Canvas 自绘 | 无图片UI |
| 碰撞优化 | 圆-矩形最近点 |
| 手势统计 | 防抖动计步 |
| 数据分离 | 二维数组迷宫 |

---

## 🧪 V1.0 环境配置

- Android Studio
- JDK 11
- minSdk 24
- compileSdk 36
- Android 7.0+

依赖：

- appcompat
- material
- constraintlayout
- activity-ktx

---

## 📱 运行方式

1. Clone 项目
2. Android Studio 打开
3. Gradle Sync
4. Run

---

## 🔥 技术亮点总结

- Canvas 自定义渲染
- Prim + 分支迷宫生成
- 手势控制系统
- 本地排行榜
- SharedPreferences JSON
- 无尽模式设计
- 多 Activity 架构

---

## 📌 项目说明

本项目为《移动平台开发技术与应用》课程期末大作业（V2.0完整版本）。
