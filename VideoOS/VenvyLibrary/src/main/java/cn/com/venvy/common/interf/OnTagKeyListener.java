package cn.com.venvy.common.interf;

import android.view.KeyEvent;

/**
 * Created by liyangyang on 2017/11/14.
 */

public interface OnTagKeyListener {
    /**
     * 按键监听器
     *
     * @param keycode  事件类型有两个值，KeyEvent.KEYCODE_DPAD_LEFT:按左键；KeyEvent.KEYCODE_DPAD_RIGHT:按右键
     * @param keyEvent 遥控器事件
     */
    void onKey(int keycode, KeyEvent keyEvent);
}
