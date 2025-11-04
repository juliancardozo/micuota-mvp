<#
E2E test script for Micuota MVP (PowerShell)
Flow tested:
 1. Wait for /health
 2. Register user
 3. Login (get JWT)
 4. Create Plan (authenticated)
 5. Create Subscription (authenticated)
 6. Simulate MercadoPago webhook payment and assert response

Usage:
  powershell -ExecutionPolicy Bypass -File .\scripts\run-e2e.ps1
#>

param(
    [string]$BaseUrl = "http://localhost:8080",
    [int]$TimeoutSec = 60
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$baseUrl = $BaseUrl

function Wait-Health {
    param($timeoutSec = 60)
    Write-Host "Waiting for $baseUrl/health (timeout ${timeoutSec}s)..."
    for ($i = 0; $i -lt $timeoutSec; $i++) {
        try {
            $r = Invoke-WebRequest -Uri "$baseUrl/health" -UseBasicParsing -TimeoutSec 5
            if ($r.StatusCode -eq 200) {
                Write-Host "HEALTH OK"
                return $true
            }
        } catch {
            # ignore
        }
        Start-Sleep -Seconds 1
    }
    Write-Error "Health check timed out after ${timeoutSec}s"
    return $false
}

function Assert-Status($obj, $name) {
    if (-not $obj) { Write-Error "$name is null or empty"; exit 1 }
    Write-Host "$name OK"
}

if (-not (Wait-Health -timeoutSec 60)) { exit 1 }

# 1) Register
$registerPayload = @{ name = 'E2E Test Prof'; email = "e2e+prof@local.test"; password = 'e2ePassword!'; role = 'PROFESOR' } | ConvertTo-Json
Write-Host "Registering user..."
$regResp = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $registerPayload -ContentType 'application/json'
Assert-Status $regResp 'Register Response'
Write-Host "Registered id: $($regResp.id) email: $($regResp.email)"

# 2) Login
$loginPayload = @{ email = $regResp.email; password = 'e2ePassword!' } | ConvertTo-Json
Write-Host "Logging in..."
$loginResp = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $loginPayload -ContentType 'application/json'
Assert-Status $loginResp.token 'Login token'
$token = $loginResp.token
Write-Host "Token length: $($token.Length)"

# 3) Create Plan
$planPayload = @{ title = 'E2E Starter'; price = '19.99'; frequency = 'monthly' } | ConvertTo-Json
Write-Host "Creating plan..."
$planResp = Invoke-RestMethod -Uri "$baseUrl/plans" -Method Post -Body $planPayload -ContentType 'application/json' -Headers @{ Authorization = "Bearer $token" }
Assert-Status $planResp 'Plan Response'
Write-Host "Plan id: $($planResp.id) mpPlanId: $($planResp.mpPlanId)"

# 4) Create Subscription
$subPayload = @{ planId = "$($planResp.id)" } | ConvertTo-Json
Write-Host "Creating subscription..."
$subResp = Invoke-RestMethod -Uri "$baseUrl/subscriptions" -Method Post -Body $subPayload -ContentType 'application/json' -Headers @{ Authorization = "Bearer $token" }
Assert-Status $subResp 'Subscription Response'
Write-Host "Subscription id: $($subResp.id) mpSubscriptionId: $($subResp.mpSubscriptionId) status: $($subResp.status)"

# 5) Simulate MercadoPago webhook payment
$webhookPayload = @{
    type = 'payment'
    data = @{
        subscription_id = $subResp.mpSubscriptionId
        amount = 19.99
        status = 'approved'
        id = "fake-payment-" + [int](Get-Date -UFormat %s)
    }
} | ConvertTo-Json -Depth 5

Write-Host "Sending webhook simulation..."
$webhookResp = Invoke-RestMethod -Uri "$baseUrl/webhooks/mercadopago" -Method Post -Body $webhookPayload -ContentType 'application/json'
if ($webhookResp -eq 'received') { Write-Host 'Webhook received OK' } else { Write-Error "Unexpected webhook response: $webhookResp"; exit 1 }

Write-Host "E2E flow completed successfully."

# Optional: print summary
Write-Output "--- SUMMARY ---"
Write-Output "User: $($regResp.email) (id: $($regResp.id))"
Write-Output "Plan: $($planResp.id) mpPlanId: $($planResp.mpPlanId)"
Write-Output "Subscription: $($subResp.id) mpSubscriptionId: $($subResp.mpSubscriptionId) status: $($subResp.status)"

exit 0
