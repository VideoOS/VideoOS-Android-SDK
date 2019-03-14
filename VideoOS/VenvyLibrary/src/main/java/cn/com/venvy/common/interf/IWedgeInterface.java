package cn.com.venvy.common.interf;

/**
 * Create by qinpc on 2017/12/29
 */
public interface IWedgeInterface {

    /**
     * 中插开始播放
     */
    void onStart();

    /**
     * 中插播放完毕通知，播放完毕分3种状态:
     * 1:中插视频自然播放到指定时间长度
     * 2:中插在播放期间由后台广告管理撤销当前中插的播放，导致当前中插页面关闭
     * 3:中插在播放时间，配置的展示时间段过期
     */
    void onFinish();

    /**
     * 没有中插广告
     */
    void onEmpty();

    /**
     * 点击中插返回按钮
     */
    void goBack();

    /**
     * 恢复播放
     */
    void onResume();

    /**
     * 暂停播放
     */
    void onPause();

    /**
     *
     * @return 由后台返回后是否需要显示中插
     */
    boolean needShowVideo();

    /**
     *
     * @return 设置中插缓存的最大值 单位 ：M
     */
    boolean downloadSwitch();

    /**
     *
     * @return 移动网络是否下载视频 true:开启下载，false 关闭下载
     */
    int maxCacheSize();
}
