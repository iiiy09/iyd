$env:JAVA_HOME = "C:\Java"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$psi = New-Object System.Diagnostics.ProcessStartInfo
$psi.FileName = "java.exe"
$psi.Arguments = "-jar E:\×ÀÃæ\iu\backend\target\iyd-learning-1.0.0.jar"
$psi.WorkingDirectory = "E:\×ÀÃæ\iu\backend"
$psi.UseShellExecute = $true
$psi.CreateNoWindow = $true
$psi.WindowStyle = [System.Diagnostics.ProcessWindowStyle]::Hidden
$p = [System.Diagnostics.Process]::Start($psi)
$p.Id | Out-File E:\×ÀÃæ\iu\backend\.backend.pid

