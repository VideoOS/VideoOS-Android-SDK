package cn.com.venvy.common.image.scanner.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * Created by mac on 18/1/15.
 */

public class GridItemDecoration extends RecyclerView.ItemDecoration {
    private final int margin;

    public GridItemDecoration(Context context) {
        margin = VenvyUIUtil.dip2px(context, 1);
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(margin, margin, margin, margin);
    }
}