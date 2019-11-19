package both.video.venvy.com.appdemo;

import cn.com.venvy.common.debug.DebugStatus;

public class UrlContent {
    private static final String URL_DEV_DEBUG_CONFIG_INFO = "/os-api-saas/api/getDevAppDebugInfo";

    public static String getUrlDevDebugConfigInfo(){
        return getHostUrl() + URL_DEV_DEBUG_CONFIG_INFO;
    }

    private static String getHostUrl(){
        String hostUrl = "";
        if(DebugStatus.isDev()){
            hostUrl = "https://dev-os-saas.videojj.com";
        } else if (DebugStatus.isTest()) {
            hostUrl = "https://test-os-saas.videojj.com";
        }else if(DebugStatus.isRelease()){
            hostUrl = "https://os-saas.videojj.com";
        } else if (DebugStatus.isPreView()) {
            hostUrl = "https://pre-os-saas.videojj.com";
        }
        return hostUrl;
    }
}
