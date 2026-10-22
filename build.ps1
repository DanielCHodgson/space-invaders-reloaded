param(
    [switch]$Run,
    [string]$JavaHome = $env:JAVA_HOME
)

$ErrorActionPreference = 'Stop'

if ($JavaHome -and (Test-Path -LiteralPath (Join-Path $JavaHome 'bin\javac.exe'))) {
    $javaBin = Join-Path $JavaHome 'bin'
} elseif (Get-Command javac -ErrorAction SilentlyContinue) {
    $javaBin = Split-Path (Get-Command javac).Source
} else {
    $installedCompiler = Get-ChildItem -Path "$env:ProgramFiles\Eclipse Adoptium\jdk-*\bin\javac.exe" -ErrorAction SilentlyContinue |
        Sort-Object FullName -Descending | Select-Object -First 1
    if (!$installedCompiler) { throw 'Install JDK 21 or newer, or pass -JavaHome with its directory.' }
    $javaBin = Split-Path $installedCompiler.FullName
}

$compiler = Join-Path $javaBin 'javac.exe'
$java = Join-Path $javaBin 'java.exe'
$jarTool = Join-Path $javaBin 'jar.exe'
$version = & $compiler -version 2>&1
if ($LASTEXITCODE -ne 0 -or "$version" -notmatch 'javac (\d+)' -or [int]$Matches[1] -lt 21) {
    throw "JDK 21 or newer is required. Found: $version"
}

$buildRoot = Join-Path $PSScriptRoot 'build'
if (Test-Path -LiteralPath $buildRoot) {
    $resolvedBuild = (Resolve-Path -LiteralPath $buildRoot).Path
    if ($resolvedBuild -ne (Join-Path $PSScriptRoot 'build')) { throw 'Unexpected build directory.' }
    Remove-Item -LiteralPath $resolvedBuild -Recurse -Force
}
$classes = Join-Path $buildRoot 'classes'
$testClasses = Join-Path $buildRoot 'test-classes'
$dist = Join-Path $PSScriptRoot 'dist'
New-Item -ItemType Directory -Force -Path $classes, $testClasses, $dist | Out-Null

$sources = @(Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot 'src') -Recurse -Filter '*.java' | ForEach-Object FullName)
& $compiler --release 21 -encoding UTF-8 -Xlint:all -Werror -d $classes $sources
if ($LASTEXITCODE -ne 0) { throw 'Source compilation failed.' }
& (Join-Path $PSScriptRoot 'scripts\check-architecture.ps1') -ClassesDirectory $classes -JavaBin $javaBin
Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot 'res') -File | Copy-Item -Destination $classes
Copy-Item -LiteralPath (Join-Path $PSScriptRoot 'src\Music') -Destination $classes -Recurse

$tests = @(Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot 'tests') -Recurse -Filter '*.java' | ForEach-Object FullName)
& $compiler --release 21 -encoding UTF-8 -Xlint:all -Werror -cp $classes -d $testClasses $tests
if ($LASTEXITCODE -ne 0) { throw 'Test compilation failed.' }
& $java '-Djava.awt.headless=true' -cp "$classes;$testClasses" invaders.tests.RegressionSuite
if ($LASTEXITCODE -ne 0) { throw 'Regression checks failed.' }

$jar = Join-Path $dist 'space-invaders-reloaded.jar'
& $jarTool --create --file $jar --main-class Launchers.BaseLauncher -C $classes .
if ($LASTEXITCODE -ne 0) { throw 'JAR packaging failed.' }
Write-Output "Built $jar"
if ($Run) { & $java -jar $jar }
