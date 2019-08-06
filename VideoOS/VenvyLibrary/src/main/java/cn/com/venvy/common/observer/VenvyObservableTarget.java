package cn.com.venvy.common.observer;

/**
 * Created by yanjiangbo on 2018/1/18.
 */

public class VenvyObservableTarget {

    public static final String TAG_ACTIVITY_CHANGED = "notifyActivityStatusChanged";
    public static final String TAG_MEDIA_POSITION_CHANGED = "notifyMediaPositionChanged";
    public static final String TAG_SCREEN_CHANGED = "notifyScreenChanged";
    public static final String TAG_MEDIA_CHANGED = "notifyMediaChanged";
    public static final String TAG_DATA_SET_CHANGED = "notifyProviderChanged";
    public static final String TAG_CLIP_MEDIA_STATUS_CHANGED = "notifyClipMediaStatusChanged";

    public static final String TAG_ARRIVED_DATA_MESSAGE = "notifyLiveOnlineMessage";
    public static final String TAG_JS_BRIDGE_OBSERVER = "notifyJSBridge";
    public static final String TAG_KEYBOARD_STATUS_CHANGED = "notifyKeyboardChanged";
    public static final String TAG_VOLUME_STATUS_CHANGED = "notifyVolumeStatusChanged";
    // 启动一个视联网小程序
    public static final String TAG_LAUNCH_VISION_PROGRAM = "notifyLaunchVisionProgram";
    public static final String KEY_APPLETS_ID = "appletId";
    public static final String KEY_ORIENTATION_TYPE = "orientationType"; // 1横屏,2竖屏

    // 关闭一个视联网小程序
    public static final String TAG_CLOSE_VISION_PROGRAM = "notifyCloseVisionProgram";
    // 添加一个lua视图到当前视联网小程序容器上
    public static final String TAG_ADD_LUA_SCRIPT_TO_VISION_PROGRAM = "notifyAddLuaScriptToContainer";


    public static class Constant {
        public static final int CONSTANT_LANDSCAPE = 1;
        public static final int CONSTANT_PORTRAIT = 2;
        public static final String CONSTANT_DATA = "data";
        public static final String CONSTANT_SCREEN_CHANGE = "screen_changed";
    }

}
