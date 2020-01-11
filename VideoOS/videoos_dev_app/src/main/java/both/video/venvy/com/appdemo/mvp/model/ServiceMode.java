package both.video.venvy.com.appdemo.mvp.model;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import both.video.venvy.com.appdemo.activity.VideoPlayActivity;
import both.video.venvy.com.appdemo.bean.DevAppDebugInfo;
import both.video.venvy.com.appdemo.bean.JsonConfigBean;
import both.video.venvy.com.appdemo.http.DevelopDebugConfigInfoModel;
import both.video.venvy.com.appdemo.mvp.presenter.base.IServicePresenter;
import both.video.venvy.com.appdemo.utils.ConfigUtil;
import both.video.venvy.com.appdemo.utils.FileUtil;
import cn.com.venvy.common.download.DownloadTask;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyFileUtil;

import static cn.com.venvy.PreloadLuaUpdate.LUA_CACHE_PATH;

/**
 * Created by videopls on 2019/12/27.
 */

public class ServiceMode {
    private Context context;

    public ServiceMode(Context context) {
        this.context = context;
    }

    public void serviceOnLineOperate(final String commitID, final String videoUrl, final IServicePresenter iServicePresenter){
        //获取下载的lua地址
        DevelopDebugConfigInfoModel developDebugConfigInfoModel = new DevelopDebugConfigInfoModel(2, commitID, new DevelopDebugConfigInfoModel.AppDebugInfoCallback() {
            @Override
            public void updateComplete(String result) {
                //TODO... 下载 Lua
                try {
                    String decryptData = VenvyAesUtil.decrypt(new JSONObject(result).optString("encryptData"), ConfigUtil.getAppSecret(), ConfigUtil.getAppSecret());
                    DevAppDebugInfo devAppDebugInfo = JSON.parseObject(decryptData, DevAppDebugInfo.class);
                    if(devAppDebugInfo != null && devAppDebugInfo.getLuaList() != null && devAppDebugInfo.getLuaList().size() > 0){
                        startDownloadLuaFile(devAppDebugInfo, iServicePresenter);
                    }else{
                        throw new IllegalArgumentException();
                    }
                } catch (Exception e) {
                    if(iServicePresenter != null){
                        iServicePresenter.onFailed();
                    }
                }
            }

            @Override
            public void updateError(Throwable t) {
                if(iServicePresenter != null){
                    iServicePresenter.onFailed();
                }
            }
        });
        developDebugConfigInfoModel.startRequest();
    }

    //下载Lua/Json 文件
    private void startDownloadLuaFile(final DevAppDebugInfo devAppDebugInfo, final IServicePresenter iServicePresenter) {
        List<String> urlArray = new ArrayList<>();
        for(DevAppDebugInfo.LuaListBean bean : devAppDebugInfo.getLuaList()){
            String url = bean.getUrl();
            if(!TextUtils.isEmpty(url)){
                urlArray.add(url);
            }
        }

        FileUtil.startDownloadLuaFile(context, devAppDebugInfo.getMiniAppId(), urlArray, new FileUtil.DownloadFileCallback() {
            @Override
            public void onFailed(String errorMessage) {
                if(iServicePresenter != null){
                    iServicePresenter.onFailed();
                }
            }

            @Override
            public void onSuccess(List<DownloadTask> successfulTasks) {
                if(iServicePresenter != null){
                    iServicePresenter.onSuccess();
                }
                //TODO 构建json
                //1.通过 DevAppDebugInfo 构建 JsonConfigBean
                JsonConfigBean jsonConfigBean = new JsonConfigBean();

                JsonConfigBean.DisplayBean displayBean = new JsonConfigBean.DisplayBean();
                if (devAppDebugInfo.getDisplay() != null) {
                    displayBean.setNavTitle(devAppDebugInfo.getDisplay().getNavTitle());
                }
                jsonConfigBean.setDisplay(displayBean);

                JsonConfigBean.MiniAppInfoBean miniAppInfoBean = new JsonConfigBean.MiniAppInfoBean();
                miniAppInfoBean.setMiniAppId(devAppDebugInfo.getMiniAppId());

                List<JsonConfigBean.MiniAppInfoBean.LuaListBean> luaList = new ArrayList<>();

                for(DownloadTask task : successfulTasks){
                    String downloadUrl = task.getDownloadUrl();
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
                    JsonConfigBean.MiniAppInfoBean.LuaListBean luaListBean = new JsonConfigBean.MiniAppInfoBean.LuaListBean();
                    luaListBean.setUrl(fileName);
                    luaList.add(luaListBean);
                }

                miniAppInfoBean.setLuaList(luaList);

                miniAppInfoBean.setTemplate(devAppDebugInfo.getTemplate());
                jsonConfigBean.setMiniAppInfo(miniAppInfoBean);


                //2.把 JsonConfigBean 转化成 json 文本
                String devConfigJson = JSON.toJSONString(jsonConfigBean);

                //3.把json 文本写入到 dev_config.json
                String devConfigJsonPath = VenvyFileUtil.getCachePath(context) + LUA_CACHE_PATH + File.separator + "dev_config.json";

                FileUtil.writeToFile(context, devConfigJsonPath, devConfigJson);
//
                Bundle playerBundler = new Bundle();
                playerBundler.putInt("program_type", VideoPlayActivity.TYPE_PROGRAM_B);
                playerBundler.putString("miniAppId", devAppDebugInfo.getMiniAppId());
                VideoPlayActivity.newIntent(context, playerBundler);
            }
        });
    }

    public void destroy(){
        context = null;
    }
}
