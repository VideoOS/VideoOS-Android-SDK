package both.video.venvy.com.appdemo.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.mvp.MvpActivity;
import both.video.venvy.com.appdemo.mvp.presenter.InteractPresenter;
import both.video.venvy.com.appdemo.mvp.view.IInteractView;
import both.video.venvy.com.appdemo.widget.DebugDialog;
import both.video.venvy.com.appdemo.widget.InteractDebugDialog;

public class InteractActivity extends MvpActivity<IInteractView,InteractPresenter> implements View.OnClickListener,IInteractView{

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

    @Override
    public InteractPresenter createPresenter() {
        return new InteractPresenter(this);
    }

    @Override
    public IInteractView createView() {
        return this;
    }

    private void initView() {
        this.findViewById(R.id.interact_back).setOnClickListener(this);
        this.findViewById(R.id.interact_debug).setOnClickListener(this);
        loadingView = this.findViewById(R.id.interact_loading);
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
                getPresenter().onDealInteract(bundleData);
            }

            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
            }
        });
        interactDialog.show();
    }
}
