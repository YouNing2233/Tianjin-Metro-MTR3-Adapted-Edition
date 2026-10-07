# 修正 fix-api.ps1 括号重写的 off-by-one 损坏
$ErrorActionPreference = 'Stop'
$root = 'E:\Project\Tianjin-metro-MTR3\common-1.21.1\src\main\java'
$enc = New-Object System.Text.UTF8Encoding($false)
$n = 0

Get-ChildItem $root -Recurse -Filter *.java | ForEach-Object {
    $o = [System.IO.File]::ReadAllText($_.FullName)
    $t = $o

    # 1) parse(tring.format( -> parse(String.format(   （首字母 S 被吞）
    $t = $t.Replace('ResourceLocation.parse(tring.format(', 'ResourceLocation.parse(String.format(')

    # 2) 被吞掉首字母 r 的实参
    $t = $t.Replace('getFormattedRouteDestination(oute,', 'getFormattedRouteDestination(route,')
    $t = $t.Replace('getDestination(urrentStationIndex)', 'getDestination(currentStationIndex')

    # 3) 误插到 isEmpty() 上的 Usage 片段
    $t = $t.Replace('isEmpty(, mtr.data.MultipartName.Usage.MAP_DEST)', 'isEmpty()')
    $t = $t.Replace('isEmpty(, mtr.data.MultipartName.Usage.PIDS_DEST)', 'isEmpty()')

    # 4) 给真正的调用补上 Usage 参数
    $t = $t.Replace('getDestination(currentStationIndex)',
                    'getDestination(currentStationIndex, mtr.data.MultipartName.Usage.MAP_DEST)')
    $t = $t.Replace('getFormattedRouteDestination(route, currentStationIndex, TEMP_CIRCULAR_MARKER)',
                    'getFormattedRouteDestination(route, currentStationIndex, TEMP_CIRCULAR_MARKER, mtr.data.MultipartName.Usage.MAP_DEST)')
    $t = $t.Replace('getFormattedRouteDestination(route, scheduleEntry.currentStationIndex, "")',
                    'getFormattedRouteDestination(route, scheduleEntry.currentStationIndex, "", mtr.data.MultipartName.Usage.PIDS_DEST)')

    if ($t -ne $o) { [System.IO.File]::WriteAllText($_.FullName, $t, $enc); $n++; Write-Host ("  修正 " + $_.Name) }
}
Write-Host ("共修正 {0} 个文件" -f $n)
