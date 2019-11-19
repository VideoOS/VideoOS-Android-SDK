package both.video.venvy.com.appdemo.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.bean.DevAppDebugInfo;
import both.video.venvy.com.appdemo.http.DevelopDebugConfigInfoModel;
import both.video.venvy.com.appdemo.utils.ConfigUtil;
import both.video.venvy.com.appdemo.utils.FileUtil;
import both.video.venvy.com.appdemo.widget.DebugDialog;
import both.video.venvy.com.appdemo.widget.InteractDebugDialog;
import cn.com.venvy.common.download.DownloadTask;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyFileUtil;

import static cn.com.venvy.PreloadLuaUpdate.LUA_CACHE_PATH;

public class InteractActivity extends AppCompatActivity implements View.OnClickListener{

    ProgressBar loadingView;

    public static void newIntent(Context context){
        context.startActivity(new Intent(context,InteractActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interact);
        initView();
    }

    private void initView() {
        this.findViewById(R.id.interact_back).setOnClickListener(this);
        this.findViewById(R.id.interact_debug).setOnClickListener(this);
        loadingView = this.findViewById(R.id.interact_loading);
        hideLoadingView();
    }

    private void showLoadingView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideLoadingView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingView.setVisibility(View.GONE);
            }
        });
    }

    private void showErrorToast() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(InteractActivity.this,"出错了，请检查配置信息是否正确。",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.interact_back:
                finish();
                break;
            case R.id.interact_debug:
                interactDebug();
                break;
        }
    }

    private void interactDebug() {
        showInteractDialog();
    }

    private void showInteractDialog() {
        InteractDebugDialog interactDialog = new InteractDebugDialog(this);
        interactDialog.setInteractDialogCallback(new DebugDialog.InteractDialogCallback() {
            @Override
            public void onOK(Dialog dialog, Bundle bundleData) {
                dialog.dismiss();
                int mode = bundleData.getInt("mode");
                if(DebugDialog.LOCAL_MODE == mode){
                    String luaPath = bundleData.getString("luaPath");
                    String jsonPath = bundleData.getString("jsonPath");
                    String videoPath = bundleData.getString("videoPath");

                    ConfigUtil.putVideoName(videoPath);
                    luaFileCopy();

                    Bundle playerBundler = new Bundle();
                    playerBundler.putInt("program_type",VideoPlayActivity.TYPE_PROGRAM_A_LOCAL);
                    playerBundler.putString("luaPath",luaPath);
                    playerBundler.putString("jsonPath",jsonPath);
                    VideoPlayActivity.newIntent(InteractActivity.this, playerBundler);

                }else if(DebugDialog.ONLINE_MODE == mode){
                    showLoadingView();
                    String commitID = bundleData.getString("commitID");
                    String jsonUrl = bundleData.getString("jsonUrl");
                    String videoUrl = bundleData.getString("videoUrl");
                    ConfigUtil.putVideoName(videoUrl);
                    interactOnLineOperate(commitID,jsonUrl,videoUrl);
                }
            }

            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
            }
        });
        interactDialog.show();
    }

    private void interactOnLineOperate(final String commitID, final String jsonUrl, final String videoUrl) {
        //获取下载的lua地址
        DevelopDebugConfigInfoModel developDebugConfigInfoModel = new DevelopDebugConfigInfoModel(1, commitID, new DevelopDebugConfigInfoModel.AppDebugInfoCallback() {
            @Override
            public void updateComplete(String result) {
                //TODO... 下载 Lua
                try {
                    String decryptData = VenvyAesUtil.decrypt(new JSONObject(result).optString("encryptData"), ConfigUtil.getAppSecret(), ConfigUtil.getAppSecret());
                    DevAppDebugInfo devAppDebugInfo = JSON.parseObject(decryptData, DevAppDebugInfo.class);
                    if(devAppDebugInfo != null && devAppDebugInfo.getLuaList() != null && devAppDebugInfo.getLuaList().size() > 0){
                        startDownloadLuaFile(jsonUrl, devAppDebugInfo);
                    }else{
                        throw new IllegalArgumentException();
                    }
                } catch (Exception e) {
                    hideLoadingView();
                    showErrorToast();
                }
            }

            @Override
            public void updateError(Throwable t) {
                hideLoadingView();
                showErrorToast();
            }
        });
        developDebugConfigInfoModel.startRequest();
    }

    private void luaFileCopy() {
        VenvyFileUtil.copyFilesFromAssets(InteractActivity.this, "alocal/lua", VenvyFileUtil.getCachePath(InteractActivity.this) + LUA_CACHE_PATH);
    }

    //下载Lua/Json 文件
    private void startDownloadLuaFile(final String jsonUrl, final DevAppDebugInfo devAppDebugInfo) {
        List<String> urlArray = new ArrayList<>();
        for(DevAppDebugInfo.LuaListBean bean : devAppDebugInfo.getLuaList()){
            String url = bean.getUrl();
            if(!TextUtils.isEmpty(url)){
                urlArray.add(url);
            }
        }

//        TODO 添加json 下载路径
        urlArray.add(jsonUrl);
        FileUtil.startDownloadLuaFile(InteractActivity.this, urlArray, new FileUtil.DownloadFileCallback() {
            @Override
            public void onFailed(String errorMessage) {
                hideLoadingView();
                showErrorToast();
            }

            @Override
            public void onSuccess(List<DownloadTask> successfulTasks) {
                hideLoadingView();
                Bundle playerBundler = new Bundle();
                playerBundler.putInt("program_type",VideoPlayActivity.TYPE_PROGRAM_A_ONLINE);
                playerBundler.putString("luaPath", devAppDebugInfo.getTemplate());
                playerBundler.putString("jsonPath", jsonUrl.substring(jsonUrl.lastIndexOf("/") + 1));
                VideoPlayActivity.newIntent(InteractActivity.this, playerBundler);
            }
        });
    }

}
