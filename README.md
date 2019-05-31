# VideoOS Android SDK
本项目是[VideoOS open](http://videojj.com/videoos-open/)的Android SDK

## 对接文档
对接文档请移步[这里](docs/index.md)

## 项目介绍
本库是一个强大灵活的Android客户端视频动态化解决方案。在视频播放过程中,通过动态加载lua脚本的方式展示定制化的内容，以带动视频交互方式，挖掘视频价值。


### Simple
![](https://upload-images.jianshu.io/upload_images/566387-86d2da3afb8352e5.jpeg)

![](https://upload-images.jianshu.io/upload_images/566387-95b261701c84b069.jpeg)

![](https://upload-images.jianshu.io/upload_images/566387-aa9eee057ef8ec08.jpeg)


### 项目结构
![](https://upload-images.jianshu.io/upload_images/566387-d3b06659d0d5f50a.png)

app_demo : Android相关环境配置好后，可直接run的模块

venvy_pub : SDK核心模块。处理lua加载，lua脚本文件下载等逻辑

VenvyLibrary : 提供本地相关lua源码，相关工具类

LuaViewSDK : [阿里巴巴开源项目](https://github.com/alibaba/LuaViewSDK)

Annotation : 注解的声明和编译。主要包含`venvy_processor_annotation`和`venvy_processor_compiler`两个模块


### demo功能介绍

- 主界面仅有直播和点播的入口，同时显示当前的服务器环境和当前的appKey&appSecret

- 切换环境的时候需要同时切换到对应的appKey&appSecret

- 视频播放页面，已设置默认播放内容。同时也可通过屏幕置底的配置按钮自定义播放资源。播放资源需要和控制台的投放计划保持一致才有效

- 通过模拟按钮支持加载本地lua脚本查看效果。自定义lua脚本于`OsActivity`或者`LiveActivity`中onClick事件内进行修改

```
	Uri uri = Uri.parse("LuaView://defaultLuaView?template=os_red_envelope_hotspot.lua&id=os_red_envelope_hotspot");
	HashMap<String, String> params = new HashMap<>();
	params.put("data", AssetsUtil.readFileAssets("local_red.json", OsActivity.this));
```

- 修改template和id为具体的lua文件名即可。如果lua文件需要初始化数据，则需要提供对应的json文件。具体请参考`VenvyLibrary`模块 assets文件夹下源码

## 贡献者指南
待补充，请直接将内容填写到[CONTRIBUTING](CONTRIBUTING)，参考：[https://help.github.com/en/articles/setting-guidelines-for-repository-contributors](https://help.github.com/en/articles/setting-guidelines-for-repository-contributors)

## 维护者
* [androidlgf](https://github.com/androidlgf) - 防防 <guofang@videopls.com>
* [vainfeng](https://github.com/vainfeng) - 疯子 <liangzifeng@videopls.com>
* [phoche](https://github.com/phoche) - 老秦 <qinpengcheng@videopls.com>
* [hzzhujf](https://github.com/hzzhujf) - 飞猪 <flyingpig@videopls.com>

## License
[GPL v3](LICENSE)
