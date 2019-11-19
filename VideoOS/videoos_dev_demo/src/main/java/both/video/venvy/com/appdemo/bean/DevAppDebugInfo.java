package both.video.venvy.com.appdemo.bean;

import java.util.List;

/**
 * Created by videopls on 2019/10/23.
 */

public class DevAppDebugInfo {

    /**
     * display : {"btnColor":"FFFFFF","miniAppName":"","navColor":"FFFFFF","navPaddingColor":"2E323A","navTitle":"","navTransparency":"1"}
     * luaList : [{"md5":"92d6d2b510333984e8f577c5a0e0ec8b","url":"http://os-saas-share.videojj.com/dev/applet/306970707076579328/1.0.0/lua/os_video_figureStarList_hotspot.lua"},{"md5":"8e16b7a91cf8dc7afcb0ad05ac71f40c","url":"http://os-saas-share.videojj.com/dev/applet/306970707076579328/1.0.0/lua/os_video_starContent_hotspot.lua"}]
     * miniAppId : 306970707076579328
     * resCode : 00
     * resMsg : 处理成功
     * template : os_video_figureStarList_hotspot.lua
     */

    private DisplayBean display;
    private String miniAppId;
    private String resCode;
    private String resMsg;
    private String template;
    private List<LuaListBean> luaList;

    public DisplayBean getDisplay() {
        return display;
    }

    public void setDisplay(DisplayBean display) {
        this.display = display;
    }

    public String getMiniAppId() {
        return miniAppId;
    }

    public void setMiniAppId(String miniAppId) {
        this.miniAppId = miniAppId;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
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

    public static class DisplayBean {
        /**
         * btnColor : FFFFFF
         * miniAppName :
         * navColor : FFFFFF
         * navPaddingColor : 2E323A
         * navTitle :
         * navTransparency : 1
         */

        private String btnColor;
        private String miniAppName;
        private String navColor;
        private String navPaddingColor;
        private String navTitle;
        private String navTransparency;

        public String getBtnColor() {
            return btnColor;
        }

        public void setBtnColor(String btnColor) {
            this.btnColor = btnColor;
        }

        public String getMiniAppName() {
            return miniAppName;
        }

        public void setMiniAppName(String miniAppName) {
            this.miniAppName = miniAppName;
        }

        public String getNavColor() {
            return navColor;
        }

        public void setNavColor(String navColor) {
            this.navColor = navColor;
        }

        public String getNavPaddingColor() {
            return navPaddingColor;
        }

        public void setNavPaddingColor(String navPaddingColor) {
            this.navPaddingColor = navPaddingColor;
        }

        public String getNavTitle() {
            return navTitle;
        }

        public void setNavTitle(String navTitle) {
            this.navTitle = navTitle;
        }

        public String getNavTransparency() {
            return navTransparency;
        }

        public void setNavTransparency(String navTransparency) {
            this.navTransparency = navTransparency;
        }
    }

    public static class LuaListBean {
        /**
         * md5 : 92d6d2b510333984e8f577c5a0e0ec8b
         * url : http://os-saas-share.videojj.com/dev/applet/306970707076579328/1.0.0/lua/os_video_figureStarList_hotspot.lua
         */

        private String md5;
        private String url;

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
