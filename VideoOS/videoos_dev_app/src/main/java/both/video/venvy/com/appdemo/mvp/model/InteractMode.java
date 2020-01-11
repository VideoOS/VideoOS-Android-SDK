package both.video.venvy.com.appdemo.mvp.model;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import both.video.venvy.com.appdemo.activity.VideoPlayActivity;
import both.video.venvy.com.appdemo.bean.DevAppDebugInfo;
import both.video.venvy.com.appdemo.http.DevelopDebugConfigInfoModel;
import both.video.venvy.com.appdemo.mvp.presenter.base.IInteractPresenter;
import both.video.venvy.com.appdemo.utils.ConfigUtil;
import both.video.venvy.com.appdemo.utils.FileUtil;
import cn.com.venvy.common.download.DownloadTask;
import cn.com.venvy.common.utils.VenvyAesUtil;

/**
 * Created by videopls on 2019/12/27.
 */

public class InteractMode {
    private Context context;
    public InteractMode(Context context) {
        this.context = context;
    }

    public void interactOnLineOperate(final String commitID, final String jsonUrl, final String videoUrl, final IInteractPresenter iInteractPresenter){
        //获取下载的lua地址
        DevelopDebugConfigInfoModel developDebugConfigInfoModel = new DevelopDebugConfigInfoModel(1, commitID, new DevelopDebugConfigInfoModel.AppDebugInfoCallback() {
            @Override
            public void updateComplete(String result) {
                //TODO... 下载 Lua
                try {
                    String decryptData = VenvyAesUtil.decrypt(new JSONObject(result).optString("encryptData"), ConfigUtil.getAppSecret(), ConfigUtil.getAppSecret());
                    DevAppDebugInfo devAppDebugInfo = JSON.parseObject(decryptData, DevAppDebugInfo.class);
                    if(devAppDebugInfo != null && devAppDebugInfo.getLuaList() != null && devAppDebugInfo.getLuaList().size() > 0){
                        startDownloadLuaFile(jsonUrl, devAppDebugInfo, iInteractPresenter);
                    }else{
                        throw new IllegalArgumentException();
                    }
                } catch (Exception e) {
                    if(iInteractPresenter != null){
                        iInteractPresenter.onFailed();
                    }
                }
            }

            @Override
            public void updateError(Throwable t) {
                if(iInteractPresenter != null){
                    iInteractPresenter.onFailed();
                }
            }
        });
        developDebugConfigInfoModel.startRequest();
    }

    //下载Lua/Json 文件
    public void startDownloadLuaFile(final String jsonUrl, final DevAppDebugInfo devAppDebugInfo, final IInteractPresenter iInteractPresenter) {
        List<String> urlArray = new ArrayList<>();
        for(DevAppDebugInfo.LuaListBean bean : devAppDebugInfo.getLuaList()){
            String url = bean.getUrl();
            if(!TextUtils.isEmpty(url)){
                urlArray.add(url);
            }
        }

//        TODO 添加json 下载路径
        urlArray.add(jsonUrl);
        FileUtil.startDownloadLuaFile(context, devAppDebugInfo.getMiniAppId(), urlArray, new FileUtil.DownloadFileCallback() {
            @Override
            public void onFailed(String errorMessage) {
                if(iInteractPresenter != null){
                    iInteractPresenter.onFailed();
                }
            }

            @Override
            public void onSuccess(List<DownloadTask> successfulTasks) {
                if(iInteractPresenter != null){
                    iInteractPresenter.onSuccess();
                }
                Bundle playerBundler = new Bundle();
                playerBundler.putInt("program_type", VideoPlayActivity.TYPE_PROGRAM_A_ONLINE);
                playerBundler.putString("miniAppId", devAppDebugInfo.getMiniAppId());
                playerBundler.putString("luaPath", devAppDebugInfo.getTemplate());
                playerBundler.putString("jsonPath", jsonUrl.substring(jsonUrl.lastIndexOf("/") + 1));
                VideoPlayActivity.newIntent(context, playerBundler);
            }
        });
    }

    public void destroy(){
        context = null;
    }
}
