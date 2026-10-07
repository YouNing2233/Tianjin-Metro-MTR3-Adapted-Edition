# 1.21.1 移植：批量 API 重写脚本
# 运行： pwsh -File tools/port1211/fix-api.ps1

$ErrorActionPreference = 'Stop'

function Rewrite-Call {
    param(
        [string]$Text,
        [string]$Prefix,
        [int]$ExpectCommas,
        [string]$Template
    )
    $result = New-Object System.Text.StringBuilder
    $i = 0
    while ($i -lt $Text.Length) {
        $idx = $Text.IndexOf($Prefix, $i)
        if ($idx -lt 0) { [void]$result.Append($Text.Substring($i)); break }

        $open = $idx + $Prefix.Length
        $depth = 0
        $j = $open
        while ($j -lt $Text.Length) {
            $ch = $Text[$j]
            if ($ch -eq '(') { $depth++ }
            elseif ($ch -eq ')') { $depth--; if ($depth -eq 0) { break } }
            $j++
        }
        if ($j -ge $Text.Length) { [void]$result.Append($Text.Substring($i)); break }

        $inner = $Text.Substring($open + 1, $j - $open - 1)

        $d = 0; $commas = 0
        foreach ($ch in $inner.ToCharArray()) {
            if ($ch -eq '(') { $d++ }
            elseif ($ch -eq ')') { $d-- }
            elseif ($ch -eq ',' -and $d -eq 0) { $commas++ }
        }

        [void]$result.Append($Text.Substring($i, $idx - $i))
        if ($commas -eq $ExpectCommas) {
            [void]$result.Append($Template.Replace('{0}', $inner))
        } else {
            [void]$result.Append($Text.Substring($idx, $j - $idx + 1))
        }
        $i = $j + 1
    }
    return $result.ToString()
}

$root = 'E:\Project\Tianjin-metro-MTR3\common-1.21.1\src\main\java'
$enc = New-Object System.Text.UTF8Encoding($false)

$stat = @{ parse = 0; dest = 0; fmt = 0; files = 0 }

Get-ChildItem $root -Recurse -Filter *.java | ForEach-Object {
    $orig = [System.IO.File]::ReadAllText($_.FullName)
    $t = $orig

    $b = $t
    $t = Rewrite-Call -Text $t -Prefix 'ResourceLocation.fromNamespaceAndPath(' -ExpectCommas 0 -Template 'ResourceLocation.parse({0})'
    if ($t -ne $b) { $stat.parse++ }

    $usage = if ($_.Name -eq 'RenderPIDSTianjin.java') { 'PIDS_DEST' } else { 'MAP_DEST' }

    $b = $t
    $t = Rewrite-Call -Text $t -Prefix '.getDestination(' -ExpectCommas 0 -Template ('.getDestination({0}, mtr.data.MultipartName.Usage.' + $usage + ')')
    if ($t -ne $b) { $stat.dest++ }

    $b = $t
    $t = Rewrite-Call -Text $t -Prefix '.getFormattedRouteDestination(' -ExpectCommas 2 -Template ('.getFormattedRouteDestination({0}, mtr.data.MultipartName.Usage.' + $usage + ')')
    if ($t -ne $b) { $stat.fmt++ }

    if ($t -ne $orig) {
        [System.IO.File]::WriteAllText($_.FullName, $t, $enc)
        $stat.files++
    }
}

Write-Host ("文件级改动: parse={0} getDestination={1} getFormattedRouteDestination={2} 文件数={3}" -f $stat.parse, $stat.dest, $stat.fmt, $stat.files)
