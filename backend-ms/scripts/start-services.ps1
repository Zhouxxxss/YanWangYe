# 研王爷 · 后端微服务启动脚本（Windows PowerShell）
# 依赖：先运行 start-infra.ps1 拉起 MySQL/Redis/Nacos；本脚本把每个业务服务作为独立 JVM 进程启动。
$ErrorActionPreference = "Stop"

$root  = Split-Path -Parent $PSScriptRoot
$mvn   = "e:\YanWangYe\.tools\apache-maven-3.9.9\bin\mvn.cmd"
$settings = "e:\YanWangYe\.tools\settings.xml"

Write-Host "[1/2] 打包所有模块 ..." -ForegroundColor Cyan
if (!(Test-Path $mvn)) { throw "未找到 Maven，请先下载到 $mvn 或修改本脚本路径" }
& $mvn -s $settings -f "$root\pom.xml" -DskipTests package | Out-Null
if ($LASTEXITCODE -ne 0) { throw "打包失败，请检查编译错误" }

$services = @("ywy-gateway", "ywy-auth", "ywy-user", "ywy-study", "ywy-rag")

Write-Host "[2/2] 以独立进程启动服务 ..." -ForegroundColor Cyan
foreach ($svc in $services) {
    $jar = Get-ChildItem "$root\$svc\target\*.jar" -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -notlike '*sources*' -and $_.Name -notlike '*original*' } |
        Select-Object -First 1
    if (-not $jar) { Write-Host "!! $svc 缺少可执行 jar" -ForegroundColor Yellow; continue }
    # 每个服务一个独立 java 进程
    Start-Process -FilePath "java" -ArgumentList "-jar", "`"$($jar.FullName)`"" -WorkingDirectory $root
    Write-Host "  started  $svc  ->  $($jar.Name)  (PID 已后台启动)" -ForegroundColor Green
}

Write-Host "`n全部服务已以独立进程启动。端口：gateway 8100 / auth 8102 / user 8103 / study 8104 / rag 8105" -ForegroundColor Magenta
Write-Host "健康检查示例： curl http://localhost:8100/actuator/health" -ForegroundColor Magenta