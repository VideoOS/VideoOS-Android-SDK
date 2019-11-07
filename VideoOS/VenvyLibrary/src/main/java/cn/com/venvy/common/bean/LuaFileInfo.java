package cn.com.venvy.common.bean;

import java.util.List;

/**
 * Created by videopls on 2019/11/7.
 */

public class LuaFileInfo{

    private String miniAppId;
    private List<LuaListBean> luaList;

    public String getMiniAppId() {
        return miniAppId;
    }

    public void setMiniAppId(String miniAppId) {
        this.miniAppId = miniAppId;
    }

    public List<LuaListBean> getLuaList() {
        return luaList;
    }

    public void setLuaList(List<LuaListBean> luaList) {
        this.luaList = luaList;
    }

    public class LuaListBean {

        private String luaFileName;
        private String luaFileMd5;
        private String luaFileUrl;
        private String luaFilePath;

        public String getLuaFileName() {
            return luaFileName;
        }

        public void setLuaFileName(String luaFileName) {
            this.luaFileName = luaFileName;
        }

        public String getLuaFileMd5() {
            return luaFileMd5;
        }

        public void setLuaFileMd5(String luaFileMd5) {
            this.luaFileMd5 = luaFileMd5;
        }

        public String getLuaFileUrl() {
            return luaFileUrl;
        }

        public void setLuaFileUrl(String luaFileUrl) {
            this.luaFileUrl = luaFileUrl;
        }

        public String getLuaFilePath() {
            return luaFilePath;
        }

        public void setLuaFilePath(String luaFilePath) {
            this.luaFilePath = luaFilePath;
        }
    }
}
