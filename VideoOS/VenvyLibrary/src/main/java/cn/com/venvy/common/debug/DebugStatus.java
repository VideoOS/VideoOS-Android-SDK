package cn.com.venvy.common.debug;

import android.os.Bundle;

import cn.com.venvy.Config;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.videopls.venvy.library.BuildConfig;

/**
 * Created by Arthur on 2017/4/28.
 */

public class DebugStatus {

    public final static String ENVIRONMENT_CHANGED = "venvy_environment_changed";
    public final static String CURRENT_ENVIRONMENT = "venvy_current_environment";

    public enum EnvironmentStatus {
        DEBUG(1),
        PREVIEW(2),
        RELEASE(3);

        int environmentValue = 0;

        EnvironmentStatus(int value) {
            environmentValue = value;
        }

        public int getEnvironmentValue() {
            return environmentValue;
        }

        public static EnvironmentStatus getStatusByIntType(int type) {
            switch (type) {
                case 1:
                    return DEBUG;
                case 2:
                    return PREVIEW;
                case 3:
                    return RELEASE;
            }
            return RELEASE;
        }
    }

    // 默认是Release
    private static EnvironmentStatus sCurrentDebugStatus = Config.DEBUG_STATUS > 0 ? EnvironmentStatus.getStatusByIntType(Config.DEBUG_STATUS) : EnvironmentStatus.RELEASE;

    public static void changeEnvironmentStatus(EnvironmentStatus environmentStatus) {
        if (sCurrentDebugStatus != environmentStatus) {
            sCurrentDebugStatus = environmentStatus;
            Bundle bundle = new Bundle();
            bundle.putSerializable(CURRENT_ENVIRONMENT, environmentStatus);
            ObservableManager.getDefaultObserable().sendToTarget(ENVIRONMENT_CHANGED, bundle);
        }
    }

    public static boolean isDebug() {
        return sCurrentDebugStatus == EnvironmentStatus.DEBUG;
    }

    public static boolean isRelease() {
        return sCurrentDebugStatus == EnvironmentStatus.RELEASE;
    }

    public static boolean isPreView() {
        return sCurrentDebugStatus == EnvironmentStatus.PREVIEW;
    }

    public static EnvironmentStatus getCurrentEnvironmentStatus() {
        return sCurrentDebugStatus;
    }


}
