package both.video.venvy.com.appdemo.bean;

import java.io.Serializable;

import cn.com.venvy.common.bean.PlatformUserInfo;
import cn.com.venvy.common.debug.DebugStatus;
import cn.com.venvy.common.interf.ScreenStatus;

/**
 * Created by liyangyang on 2018/1/3.
 */

public class SettingsBean implements Serializable {

    public DebugStatus.EnvironmentStatus mStatus = DebugStatus.EnvironmentStatus.DEV;
    //点播参数相关
    public String mAppkey;
    public String mUrl;

    //直播参数相关
    public String mRoomId;
    public String mPlatformId;
    public String mCate;
    public ScreenStatus mScreenStatus = ScreenStatus.SMALL_VERTICAL;

    //互娱增加参数，在直播基础上增加用户和主播
    public PlatformUserInfo.UserType mUserType;
    public boolean isUserApp = true;
    public boolean isFullScreen = true;
    public String mUid;

    public int mVerticalHeight;
}
