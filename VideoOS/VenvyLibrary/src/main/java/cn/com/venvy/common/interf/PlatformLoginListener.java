package cn.com.venvy.common.interf;

import java.io.Serializable;

import cn.com.venvy.common.bean.PlatformUserInfo;

/**
 * Created by yanjiangbo on 2018/1/30.
 */

public abstract class PlatformLoginListener implements IPlatformLoginInterface, Serializable {

    private static final long serialVersionUID = 19759952624L;

    public void userLogined(PlatformUserInfo userInfo) {

    }

    public void screenChanged(ScreenChangedInfo changedInfo) {

    }

    @Override
    public boolean isLogined() {
        return getLoginUser() != null && (getLoginUser().getUserToken() != null || getLoginUser().getUid() != null);
    }
}
