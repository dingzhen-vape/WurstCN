@echo off
rem 初始化git仓库
git init
rem 添加远程仓库地址
git remote add origin https://github.com/dingzhen-vape/wurst_cn
rem 添加修改过的文件
git add .
rem 提交更改
git commit -m "upload modified file"
rem 推送到远程仓库
git push origin master