package both.video.venvy.com.appdemo.helper;

import android.support.annotation.Nullable;

import both.video.venvy.com.appdemo.bean.OsConfigureBean;
import both.video.venvy.com.appdemo.utils.ParseUtil;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.http.urlconnection.HttpUrlConnectionHelper;
import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * Create by bolo on 06/06/2018
 */
public class ConfigRequestHelper {

    private static final String url = "http://mock.videojj" +
            ".com/mock/5b179bd28e21c409b29a2301/example/user_config";

    private IGetConfigSuccessListener mListener;
    private HttpUrlConnectionHelper mHelper;

    public void setListener(IGetConfigSuccessListener listener) {
        mListener = listener;
    }

    public void abort() {
        if (mHelper != null) {
            mHelper.abortAll();
            mListener = null;
        }
    }

    public void request() {
        if (mHelper == null) {
            mHelper = new HttpUrlConnectionHelper();
        }
        Request request = HttpRequest.get(url);
        mHelper.connect(request, new IRequestHandler() {
            @Override
            public void requestFinish(Request request, IResponse response) {
                final OsConfigureBean bean = ParseUtil.parseConfig(response.getResult());
                if (bean == null) {
                    return;
                }
                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) {
                            mListener.success(bean);
                        }
                    }
                });
            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {
                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) {
                            mListener.failed();
                        }
                    }
                });
            }

            @Override
            public void startRequest(Request request) {

            }

            @Override
            public void requestProgress(Request request, final int progress) {
                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) {
                            mListener.progress(progress);
                        }
                    }
                });
            }
        });
    }

    public interface IGetConfigSuccessListener {
        void success(OsConfigureBean bean);

        void progress(int progress);

        void failed();
    }
}
