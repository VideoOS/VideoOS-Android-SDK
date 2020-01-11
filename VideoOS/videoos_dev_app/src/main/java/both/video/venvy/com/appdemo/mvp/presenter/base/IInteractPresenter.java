package both.video.venvy.com.appdemo.mvp.presenter.base;

import android.os.Bundle;

/**
 * Created by videopls on 2019/12/27.
 */

public interface IInteractPresenter {
    public void onDealInteract(Bundle bundleData);
    public void onFailed();
    public void onSuccess();
}
