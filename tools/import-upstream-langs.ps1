# 从上游（ZiYueCommentary/Tianjin-Metro）导入除 ja_jp / fr_fr / zh_tw 之外的语言文件。
#
# 说明：
#   * 这些语言我们不自行翻译，直接沿用上游 Weblate 的成品，保证与上游一致；
#   * 上游是部分翻译（例如 de_de 234 键、ko_kr 115 键），缺失的键由 Minecraft
#     自动回退到 en_us，属于正常做法；
#   * 只保留 en_us 里存在的键，避免把上游已删除的键带进来；
#   * zh_hk 例外：上游港版只有 177 键，这里用我们的 zh_tw 补齐到全量，
#     并按港版用字习惯把「臺」→「台」。
#
# 前置：上游语言文件需先下载到 build/upstream-lang/<locale>.json
# 用法：powershell -NoProfile -ExecutionPolicy Bypass -File tools/import-upstream-langs.ps1

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$langDirs = @(
    'common', 'common-1.16.5', 'common-1.18.2', 'common-1.19.2', 'common-1.19.4', 'common-1.21.1'
) | ForEach-Object { Join-Path $root "$_\src\main\resources\assets\tjmetro\lang" }
$upDir = Join-Path $root 'build\upstream-lang'
$enPath = Join-Path $root 'common\src\main\resources\assets\tjmetro\lang\en_us.json'
$twPath = Join-Path $root 'common\src\main\resources\assets\tjmetro\lang\zh_tw.json'

function Read-Json([string]$path) {
    return ([IO.File]::ReadAllText($path, [Text.Encoding]::UTF8) | ConvertFrom-Json)
}

$en = Read-Json $enPath
$enKeys = $en.PSObject.Properties.Name
$enMap = @{}
$en.PSObject.Properties | ForEach-Object { $enMap[$_.Name] = [string]$_.Value }

# 至少要有这么多键才值得单独发一份语言文件（更少的都是上游 Weblate 的空壳）
$minKeys = 50

$locales = @('ar_aa', 'cs_cz', 'de_de', 'el_gr', 'fi_fi', 'hu_hu', 'ko_kr', 'ms_my', 'pl_pl', 'pt_br', 'pt_pt', 'ru_ru', 'sk_sk', 'tr_tr', 'zh_hk')

function Write-Lang([string]$fileName, $ordered) {
    $lines = New-Object System.Collections.Generic.List[string]
    $lines.Add('{')
    $i = 0
    $total = $ordered.Count
    foreach ($k in $ordered.Keys) {
        $i++
        $v = $ordered[$k]
        $v = [regex]::Replace($v, ' {2,}', ' ')
        $vEsc = $v.Replace('\', '\\').Replace('"', '\"').Replace("`r", '').Replace("`n", '\n')
        $kEsc = $k.Replace('\', '\\').Replace('"', '\"')
        $comma = if ($i -lt $total) { ',' } else { '' }
        $lines.Add(('    "{0}": "{1}"{2}' -f $kEsc, $vEsc, $comma))
    }
    $lines.Add('}')
    $content = ($lines -join "`r`n") + "`r`n"
    $utf8 = New-Object System.Text.UTF8Encoding($false)
    foreach ($dir in $langDirs) {
        [IO.File]::WriteAllText((Join-Path $dir $fileName), $content, $utf8)
    }
    Write-Host ("wrote {0} ({1} keys) into {2} module(s)" -f $fileName, $total, $langDirs.Count)
}

foreach ($locale in $locales) {
    $file = Join-Path $upDir "$locale.json"
    if (-not (Test-Path $file)) {
        Write-Host ("skip {0}: upstream file missing" -f $locale)
        continue
    }
    $up = Read-Json $file
    $ordered = [ordered]@{}
    $up.PSObject.Properties | ForEach-Object {
        if ($enKeys -contains $_.Name -and -not [string]::IsNullOrWhiteSpace([string]$_.Value)) {
            $ordered[$_.Name] = [string]$_.Value
        }
    }

    if ($locale -eq 'zh_hk') {
        # 港版用台版补齐缺失键，并把「臺」统一成港版常用的「台」
        $tw = Read-Json $twPath
        foreach ($key in $enKeys) {
            $value = [string]$ordered[$key]
            if ([string]::IsNullOrWhiteSpace($value)) { $value = [string]$tw.$key }
            if ([string]::IsNullOrWhiteSpace($value)) { $value = $enMap[$key] }
            $ordered[$key] = ($value -replace '臺', '台')
        }
        Write-Lang 'zh_hk.json' $ordered
        continue
    }

    if ($ordered.Count -lt $minKeys) {
        Write-Host ("skip {0}: only {1} translated keys upstream (stub)" -f $locale, $ordered.Count)
        continue
    }
    Write-Lang "$locale.json" $ordered
}
