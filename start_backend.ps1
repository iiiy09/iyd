$psi = New-Object System.Diagnostics.ProcessStartInfo
$psi.FileName = "java.exe"
$psi.Arguments = "-jar E:\桌面\iu\backend\target\iyd-learning-1.0.0.jar"
$psi.WorkingDirectory = "E:\桌面\iu\backend"
$psi.UseShellExecute = $true
$psi.CreateNoWindow = $true
$psi.WindowStyle = [System.Diagnostics.ProcessWindowStyle]::Hidden
$p = [System.Diagnostics.Process]::Start($psi)
$p.Id | Out-File E:\桌面\iu\backend\.backend.pid
