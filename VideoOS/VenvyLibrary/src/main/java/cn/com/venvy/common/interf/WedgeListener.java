package cn.com.venvy.common.interf;

/**
 *对外使用的接口
 * Created by Arthur on 2017/5/25.
 */

public abstract class WedgeListener implements IWedgeInterface {

    @Override
    public void onStart() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onEmpty() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void goBack() {

    }

    @Override
    public boolean needShowVideo() {
        return true;
    }

    public int maxCacheSize() {
        return 0;
    }


    public boolean downloadSwitch() {
        return true;
    }
}
