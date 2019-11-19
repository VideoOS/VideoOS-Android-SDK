package both.video.venvy.com.appdemo.http;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.http.urlconnection.HttpUrlConnectionHelper;
import cn.com.venvy.common.utils.VenvyLog;

public abstract class IConfigModel {
    protected final static String TAG = IConfigModel.class.getName();
    private HttpUrlConnectionHelper mConnectionHelper;
    private Request mCurrentRequest;
    public IConfigModel() {
        mConnectionHelper = new HttpUrlConnectionHelper();
        mConnectionHelper.setReadTimeOut(500000);
    }

    public abstract Request createRequest();

    public abstract IRequestHandler createRequestHandler();

    public void startRequest() {
        if (mConnectionHelper == null) {
            VenvyLog.e(TAG, "connect error, connect can't be null");
            return;
        }
        Request request = createRequest();
        if (request == null) {
            return;
        }
        mConnectionHelper.connect(request, createRequestHandler());
        mCurrentRequest = request;
    }

    public void destroy() {
        if (mConnectionHelper != null && mCurrentRequest != null) {
            mConnectionHelper.abort(mCurrentRequest.mRequestId);
        }
    }


    public HttpUrlConnectionHelper getRequestConnect() {
        return mConnectionHelper;
    }
}
