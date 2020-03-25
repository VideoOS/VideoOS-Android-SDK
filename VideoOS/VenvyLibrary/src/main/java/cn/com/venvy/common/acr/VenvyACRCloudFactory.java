package cn.com.venvy.common.acr;

import cn.com.venvy.VenvyRegisterLibsManager;
import cn.com.venvy.common.interf.IACRCloud;
import cn.com.venvy.common.utils.VenvyLog;

/***
 *
 */

public class VenvyACRCloudFactory {

    public static IACRCloud getACRCloud() {
        try {
            Class<? extends IACRCloud> ACRCloud = VenvyRegisterLibsManager.getACRCloud();
            if (ACRCloud == null) {
                return null;
            }
            return ACRCloud.newInstance();
        } catch (Exception e) {
            VenvyLog.e(VenvyACRCloudFactory.class.getName(), e);
        }
        return null;
    }
}
