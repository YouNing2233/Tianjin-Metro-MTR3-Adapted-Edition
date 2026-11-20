# Tianjin Metro · MTR3 Adapted Edition / 天津地铁模组 · MTR3 适配版

An add-on for [Minecraft Transit Railway](https://github.com/jonafanho/Minecraft-Transit-Railway), ported and
refactored from the upstream [Tianjin Metro mod](https://github.com/ZiYueCommentary/Tianjin-Metro).

基于上游 [天津地铁模组](https://github.com/ZiYueCommentary/Tianjin-Metro) 移植与重构的
[Minecraft Transit Railway](https://github.com/jonafanho/Minecraft-Transit-Railway) 附属模组。

---

## Support matrix / 支持矩阵

| Minecraft | Loader / 加载器 | MTR dependency / MTR 依赖 |
| --- | --- | --- |
| 1.16.5 | Fabric / Forge | MTR 3.2.2 |
| 1.17.1 | Fabric / Forge | MTR 3.2.2 |
| 1.18.2 | Fabric / Forge | MTR 3.2.2 |
| 1.19.2 | Fabric / Forge | MTR 3.2.2 |
| 1.19.4 | Fabric / Forge | MTR 3.2.2 |
| 1.20.1 | Fabric / Forge | MTR 3.x — on 1.20.1 this means a 3.x API fork such as YMTR / 1.20.1 上需使用 YMTR 等 3.x API 分支 |
| 1.21.1 | Fabric / NeoForge | NeoMTR 300.x |

Java: **16+** for 1.16.5–1.19.4, **17+** for 1.20.1, **21** for 1.21.1.
Java 版本：1.16.5–1.19.4 需 16+，1.20.1 需 17+，1.21.1 需 21。

The mod is client-and-server side; it is required on both ends.
本模组为客户端 + 服务端模组，两端都需安装。

---

## Build / 构建

**Requirements:** JDK 21, and the MTR jar of the matching Minecraft version.
**环境要求：** JDK 21，以及对应 Minecraft 版本的 MTR jar。

```bash
# MTR jars are read from E:/Project/MTRjars by default,
# or from the directory given by -PmtrJarDir=<dir>.
./gradlew :fabric:build -PmtrJarDir=/path/to/mtr-jars
# outputs build/TianjinMetro-<loader>-<mc>-<version>.jar
```

每次切换 Minecraft 版本或首次配置时，请依次：同步 Gradle 项目 → 运行根项目 Gradle 的 `setupLibrary` 任务 → 再次同步。
On first setup or after switching Minecraft versions: sync the Gradle project, run the root project's
`setupLibrary` task, then sync again.

The repository ships 14 targets (`fabric`/`forge` × 1.16.5/1.17.1/1.18.2/1.19.2/1.19.4/1.20.1,
`fabric-1.21.1`, `neoforge-1.21.1`). Each module can be built on its own;
CI builds all of them in a matrix (see `.github/workflows/build.yml`).
本仓库共 14 个目标（`fabric`/`forge` × 1.16.5/1.17.1/1.18.2/1.19.2/1.19.4/1.20.1，外加 `fabric-1.21.1`、`neoforge-1.21.1`），
每个模块可单独构建；CI 通过矩阵一次性构建全部版本（见 `.github/workflows/build.yml`）。

---

## Licence & credits / 版权、致谢与许可

| Role / 角色 | Credit / 署名 |
| --- | --- |
| Original mod author / 原模组作者 | 子悦解说 (ZiYueCommentary) · MIT |
| Original mod / 原模组地址 | https://github.com/ZiYueCommentary/Tianjin-Metro |
| Port author / 本移植版作者 | 攸宁 (formerly 泠惜i / __Lingxii__) · GPL-3.0-or-later |
| Contributors / 贡献者 | 厦航在拍车的无名机长 |
| Thanks / 特别致谢 | 感谢江兴城、临岚工艺等服务器提出的问题反馈与测试支持 |

**This is a mixed-licence project / 本项目是复合许可（mixed-licence）项目：**

- Files taken from upstream **unmodified** / 直接取自上游、**未修改**的文件 — **MIT** (`LICENSE-MIT`)
- Upstream files **modified** by this port / 本移植版**修改过**的上游文件 — **GPL-3.0-or-later**, upstream parts keep their MIT notice / 整体 GPL-3.0-or-later，其中上游部分仍保留 MIT 声明
- Files **added** by this port / 本移植版**新增**的文件 — **GPL-3.0-or-later** (`LICENSE-GPL-3.0-or-later`)
- The **distribution as a whole** / **整个发行包** — distributed under **GPL-3.0-or-later** with the MIT notices preserved / 按 GPL-3.0-or-later 分发，并保留 MIT 声明

`SPDX-License-Identifier: MIT AND GPL-3.0-or-later`

Per-file provenance is recorded in [`LICENSING.md`](LICENSING.md) and [`licensing/file-map.txt`](licensing/file-map.txt)
(regenerable with `tools/classify-license.ps1` against any upstream revision).
逐文件归属见 [`LICENSING.md`](LICENSING.md) 与 [`licensing/file-map.txt`](licensing/file-map.txt)
（可用 `tools/classify-license.ps1` 针对上游任一版本重新生成）。

In short / 简而言之：upstream MIT rights are not revoked by this project's GPL notice; the code added or modified
here is GPLv3, so redistributing a modified build requires the corresponding source — this repository is that source.
上游的 MIT 权利不会被本项目的 GPL 声明收走；本移植版新增与修改的部分是 GPLv3，分发修改版本时需提供对应源码（本仓库即为源码）。

---

## Repository layout / 仓库结构

| Directory / 目录 | Purpose / 说明 |
| --- | --- |
| `common`, `common-1.16.5`, `common-1.17.1`, `common-1.18.2`, `common-1.19.2`, `common-1.19.4`, `common-1.21.1` | Code shared per Minecraft version / 各 MC 版本共享源码 |
| `fabric`, `forge`, `fabric-1.21.1`, `neoforge-1.21.1`, … | Loader/version platform layers / 各加载器与 MC 版本的平台层 |
| `tools/` | Build helpers, language-file and licence-map generators / 构建辅助、语言文件生成、许可清单生成脚本 |
| `licensing/` | Generated per-file licence map / 自动生成的逐文件许可清单 |
