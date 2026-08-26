# 更新日志

## [1.1.2-hotfix-1] — 2026-08-26

### 修复
- **1.18.2 打掉 1号线低站台门(APG/TRT)崩溃**:`RenderAPGDoorTianjinTRT.tryUpdateLightState` 在方块被打掉变成空气后仍尝试设置 `light` 属性导致崩溃,已加方块类型判断。
- **1.18.2 1号线低站台门 / 9号线高站台门线路图空白**:MTR 3.2.2 站点数据缺失时线路图生成器 NPE → 返回透明纹理;已加 `getInterchangeRoutes` null 安全 + `generateRouteMapFallback` 兜底(空数据时显示占位线路+站名,数据到位后自动刷新为完整线路图)。
- **津静线高站台门玻璃吊板「下一站」语言**:修复中英文切换(不再出现两个中文、无英文)。

---

## [1.1.2] — 2026-08-26

### 新增 — 屏蔽门系列（高低站台门、玻璃、吊板）
本版将 MTR4 的天津地铁屏蔽门/安全门系列完整移植（1.18.2 / 1.19.2 / 1.19.4 / 1.20.1 × Fabric / Forge）：

- **高站台门（全高屏蔽门 PSD）**：`psd_door_tianjin`（1号线）、`psd_door_tianjin_bmt`、`psd_door_tianjin_jinjing`（津静），及其 `psd_glass_tianjin*` 玻璃、`psd_glass_end_tianjin*` 端玻璃。
- **吊板**：`psd_top_tianjin`、`psd_top_tianjin_bmt`（放置时自动生成站名/线路图内容）。
- **低站台门（半高安全门 APG）**：`apg_door_tianjin`（1号线）、`apg_door_tianjin_bmt`、`apg_door_tianjin_trt`、`apg_door_tianjin_jinjing`，及其玻璃/端玻璃。
- 新增对应渲染器（`RenderPSDDoorTianjin/BMT`、`RenderPSDTopTianjin/BMT`、`RenderAPGDoorTianjin/Jinjing/TRT`、`RenderAPGGlassTianjinBMT/Jinjing/TRT`）。
- 吊板动态内容全面对齐 MTR4：站名、开往/下一站、线路图、环线顺/逆时针标识、TRT/Jinjing 专用版式、站名 -45° 斜排（`rotated_station_name`，默认开启）。
- PSD 单门现在正确放置天津吊板。

### 新增 — 功能
- **一键复制侧线设置**：在车辆段侧线界面新增按钮，把当前侧线的车种/运行参数复制到同车辆段所有侧线（`SidingScreenMixin`）。

### 修复
- **站外导向标志**：修复端块模型（侧面透明）、以及选择线路后不显示的问题（线路颜色/ID 匹配错位）。
- **乘客资讯显示屏（PIDS）**：自动模式只显示最近一个站台；未配置时默认显示现实时间。
- **时间显示器**：改用天津地铁等线字体；双数（双宽）版本显示对齐 MTR4。
- **站名牌**（1.18.2 渲染成白色）：修复站点数据缺失时回退为空白/白板的问题。
- **指示牌/站名指示牌**（1.18.2/1.19.2/1.19.4 选择标识不渲染）：修复 MTR 3.2.2 目的地文本空白导致透明的问题。
- **安检门**：修复物品/方块黑色或紫色贴图问题。
- **MTR 铁路仪表盘/侧线界面打不开**：修复 `SidingScreenMixin` 混入注入类型错误。

### 其他
- 各版本语言文件（lang）补齐并同步到所有加载器子工程。
- 混入（mixin）配置在全部加载器注册。
- 开源协议改为 MIT。
- 版本号：1.0.2 → 1.1.0 → 1.1.2。

---

## [1.0.2] — 早前版本
移植 MTR4 新增功能（8 个方块、游戏规则、烟雾报警音效、铁路牌平台选择），修复翻译键与版本差异。
