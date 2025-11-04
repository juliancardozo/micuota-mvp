# Restart ngrok and print public URL
Get-Process ngrok -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.Id -Force -ErrorAction SilentlyContinue }
Start-Sleep -Seconds 1
Write-Host 'Starting ngrok...'

$argList = @('http', '8080', '--log=stdout')
$p = Start-Process -FilePath ngrok -ArgumentList $argList -PassThru

for ($i=0; $i -lt 20; $i++) {
    Start-Sleep -Seconds 1
    try {
        $t = Invoke-RestMethod -Uri 'http://127.0.0.1:4040/api/tunnels' -TimeoutSec 3
        if ($null -ne $t -and $t.tunnels.Count -gt 0) {
            $pub = $t.tunnels | Where-Object { $_.proto -eq 'https' } | Select-Object -First 1
            if (-not $pub) { $pub = $t.tunnels[0] }
            Write-Output ("NGROK_URL: $($pub.public_url)")
            exit 0
        }
    } catch {
        # ignore transient failures
    }
}
Write-Output 'NGROK_TIMEOUT'
exit 1
