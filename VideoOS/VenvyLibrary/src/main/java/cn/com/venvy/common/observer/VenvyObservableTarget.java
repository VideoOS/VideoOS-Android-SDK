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
    public static final String TAG_ACR_DATA_MESSAGE = "notifyAcrRecognizeMessage";
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
    // 展示视联网小程序异常处理逻辑
    public static final String TAG_SHOW_VISION_ERROR_LOGIC = "notifyVisionProgramErrorLogic";
    // 更新视联网小程序Title
    public static final String TAG_UPDATE_VISION_TITLE = "notifyUpdateVisionProgramTitle";
    // 拉起一个H5小程序
    public static final String TAG_H5_VISION_PROGRAM = "notifyLaunchH5Program";
    // 关闭一个H5小程序
    public static final String TAG_CLOSE_H5_VISION_PROGRAM = "notifyCloseH5Program";
    // 启动桌面小程序
    public static final String TAG_LAUNCH_DESKTOP_PROGRAM = "notifyLaunchDesktopProgram";
    // 在顶层添加一个lua脚本
    public static final String TAG_ADD_LUA_SCRIPT_TO_TOP_LEVEL = "notifyAddLuaScriptToTopLevel";
    // 清除所有的视联网小程序
    public static final String TAG_CLEAR_ALL_VISION_PROGRAM = "notifyClearAllVisionProgram";
    // 启动一个下载链接
    public static final String TAG_DOWNLOAD_TASK = "notifyDownLoadTask";
    // 开始安装
    public static final String TAG_INSTALL_START = "notifyInstallStart";

    public static class Constant {
        public static final int CONSTANT_LANDSCAPE = 1;
        public static final int CONSTANT_PORTRAIT = 2;
        public static final String CONSTANT_DATA = "data";
        public static final String CONSTANT_SCREEN_CHANGE = "screen_changed";
        public static final String CONSTANT_MSG = "msg";
        public static final String CONSTANT_NEED_RETRY = "needRetry";
        public static final String CONSTANT_TITLE = "title";
        public static final String CONSTANT_NVG_SHOW = "navIsShow";
        public static final String CONSTANT_APP_TYPE = "appType";
        public static final int CONSTANT_APP_TYPE_LUA = 1; // 视联网小程序lua
        public static final int CONSTANT_APP_TYPE_H5 = 2; // 视联网小程序 H5
        public static final int CONSTANT_APP_TYPE_TOOLS = 3; // 视联网小工具
        public static final String CONSTANT_H5_URL = "h5Url";
        public static final String CONSTANT_LUA_NAME = "luaName";
        public static final String CONSTANT_TEMPLATE = "template";
        public static final String CONSTANT_ID = "id";
        public static final String CONSTANT_MINI_APP_INFO = "miniAppInfo";
        public static final String CONSTANT_VIDEO_MODE_TYPE = "videoModeType";
        public static final String CONSTANT_DEVELOPER_USER_ID = "developerUserId";
        public static final String CONSTANT_VIDEO_MODE_X_OFFSET = "eyeOriginPointX";
        public static final String CONSTANT_VIDEO_MODE_Y_OFFSET = "eyeOriginPointY";
        public static final String CONSTANT_LABEL_CONF_DATA = "labelConfData";
        public static final String CONSTANT_DOWNLOAD_API = "downloadAPI";
        public static final String CONSTANT_LEVEL = "level";
        public static final String CONSTANT_LAUNCH_PLAN_ID = "launchPlanId";
    }

}
