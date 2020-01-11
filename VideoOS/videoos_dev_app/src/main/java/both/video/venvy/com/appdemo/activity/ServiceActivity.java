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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.bean.DevAppDebugInfo;
import both.video.venvy.com.appdemo.bean.JsonConfigBean;
import both.video.venvy.com.appdemo.http.DevelopDebugConfigInfoModel;
import both.video.venvy.com.appdemo.mvp.MvpActivity;
import both.video.venvy.com.appdemo.mvp.presenter.ServicePresenter;
import both.video.venvy.com.appdemo.mvp.view.IServiceView;
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

public class ServiceActivity extends MvpActivity<IServiceView,ServicePresenter> implements View.OnClickListener,IServiceView{

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

    @Override
    public ServicePresenter createPresenter() {
        return new ServicePresenter(this);
    }

    @Override
    public IServiceView createView() {
        return this;
    }

    private void initView() {
        this.findViewById(R.id.service_back).setOnClickListener(this);
        this.findViewById(R.id.service_debug).setOnClickListener(this);
        loadingView = this.findViewById(R.id.service_loading);
        hideLoadingView();
    }

    public void showLoadingView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingView.setVisibility(View.VISIBLE);
            }
        });
    }

    public void hideLoadingView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingView.setVisibility(View.GONE);
            }
        });
    }

    public void showErrorToast() {
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
                getPresenter().onDealInteract(bundleData);
            }

            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
            }
        });
        serviceDialog.show();
    }
}
