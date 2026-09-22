#!/system/bin/sh
# coverRoot 启动脚本
#
# 用途：在设备上通过 shell（adb / 终端，必要时 su）启动 coverRoot 应用。
# 用法：
#   sh /data/local/tmp/start.sh
#
# 说明：
#   - 后端地址由应用私有目录的配置文件管理：
#       /data/data/com.demo.themeswitch/files/config.json
#     字段 backendUrl（默认 http://127.0.0.1:8080）。
#   - C++ 后端启动时读取同一配置，据此确定监听地址/端口。

PKG="com.demo.themeswitch"
ACTIVITY="com.demo.themeswitch.MainActivity"
CONFIG="/data/data/${PKG}/files/config.json"

# 以 root 运行时，可在此处插入启动 C++ 后端的命令，例如：
#   /data/adb/coverRoot/bin/coverrootd --config "${CONFIG}" &

# 拉起应用主界面（MainActivity 含 MAIN/LAUNCHER intent-filter）
am start -n "${PKG}/${ACTIVITY}" "$@"
