<#
Quick script to expose localhost:8080 using ngrok (PowerShell)
Prereqs: ngrok installed and `ngrok authtoken <token>` already configured.
#>

param(
    [int]$Port = 8080
)

Write-Host "Starting ngrok HTTP tunnel to localhost:$Port"

# Check if ngrok is in PATH
$ngrokPath = (Get-Command ngrok -ErrorAction SilentlyContinue).Source
if (-not $ngrokPath) {
    Write-Error "ngrok not found in PATH. Install ngrok from https://ngrok.com and configure your authtoken."
    exit 1
}

# Start ngrok in background and capture the URL from the API
$argList = @('http', $Port.ToString(), '--log=stdout')
$process = Start-Process -FilePath ngrok -ArgumentList $argList -PassThru

# Wait for ngrok web api to appear
$apiUrl = 'http://127.0.0.1:4040/api/tunnels'
for ($i=0; $i -lt 20; $i++) {
    Start-Sleep -Seconds 1
    try {
        $tunnels = Invoke-RestMethod -Uri $apiUrl -Method Get -TimeoutSec 3
        if ($null -ne $tunnels -and $tunnels.tunnels.Count -gt 0) {
            $public = $tunnels.tunnels | Where-Object { $_.proto -eq 'https' } | Select-Object -First 1
            if (-not $public) { $public = $tunnels.tunnels[0] }
            Write-Host "Public URL: $($public.public_url)"
            Write-Host "Ngrok process id: $($process.Id)"
            Write-Host "Press Ctrl+C to stop ngrok when done."
            exit 0
        }
    } catch {
        # ignore transient failures
    }
}

Write-Error "ngrok did not report any tunnels after waiting. Check ngrok logs or run 'ngrok http $Port' manually.";
exit 1
