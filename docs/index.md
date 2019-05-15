# VideoOS Android SDK

## SDK集成
有两种方式将Video++互动层添加到你的工程：

- 使用项目依赖
- 手动添加配置compile project(':venvy_pub')

### 使用Gradle
```
repositories {
  maven { url 'https://dl.bintray.com/videoli/maven/' }
}
dependencies {
  implementation 'com.videoli:VideoOS:1.2.0'
}
```

##### 兼容性
```
向下兼容 Android SDK: 16
编译 Android SDK: API 26或更高版本进行编译
```

#### 快速集成SDK
1. 使用Gradle集成，具体可参看 VideoOS/app_demo 工程配置：
```
repositories {
  mavenCentral()
  maven { url 'https://dl.bintray.com/videoli/maven/' }
}
dependencies {
  implementation 'com.videoli:VideoOS:1.2.0'
}
```

2. AndroidManifest.xml需要配置：
```
<!-- 允许程序打开网络套接字 -->
<uses-permission android:name="android.permission.INTERNET" />
```
   
3. 依赖的第三方库(具体视平台不同而不一致)

```
compile "com.github.bumptech.glide:glide:3.7.0"
compile "com.squareup.okhttp3:okhttp:3.8.0"
compile 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.0.2'
```
	  
## 互动层对接	

### SDK初始化
在 `Application`项目入口初始化SDK。

示例代码：

```java
VideoPlus.appCreate(Application application);
```
### 对接`VideoPlusView`
	
1. 根据需要接入的`SDK`创建`VideoOsView`，将`SDK`需要的信息配置在`VideoPlusAdapter`中。
	
	* videoID 为点播视频url或直播房间号
	* types 为视频类型（点播or直播），默认为点播(注：VideoType.VIDEOOS 表示点播，VideoType.LIVEOS 表示直播)


2. 初始化`VideoPlusAdapter`， `VideoOsView`就是生成的互动层，将这个`view`添加到播放器层之上就可以了。`SDK`所需参数需复写VideoPlusAdapter相关方法,详细作用请看注释。

```java
//适配器VideoPlusAdapter (注：VideoView代表平台播放器,非必填)
public class PlusAdapter extends VideoPlusAdapter {
    private VideoView player;

    public PlusAdapter(VideoView player) {
        this.player = player;
    }
    //设置配置信息(注:setVideoID为点播视频ID,直播为房间号)

    /***
     * 设置配置信息
     * @return Provider配置信息类
     * 注:setVideoID(String videoId)为点播视频ID,直播为房间号
     *    setVideoType(VideoType videoType)为视频类型，VideoType.VIDEOOS点播 VideoType.LIVEOS直播
     */
    @Override
    public Provider createProvider() {
        Provider provider = new Provider.Builder().setVideoID(String.valueOf(12)).setVideoType(VideoType.LIVEOS).build();
        return provider;
    }

    /***
     *
     * @return IMediaControlListener 平台方播放器相关状态
     * 注:     getVideoSize(int horVideoWidth, int horVideoHeight, int verVideoWidth, int verVideoHeight, int portraitSmallScreenOriginY)为视频播放器横竖屏Size(必填)
     *         getCurrentPosition()为播放器当前播放时间(单位:毫秒)，点播必须复写处理 直播无需此操作。
     */
    @Override
    public IMediaControlListener buildMediaController() {
        return new VideoOSMediaController() {
            @Override
            public VideoPlayerSize getVideoSize() {
                return new VideoPlayerSize(VenvyUIUtil.getScreenWidth(player.getContext()), VenvyUIUtil.getScreenHeight(player.getContext()),
                        VenvyUIUtil.getScreenWidth(player.getContext()), 200, 0);
            }

            @Override
            public long getCurrentPosition() {
                return player != null ? player.getCurrentPosition() : -1;
            }
        };
    }

    //广告展示监听
    @Override
    public IWidgetShowListener buildWidgetShowListener() {
        return super.buildWidgetShowListener();
    }

    //广告点击监听
    @Override
    public IWidgetClickListener buildWidgetClickListener() {
        return super.buildWidgetClickListener();
    }

    //广告关闭监听
    @Override
    public IWidgetCloseListener buildWidgetCloseListener() {
        return super.buildWidgetCloseListener();
    }

    //注册网络图片架构插件
    @Override
    public Class<? extends IImageLoader> buildImageLoader() {
        return GlideImageLoader.class;
    }

    //注册网络请求架构插件
    @Override
    public Class<? extends IRequestConnect> buildConnectProvider() {
        return cn.com.venvy.common.okhttp.OkHttpHelper.class;
    }

    //MQTT长连接结构插件
    @Override
    public Class<? extends ISocketConnect> buildSocketConnect() {
        return VenvyMqtt.class;
    }
}
/* 详细调用请查看 VideoOS/app_demo 项目 */
```
 
3. 接着设置适配器，代码如下所示

```java
VideoPlusView plusView = new VideoOsView(Context context);
PlusAdapter plusAdapter = new PlusAdapter(MediaPlay play);
plusView.setVideoOSAdapter(plusAdapter);
```

4. 全部完成之后调用 `start` ，开启互动层
```java
plusView.start();
```
5. 如退出播放页面或直播间，调用`stop`方法
```java
plusView.stop();
```
6. 其它

   6.1屏幕旋转处理（注：FULL_VERTICAL,SMALL_VERTICAL,LANDSCAPE分别代表平台方播放器界面调用）：
   ```java
   plusAdapter.notifyVideoScreenChanged(ScreenStatus.SMALL_VERTICAL);
   ```
   
   6.2中插视频广告暂停唤醒调用:
   ```java
   plusAdapter.notifyMediaStatusChanged(MediaStatus.PLAYING);
   ```
7. 互动层状态相关参数说明 		
* adID 为广告的唯一标识
* adName 为广告名
* eventType 为广告触发的事件，包括展示、点击、关闭等
* actionType 为对接方需要做的操作，包括打开外链，暂停视频，播放视频
* url 为外链地址

#### VideoOS/app_demo 项目功能

1. Demo项目首页分为直播,点播，点击进入对应的平台。
2. 进入默认开启播放器 开启互动。
3. 底部提供俩个配置按钮，实现对互动广告的配置。
4. 底部“模拟”按钮为测试本地广告功能。
5. 底部右侧按钮为互动配置项，点击弹出配置项，可输入“素材名称”或“VideoID”，其中输入“素材名称”为预览未投放的素材，输入“VideoID”展示已投放的素材。

#### 注意事项

1. VideoPlusAdapter Provider参数为视频的标识(原url),可以用url作为参数 或 使用拼接 ID的方式来识别。
2. 文档中的代码仅供参考，实际参数请根据项目自行配置。
3. 请将互动层置于合适位置以防阻挡事件分发操作。
4. 最佳位置为加载控制栏的下方,播放器上方。
 
## 本地化配置

### host配置
修改`VideoOS/VenvyLibrary/src/main/java/cn/com/venvy/Config.java`中`HOST_VIDEO_OS`地址

### 加密key设置
修改`VideoOS/VenvyLibrary/src/main/java/cn/com/venvy/common/utils/VenvyRSAUtil.java`中`KEY_PUBLIC`值
