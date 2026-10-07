# tools —— 构建辅助脚本

这些脚本不是模组的一部分，只是为了让本工程在**国内网络环境**下能顺利构建。

## 背景

本工程用 Architectury Loom 构建，Loom 在配置阶段会为**每一个** mod 依赖去它所在的 maven
发请求找 `-sources.jar`。而下面这些源在国内要么直连超时、要么返回反爬页面：

| 源 | 情况 |
|---|---|
| `maven.fabricmc.net` | 直连超时（TLS 握手挂死），会把整个构建卡住几十分钟 |
| `maven.minecraftforge.net` | 极慢，单个包可能几分钟 |
| `maven.neoforged.net` | 经常 Connection reset |
| BMCLAPI | 唯一的国内镜像，但它对 **GET** 请求会返回 JS 反爬页（HEAD 却是 200，容易误判为可用） |

阿里云 / 华为云 / 腾讯云只镜像 Maven Central，**没有** Forge、fabric-api、architectury、cloth-config
这些自有 maven。

## cache-warm —— 依赖缓存预热

```
gradlew -p tools/cache-warm warmup
gradlew -p tools/cache-warm warmup -Pcoords="g:a:v,g2:a2:v2"
```

用一个只挂国内镜像的独立工程，把依赖灌进 Gradle 共享缓存（`~/.gradle/caches/modules-2`）。
预热完成后主工程就能用 `--offline` 构建，完全不碰网络。

主工程离线构建报 `No cached version available for offline mode` 时，把报出的坐标补到
`build.gradle` 的 `DEPENDENCIES` 里再跑一次，或者用 `-Pcoords=` 临时加。

> 注意：`net.fabricmc.fabric-api` 系列 BMCLAPI 不镜像，得直连 `maven.fabricmc.net`，
> 国内时通时断，多试几次。

## local-maven —— 本地 maven 仓库（**不进版本库**）

存放国内各镜像都拿不到、官方源又极慢的构件，目前是 Forge 1.16.5 的
`net.minecraftforge:forge:1.16.5-36.2.39`（pom + jar + installer / userdev / universal / sources）
以及它 userdev 声明的整套 dev 依赖（modlauncher、accesstransformers、coremods、ASM 9.1 等）。

它在根 `build.gradle` 的 `repositories` 里排在**第一位**——必须如此，否则 Gradle 会在 BMCLAPI
找到模块描述符后只去 BMCLAPI 找构件，而那里对 GET 是反爬页。

被 `.gitignore` 排除了（24 MB 第三方 jar），换台机器构建 1.16.5 前需要重新生成：

```
# 在 PowerShell 里（执行策略禁止 .ps1 直接运行）
Invoke-Expression ([System.IO.File]::ReadAllText('tools/port1165/mirror-forge-deps.ps1'))
```

## port1165 / port1211 —— 两个新版本的构建脚本

- `adapt.ps1` —— 把从 1.18.2 复制来的模块适配成 1.16.5（改版本号、模块引用、mixin
  compatibilityLevel、fabric.mod.json / mods.toml 的版本区间）。
- `mirror-forge-deps.ps1` —— 见上。
- `fix-api.ps1` / `fix-api2.ps1` / `fix-api3.ps1` / `fix-corruption.ps1` —— 1.21.1 移植时批量改写
  API 调用的脚本，**已执行完毕**，留作记录（`fix-corruption.ps1` 是修 `fix-api.ps1` 里一个
  off-by-one 造成的损坏，勿重复运行）。

## 常用命令

```powershell
# 只构建某个新版本（避免其它模块的 Loom sources 拉取）
# 先临时把 settings.gradle 收窄到该版本的三/两个模块，再：
gradlew :common-1.21.1:compileJava --offline --console=plain

# javac 输出是 GBK，PowerShell 直接读会乱码，这样读：
$bytes = [System.IO.File]::ReadAllBytes('日志路径')
[System.Text.Encoding]::GetEncoding(936).GetString($bytes)
```

**切勿在 Gradle 运行中删除 `~/.gradle/caches/fabric-loom` 下的 `.lock` 文件**：那会让 Loom
判定为「上次异常终止」而重建缓存，进而重新联网拉取全部 sources，在国内网络下会卡很久。
