# 测试登录并获取JWT令牌
$body = @{userName='admin';password='admin123'} | ConvertTo-Json
$loginResponse = Invoke-WebRequest -Uri 'http://localhost:8089/in/login' -Method POST -ContentType 'application/json' -Body $body -UseBasicParsing
$loginData = $loginResponse.Content | ConvertFrom-Json
$token = $loginData.data.token
Write-Host "获取到的令牌: $token"

# 构建Authorization头
$headers = @{'Authorization' = "Bearer $token"}

# 测试 /users/admin/searchUserByPage 端点
Write-Host "\n测试 /users/admin/searchUserByPage 端点:"
$searchBody = @{page=1; size=10} | ConvertTo-Json
$searchResponse = Invoke-WebRequest -Uri 'http://localhost:8089/users/admin/searchUserByPage' -Method POST -Headers $headers -Body $searchBody -ContentType "application/json" -UseBasicParsing
Write-Host "状态码: $($searchResponse.StatusCode)"
Write-Host "响应内容: $($searchResponse.Content)"

# 测试 /system/enterprise/list 端点
Write-Host "\n测试 /system/enterprise/list 端点:"
$enterpriseBody = @{} | ConvertTo-Json
$enterpriseResponse = Invoke-WebRequest -Uri 'http://localhost:8089/system/enterprise/list' -Method POST -Headers $headers -Body $enterpriseBody -ContentType "application/json" -UseBasicParsing
Write-Host "状态码: $($enterpriseResponse.StatusCode)"
Write-Host "响应内容: $($enterpriseResponse.Content)"