package both.video.venvy.com.appdemo.mvp.view;

import both.video.venvy.com.appdemo.mvp.MvpView;

/**
 * Created by videopls on 2019/12/27.
 */

public interface IInteractView extends MvpView {
    void showLoadingView();
    void hideLoadingView();
    void showErrorToast();
}
