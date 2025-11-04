param(
    [string]$BaseUrl = "http://localhost:8080"
)

Write-Host "Seeding demo data..."
$seed = Invoke-RestMethod -Uri "$BaseUrl/demo/seed" -Method Post
Write-Host "Seed result:`n" ($seed | ConvertTo-Json -Depth 5)

Write-Host "Logging in as student..."
$login = Invoke-RestMethod -Uri "$BaseUrl/auth/login" -Method Post -Body (@{ email = $seed.student_email; password = $seed.student_password } | ConvertTo-Json) -ContentType 'application/json'
$token = $login.token
Write-Host "Token length: $($token.Length)"

Write-Host "Creating subscription as student (POST /subscriptions)..."
$headers = @{ Authorization = "Bearer $token"; 'Content-Type' = 'application/json' }
$body = @{ planId = $seed.plan_id }
$sub = Invoke-RestMethod -Uri "$BaseUrl/subscriptions" -Method Post -Body ($body | ConvertTo-Json) -Headers $headers -ContentType 'application/json'
Write-Host "Subscription created:`n" ($sub | ConvertTo-Json -Depth 5)

Write-Host "Charging the subscription (POST /payments/charge)..."
$chargeBody = @{ subscriptionId = $sub.id }
$payment = Invoke-RestMethod -Uri "$BaseUrl/payments/charge" -Method Post -Body ($chargeBody | ConvertTo-Json) -Headers $headers -ContentType 'application/json'
Write-Host "Payment result:`n" ($payment | ConvertTo-Json -Depth 5)

Write-Host "Demo flow complete. Student logged in and paid for subscription."
