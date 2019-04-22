# -Android-speech-synthesis
一个简单的demo



1. 测试安卓端发送特定字段到服务器是否连接成功
在安卓上先测试是否能建立到后端的http的post请求。

后端的服务器开发者给我的接口是只接受三个字段，分别是{“src_text”:“水水水水水水水水水水水水水水水水水水”,“VerifyValue”:“36”,“Id”:“fs13a2ztDd2gK1UHqXZa”}

我准备在安卓上，当点击提交按钮的时候，exitview上面的内容即文本内容srctext。后面的两个字段不重要，随意填写，其实src_text也是随意写也行。不影响返回200.



2. 对服务器返回Base64加密字符串进行解密转语音，并播放语音。


