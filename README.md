# VideoOS Android SDK
本项目是[VideoOS open](http://videojj.com/videoos-open/)的Android SDK

## 对接文档
对接文档请移步[这里](docs/index.md)

## 项目概述
[VideoOS](http://videojj.com/videoos-open/)为视频流量主提供基于视频场景生态下的小程序解决方案

### 紧贴视频内容运营
结合视频内容，选择紧贴视频内容的运营方案，在播放视频时，自然的出现小程序

![云图](ScreenShot/cloud.gif)

### 提高观看的互动性
丰富多样的小程序，涵盖9大行业，激发用户观看视频的互动性，带来更多流量的转化

![气泡](ScreenShot/bubble.gif)

### 可实现快速变现
小程序提供电商、广告等多种变现形式，流量主使用后可快速体验到流量的变现价值

![卡牌](ScreenShot/card.gif)

### AI助力加持
Video++有成熟的视频AI算法加持，自动识别视频内容中的场景点位，减少人工运营成本

![视联网模式](ScreenShot/videomode.gif)


## 项目介绍
[VideoOS](http://videojj.com/videoos-open/)使用了Lua脚本语言来完成动态化方案，具有可扩展性强、简单、高效、占用体积小、启动速度快等诸多优势


** VideoOS-Android-SDK 结构 **
![](https://upload-images.jianshu.io/upload_images/566387-d3b06659d0d5f50a.png)

app_demo : Android相关环境配置好后，可直接run的模块，演示demo模块

venvy_pub : SDK核心模块。处理lua加载，lua脚本文件下载等逻辑

VenvyLibrary : 提供本地相关lua源码，相关工具类

LuaViewSDK : [阿里巴巴开源项目](https://github.com/alibaba/LuaViewSDK)提供Lua和Java的桥接

Annotation : 注解的声明和编译。主要包含`venvy_processor_annotation`和`venvy_processor_compiler`两个模块


## 维护者
* [androidlgf](https://github.com/androidlgf) - 防防 <guofang@videopls.com>
* [vainfeng](https://github.com/vainfeng) - 疯子 <liangzifeng@videopls.com>
* [phoche](https://github.com/phoche) - 老秦 <qinpengcheng@videopls.com>
* [hzzhujf](https://github.com/hzzhujf) - 飞猪 <flyingpig@videopls.com>

## License
[GPL v3](LICENSE)
