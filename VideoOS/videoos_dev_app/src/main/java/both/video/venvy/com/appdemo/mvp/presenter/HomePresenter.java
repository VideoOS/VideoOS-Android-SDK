package both.video.venvy.com.appdemo.mvp.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;

import both.video.venvy.com.appdemo.activity.ConfigActivity;
import both.video.venvy.com.appdemo.activity.InteractActivity;
import both.video.venvy.com.appdemo.activity.ServiceActivity;
import both.video.venvy.com.appdemo.mvp.MvpPresenter;
import both.video.venvy.com.appdemo.mvp.presenter.base.IHomePresenter;
import both.video.venvy.com.appdemo.mvp.view.IHomeView;
import both.video.venvy.com.appdemo.utils.ConfigUtil;

/**
 * Created by videopls on 2019/12/27.
 */

public class HomePresenter extends MvpPresenter<IHomeView> implements IHomePresenter {
    private Activity activity;
    public HomePresenter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onHomeConfig() {
        ConfigActivity.newIntent(activity);
    }

    @Override
    public void onInteractProgram() {
        if(isCachedAppKeySecret()){
            InteractActivity.newIntent(activity);
        }else{
            Toast.makeText(activity,"请先填写后台的应用信息再调试",Toast.LENGTH_SHORT).show();
            ConfigActivity.newIntent(activity);
        }
    }

    @Override
    public void onServiceProgram() {
        if (isCachedAppKeySecret()) {
            ServiceActivity.newIntent(activity);
        }else{
            Toast.makeText(activity,"请先填写后台的应用信息再调试",Toast.LENGTH_SHORT).show();
            ConfigActivity.newIntent(activity);
        }
    }

    private boolean isCachedAppKeySecret() {
        if(!TextUtils.isEmpty(ConfigUtil.getAppKey()) && !TextUtils.isEmpty(ConfigUtil.getAppSecret())){
            return true;
        }
        return false;
    }
}
