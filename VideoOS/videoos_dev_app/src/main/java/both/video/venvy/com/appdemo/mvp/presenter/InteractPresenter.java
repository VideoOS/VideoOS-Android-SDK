package both.video.venvy.com.appdemo.mvp.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import both.video.venvy.com.appdemo.activity.InteractActivity;
import both.video.venvy.com.appdemo.activity.VideoPlayActivity;
import both.video.venvy.com.appdemo.bean.DevAppDebugInfo;
import both.video.venvy.com.appdemo.http.DevelopDebugConfigInfoModel;
import both.video.venvy.com.appdemo.mvp.MvpPresenter;
import both.video.venvy.com.appdemo.mvp.model.InteractMode;
import both.video.venvy.com.appdemo.mvp.presenter.base.IInteractPresenter;
import both.video.venvy.com.appdemo.mvp.view.IInteractView;
import both.video.venvy.com.appdemo.utils.ConfigUtil;
import both.video.venvy.com.appdemo.utils.FileUtil;
import both.video.venvy.com.appdemo.widget.DebugDialog;
import cn.com.venvy.common.download.DownloadTask;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyFileUtil;

import static cn.com.venvy.PreloadLuaUpdate.LUA_CACHE_PATH;

/**
 * Created by videopls on 2019/12/27.
 */

public class InteractPresenter extends MvpPresenter<IInteractView> implements IInteractPresenter {

    private Activity activity;
    private InteractMode interactMode;

    public InteractPresenter(Activity activity) {
        this.activity = activity;
        interactMode = new InteractMode(activity);
    }

    @Override
    public void onDealInteract(Bundle bundleData) {
        int mode = bundleData.getInt("mode");
        if(DebugDialog.LOCAL_MODE == mode){
            String luaPath = bundleData.getString("luaPath");
            String jsonPath = bundleData.getString("jsonPath");
            String videoPath = bundleData.getString("videoPath");

            ConfigUtil.putVideoName(videoPath);
            luaFileCopy();

            Bundle playerBundler = new Bundle();
            playerBundler.putInt("program_type", VideoPlayActivity.TYPE_PROGRAM_A_LOCAL);
            playerBundler.putString("luaPath",luaPath);
            playerBundler.putString("jsonPath",jsonPath);
            VideoPlayActivity.newIntent(activity, playerBundler);

        }else if(DebugDialog.ONLINE_MODE == mode){
            getView().showLoadingView();
            String commitID = bundleData.getString("commitID");
            String jsonUrl = bundleData.getString("jsonUrl");
            String videoUrl = bundleData.getString("videoUrl");
            ConfigUtil.putVideoName(videoUrl);
            interactOnLineOperate(commitID,jsonUrl,videoUrl);
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

    private void interactOnLineOperate(final String commitID, final String jsonUrl, final String videoUrl) {
        interactMode.interactOnLineOperate(commitID, jsonUrl, videoUrl, this);
    }

    private void luaFileCopy() {
        VenvyFileUtil.copyFilesFromAssets(activity, "alocal/lua", VenvyFileUtil.getCachePath(activity) + LUA_CACHE_PATH);
    }

    @Override
    protected void destroy() {
        interactMode.destroy();
    }
}
