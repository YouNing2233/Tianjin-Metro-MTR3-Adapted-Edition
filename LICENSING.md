# 许可说明 / Licensing

**Tianjin Metro - MTR3 Adapted Edition** 是复合许可（mixed-licence）项目，**不是**二选一的双许可项目。
不同组成部分适用不同条款：

| 组成部分 | 许可证 |
| --- | --- |
| 与上游完全一致的文件（A 类） | **MIT**（`LICENSE-MIT`） |
| 本移植版修改过的上游文件（B 类） | **GPL-3.0-or-later** 整体分发；其中上游部分仍保留 MIT 声明 |
| 本移植版新增的文件（C 类） | **GPL-3.0-or-later**（`LICENSE-GPL-3.0-or-later`） |
| 整个发行包（组合成果） | **GPL-3.0-or-later**，并保留 MIT 声明 |
| 依赖（MTR、Fabric API、Architectury、Cloth Config、Mod Menu、Forge/NeoForge） | 各自许可证；**不随本模组分发** |
| 上游名称与美术素材的商标权 | 未授予，见 `LICENSE` 末节 |

`SPDX-License-Identifier: MIT AND GPL-3.0-or-later`

---

## 1. 为什么是复合许可

上游模组 [Tianjin Metro](https://github.com/ZiYueCommentary/Tianjin-Metro) 采用 **MIT**，
`Copyright (c) 2022-2024 ZiYueCommentary`。MIT 允许修改与再分发，但要求**保留版权与许可声明**。
因此：

- 我们**不能**把上游代码整体改写为「GPL-3.0-only」并当作自己的作品发布——MIT 声明必须保留；
- 我们**可以**把自己新增/修改的部分按 GPL-3.0-or-later 授权（MIT 与 GPLv3 兼容）；
- 两者混在同一个 jar 里时，按 GPLv3 §5(c)，**整个组合作品按 GPL-3.0-or-later 分发**，MIT 声明随附保留。

这与上游、与本项目之外的 MTR 附属模组生态都不冲突：MTR 本体同样是 MIT。

---

## 2. 三类文件的定义

1. **A 类（MIT）**：逻辑路径在上游存在，且本仓库中的副本与上游对应文件**逐字节一致**。
2. **B 类（混合）**：逻辑路径在上游存在，但本仓库中的副本**内容有差异**（适配 Architectury、
   移植 MTR 3.x、修复、扩展等）。作为整体按 GPL-3.0-or-later 分发，上游部分仍保留 MIT 声明。
3. **C 类（原创）**：上游**没有**对应文件，为本次移植新增。GPL-3.0-or-later。

边界以 `licensing/file-map.txt` 为准（生成文件，逐条列出 1140 个逻辑文件）。
若某个文件归属存疑，**按 B 类（混合）处理，并保留 MIT 声明**——这是最保守、也最安全的一侧。

---

## 3. 统计（生成脚本实测）

逻辑文件（同一文件在 6 个源码模块中的副本算 1 条）：

| 类别 | java | 资源 | 小计 | 许可证 |
| --- | --- | --- | --- | --- |
| A 上游原样 | 0 | 245 | 245 | MIT |
| B 上游已修改 | 148 | 581 | 729 | GPL-3.0-or-later（保留 MIT） |
| C 本移植版原创 | 59 | 107 | 166 | GPL-3.0-or-later |
| **合计** | **207** | **933** | **1140** | `MIT AND GPL-3.0-or-later` |

*A 类 java 为 0 的原因很简单：上游代码全部经过 Architectury 重构与 MTR 3.x API 回移，
没有一行 java 能保持逐字节一致；保持原样的只有美术资源。*

### 目录级分布

| 类别 | 目录 | 数量 |
| --- | --- | --- |
| A | `assets/tjmetro/textures` | 223 |
| A | `assets/tjmetro/models` | 16 |
| A | `assets/tjmetro/advertisements` | 3 |
| A | `assets/tjmetro/{blockstates,font,sounds}` | 3 |
| B | `assets/tjmetro/models` | 396 |
| B | `assets/tjmetro/blockstates` | 162 |
| B | `assets/tjmetro/lang` | 13 |
| B | `assets/tjmetro/{textures,font}` | 2 |
| B | `assets/tjmetro/{pids_tianjin_ads.json,sounds.json}` | 2 |
| B | `fabric.mod.json`、`META-INF/mods.toml`、`pack.mcmeta`、`icon.png`、`tjmetro.mixins.json`、`tjmetro-fabric.mixins.json` | 6 |
| C | `assets/tjmetro/models` | 40 |
| C | `assets/tjmetro/blockstates` | 31 |
| C | `assets/tjmetro/textures` | 30 |
| C | `assets/tjmetro/font` | 2 |
| C | `assets/tjmetro/icon.png` | 1 |
| C | `META-INF/neoforge.mods.toml`、`tjmetro.forge.mixins.json`、`tjmetro.neoforge.mixins.json` | 3 |

---

## 4. C 类文件（本移植版原创，GPL-3.0-or-later）

### 4.1 Java（59 个）

```
ziyue/tjmetro/MainFabric.java
ziyue/tjmetro/MainFabricClient.java
ziyue/tjmetro/MainForge.java
ziyue/tjmetro/PlatformUtils.java
ziyue/tjmetro/block/IBlockExtends.java
ziyue/tjmetro/block/BlockCeilingNotLit.java
ziyue/tjmetro/block/BlockDecorationLight.java
ziyue/tjmetro/block/BlockPlatformSlab.java
ziyue/tjmetro/block/BlockRailwaySignTianjinDouble.java
ziyue/tjmetro/block/BlockRouteMapBMTWall.java
ziyue/tjmetro/block/BlockStationColorCeilingNotLit.java
ziyue/tjmetro/block/base/BlockCustomContentBlockBase.java
ziyue/tjmetro/block/base/RenderAttachmentBlockEntity.java
ziyue/tjmetro/client/ClientCache.java
ziyue/tjmetro/client/Config.java
ziyue/tjmetro/client/ConfigScreen.java
ziyue/tjmetro/client/ConfigScreenOpener.java
ziyue/tjmetro/client/IDrawingExtends.java
ziyue/tjmetro/client/MissingClothScreen.java
ziyue/tjmetro/data/IGuiExtends.java
ziyue/tjmetro/entity/base/BaseContainerBlockEntityMapper.java
ziyue/tjmetro/entity/base/RandomizableContainerBlockEntityMapper.java
ziyue/tjmetro/fabric/RegistryImpl.java
ziyue/tjmetro/fabric/TianjinNetwork.java
ziyue/tjmetro/fabric/TianjinNetworkClient.java
ziyue/tjmetro/fabric/mixin/RenderTrainsFabricMixin.java
ziyue/tjmetro/forge/ClientRegistryImpl.java
ziyue/tjmetro/forge/RegistryImpl.java
ziyue/tjmetro/forge/mixin/GameRulesBooleanValueAccessor.java
ziyue/tjmetro/forge/mixin/GameRulesIntegerValueAccessor.java
ziyue/tjmetro/forge/mixin/StorageMinecartEntityMixin.java
ziyue/tjmetro/inventory/ContainerMenu.java
ziyue/tjmetro/mixin/ClientDataMixin.java
ziyue/tjmetro/mixin/ConfigScreenMixin.java
ziyue/tjmetro/mixin/CustomResourcesMixin.java
ziyue/tjmetro/mixin/PacketTrainDataGuiClientMixin.java
ziyue/tjmetro/mixin/RenderTrainsMixin.java
ziyue/tjmetro/neoforge/ClientRegistryImpl.java
ziyue/tjmetro/neoforge/RegistryImpl.java
ziyue/tjmetro/neoforge/mixin/FallingBlockMixin.java
ziyue/tjmetro/neoforge/mixin/GameRulesBooleanValueAccessor.java
ziyue/tjmetro/neoforge/mixin/GameRulesIntegerValueAccessor.java
ziyue/tjmetro/neoforge/mixin/StorageMinecartEntityMixin.java
ziyue/tjmetro/packet/IPacket.java
ziyue/tjmetro/packet/PacketGuiClient.java
ziyue/tjmetro/packet/PacketGuiServer.java
ziyue/tjmetro/render/ModelAPGDoorBottom.java
ziyue/tjmetro/render/ModelAPGDoorLight.java
ziyue/tjmetro/render/ModelSingleCube.java
ziyue/tjmetro/render/RenderPIDSTianjinSingle.java
ziyue/tjmetro/render/RenderRailwaySignBMTBase.java
ziyue/tjmetro/render/RenderRailwaySignTianjinDouble.java
ziyue/tjmetro/render/RenderRouteMapBMTWall.java
ziyue/tjmetro/render/RenderTianjinAPGDoor.java
ziyue/tjmetro/render/RenderTianjinPSDDoor.java
ziyue/tjmetro/render/RenderTimeDisplayEven.java
ziyue/tjmetro/screen/CustomContentScreen.java
ziyue/tjmetro/screen/StationNavigatorScreen.java
ziyue/tjmetro/screen/TianjinImageButton.java
```

此外，**全部构建与工程脚本**均为原创（GPL-3.0-or-later）：`build.gradle`、`settings.gradle`、
`gradle.properties`、各模块 `build.gradle`、`tools/**`、`.github/workflows/**`、`licensing/**`。

### 4.2 资源（107 个）

新增方块/方块的模型与贴图、`META-INF/neoforge.mods.toml`、NeoForge/1.16.5 专用 mixins 配置等，
见 `licensing/file-map.txt` 中 `C` 开头的行。

---

## 5. B 类文件（混合，GPL-3.0-or-later + 保留 MIT）

上游存在、但被本移植版修改过的文件：148 个 java + 581 个资源。
典型例如 `blockstates/**`（新增 `half=top|bottom` 变体）、`models/**`（贴图/模型重排）、
`lang/*.json`（补齐与新增键）、`fabric.mod.json` / `mods.toml`（依赖与元数据）。

对 B 类文件：

- 上游衍生的部分仍是 **MIT**（版权归 ZiYueCommentary）；
- 本移植版修改的部分是 **GPL-3.0-or-later**（版权归 __Lingxii__）；
- 文件**整体**随发行包按 GPL-3.0-or-later 分发，且**必须保留 MIT 声明**。

---

## 6. 修改标记（GPLv3 §5(a)）

本仓库本身就是「已修改」的显著标记：

- 仓库名与模组名均标明 **"MTR3 Adapted Edition"**；
- `README.md` 第 2 节列明原作者、原模组链接与移植作者；
- 每个 jar 的 `META-INF/licenses/` 内随附 `LICENSE`、`LICENSE-MIT`、`LICENSE-GPL-3.0-or-later`、`LICENSING.md` 四份文件；
- `fabric.mod.json` / `mods.toml` / `neoforge.mods.toml` 的 license 字段为 `MIT AND GPL-3.0-or-later`；
- 提交历史完整保留（`git log` 可追溯每一处改动的时间与内容）。

---

## 7. 常见问题

**Q：我可以在整合包里放这个模组吗？**
可以。原样放入 jar、保留名称与许可文件即可；需同时满足 GPLv3（向接收者提供对应源码，本仓库即为源码）
与 MIT 通知要求。

**Q：我能闭源二次开发吗？**
不能。B、C 两类文件是 GPL-3.0-or-later。若只需要宽松授权，请**只取 A 类文件**（纯 MIT），
或直接使用上游原版（MIT）。

**Q：我能把整个项目改标成 MIT 吗？**
不能。你可以把**自己新写的**代码按 MIT 或其他兼容许可发布，但已经进入 GPLv3 覆盖范围的
组合成果不能单方面收回；同时上游 MIT 部分的权利也不受影响。

**Q：上游能拿去用我们的代码吗？**
可以——GPLv3 允许，但上游若把我们的代码并入其 MIT 项目，需要同时满足 GPLv3 的相应义务
（这也是为什么上游与第三方如需复用本移植版，应当先沟通）。

**Q：为什么 metadata 曾经写着 `GPL-3.0-only`，而 `LICENSE` 是 MIT？**
历史遗留错误（见 git 历史 `e18bdfd` → `838b805`）。现已统一为 `MIT AND GPL-3.0-or-later`，
并以 `licensing/file-map.txt` 作为逐文件依据。

---

## 8. 重新生成清单

```powershell
git clone https://github.com/ZiYueCommentary/Tianjin-Metro E:\tmp\Tianjin-Metro
powershell -NoProfile -File tools/classify-license.ps1 -Upstream E:\tmp\Tianjin-Metro
```

脚本会把 `licensing/file-map.txt` 重写为当前状态，并打印 A/B/C 三类计数。
上游代码已迁到 `ziyue/tjmetro/mod/**`，脚本会自动归一化到本项目的 `ziyue/tjmetro/**` 布局。
