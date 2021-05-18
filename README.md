# Frida Manager

通过APP管理frida：安装、启停、开机启动、修改端口号。

> 需要有root权限

## 安装
在设置页面，可以选择本地或在线安装frida。

从[frida](https://github.com/frida/frida/releases)下载，对应版本的frida-server，本地选择安装。

推荐使用在线安装，写入版本号如：12.10.4，即可通过手机型号自动拼接下载地址提供下载，避免架构错误。

## 开机启动
在设置页面可打开【开机启动】，由于开机启动是接收`RECEIVE_BOOT_COMPLETED`广播，建议从权限中打开开启启动，并锁定APP，不要关闭进程，否则将无法接收到广播。

## 设置端口
在设置页面填写端口号，默认是`27042`。