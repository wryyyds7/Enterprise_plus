# 测试JWT认证脚本
# 1. 登录获取token
Write-Host "开始登录..."
$loginResponse = Invoke-WebRequest -Uri "http://localhost:8089/in/login" -Method POST -Body '{"userName":"admin","password":"admin123"}' -ContentType "application/json" -UseBasicParsing
Write-Host "登录状态码: " $loginResponse.StatusCode
Write-Host "登录响应: " $loginResponse.Content

# 2. 提取token
$token = ($loginResponse.Content | ConvertFrom-Json).data.token
Write-Host "Token: " $token

# 3. 测试受保护端点
Write-Host "\n测试受保护端点..."
$headers = @{"Authorization" = "Bearer $token"}
$searchResponse = Invoke-WebRequest -Uri "http://localhost:8089/users/admin/searchUserByPage" -Method POST -Headers $headers -Body '{"page":1,"size":10}' -ContentType "application/json" -UseBasicParsing
Write-Host "搜索状态码: " $searchResponse.StatusCode
Write-Host "搜索响应: " $searchResponse.Content