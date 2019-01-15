/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.method.SingleLineTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 文字相关的工具类
 *
 * @author song
 * @date 15/10/22
 */
public class TextUtil {

    /**
     * 使文字大小适应frame
     *
     * @param view
     */
    public static void adjustTextSize(TextView view) {
        adjustTextSize(view, new TextPaint(),
                view.getContext().getResources().getDisplayMetrics().scaledDensity * 8,
                view.getTextSize(),
                getMaxLines(view), 0.5f);
    }

    /**
     * 改变外框大小，适应文字
     * 如果当前文字只有一行，则缩到外框适应文字大小
     * 如果文字有多行，则宽度不变，缩小高度到适应文字大小
     * 文字外框的位置不变
     *
     * @param view
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void adjustSize(TextView view) {
        if (view != null && view.getLayoutParams() != null) {
            /*
            //更好的实现方式，但是ios那边不支持这种方式
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            view.setLayoutParams(layoutParams);*/

            // 废弃的实现方式，使用WRAP_CONTENT最简单，且这种方式算出来的width有bug
            // @Deprecated
            /*int lineCount = Math.min(view.getLineCount(), getMaxLines(view));
            int width, height;
            if (lineCount > 1) {//多行，宽度不变，改变高度
                width = view.getWidth();
                height = view.getLineHeight() * lineCount + view.getPaddingTop() + view.getPaddingBottom();
            } else {//一行，改变宽度&高度
                 Paint paint = view.getPaint();
                width = (int) paint.measureText(view.getText().toString()) + view.getPaddingLeft() + view.getPaddingRight();
                height = view.getLineHeight() + view.getPaddingTop() + view.getPaddingBottom();
            }*/

            int lineCount = Math.min(view.getLineCount(), getMaxLines(view));
            float width, height;
            if (lineCount > 1) {//多行，宽度不变，改变高度
                width = view.getWidth();
                height = view.getLineHeight() * lineCount + view.getPaddingTop() + view.getPaddingBottom();
            } else {//一行，改变宽度&高度
                width = view.getPaddingLeft() + Layout.getDesiredWidth(view.getText(), view.getPaint()) + view.getPaddingRight();
                height = view.getLineHeight() + view.getPaddingTop() + view.getPaddingBottom();
            }

            if (view.getLayoutParams() != null) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = (int) width;
                layoutParams.height = (int) height;
                view.setLayoutParams(layoutParams);
            }
        }
    }

    /**
     * Re-sizes the textSize of the TextView so that the text fits within the bounds of the View.
     */
    static void adjustTextSize(TextView view, TextPaint paint, float minTextSize, float maxTextSize, int maxLines, float precision) {
        if (maxLines <= 0 || maxLines == Integer.MAX_VALUE) {
            // Don't auto-size since there's no limit on lines.
            return;
        }

        int targetWidth = view.getWidth() - view.getPaddingLeft() - view.getPaddingRight();
        if (targetWidth <= 0) {
            return;
        }

        CharSequence text = view.getText();
        TransformationMethod method = view.getTransformationMethod();
        if (method != null) {
            text = method.getTransformation(text, view);
        }

        Context context = view.getContext();
        Resources r = Resources.getSystem();
        DisplayMetrics displayMetrics;

        float size = maxTextSize;
        float high = size;
        float low = 0;

        if (context != null) {
            r = context.getResources();
        }
        displayMetrics = r.getDisplayMetrics();

        paint.set(view.getPaint());
        paint.setTextSize(size);

        if ((maxLines == 1 && paint.measureText(text, 0, text.length()) > targetWidth)
                || getLineCount(text, paint, size, targetWidth, displayMetrics) > maxLines) {
            size = getAutofitTextSize(text, paint, targetWidth, maxLines, low, high, precision, displayMetrics);
        }

        if (size < minTextSize) {
            size = minTextSize;
        }

        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    /**
     * Recursive binary search to find the best size for the text.
     */
    static float getAutofitTextSize(CharSequence text, TextPaint paint, float targetWidth, int maxLines, float low, float high, float precision, DisplayMetrics displayMetrics) {
        float mid = (low + high) / 2.0f;
        int lineCount = 1;
        StaticLayout layout = null;

        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mid, displayMetrics));

        if (maxLines != 1) {
            layout = new StaticLayout(text, paint, (int) targetWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
            lineCount = layout.getLineCount();
        }


        if (lineCount > maxLines) {
            // For the case that `text` has more newline characters than `maxLines`.
            if ((high - low) < precision) {
                return low;
            }
            return getAutofitTextSize(text, paint, targetWidth, maxLines, low, mid, precision, displayMetrics);
        } else if (lineCount < maxLines) {
            return getAutofitTextSize(text, paint, targetWidth, maxLines, mid, high, precision, displayMetrics);
        } else {
            float maxLineWidth = 0;
            if (maxLines == 1) {
                maxLineWidth = paint.measureText(text, 0, text.length());
            } else {
                for (int i = 0; i < lineCount; i++) {
                    if (layout.getLineWidth(i) > maxLineWidth) {
                        maxLineWidth = layout.getLineWidth(i);
                    }
                }
            }

            if ((high - low) < precision) {
                return low;
            } else if (maxLineWidth > targetWidth) {
                return getAutofitTextSize(text, paint, targetWidth, maxLines, low, mid, precision, displayMetrics);
            } else if (maxLineWidth < targetWidth) {
                return getAutofitTextSize(text, paint, targetWidth, maxLines, mid, high, precision, displayMetrics);
            } else {
                return mid;
            }
        }
    }

    static int getLineCount(CharSequence text, TextPaint paint, float size, float width, DisplayMetrics displayMetrics) {
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, size, displayMetrics));
        return new StaticLayout(text, paint, (int) width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true).getLineCount();
    }

    static int getMaxLines(TextView view) {
        int maxLines = -1; // No limit (Integer.MAX_VALUE also means no limit)

        TransformationMethod method = view.getTransformationMethod();
        if (method != null && method instanceof SingleLineTransformationMethod) {
            maxLines = 1;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // setMaxLines() and getMaxLines() are only available on android-16+
            maxLines = view.getMaxLines();
        }

        return maxLines;
    }
}
