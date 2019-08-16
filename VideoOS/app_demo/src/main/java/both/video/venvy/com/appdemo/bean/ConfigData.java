package both.video.venvy.com.appdemo.bean;

import java.util.List;

public class ConfigData {

    /**
     * data : {"apps":[{"appKey":"550ec7d2-6cb0-4f46-b2df-2a1505ec82d8","appSecret":"d0bf7873f7fa42a6"}]}
     */

    private ConfigInfo data;

    public ConfigInfo getData() {
        return data;
    }

    public void setData(ConfigInfo data) {
        this.data = data;
    }

    public static class ConfigInfo {
        private List<AppInfo> apps;

        public List<AppInfo> getApps() {
            return apps;
        }

        public void setApps(List<AppInfo> apps) {
            this.apps = apps;
        }

        public static class AppInfo {
            /**
             * appKey : 550ec7d2-6cb0-4f46-b2df-2a1505ec82d8
             * appSecret : d0bf7873f7fa42a6
             */

            private String appKey;
            private String appSecret;

            public String getAppKey() {
                return appKey;
            }

            public void setAppKey(String appKey) {
                this.appKey = appKey;
            }

            public String getAppSecret() {
                return appSecret;
            }

            public void setAppSecret(String appSecret) {
                this.appSecret = appSecret;
            }
        }
    }
}
