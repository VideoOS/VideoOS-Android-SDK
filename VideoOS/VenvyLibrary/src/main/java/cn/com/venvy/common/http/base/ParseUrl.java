package cn.com.venvy.common.http.base;

import java.util.HashMap;

import cn.com.venvy.common.debug.DebugStatus;

/**
 * Created by yanjiangbo on 2017/5/25.
 */

public class ParseUrl {

    public enum VenvyUrl {
        ADS_API("ads-api.videojj.com", "ads-api.videojj.com", "test-ads-api.videojj.com", "test-ads-api.videojj.com"),

        VA("va.videojj.com", "va.videojj.com", "test-va.videojj.com", "test-va.videojj.com"),

        CYTRON("cytron.videojj.com", "precytron.videojj.com", "test-cytron.videojj.com", "test-cytron.videojj.com"),

        LIVE("liveapi.videojj.com", "liveapi.videojj.com", "test.liveapi.videojj.com", "test.liveapi.videojj.com"),

        LOG_REPORT("log.videojj.com", "test-log.videojj.com", "test-log.videojj.com", "test-log.videojj.com"),

        OS("os-open.videojj.com", "pre-os-open.videojj.com", "test-os-open.videojj.com", "dev-os-open.videojj.com"),

        OS_SAAS("os-saas.videojj.com", "pre-os-saas.videojj.com", "test-os-saas.videojj.com", "dev-os-saas.videojj.com"),

        PLAT_LIVE("plat.videojj.com", "pre-plat.videojj.com", "test-plat.videojj.com", "dev-plat.videojj.com");

        private String url;
        private String preUrl;
        private String testUrl;
        private String devUrl;

        VenvyUrl(String url, String preUrl, String testUrl, String devUrl) {
            this.url = url;
            this.preUrl = preUrl;
            this.testUrl = testUrl;
            this.devUrl = devUrl;
        }

        public String getCurrentUrl() {
            if (DebugStatus.isDev()) {
                return devUrl;
            } else if (DebugStatus.isPreView()) {
                return preUrl;
            } else if(DebugStatus.isTest()){
                return testUrl;
            }else {
                return url;
            }
        }

        public String getOnlineUrl() {
            return url;
        }
    }

    public static HashMap<String, VenvyUrl> urlMap = new HashMap<>();

    static {
        urlMap.put(VenvyUrl.ADS_API.getOnlineUrl(), VenvyUrl.ADS_API);
        urlMap.put(VenvyUrl.VA.getOnlineUrl(), VenvyUrl.VA);
        urlMap.put(VenvyUrl.CYTRON.getOnlineUrl(), VenvyUrl.CYTRON);
        urlMap.put(VenvyUrl.LIVE.getOnlineUrl(), VenvyUrl.LIVE);
        urlMap.put(VenvyUrl.LOG_REPORT.getOnlineUrl(), VenvyUrl.LOG_REPORT);
        urlMap.put(VenvyUrl.PLAT_LIVE.getOnlineUrl(), VenvyUrl.PLAT_LIVE);
        urlMap.put(VenvyUrl.OS.getOnlineUrl(), VenvyUrl.OS);
        urlMap.put(VenvyUrl.OS_SAAS.getOnlineUrl(), VenvyUrl.OS_SAAS);
    }
}
