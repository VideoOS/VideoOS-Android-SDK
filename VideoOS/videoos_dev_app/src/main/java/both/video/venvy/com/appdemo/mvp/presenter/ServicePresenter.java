package both.video.venvy.com.appdemo.mvp.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.io.File;

import both.video.venvy.com.appdemo.activity.VideoPlayActivity;
import both.video.venvy.com.appdemo.bean.JsonConfigBean;
import both.video.venvy.com.appdemo.mvp.MvpPresenter;
import both.video.venvy.com.appdemo.mvp.model.ServiceMode;
import both.video.venvy.com.appdemo.mvp.presenter.base.IServicePresenter;
import both.video.venvy.com.appdemo.mvp.view.IServiceView;
import both.video.venvy.com.appdemo.utils.ConfigUtil;
import both.video.venvy.com.appdemo.utils.FileUtil;
import both.video.venvy.com.appdemo.widget.DebugDialog;
import cn.com.venvy.App;
import cn.com.venvy.PreloadLuaUpdate;
import cn.com.venvy.common.utils.VenvyFileUtil;

/**
 * Created by videopls on 2019/12/27.
 */

public class ServicePresenter extends MvpPresenter<IServiceView> implements IServicePresenter {

    private Activity activity;
    private ServiceMode serviceMode;

    public ServicePresenter(Activity activity) {
        this.activity = activity;
        serviceMode = new ServiceMode(activity);
    }

    @Override
    public void onDealInteract(Bundle bundleData) {
        try {
            int mode = bundleData.getInt("mode");
            if(DebugDialog.LOCAL_MODE == mode){
                String jsonPath = bundleData.getString("jsonPath");
                String videoPath = bundleData.getString("videoPath");

                ConfigUtil.putVideoName(videoPath);

                FileUtil.copyFileFromAssetsFile(activity, "blocal/dev_config.json", VenvyFileUtil.getCachePath(App.getContext()) + PreloadLuaUpdate.LUA_CACHE_PATH + File.separator + "dev_config.json");

                //获取dev_config.json中的miniAppId
                String obtainJsonText = obtainJsonText();
                if(!TextUtils.isEmpty(obtainJsonText)){
                    String miniAppId = null;
                    JsonConfigBean jsonConfigBean = JSON.parseObject(obtainJsonText, JsonConfigBean.class);
                    if(jsonConfigBean != null && jsonConfigBean.getMiniAppInfo() != null){
                        miniAppId = jsonConfigBean.getMiniAppInfo().getMiniAppId();
                    }

                    if(!TextUtils.isEmpty(miniAppId)){
                        VenvyFileUtil.copyFilesFromAssets(activity, "blocal", VenvyFileUtil.getCachePath(App.getContext()) + PreloadLuaUpdate.LUA_CACHE_PATH + File.separator + miniAppId);
                        Bundle playerBundler = new Bundle();
                        playerBundler.putInt("program_type", VideoPlayActivity.TYPE_PROGRAM_B);
                        playerBundler.putString("miniAppId", miniAppId);
                        VideoPlayActivity.newIntent(activity, playerBundler);
                    }
                }
            }else if(DebugDialog.ONLINE_MODE == mode){
                getView().showLoadingView();
                String commitID = bundleData.getString("commitID");
                String videoUrl = bundleData.getString("videoUrl");
                ConfigUtil.putVideoName(videoUrl);
                serviceOnLineOperate(commitID, videoUrl);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onFailed() {
        getView().hideLoadingView();
        getView().showErrorToast();
    }

    @Override
    public void onSuccess() {
        getView().hideLoadingView();
    }

    private String obtainJsonText() {
        String text = null;
        File jsonTextFile = new File(VenvyFileUtil.getCachePath(App.getContext()) + PreloadLuaUpdate.LUA_CACHE_PATH + File.separator + "dev_config.json");
        if(jsonTextFile != null && jsonTextFile.exists()){
            text = VenvyFileUtil.readFormFile(activity, jsonTextFile.getAbsolutePath());
        }
        return text;
    }

    private void serviceOnLineOperate(String commitID, String videoUrl) {
        serviceMode.serviceOnLineOperate(commitID, videoUrl, this);
    }

    @Override
    protected void destroy() {
        serviceMode.destroy();
    }
}
