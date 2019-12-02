# VideoOS Android SDK
VideoOS Android SDK 开源地址：[https://github.com/VideoOS/VideoOS-Android-SDK](https://github.com/VideoOS/VideoOS-Android-SDK)
## SDK基本能力及对接要求

| 功能 |  对接要求  |
| ----- | --------- | 
| 前期准备 | 必须对接 | 
| 基础接口  |  必须对接  |
| 前后帖广告  |  可选对接  | 
具体对接方法请参考下面详细集成流程。
## SDK集成

### 使用Gradle集成
在Project的gradle文件中加入对应的maven库

```
allprojects {
    repositories {
        maven { url 'https://dl.bintray.com/videoli/maven/' }
    }
}

```
在项目的gradle文件中加入

```
dependencies {
	implementation 'com.videoli:VideoOS:1.2.0'
	implementation 'com.videoli:venvy_processor_annotation:1.0.0'
	annotationProcessor 'com.videoli:venvy_processor_compiler:1.0.1'
	// SDK Glide图片加载插件 目前仅支持Glide 和 fresco
	// glide 可选 com.videoli:venvy_glide:1.0.4  or com.videoli:venvy_glide_v4:1.0.1
	// fresco 选 com.videoli:venvy_fresco:1.0.4
	implementation "com.videoli:venvy_glide:1.0.4"
	implementation 'com.videoli:venvy_svga:1.0.9'
    	implementation "com.just.agentweb:agentweb:4.1.2"
	
	// 依赖的其他第三方库，具体视平台不同版本而不一致
	implementation "com.github.bumptech.glide:glide:3.7.0"
	implementation "com.squareup.okhttp3:okhttp:3.8.0"
	implementation 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.0.2'
}
```

### 设备权限

```
<!-- 授予网络权限 -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.VIBRATE" />
```


### 混淆设置

```
# Mqtt
-keep class org.eclipse.paho.client.mqttv3.** {
    <fields>;
    <methods>;
}

# videojj
-keep public class cn.com.venvy.processor.build.venvy_pub$$VenvyRoleMapUtil{ *; }
-keep class cn.com.venvy.lua.** {
     <fields>;
     <methods>;
}
-keep class cn.com.videopls.** {
    <fields>;
    <methods>;
}
```

### 兼容性
```
向下兼容 Android SDK: 16
编译 Android SDK: API 26或更高版本进行编译
```


## 互动层对接    

### SDK初始化
在项目`Application`类的`onCreate`生命周期函数中初始化SDK

```java
//appKey, appSecret 请去控制台查看
VideoPlus.appCreateSAAS(this, appKey, appSecret); 
```

### 核心功能对接 ( Step1-5 ) 

#### Step1. VideoOsView声明

在播放器所在对应的layout文件中声明`VideoOsView `控件：

- 确保视图层级在播放器视图之上
- 确保`VideoOsView`宽高填充整个根视图

```
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true">
    <!-- 播放器 -->
    <both.video.venvy.com.appdemo.widget.StandardVideoOSPlayer
        android:id="@+id/player"
        android:layout_width="match_parent"
        android:layout_height="@dimen/activity_player_height"/>
        
	 <!-- 确保VideoOsView视图层级在播放器之上，且宽高填充整个根视图 -->   
    <cn.com.videopls.pub.os.VideoOsView
        android:id="@+id/os_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        />
</FrameLayout>
```

#### Step2. 激活广告互动功能

在对应的Activity或者Fragment的初始化生命周期函数中，初始化`VideoOsView`且设置对应的`VideoPlusAdapter`：

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(...)
      // 播放器相关初始化...
      .....
    
    mVideoPlusView = mRootView.findViewById(R.id.os_view);     
    // 初始化VideoPlusAdapter的子类,mVideoPlayer为播放器View
    mAdapter = new VideoOsAdapter(mVideoPlayer);
    // 将adapter设置进`VideoOsView`
    mVideoPlusView.setVideoOSAdapter(mAdapter);
    
}
```


在播放器相关监听中，在播放视频前的回调中（onStartPre,onPlayPre..等类似回调）激活`VideoOsView`：

```java
if (isFirstPlayVideo) {
	  // 首次播放，只需调用start启动
	  mVideoPlusView.start();
    isFirstPlayVideo = false;
} else {
	  // 非首次播放，视为切集操作
	  mVideoPlusView.stop();
	  mAdapter.updateProvider(mAdapter.generateProvider(appKey,appSecret,videoId));
	  mVideoPlusView.start();
}
```

`generateProvider`为demo中子类封装的函数，非`VideoPlusAdapter `自有。


#### Step3. 实现VideoPlusAdapter

自定义`VideoOsAdapter`集成自SDK`VideoPlusAdapter`

构造函数中可传入平台播放器，基础功能需要实现`createProvider `、`buildMediaController`和一些必要的回调：


```java
public class VideoOsAdapter extends VideoPlusAdapter {
    /***
    * 设置配置信息
    * @return Provider配置信息类
    * 注:setVideoID(String videoId)为点播视频ID,直播为房间号
    *    VideoType为视频类型，VideoType.VIDEOOS为点播,VideoType.LIVEOS为直播
    *    appKey 平台创建的应用信息（注：saas版本需要设置）
    *    appSecret 平台创建的应用信息（注：saas版本需要设置）
    */
    @Override
    public Provider createProvider() {
         return new Provider.Builder()
                .setAppKey(appKey)
                .setAppSecret(appSecret)
                .setVideoID(videoId)
                .setVideoType(VideoType.VIDEOOS)
                .build();    
                }
                
     /**
     * 横竖屏切换时的回调
     * @return
     */
    @Override
    public IWidgetRotationListener buildWidgetRotationListener() {
        return new IWidgetRotationListener() {
            @Override
            public void onRotate(RotateStatus status) {
                if(status == RotateStatus.TO_VERTICAL){
                    // 横屏转竖屏
                    notifyVideoScreenChanged(ScreenStatus.SMALL_VERTICAL);
                }else if(status == RotateStatus.TO_LANDSCAPE){
                    // 竖屏转横屏
                    notifyVideoScreenChanged(ScreenStatus.LANDSCAPE);
                }
            }
        };
    }
    
     /**
     * 平台方播放器相关业务状态
     */
    @Override
    public IMediaControlListener buildMediaController() {
        return new VideoOSMediaController() {
            /**
             * 获取当前播放器播放时间
             * 直播无需复写。仅针对点播
             */
            @Override
            public long getCurrentPosition() {
                return mPlayer != null ? mPlayer.getCurrentPositionWhenPlaying() : -1;
            }
            /**
             * 获取当前播放器播放横竖屏大小
             * osContentWidth, osContentHeight为广告展示内容的大小，通常传入设备屏幕的宽高，实际情况中需要考虑状态栏和底部导航栏做出一定调整
             * videoVerticalWidth,videoVerticalHeight为竖屏状态下播放器的宽高
             */
            @Override
            public VideoPlayerSize getVideoSize() {
                return  return new VideoPlayerSize(osContentWidth,osContentHeight,videoVerticalWidth,videoVerticalHeight,0);;
            }
	    /**
	    * 视频真实宽高以及X，Y偏移量
	    * 针对实际视频比例与设备物理尺寸的情况
	    /
	    @Override
            public VideoFrameSize getVideoFrameSize() {
                return new VideoFrameSize(VenvyUIUtil.getScreenWidth(MyApp.getInstance()),
                        VenvyUIUtil.getScreenHeight(MyApp.getInstance()), 0, 0);
            }
	    
	    @Override
            public String getVideoEpisode() {
                return "当前的剧集名称";
            }

            @Override
            public String getVideoTitle() {
                return "当前的视频标题";
            }
        };
    }
}

```

##### Adapter高级功能

- 功能插件注册

```java
//注册网络图片架构插件
@Override
public Class<? extends IImageLoader> buildImageLoader() {
    return GlideImageLoader.class;
}

//注册网络请求架构插件
@Override
public Class<? extends IRequestConnect> buildConnectProvider() {
    return OkHttpHelper.class;
}

//MQTT长连接结构插件
@Override
public Class<? extends ISocketConnect> buildSocketConnect() {
    return VenvyMqtt.class;
}

// SVGA 动画插件
@Override
    public Class<? extends ISvgaImageView> buildSvgaImageView() {
        return VenvySvgaImageView.class;
    }

```

- 广告对应监听

```java
/**
  * 广告展示监听
  */
@Override
public IWidgetShowListener buildWidgetShowListener() {
    return new IWidgetShowListener<WidgetInfo>() {
        @Override
        public void onShow(WidgetInfo info) {
            //展示回调方法
            if (info == null)
                return;
            widgetAction(info);
        }
    };
}

/**
  * 广告点击监听
  */
@Override
public IWidgetClickListener buildWidgetClickListener() {
    return new IWidgetClickListener<WidgetInfo>() {
        @Override
        public void onClick(@Nullable WidgetInfo info) {
            widgetAction(info);
        }
    };
}

/**
  * 广告关闭监听
  */
@Override
public IWidgetCloseListener buildWidgetCloseListener() {
    return new IWidgetCloseListener<WidgetInfo>() {
        @Override
        public void onClose(WidgetInfo info) {
            widgetAction(info);
        }
    };
}

/**
  * 中插back按钮点击回调
  */
@Override
public WedgeListener buildWedgeListener() {
    return new WedgeListener() {
        @Override
        public void goBack() 
        }
    };
}



/**
  * 处理广告行为
  * @param info
  */
private void widgetAction(WidgetInfo info) {
    //注(actionType为广告出现，销毁，点击等等需要平台方处理事件类型)
    WidgetInfo.WidgetActionType actionType = info.getWidgetActionType();
    String url = info.getUrl();
    switch (actionType) {
        case ACTION_NONE:
            break;
        //平台方暂停播放器事件
        case ACTION_PAUSE_VIDEO:
            if (mPlayer != null) {
                mPlayer.onVideoPause();
            }
            break;
        //平台方重新开启播放器事件
        case ACTION_PLAY_VIDEO:
            if (mPlayer != null) {
                mPlayer.onVideoResume();
            }
            break;
        //平台方打开H5事件
        case ACTION_OPEN_URL:
            // 打开H5
            break;
        case ACTION_GET_ITEM:

            break;
    }
}

```

`VideoOsAdapter`详情查看app_demo项目中源码


#### Step4. 播放器横竖屏切换处理

复写Activity中`onConfigurationChanged`函数处理视频的横竖屏情况

```java
/**
  * 屏幕切换回调此生命周期
  * 需要通过adapter.notifyVideoScreenChanged(ScreenStatus.LANDSCAPE)来通知屏幕状态
  * ScreenStatus.LANDSCAPE 横屏
  * ScreenStatus.SMALL_VERTICAL 竖屏小屏
  * ScreenStatus.FULL_VERTICAL  竖屏全屏
  */
@Override
public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (mVideoPlayer == null) {
        return;
    }
    ViewGroup.LayoutParams params = mVideoPlayer.getLayoutParams();
    if (params == null) {
        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
    if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        // 手机竖屏
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = VenvyUIUtil.dip2px(this, 200);
        if (mAdapter != null) {
            mAdapter.notifyVideoScreenChanged(ScreenStatus.SMALL_VERTICAL);
        }
    } else {
        // 手机横屏
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //横屏隐藏状态栏
        if (mAdapter != null) {
            mAdapter.notifyVideoScreenChanged(ScreenStatus.LANDSCAPE);
        }
    }
    mVideoPlayer.setLayoutParams(params);
}
```

#### Step5. 资源释放

在相关Activity的`onDestroy`生命周期函数中释放相关资源

```java
@Override
protected void onDestroy() {
    super.onDestroy();
    if (mVideoPlusView != null) {
        VideoPositionHelper.getInstance().cancel();
        mVideoPlusView.stop();
    }
}

```

## 视联网模式

额外引用

```
    // 需在对应module 的gradle中额外引用
    provided "com.just.agentweb:agentweb:4.1.2"

    // 在VideoOsAdapter中实现对应插件
    // webView插件
    @Override
    public Class<? extends IVenvyWebView> buildWebView() {
        return VenvyWebView.class;
    }
```


通过`videoOsView`的startService()启动视联网模式

```
// 视联网启动分气泡模式（ServiceTypeVideoMode_POP）和标签模式（ServiceTypeVideoMode_TAG）
// 气泡模式仅出现视联网气泡识别结果，标签模式除了气泡识别结果还会识别展示内容对象的对应tag.

mVideoPlusView.startService(ServiceType.ServiceTypeVideoMode_TAG, new HashMap<String, String>(), new IServiceCallback() {

                            @Override
                            public void onCompleteForService() {
                                // 启动成功
                            }

                            @Override
                            public void onFailToCompleteForService(Throwable throwable) {
                               // 启动失败
                            }
                        });
                    }

 mVideoPlusView.stopService(ServiceType.ServiceTypeVideoMode); // 关闭视联网模式

```

## 前后贴

跟`视联网模式`的启动方式差不多，第一个ServiceType是个枚举。

```
  // ServiceTypeVideoMode_POP(1),//视联网模式- 气泡模式
  // ServiceTypeFrontVideo(3),//前帖广告
  // ServiceTypeLaterVideo(4),//后贴广告
  // ServiceTypePauseAd(5);//暂停广告
  // ServiceTypeVideoMode_TAG(6),//视联网模式 - 标签模式
  // ServiceTypeVideoTools(7); // 视联网小工具

        HashMap<String, String> params = new HashMap<>();
        // duration 参数指定播放倒计时
        params.put("duration", "60");
        mVideoPlusView.startService(ServiceType.ServiceTypeFrontVideo, params, new IServiceCallback() {
            @Override
            public void onCompleteForService() {
 		 
            }

            @Override
            public void onFailToCompleteForService(Throwable throwable) {
               
            }
        });

```


## 其他

- 广告监听中相关属性说明（WidgetInfo）

```
 - adID 为广告的唯一标识
 - adName 为广告名
 - eventType 为广告触发的事件，包括展示、点击、关闭等
 - actionType 为对接方需要做的操作，包括打开外链，暂停视频，播放视频
 - url 为外链地址
```

- 中插视频广告暂停唤醒，可调用

``` java
adapter.notifyMediaStatusChanged(MediaStatus.PLAYING);

```

- VideoOs/app_demo 项目功能介绍

```
 - Demo项目首页分为直播,点播，点击进入对应的平台。
 - 进入默认开启播放器 开启互动。
 - 底部提供俩个配置按钮，实现对互动广告的配置。
 - 底部“模拟”按钮为测试本地广告功能。
 - 底部右侧按钮为互动配置项，点击弹出配置项，可输入“素材名称”或“VideoID”，其中输入“素材名称”为预览未投放的素材，输入“VideoID”展示已投放的素材。

```

### 注意事项

- VideoPlusAdapter中Provider的videoId参数为视频的标识(原url),可以用url作为参数 或 使用拼接 ID的方式来识别。此值需要与控制台输入的视频ID(直播为房间号)保持一致
- 文档中的代码仅供参考，实际参数请根据项目自行配置
- 请将互动层（VideoOsView）置于合适视图层级以防阻挡事件分发操作。建议视图层级为加载控制栏的下方,播放器上方


## 常见问题

### 在预期的时间没有展示对应的投放效果

- 确定对应的服务器环境和appKey&appSecret是否对应正确

- 检查adapter中的getVideoPlayerSize（）返回的VideoPlayerSize参数是否正确

- 如果是点播，请检查adapter中getCurrentPosition（）返回的当前播放时间是否与投放计划中设置的一致

### 横竖屏切换视图显示异常

- 请检查在对应Activity中有无处理onConfigurationChanged()方法。须通过这个回调通知adapter屏幕变化

```
 mAdapter.notifyVideoScreenChanged(ScreenStatus.SMALL_VERTICAL);// 竖屏小屏
 mAdapter.notifyVideoScreenChanged(ScreenStatus.FULL_VERTICAL);// 竖屏全屏
 mAdapter.notifyVideoScreenChanged(ScreenStatus.LANDSCAPE);// 横屏
```

### 刘海屏等异形屏适配有问题

- 需要计算设备实际的宽高，设置adapter中VideoPlayerSize的contentHeight参数。这个参数需要与`VideoOsView`的实际高度一致（match_parent）。在不同的设备上，通过WindowManager获取屏幕高度与高度为match_parent的`VideoOsView`并不一致。需要考虑异形屏，状态栏，底部导航栏等因素，得出一个实际的值传入VidePlayerSize中


### 中插视频如何暂停/恢复

 - adapter.notifyMediaStatusChanged(MediaStatus.PLAYING);



### 广告监听处理

- `VideoPlusAdapter `中覆盖相关方法

```
	//广告展示监听插件
    @Override
    public IWidgetShowListener buildWidgetShowListener() {}

    //广告点击监听插件
    @Override
    public IWidgetClickListener buildWidgetClickListener() {}

    //广告关闭监听插件
    @Override
    public IWidgetCloseListener buildWidgetCloseListener() {}

```
##技术支持

* [androidlgf](https://github.com/androidlgf) - 防防 <guofang@videopls.com>
* [vainfeng](https://github.com/vainfeng) - 疯子 <liangzifeng@videopls.com>
* [phoche](https://github.com/phoche) - 老秦 <qinpengcheng@videopls.com>
* [hzzhujf](https://github.com/hzzhujf) - 飞猪 <flyingpig@videopls.com>
