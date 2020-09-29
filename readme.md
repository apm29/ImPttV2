# 项目说明

## 基本架构
1. service：ImService 运行在单独的:ptt 进程中，负责连接、接收数据（WebRTC的信令、P2P流）
2. view:各种Activity、Fragment，运行在应用进程，负责展示数据
3. service 与 view 的通信：通过Messenger进行 详细说明：(Messenger)[https://developer.android.google.cn/guide/components/bound-services#Messenger]
     * Messenger传递的参数需要是Parcelable的
     * 通过为Message添加replyTo来传递Messenger对象到另一进程，可参考Android Developer的模式将主进程的Messenger`注册`到ptt进程，ptt可在需要时通知（发送Message）主进程
     * 定义好Message.what

     