@echo off
setlocal enabledelayedexpansion

REM ==========================
REM 删除 Git 历史中的大文件
REM ==========================

echo [1/4] 正在删除历史中的大文件...

REM 这里列出你要删除的文件路径（注意用双引号括起来）
set "files_to_delete=chat/target/chat-0.0.1-SNAPSHOT.jar"
set "files_to_delete=%files_to_delete% users/target/users-0.0.1-SNAPSHOT.jar"
set "files_to_delete=%files_to_delete% search/target/search-0.0.1-SNAPSHOT.jar"
set "files_to_delete=%files_to_delete% users/target/users-0.0.1-SNAPSHOT-exec.jar"
set "files_to_delete=%files_to_delete% AI/target/AI-0.0.1-SNAPSHOT.jar"

for %%f in (%files_to_delete%) do (
    echo 正在删除: %%f
    git filter-branch --force --index-filter ^
    "git rm --cached --ignore-unmatch '%%f'" ^
    --prune-empty --tag-name-filter cat -- --all
)

echo.
echo [2/4] 清理 Git 缓存...
git reflog expire --expire=now --all
git gc --prune=now --aggressive

echo.
echo [3/4] 添加 .gitignore 规则，防止以后再提交大文件...
(
    echo # Maven 构建产物
    echo target/
    echo *.jar
) >> .gitignore

echo.
echo [4/4] 强制推送清理后的仓库到 GitHub...
git push origin master --force

echo.
echo 完成！已删除历史中的大文件并推送。
pause