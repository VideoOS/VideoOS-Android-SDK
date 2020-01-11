package cn.com.venvy.common.statistics;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.com.venvy.Platform;
import cn.com.venvy.common.download.DownloadTask;

/**
 * Created by videopls on 2019/8/19.
 */

public final class VenvyStatisticsManager {

    public static final int AB_APPLET_TRACK = 1;
    public static final int USER_ACTION = 2;
    public static final int VISUAL_SWITCH_COUNT = 3;
    public static final int PLAY_CONFIRM = 4;
    public static final int PRELOAD_FLOW = 5;
    public static final int OPEN_PAGE = 6;
    public static final int CLOSE_PAGE = 7;
    public static final int PAGE_NAME = 8;

    private Platform platform;

    private VenvyStatisticsManager() {

    }

    private static class Holder {
        private static VenvyStatisticsManager instance = new VenvyStatisticsManager();
    }

    public static VenvyStatisticsManager getInstance() {
        return Holder.instance;
    }

    public void init(Platform platform) {
        this.platform = platform;
    }

    public void submitCommonTrack(int type, JSONObject jsonObj) {
        if (jsonObj == null) {
            return;
        }
        if (VenvyStatisticsManager.AB_APPLET_TRACK == type) {
            submitABAppletTrackStatisticsInfo(jsonObj);
        } else if (VenvyStatisticsManager.USER_ACTION == type) {
            submitUserActionStatisticsInfo(jsonObj);
        } else if (VenvyStatisticsManager.VISUAL_SWITCH_COUNT == type) {
            submitVisualSwitchStatisticsInfo(jsonObj);
        } else if (VenvyStatisticsManager.PLAY_CONFIRM == type) {
            submitPlayConfirmStatisticsInfo(jsonObj);
        } else if (VenvyStatisticsManager.PRELOAD_FLOW == type) {
            submitPreLoadFlowStatisticsInfo(jsonObj);
        } else if (VenvyStatisticsManager.OPEN_PAGE == type
                || VenvyStatisticsManager.CLOSE_PAGE == type
                || VenvyStatisticsManager.PAGE_NAME == type) {
            submitABAppletTrackStatisticsInfo(type, jsonObj);
        }
    }

    private void submitPreLoadFlowStatisticsInfo(JSONObject jsonObj) {
        try {
            String dataJson = StatisticDCUtils.obtainPreLoadFlowStatisticsJson(VenvyStatisticsManager.PRELOAD_FLOW, jsonObj);
            executeThread(dataJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void submitPlayConfirmStatisticsInfo(JSONObject jsonObj) {
        try {
            String dataJson = StatisticDCUtils.obtainPlayConfirmStatisticsJson(VenvyStatisticsManager.PLAY_CONFIRM, jsonObj);
            executeThread(dataJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void submitVisualSwitchStatisticsInfo(JSONObject jsonObj) {
        try {
            String dataJson = StatisticDCUtils.obtainVisualSwitchStatisticsJson(VenvyStatisticsManager.VISUAL_SWITCH_COUNT, jsonObj);
            executeThread(dataJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void submitUserActionStatisticsInfo(JSONObject jsonObj) {
        try {
            String dataJson = StatisticDCUtils.obtainUserActionStatisticsJson(VenvyStatisticsManager.USER_ACTION, jsonObj);
            executeThread(dataJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void submitABAppletTrackStatisticsInfo(JSONObject jsonObj) {
        try {
            String dataJson = StatisticDCUtils.obtainABAppletTrackStatisticsJson(VenvyStatisticsManager.AB_APPLET_TRACK, jsonObj);
            executeThread(dataJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void submitABAppletTrackStatisticsInfo(int type, JSONObject jsonObj) {
        try {
            String dataJson = StatisticDCUtils.obtainABAppletTrackStatisticsJson(type, jsonObj);
            executeThread(dataJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void executeThread(String dataJson) {
        if (!TextUtils.isEmpty(dataJson) && platform != null) {
            ThreadManager.getInstance().createLongPool().execute(new AsyncStatisticsRunnable(platform, dataJson));
        }
    }

    public void submitVisualSwitchStatisticsInfo(String onOrOff) {
        if (TextUtils.isEmpty(onOrOff) || platform == null) {
            return;
        }
        try {
            String dataJson = StatisticDCUtils.obtainVisualSwitchStatisticsJson(VenvyStatisticsManager.VISUAL_SWITCH_COUNT, onOrOff);
            executeThread(dataJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void submitFileStatisticsInfo(List<DownloadTask> downloadTaskList, int downLoadStage) {
        if (downloadTaskList == null || downloadTaskList.size() <= 0 || platform == null) {
            return;
        }
        try {
            String dataJson = StatisticDCUtils.obtainFlowStatisticJson(VenvyStatisticsManager.PRELOAD_FLOW, downloadTaskList, downLoadStage);
            executeThread(dataJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void submitFileStatisticsInfo(StatisticsInfoBean.FileInfoBean fileInfoBean, int downLoadStage) {
        if (fileInfoBean == null || platform == null) {
            return;
        }
        try {
            String dataJson = StatisticDCUtils.obtainFlowStatisticJson(VenvyStatisticsManager.PRELOAD_FLOW, fileInfoBean, downLoadStage);
            executeThread(dataJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
