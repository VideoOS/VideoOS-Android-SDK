package cn.com.venvy.common.utils;

/**
 * Created by yanjiangbo on 2017/5/10.
 */

public class VenvyIDHelper {

    private static VenvyIDHelper sRequestIDHelper;

    private int _ID = Integer.MIN_VALUE;

    public synchronized static VenvyIDHelper getInstance() {
        if (sRequestIDHelper == null) {
            sRequestIDHelper = new VenvyIDHelper();
        }
        return sRequestIDHelper;
    }

    /**
     * 此处获取动态ID
     *
     * @return
     */
    public int getId() {
        _ID = _ID + 1;
        if (_ID >= Integer.MAX_VALUE) {
            _ID = Integer.MIN_VALUE;
            getId();
        }
        return _ID;
    }
}
