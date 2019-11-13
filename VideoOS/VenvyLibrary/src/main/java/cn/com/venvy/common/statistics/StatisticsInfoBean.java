package cn.com.venvy.common.statistics;

import java.util.List;

/**
 * Created by videopls on 2019/8/22.
 */

public class StatisticsInfoBean {
    public int type;

    //AB小程序跟踪统计
    public String originMiniAppId;
    public String miniAppId;

    //视联网开关次数统计
    public String onOrOff;

    //预加载流量统计
    public String videoId;
    public int downLoadStage;
    public List<FileInfoBean> fileInfoBeans;

    public static class FileInfoBean {
        public String fileName;
        public String filePath;
        public long fileSize;
    }
}
