# 把从 1.18.2 复制来的模块适配成 1.16.5
$ErrorActionPreference = 'Stop'
$base = 'E:\Project\Tianjin-metro-MTR3'
$enc = New-Object System.Text.UTF8Encoding($false)

function Replace-InFile {
    param([string]$Path, [string]$From, [string]$To)
    if (-not (Test-Path $Path)) { Write-Host "  跳过(不存在) $Path"; return }
    $t = [System.IO.File]::ReadAllText($Path)
    $n = $t.Replace($From, $To)
    if ($n -ne $t) { [System.IO.File]::WriteAllText($Path, $n, $enc); Write-Host "  改 $([System.IO.Path]::GetFileName($Path)) : '$From' -> '$To'" }
}

# ---------- 1) build.gradle 版本号与模块引用 ----------
foreach ($f in 'fabric-1.16.5\build.gradle', 'forge-1.16.5\build.gradle', 'common-1.16.5\build.gradle') {
    $p = Join-Path $base $f
    $t = [System.IO.File]::ReadAllText($p)
    $o = $t
    $t = $t.Replace('1.18.2', '1.16.5')
    $t = $t.Replace('1182', '1165')
    $t = $t.Replace('JAVA_17', 'JAVA_8')
    # architectury 组名按版本区分
    $t = $t.Replace('${rootProject.architectury_id}', '${project.subArchitecturyId}')
    $t = $t.Replace('dev.architectury:architectury-fabric', '${project.subArchitecturyId}:architectury-fabric')
    $t = $t.Replace('dev.architectury:architectury-forge', '${project.subArchitecturyId}:architectury-forge')
    if ($t -ne $o) { [System.IO.File]::WriteAllText($p, $t, $enc); Write-Host "  适配 $f" }
}

# ---------- 2) mixin 配置：compatibilityLevel 降到 JAVA_8 ----------
Get-ChildItem (Join-Path $base 'fabric-1.16.5'), (Join-Path $base 'forge-1.16.5'), (Join-Path $base 'common-1.16.5') -Recurse -Filter '*.mixins.json' -ErrorAction SilentlyContinue | ForEach-Object {
    $t = [System.IO.File]::ReadAllText($_.FullName)
    $n = $t -replace '"compatibilityLevel"\s*:\s*"JAVA_17"', '"compatibilityLevel": "JAVA_8"'
    if ($n -ne $t) { [System.IO.File]::WriteAllText($_.FullName, $n, $enc); Write-Host "  compatibilityLevel -> JAVA_8 : $($_.Name)" }
}

# ---------- 3) fabric.mod.json ----------
$fmj = Join-Path $base 'fabric-1.16.5\src\main\resources\fabric.mod.json'
$t = [System.IO.File]::ReadAllText($fmj)
$t = $t.Replace('"minecraft": "1.18.2"', '"minecraft": "1.16.5"')
$t = $t.Replace('"java": ">=16"', '"java": ">=8"')
[System.IO.File]::WriteAllText($fmj, $t, $enc)
Write-Host '  fabric.mod.json 已改'

# ---------- 4) mods.toml ----------
$mt = Join-Path $base 'forge-1.16.5\src\main\resources\META-INF\mods.toml'
$t = [System.IO.File]::ReadAllText($mt)
$t = $t.Replace('loaderVersion = "[40,)"', 'loaderVersion = "[36,)"')
$t = $t.Replace('versionRange = "[40,)"', 'versionRange = "[36,)"')
$t = $t.Replace('versionRange = "[1.18.2,1.19)"', 'versionRange = "[1.16.5,1.17)"')
$t = $t.Replace('versionRange = "[4.0,)"', 'versionRange = "[1.32,)"')
[System.IO.File]::WriteAllText($mt, $t, $enc)
Write-Host '  mods.toml 已改'

Write-Host '完成'
