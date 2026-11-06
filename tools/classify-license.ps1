<#
.SYNOPSIS
    Regenerate the licence map for "Tianjin Metro - MTR3 Adapted Edition".

.DESCRIPTION
    Compares every java/resource file of this repository against a clone of the
    upstream mod (https://github.com/ZiYueCommentary/Tianjin-Metro) and
    classifies each logical file as

        A  identical to upstream                -> MIT
        B  upstream file, modified by this port -> mixed (GPL-3.0-or-later, MIT retained)
        C  no upstream counterpart              -> GPL-3.0-or-later

    Baseline used for the committed licensing/file-map.txt:
        upstream main @ 50eafdc (2026-10-16)

.EXAMPLE
    powershell -NoProfile -File tools/classify-license.ps1 -Upstream E:\path\to\Tianjin-Metro
#>
param(
    [Parameter(Mandatory = $true)][string]$Upstream,
    [string]$OutFile = "licensing/file-map.txt"
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)

function Get-Rel([string]$base, [string]$full) {
    $b = $base.TrimEnd('\') + '\'
    if ($full.StartsWith($b, [StringComparison]::OrdinalIgnoreCase)) {
        return $full.Substring($b.Length).Replace('\', '/')
    }
    return $full.Replace('\', '/')
}

function Get-Sha([string]$path) {
    $h = [System.Security.Cryptography.SHA256]::Create()
    $fs = [IO.File]::OpenRead($path)
    $bytes = $h.ComputeHash($fs)
    $fs.Close(); $h.Dispose()
    return [BitConverter]::ToString($bytes).Replace('-', '')
}

function Normalize([string]$p) {
    # upstream moved its code to ziyue/tjmetro/mod/** under MTR 4; this port
    # keeps the older ziyue/tjmetro/** layout, so strip the extra segment.
    return ($p -replace '^ziyue/tjmetro/mod/', 'ziyue/tjmetro/')
}

function Get-Key([string]$rel) {
    if ($rel -match '^([^/]+)/src/main/java/(.+)$')      { return 'java|' + (Normalize $matches[2]) }
    if ($rel -match '^([^/]+)/src/main/resources/(.+)$') { return 'res|'  + (Normalize $matches[2]) }
    return $null
}

$upIndex = @{}
Get-ChildItem $Upstream -Recurse -File -ErrorAction SilentlyContinue |
    Where-Object { $_.FullName -notmatch '\\\.git\\|\\build\\|\\libs\\' } |
    ForEach-Object {
        $key = Get-Key (Get-Rel $Upstream $_.FullName)
        if ($key) {
            $sha = Get-Sha $_.FullName
            if (-not $upIndex.ContainsKey($key)) { $upIndex[$key] = New-Object System.Collections.Generic.HashSet[string] }
            [void]$upIndex[$key].Add($sha)
        }
    }

$class = @{}
Get-ChildItem $repoRoot -Recurse -File -ErrorAction SilentlyContinue |
    Where-Object { $_.FullName -notmatch '\\\.git\\|\\build\\|\\\.gradle\\|\\run\\|\\\.architectury' } |
    ForEach-Object {
        $key = Get-Key (Get-Rel $repoRoot $_.FullName)
        if (-not $key) { return }
        $cat = 'C'
        if ($upIndex.ContainsKey($key)) {
            $cat = if ($upIndex[$key].Contains((Get-Sha $_.FullName))) { 'A' } else { 'B' }
        }
        if (-not $class.ContainsKey($key)) { $class[$key] = $cat }
        elseif ($class[$key] -ne 'A' -and $cat -eq 'A') { $class[$key] = 'A' }
    }

$lines = New-Object System.Collections.Generic.List[string]
$lines.Add('# Tianjin Metro - MTR3 Adapted Edition -- licence map (generated, do not hand-edit)')
$lines.Add('# class' + [char]9 + 'type' + [char]9 + 'logical-path')
$lines.Add('# A = identical to upstream (MIT) | B = upstream file modified by this port (mixed) | C = new in this port (GPL-3.0-or-later)')
$lines.Add('# Logical paths apply to every module copy (common, common-1.16.5, common-1.18.2, common-1.19.2, common-1.19.4, common-1.21.1).')
$lines.Add('# Regenerate: powershell -NoProfile -File tools/classify-license.ps1 -Upstream <upstream-clone>')
$lines.Add('#')
foreach ($key in ($class.Keys | Sort-Object { $class[$_] }, { $_ })) {
    $parts = $key -split '\|'
    $lines.Add($class[$key] + [char]9 + $parts[0] + [char]9 + $parts[1])
}

$target = if ([IO.Path]::IsPathRooted($OutFile)) { $OutFile } else { Join-Path $repoRoot $OutFile }
[IO.File]::WriteAllLines($target, $lines, [Text.UTF8Encoding]::new($false))

$summary = $class.Values | Group-Object | Sort-Object Name
Write-Output ("written: " + $target)
foreach ($g in $summary) { Write-Output ("  " + $g.Name + " = " + $g.Count) }
