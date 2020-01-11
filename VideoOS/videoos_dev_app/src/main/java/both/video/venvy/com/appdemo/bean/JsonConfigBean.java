package both.video.venvy.com.appdemo.bean;

import java.util.List;

/**
 * Created by videopls on 2019/10/15.
 */

public class JsonConfigBean {

    /**
     * display : {"navTitle":"开发模式测试"}
     * h5Url :
     * miniAppInfo : {"miniAppId":"123456","template":"os_video_test_hotspot.lua","luaList":[{"url":"os_video_test_hotspot.lua"}]}
     */

    private DisplayBean display;
    private String h5Url;
    private MiniAppInfoBean miniAppInfo;

    public DisplayBean getDisplay() {
        return display;
    }

    public void setDisplay(DisplayBean display) {
        this.display = display;
    }

    public String getH5Url() {
        return h5Url;
    }

    public void setH5Url(String h5Url) {
        this.h5Url = h5Url;
    }

    public MiniAppInfoBean getMiniAppInfo() {
        return miniAppInfo;
    }

    public void setMiniAppInfo(MiniAppInfoBean miniAppInfo) {
        this.miniAppInfo = miniAppInfo;
    }

    public static class DisplayBean {
        /**
         * navTitle : 开发模式测试
         */

        private String navTitle;

        public String getNavTitle() {
            return navTitle;
        }

        public void setNavTitle(String navTitle) {
            this.navTitle = navTitle;
        }
    }

    public static class MiniAppInfoBean {
        /**
         * miniAppId : 123456
         * template : os_video_test_hotspot.lua
         * luaList : [{"url":"os_video_test_hotspot.lua"}]
         */

        private String miniAppId;
        private String template;
        private List<LuaListBean> luaList;

        public String getMiniAppId() {
            return miniAppId;
        }

        public void setMiniAppId(String miniAppId) {
            this.miniAppId = miniAppId;
        }

        public String getTemplate() {
            return template;
        }

        public void setTemplate(String template) {
            this.template = template;
        }

        public List<LuaListBean> getLuaList() {
            return luaList;
        }

        public void setLuaList(List<LuaListBean> luaList) {
            this.luaList = luaList;
        }

        public static class LuaListBean {
            /**
             * url : os_video_test_hotspot.lua
             */

            private String url;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
