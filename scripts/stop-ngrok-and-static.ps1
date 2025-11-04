Write-Host 'Stopping ngrok processes...'
Get-Process ngrok -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.Id -Force -ErrorAction SilentlyContinue; Write-Host 'Stopped ngrok pid' $_.Id }
Start-Sleep -Seconds 1
Write-Host 'Stopping python server on port 3000 (if any)...'
$ownerPid = (Get-NetTCPConnection -LocalPort 3000 -ErrorAction SilentlyContinue).OwningProcess
if ($ownerPid) {
    Stop-Process -Id $ownerPid -Force -ErrorAction SilentlyContinue
    Write-Host 'Stopped process on port 3000 PID:' $ownerPid
} else {
    Write-Host 'No process listening on port 3000'
}
Start-Sleep -Seconds 1
Write-Host 'Verifying ngrok...'
try {
    $t = Invoke-RestMethod -Uri 'http://127.0.0.1:4040/api/tunnels' -TimeoutSec 2
    if ($t.tunnels.Count -gt 0) {
        Write-Host 'ngrok still running'
        $t.tunnels | ForEach-Object { Write-Host $_.public_url }
    } else {
        Write-Host 'no tunnels'
    }
} catch {
    Write-Host 'ngrok not running'
}
if (Get-NetTCPConnection -LocalPort 3000 -ErrorAction SilentlyContinue) { Write-Host 'port 3000 still in use' } else { Write-Host 'port 3000 free' }
