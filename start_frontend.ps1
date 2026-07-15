$psi2 = New-Object System.Diagnostics.ProcessStartInfo
$psi2.FileName = "node.exe"
$psi2.Arguments = "E:\桌面\iu\web\node_modules\vite\bin\vite.js --host"
$psi2.WorkingDirectory = "E:\桌面\iu\web"
$psi2.UseShellExecute = $true
$psi2.CreateNoWindow = $true
$psi2.WindowStyle = [System.Diagnostics.ProcessWindowStyle]::Hidden
$p2 = [System.Diagnostics.Process]::Start($psi2)
$p2.Id | Out-File E:\桌面\iu\web\.frontend.pid
