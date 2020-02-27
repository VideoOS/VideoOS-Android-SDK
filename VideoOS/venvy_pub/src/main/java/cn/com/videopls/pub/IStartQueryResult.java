package cn.com.videopls.pub;

import cn.com.venvy.common.interf.CallbackType;

/**
 * Created by yanjiangbo on 2018/4/12.
 */

public interface IStartQueryResult {
    void successful(CallbackType callbackType, Object result, String miniAppInfo, String originData, ServiceQueryAdsInfo queryAdsInfo);

    void failed(Throwable throwable);
}
