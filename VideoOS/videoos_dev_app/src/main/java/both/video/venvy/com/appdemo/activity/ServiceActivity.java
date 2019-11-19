package both.video.venvy.com.appdemo.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.bean.DevAppDebugInfo;
import both.video.venvy.com.appdemo.bean.JsonConfigBean;
import both.video.venvy.com.appdemo.http.DevelopDebugConfigInfoModel;
import both.video.venvy.com.appdemo.utils.ConfigUtil;
import both.video.venvy.com.appdemo.utils.FileUtil;
import both.video.venvy.com.appdemo.widget.DebugDialog;
import both.video.venvy.com.appdemo.widget.ServiceDebugDialog;
import cn.com.venvy.App;
import cn.com.venvy.PreloadLuaUpdate;
import cn.com.venvy.common.download.DownloadTask;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyFileUtil;

import static cn.com.venvy.PreloadLuaUpdate.LUA_CACHE_PATH;

public class ServiceActivity extends AppCompatActivity implements View.OnClickListener{

    ProgressBar loadingView;

    public static void newIntent(Context context){
        context.startActivity(new Intent(context,ServiceActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        initView();
    }

    private void initView() {
        this.findViewById(R.id.service_back).setOnClickListener(this);
        this.findViewById(R.id.service_debug).setOnClickListener(this);
        loadingView = this.findViewById(R.id.service_loading);
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
                Toast.makeText(ServiceActivity.this,"出错了，请检查配置信息是否正确。",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.service_back:
                finish();
                break;
            case R.id.service_debug:
                showServiceDialog();
                break;
        }
    }

    private void showServiceDialog() {
        ServiceDebugDialog serviceDialog = new ServiceDebugDialog(this);
        serviceDialog.setInteractDialogCallback(new DebugDialog.InteractDialogCallback() {
            @Override
            public void onOK(Dialog dialog, Bundle bundleData) {
                dialog.dismiss();
                int mode = bundleData.getInt("mode");
                if(DebugDialog.LOCAL_MODE == mode){
                    String jsonPath = bundleData.getString("jsonPath");
                    String videoPath = bundleData.getString("videoPath");

                    ConfigUtil.putVideoName(videoPath);

                    VenvyFileUtil.copyFilesFromAssets(ServiceActivity.this, "blocal", VenvyFileUtil.getCachePath(App.getContext()) + PreloadLuaUpdate.LUA_CACHE_PATH);


                    //获取dev_config.json中的miniAppId
                    String obtainJsonText = obtainJsonText();
                    if(!TextUtils.isEmpty(obtainJsonText)){
                        String miniAppId = null;
                        JsonConfigBean jsonConfigBean = JSON.parseObject(obtainJsonText, JsonConfigBean.class);
                        if(jsonConfigBean != null){
                            miniAppId = jsonConfigBean.getMiniAppId();
                        }

                        if(!TextUtils.isEmpty(miniAppId)){
                            Bundle playerBundler = new Bundle();
                            playerBundler.putInt("program_type",VideoPlayActivity.TYPE_PROGRAM_B);
                            playerBundler.putString("miniAppId", miniAppId);
                            VideoPlayActivity.newIntent(ServiceActivity.this, playerBundler);
                        }
                    }
                }else if(DebugDialog.ONLINE_MODE == mode){
                    showLoadingView();
                    String commitID = bundleData.getString("commitID");
                    String videoUrl = bundleData.getString("videoUrl");
                    ConfigUtil.putVideoName(videoUrl);
                    serviceOnLineOperate(commitID, videoUrl);
                }
            }

            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
            }
        });
        serviceDialog.show();
    }

    private String obtainJsonText() {
        String text = null;
        File jsonTextFile = new File(VenvyFileUtil.getCachePath(App.getContext()) + PreloadLuaUpdate.LUA_CACHE_PATH + File.separator + "dev_config.json");
        if(jsonTextFile != null && jsonTextFile.exists()){
            text = VenvyFileUtil.readFormFile(ServiceActivity.this, jsonTextFile.getAbsolutePath());
        }
        return text;
    }

    private void serviceOnLineOperate(String commitID, String videoUrl) {
        //获取下载的lua地址
        DevelopDebugConfigInfoModel developDebugConfigInfoModel = new DevelopDebugConfigInfoModel(2, commitID, new DevelopDebugConfigInfoModel.AppDebugInfoCallback() {
            @Override
            public void updateComplete(String result) {
                //TODO... 下载 Lua
                try {
                    String decryptData = VenvyAesUtil.decrypt(new JSONObject(result).optString("encryptData"), ConfigUtil.getAppSecret(), ConfigUtil.getAppSecret());
                    DevAppDebugInfo devAppDebugInfo = JSON.parseObject(decryptData, DevAppDebugInfo.class);
                    if(devAppDebugInfo != null && devAppDebugInfo.getLuaList() != null && devAppDebugInfo.getLuaList().size() > 0){
                        startDownloadLuaFile(devAppDebugInfo);
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

    //下载Lua/Json 文件
    private void startDownloadLuaFile(final DevAppDebugInfo devAppDebugInfo) {
        List<String> urlArray = new ArrayList<>();
        for(DevAppDebugInfo.LuaListBean bean : devAppDebugInfo.getLuaList()){
            String url = bean.getUrl();
            if(!TextUtils.isEmpty(url)){
                urlArray.add(url);
            }
        }

        FileUtil.startDownloadLuaFile(ServiceActivity.this, urlArray, new FileUtil.DownloadFileCallback() {
            @Override
            public void onFailed(String errorMessage) {
                hideLoadingView();
                showErrorToast();
            }

            @Override
            public void onSuccess(List<DownloadTask> successfulTasks) {
                hideLoadingView();
                //TODO 构建json
                //1.通过 DevAppDebugInfo 构建 JsonConfigBean
                JsonConfigBean jsonConfigBean = new JsonConfigBean();

                jsonConfigBean.setMiniAppId(devAppDebugInfo.getMiniAppId());

                JsonConfigBean.DisplayBean displayBean = new JsonConfigBean.DisplayBean();
                displayBean.setNavTitle(devAppDebugInfo.getDisplay().getNavTitle());
                jsonConfigBean.setDisplay(displayBean);

                List<JsonConfigBean.LuaListBean> luaList = new ArrayList<JsonConfigBean.LuaListBean>();

                for(DownloadTask task : successfulTasks){
                    String downloadUrl = task.getDownloadUrl();
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
                    JsonConfigBean.LuaListBean luaListBean = new JsonConfigBean.LuaListBean();
                    luaListBean.setUrl(fileName);
                    luaList.add(luaListBean);
                }

                jsonConfigBean.setLuaList(luaList);

                jsonConfigBean.setTemplate(devAppDebugInfo.getTemplate());

                //2.把 JsonConfigBean 转化成 json 文本
                String devConfigJson = JSON.toJSONString(jsonConfigBean);

                //3.把json 文本写入到 dev_config.json
                String devConfigJsonPath = VenvyFileUtil.getCachePath(ServiceActivity.this) + LUA_CACHE_PATH + File.separator + "dev_config.json";

                FileUtil.writeToFile(ServiceActivity.this, devConfigJsonPath, devConfigJson);
//
                Bundle playerBundler = new Bundle();
                playerBundler.putInt("program_type", VideoPlayActivity.TYPE_PROGRAM_B);
                playerBundler.putString("miniAppId", devAppDebugInfo.getMiniAppId());
                VideoPlayActivity.newIntent(ServiceActivity.this, playerBundler);
            }
        });
    }
}
