package cn.com.venvy.common.debug;

import android.os.Bundle;

import cn.com.venvy.Config;
import cn.com.venvy.common.observer.ObservableManager;

/**
 * Created by Arthur on 2017/4/28.
 */

public class DebugStatus {

    public final static String ENVIRONMENT_CHANGED = "venvy_environment_changed";
    public final static String CURRENT_ENVIRONMENT = "venvy_current_environment";

    public enum EnvironmentStatus {
        RELEASE(1),
        PREVIEW(2),
        TEST(3),
        DEV(4);

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
                    return RELEASE;
                case 2:
                    return PREVIEW;
                case 3:
                    return TEST;
                case 4:
                    return DEV;
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

    public static boolean isRelease() {
        return sCurrentDebugStatus == EnvironmentStatus.RELEASE;
    }

    public static boolean isPreView() {
        return sCurrentDebugStatus == EnvironmentStatus.PREVIEW;
    }

    public static boolean isTest() {
        return sCurrentDebugStatus == EnvironmentStatus.TEST;
    }

    public static boolean isDev() {
        return sCurrentDebugStatus == EnvironmentStatus.DEV;
    }

    public static EnvironmentStatus getCurrentEnvironmentStatus() {
        return sCurrentDebugStatus;
    }

}
