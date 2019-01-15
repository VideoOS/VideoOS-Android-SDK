package cn.com.venvy.common.interf;

import cn.com.venvy.common.bean.PlatformUserInfo;
import cn.com.venvy.common.exception.LoginException;

/**
 * Create by qinpc on 2017/9/12
 */
public interface IPlatformLoginInterface {

    PlatformUserInfo getLoginUser();

    boolean isLogined();

    void login(LoginCallback loginCallback);

    void userLogined(PlatformUserInfo userInfo);

    void screenChanged(ScreenChangedInfo changedInfo);

    class ScreenChangedInfo {
        public int screenType;
        public String url;
        public String ssid;
    }

    interface LoginCallback {
        void loginSuccess(PlatformUserInfo platformUserInfo);

        void loginError(LoginException loginException);
    }

}
