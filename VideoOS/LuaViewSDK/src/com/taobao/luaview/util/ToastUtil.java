/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * toast a message
 *
 * @author song
 */
public class ToastUtil {
    static CharSequence oldMsg;
    static Toast toast = null;
    static long oneTime = 0;
    static long twoTime = 0;
    static int LENGTH = Toast.LENGTH_SHORT;
    static Handler handler;


    /**
     * show a toast directly
     *
     * @param context
     * @param msg
     */
    public static void showToast(final Context context, final CharSequence msg) {
        if (context == null || msg == null || msg.length() == 0)
            return;
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(context, msg, LENGTH);
                    toast.show();
                    oneTime = System.currentTimeMillis();
                } else {
                    twoTime = System.currentTimeMillis();
                    if (msg.equals(oldMsg)) {//only show msg when time elapse or show a different msg
                        if (twoTime - oneTime > LENGTH) {
                            toast.show();
                        }
                    } else {
                        oldMsg = msg;
                        toast.setText(msg);
                        toast.show();
                    }
                }
                oneTime = twoTime;
            }
        });
    }

}
