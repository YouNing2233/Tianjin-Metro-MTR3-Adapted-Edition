# 1.21.1 移植：第三批修正
$ErrorActionPreference = 'Stop'
$root = 'E:\Project\Tianjin-metro-MTR3\common-1.21.1\src\main\java'
$enc = New-Object System.Text.UTF8Encoding($false)
$n = 0

Get-ChildItem $root -Recurse -Filter *.java | ForEach-Object {
    $o = [System.IO.File]::ReadAllText($_.FullName)
    $t = $o

    # 1) 被吞掉首字母 R 的 Reference
    $t = $t.Replace('fromNamespaceAndPath(eference.MOD_ID', 'fromNamespaceAndPath(Reference.MOD_ID')
    $t = $t.Replace('(eference.MOD_ID', '(Reference.MOD_ID')

    # 2) RouteMapGenerator 上被误加 Usage 的 List.get
    $t = $t.Replace('platformIds.get(currentStationIndex + 1, mtr.data.MultipartName.Usage.MAP_DEST)',
                    'platformIds.get(currentStationIndex + 1)')

    # 3) WidgetBetterTextField.tick()：EditBox 在 1.21 已移除 tick()
    $t = $t -replace '(?m)^\s*textField\w*\.tick\(\);\r?\n', ''

    # 4) ImageButton：改用 WidgetSprites
    $t = [regex]::Replace($t,
        'new ImageButton\(0, 0, 0, SQUARE_SIZE, 0, 0, 20, (.+?), 20, 40,',
        'new ImageButton(0, 0, 0, SQUARE_SIZE, new net.minecraft.client.gui.components.WidgetSprites($1, $1),')

    # 5) mouseScrolled 参数改名后残留的 amount
    $t = $t.Replace('Math.signum(-amount)', 'Math.signum(-scrollY)')

    if ($t -ne $o) { [System.IO.File]::WriteAllText($_.FullName, $t, $enc); $n++; Write-Host ("  修改 " + $_.Name) }
}
Write-Host ("共修改 {0} 个文件" -f $n)
