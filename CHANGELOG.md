# 更新日志 / Changelog

天津地铁 MTR3 适配版 —— 自 CI（GitHub Actions）首次自动构建以来的全部版本。

> 目标平台：Minecraft 1.16.5 / 1.17.1 / 1.18.2 / 1.19.2 / 1.19.4 / 1.20.1 / 1.21.1 × Fabric / Forge（1.21.1 为 Fabric + NeoForge）

---

## 1.1.3.7

### 新增
- **乘客资讯显示屏（天津、墙上）/（天津、2号线、墙上）**：贴墙放置、占 1 格、薄板碰撞箱，右键沿用 PIDS 配置界面，可播广告。（仅 1.20.1）
- **乘客资讯显示屏（吊板版）×2**：贴在屏蔽门吊板上，2.9 × 0.9 格横屏；到站信息页 ↔ 线路图页 15 秒轮播，本站台线路 20 秒内有车时自动切「列车到站」页；底部滚动条滚广告文字；线路图用津静线风格生成器渲染，白底。（仅 1.20.1）
- **列车到站页编组条**：移植 mtr-external-lights 的 `PidsXiamenConsist` 绘制逻辑（GPL 归属已注明），按 `ScheduleEntry.trainCars` 显示车厢编组，白色底带 + 斜车头。
- **「仅显示首末车时间」开关**：PIDS 配置界面新增复选框；首末班时刻与终点站取自 MTR 车辆段（`Depot.departures` / `getFrequency`）并做时区换算，不再读取只含未来班次的站点日程。
- **方块「金属柱（原色）」**：复用「金属柱（9号线）」模型，颜色取贴图原色，可与既有金属柱互连。

### 变更
- 吊板版 PIDS 背景 `#0537D1`，字体沿用 MTR/原版直绘（去掉图内烘焙底色与描边）。
- 「车门开启中」提示改为「请先上后下，谨防夹伤」。

### 说明
- 本轮新增的墙上 / 吊板 PIDS 与首末车开关**目前只在 1.20.1（Fabric + Forge）构建里提供**，其余 12 个包保持 1.1.3.5 的功能集，后续版本会逐树补齐。

---

## 1.1.3.5

### 修复
- **Forge 全版本启动崩溃**：扳手开关屏蔽门的 `@Redirect` 处理器写成 `private static`，而目标 `use` / `useWithoutItem` 是实例方法，Mixin 0.8.5（Forge 1.16.5–1.20.1）会抛 `InvalidInjectionException: 'static' modifier of handler method does not match target`。已改为实例方法，影响面覆盖 1.1.3.3 / 1.1.3.4 / 1.1.3.6 的 Forge 包。
- 字体文件名 `HYWenHei-85W.ttf` 改为小写 `hywenhei-85w.ttf`（1.19+ 资源包拒绝大写路径，导致 `pids` 字体从未真正加载）。

---

## 1.1.3.6

### 变更
- 乘客资讯显示屏（天津、2号线）文字位置微调：到站行 `ARRIVAL_Y=45.5`、时钟 `CLOCK_Y=99.2`、日期 `DATE_Y=106.4`。

---

## 1.1.3.4

### 新增
- **三扇宽高屏蔽门 + 三扇宽半高屏蔽门**：门洞 3 格宽，两扇同向收进玻璃袋、单独一扇反向收进另一侧，各滑 1 格；开口方向存于方块实体 NBT，扳手点击单扇切换 6B / 8B。
- **三格宽吊板（津静线样式）**：3 格吊板画一整幅站名牌贴图，刷子切箭头、扳手切「站名 / 下一站」整组生效。

### 修复
- 1.21.1 `ModelSingleCube.renderToBuffer` 参数改为 5 参 ARGB 重载。
- 1.16.5 `ItemStack.is(Item)` 改为 `getItem() == ItemList.WRENCH.get()`。
- `Component.translatable` 改为 `mtr.mappings.Text.translatable`（1.16.5/1.17.1 无 `Component`）。

---

## 1.1.3.3

### 新增
- **扳手也能开关 MTR 平台门**（`BlockPSDAPGDoorBase` 的 `checkHoldingBrush` 重定向，兼容原版刷子）。
- **卷帘门（转角）**：从 mtr-external-lights 移植 OBJ，8 个烘焙模型覆盖 4 朝向 × 上/下部。
- **乘客资讯显示屏（天津、2号线）**：双面 / 单面两种，左栏线路名 + 本次列车 + 倒计时 + 开往 + 左下 LOGO、时钟、日期，右侧广告位，底部滚动条。

### 变更
- 删除高屏蔽门玻璃显示屏。

---

## 1.1.3.2

### 变更
- dynatex 字符串拼接优化（`ClientCache` 7 棵树共 24 处 `String.format` → 拼接）。
- 出口标志 `getExitSignLetter` 补 `backgroundColor`，修复缓存键碰撞导致的贴图串台。

---

## 1.1.3.1

### 新增
- **垃圾桶改为真正的双层方块**（`Half` 属性，上下两半贴图与形状独立，成对放置/破坏）。
- **1.21.1 长椅落座**：`EntitySeat` / `RenderSeat` / `EntityTypes.SEAT`，右击坐下、再右击起身。
- **多语言扩充到 13 种**：`en_us`、`zh_cn`、`zh_tw`、`zh_hk`、`ja_jp`、`fr_fr`、`de_de`、`pl_pl`、`cs_cz`、`sk_sk`、`ar_aa`、`ko_kr`、`tr_tr`。
- **复合许可证**：上游代码 MIT + 本项目新增/修改部分 GPL-3.0-or-later（`LICENSE` / `LICENSE-MIT` / `LICENSE-GPL-3.0-or-later` / `LICENSING.md` / `licensing/file-map.txt`，jar 内 `META-INF/licenses/` 五条目）。

### 新增平台
- **Minecraft 1.17.1**（Fabric + Forge），使发布矩阵达到 14 个包。

---

## 构建矩阵

| Minecraft | Fabric | Forge | NeoForge |
|---|---|---|---|
| 1.16.5 | ✅ | ✅ | — |
| 1.17.1 | ✅ | ✅ | — |
| 1.18.2 | ✅ | ✅ | — |
| 1.19.2 | ✅ | ✅ | — |
| 1.19.4 | ✅ | ✅ | — |
| 1.20.1 | ✅ | ✅ | — |
| 1.21.1 | ✅ | — | ✅ |
