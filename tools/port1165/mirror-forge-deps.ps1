# 将 Forge 1.16.5 userdev 声明的全部 dev 依赖镜像到本地 maven 仓库。
# 阿里镜像能拿到的走阿里，拿不到的走 maven.minecraftforge.net（慢，但都是小包）。
$ErrorActionPreference = 'Continue'
$ProgressPreference = 'SilentlyContinue'
$root = 'E:\Project\Tianjin-metro-MTR3\tools\local-maven'
$aliyun = 'https://maven.aliyun.com/repository/public'
$forge  = 'https://maven.minecraftforge.net'

# 坐标 -> 相对路径
$coords = @(
    'org.ow2.asm:asm:9.1',
    'org.ow2.asm:asm-commons:9.1',
    'org.ow2.asm:asm-tree:9.1',
    'org.ow2.asm:asm-util:9.1',
    'org.ow2.asm:asm-analysis:9.1',
    'cpw.mods:modlauncher:8.1.3',
    'cpw.mods:grossjava9hacks:1.3.3',
    'net.minecraftforge:accesstransformers:3.0.1',
    'org.antlr:antlr4-runtime:4.9.1',
    'net.minecraftforge:eventbus:4.0.0',
    'net.minecraftforge:forgespi:3.2.0',
    'net.minecraftforge:coremods:4.0.6',
    'net.minecraftforge:unsafe:0.2.0',
    'com.electronwill.night-config:core:3.6.3',
    'com.electronwill.night-config:toml:3.6.3',
    'org.jline:jline:3.12.1',
    'org.apache.maven:maven-artifact:3.6.3',
    'net.jodah:typetools:0.8.3',
    'org.apache.logging.log4j:log4j-api:2.15.0',
    'org.apache.logging.log4j:log4j-core:2.15.0',
    'org.apache.logging.log4j:log4j-slf4j18-impl:2.15.0',
    'net.minecrell:terminalconsoleappender:1.2.0',
    'net.sf.jopt-simple:jopt-simple:5.0.4',
    'org.spongepowered:mixin:0.8.4',
    'net.minecraftforge:nashorn-core-compat:15.1.1.1'
)

function Get-RelPath($g, $a, $v, $ext) {
    ($g -replace '\.', '/') + "/$a/$v/$a-$v.$ext"
}

$okCount = 0; $failList = @()
foreach ($c in $coords) {
    $parts = $c.Split(':')
    $g = $parts[0]; $a = $parts[1]; $v = $parts[2]
    $dir = Join-Path $root (($g -replace '\.', '\') + "\$a\$v")
    New-Item -ItemType Directory -Force -Path $dir | Out-Null
    foreach ($ext in 'jar', 'pom') {
        $rel = Get-RelPath $g $a $v $ext
        $out = Join-Path $dir "$a-$v.$ext"
        if ((Test-Path $out) -and (Get-Item $out).Length -gt 200) { $okCount++; continue }
        $got = $false
        foreach ($base in @($aliyun, $forge)) {
            try {
                Invoke-WebRequest -Uri "$base/$rel" -OutFile $out -TimeoutSec 300 -UseBasicParsing -ErrorAction Stop
                if ((Get-Item $out).Length -gt 200) { $okCount++; $got = $true; break }
            } catch { }
        }
        if (-not $got) { $failList += "${c}:$ext"; Remove-Item $out -Force -ErrorAction SilentlyContinue }
    }
    Write-Host "  $c"
}

# forge launcher classifier
$dir = Join-Path $root 'net\minecraftforge\forge\1.16.5-36.2.39'
New-Item -ItemType Directory -Force -Path $dir | Out-Null
$out = Join-Path $dir 'forge-1.16.5-36.2.39-launcher.jar'
if (-not ((Test-Path $out) -and (Get-Item $out).Length -gt 200)) {
    try {
        Invoke-WebRequest -Uri "$forge/net/minecraftforge/forge/1.16.5-36.2.39/forge-1.16.5-36.2.39-launcher.jar" -OutFile $out -TimeoutSec 600 -UseBasicParsing -ErrorAction Stop
        $okCount++
    } catch { $failList += 'forge:launcher' }
}

Write-Host ""
Write-Host "成功 $okCount 个文件"
if ($failList.Count) { Write-Host "失败: $($failList -join ', ')" } else { Write-Host "无失败" }
