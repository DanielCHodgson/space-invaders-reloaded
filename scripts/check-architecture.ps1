param(
    [Parameter(Mandatory=$true)][string]$ClassesDirectory,
    [Parameter(Mandatory=$true)][string]$JavaBin
)
$ErrorActionPreference = 'Stop'
$dependencies = & (Join-Path $JavaBin 'jdeps.exe') -verbose:class -filter:none $ClassesDirectory
if ($LASTEXITCODE -ne 0) { throw 'Dependency analysis failed.' }

# Each package may depend on itself plus these lower-level packages. App is the composition root.
$allowed = @{
    config = @()
    control = @()
    event = @()
    entity = @('config')
    world = @('entity')
    encounter = @('control','event')
    system = @('config','entity','world')
    game = @('config','control','encounter','entity','event','system','world')
    scene = @('control','encounter')
    asset = @()
    audio = @('asset','event','scene')
    render = @('asset','config','entity','world','scene')
    input = @('control','scene','render')
    loop = @('config')
    ui = @('config','render')
    app = @('*')
}
$domain = @('config','control','event','entity','world','encounter','system','game','scene')
$violations = [Collections.Generic.List[string]]::new()
foreach ($line in $dependencies) {
    if ($line -notmatch '^\s+(invaders\.\S+)\s+->\s+(\S+)') { continue }
    $sourceClass = $Matches[1]
    $targetClass = $Matches[2]
    $sourceLayer = ($sourceClass -split '\.')[1]
    if (!$allowed.ContainsKey($sourceLayer)) { $violations.Add("Unknown architecture layer: $sourceClass"); continue }
    if ($sourceLayer -in $domain -and $targetClass -match '^(java\.awt|javax\.swing|javax\.sound)') {
        $violations.Add("Platform dependency in model/scene: $sourceClass -> $targetClass")
    }
    if ($sourceLayer -eq 'render' -and $targetClass -eq 'invaders.world.GameWorld') {
        $violations.Add("Renderer depends on mutable world: $sourceClass -> $targetClass")
    }
    if ($targetClass -match '^invaders\.([^.]+)\.') {
        $targetLayer = $Matches[1]
        if ($targetLayer -ne $sourceLayer -and '*' -notin $allowed[$sourceLayer] -and $targetLayer -notin $allowed[$sourceLayer]) {
            $violations.Add("Layer dependency: $sourceClass -> $targetClass")
        }
    }
}
if ($violations.Count) { throw ($violations -join [Environment]::NewLine) }
Write-Output 'Architecture boundaries passed.'
