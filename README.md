# 天津地铁模组 · MTR3 Adapted Edition

一个 [Minecraft Transit Railway](https://github.com/jonafanho/Minecraft-Transit-Railway) 的附属模组，
基于上游 [天津地铁模组](https://github.com/ZiYueCommentary/Tianjin-Metro) 移植与重构。

## 支持矩阵

| Minecraft | 加载器 | MTR 依赖 |
| --- | --- | --- |
| 1.16.5 | Fabric / Forge | MTR 3.2.2 |
| 1.18.2 | Fabric / Forge | MTR 3.2.2 |
| 1.19.2 | Fabric / Forge | MTR 3.2.2 |
| 1.19.4 | Fabric / Forge | MTR 3.2.2 |
| 1.20.1 | Fabric / Forge | MTR 3.x（1.20.1 上为 YMTR 等 3.x API 分支） |
| 1.21.1 | Fabric / NeoForge | NeoMTR 300.x |

## 构建步骤

1. 克隆仓库
2. 同步 Gradle 项目
3. 切换 Minecraft 版本 / 初次运行时：
   1. 同步 Gradle 项目
   2. 运行根项目 Gradle 的 `setupLibrary` 任务
   3. 再次同步 Gradle 项目

本地构建需要把对应版本的 MTR jar 放在 `E:/Project/MTRjars`（或通过 `-PmtrJarDir=<目录>` 指定）。

## 版权、致谢与许可

| 角色 | 署名 |
| --- | --- |
| 原模组作者 | 子悦解说（ZiYueCommentary）· MIT |
| 原模组地址 | https://github.com/ZiYueCommentary/Tianjin-Metro |
| 本移植版作者 | 泠惜i（\_\_Lingxii\_\_）· GPL-3.0-or-later |
| 贡献者 | jiangxingyunjian、厦航在拍车的无名机长 |
| 特别致谢 | 感谢江兴城、临岚工艺等服务器提出的问题反馈与测试支持 |

**本项目是复合许可（mixed-licence）项目，不是二选一的双许可项目：**

- 直接取自上游、**未修改**的文件 —— **MIT**（`LICENSE-MIT`）
- 本移植版**修改过**的上游文件 —— 整体 **GPL-3.0-or-later**，其中上游部分仍保留 MIT 声明
- 本移植版**新增**的文件 —— **GPL-3.0-or-later**（`LICENSE-GPL-3.0-or-later`）
- **整个发行包** —— 按 **GPL-3.0-or-later** 分发，并保留 MIT 声明

`SPDX-License-Identifier: MIT AND GPL-3.0-or-later`

逐文件归属见 [`LICENSING.md`](LICENSING.md) 与 [`licensing/file-map.txt`](licensing/file-map.txt)
（可用 `tools/classify-license.ps1` 针对上游任一版本重新生成）。

简而言之：上游的 MIT 权利不会被本项目的 GPL 声明收走；本移植版新增与修改的部分是 GPLv3，
分发修改版本时需提供对应源码（本仓库即为源码）。

## 仓库结构

| 目录 | 说明 |
| --- | --- |
| `common` / `common-1.16.5` / `common-1.18.2` / `common-1.19.2` / `common-1.19.4` / `common-1.21.1` | 各 MC 版本共享源码 |
| `fabric` / `forge` / `neoforge-1.21.1` 等 | 各加载器与 MC 版本的平台层 |
| `tools/` | 构建辅助、语言文件生成、许可清单生成脚本 |
| `licensing/` | 自动生成的逐文件许可清单 |
