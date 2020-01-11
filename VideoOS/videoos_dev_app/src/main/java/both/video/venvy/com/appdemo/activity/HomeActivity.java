package both.video.venvy.com.appdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.mvp.MvpActivity;
import both.video.venvy.com.appdemo.mvp.presenter.HomePresenter;
import both.video.venvy.com.appdemo.mvp.view.IHomeView;
import both.video.venvy.com.appdemo.utils.ConfigUtil;

public class HomeActivity extends MvpActivity<IHomeView,HomePresenter> implements View.OnClickListener,IHomeView{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
    }

    @Override
    public HomePresenter createPresenter() {
        return new HomePresenter(this);
    }

    @Override
    public IHomeView createView() {
        return this;
    }

    private void initView() {
        this.findViewById(R.id.home_config).setOnClickListener(this);
        this.findViewById(R.id.home_interact_program).setOnClickListener(this);
        this.findViewById(R.id.home_service_program).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_config:
                getPresenter().onHomeConfig();
                break;
            case R.id.home_interact_program:
                getPresenter().onInteractProgram();
                break;
            case R.id.home_service_program:
                getPresenter().onServiceProgram();
                break;
        }
    }
}
