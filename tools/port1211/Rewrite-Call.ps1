# 1.21.1 移植：按「括号匹配」重写调用式
# 用法示例见文件末尾的 Run 部分。

function Rewrite-Call {
    param(
        [string]$Text,
        [string]$Prefix,        # 例如 'ResourceLocation.fromNamespaceAndPath('
        [int]$ExpectCommas,     # 期望的「顶层逗号个数」；不匹配则原样保留
        [string]$Template       # 用 {0} 代表原来的实参列表
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

        # 统计顶层逗号
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
