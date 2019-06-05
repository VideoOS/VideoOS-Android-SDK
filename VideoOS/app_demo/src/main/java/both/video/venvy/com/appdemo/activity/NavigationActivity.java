package both.video.venvy.com.appdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import both.video.venvy.com.appdemo.R;

/**
 * Created by videojj_pls on 2018/9/13.
 */

public class NavigationActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_home_item_1).setOnClickListener(this);
        findViewById(R.id.tv_home_item_2).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_home_item_2) {
            startOsActivity();
        } else if (id == R.id.tv_home_item_1) {
            startLiveOsActivity();
        }
    }

    /***
     * 启动OS
     */
    private void startOsActivity() {
        Intent i = new Intent(NavigationActivity.this, OsActivity.class);
        startActivity(i);
    }

    private void startLiveOsActivity() {
        Intent i = new Intent(NavigationActivity.this, LiveActivity.class);
        startActivity(i);
    }
}
