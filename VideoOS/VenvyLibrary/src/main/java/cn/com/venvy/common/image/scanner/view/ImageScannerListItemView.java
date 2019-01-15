package cn.com.venvy.common.image.scanner.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.venvy.common.image.VenvyImageView;
import cn.com.venvy.common.utils.VenvyResourceUtil;
import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * ListView 的item view
 * Created by mac on 17/12/13.
 */

public class ImageScannerListItemView extends FrameLayout {
    private Context mContext;
    public VenvyImageView mImageView;
    private FrameLayout mInfoLayout;
    private int mImgSize = 0;
    private VenvyImageView mArrowView;
    public TextView mNameView;
    public TextView mCountView;

    public ImageScannerListItemView(@NonNull Context context, int height) {
        super(context);
        mContext = context;

        RecyclerView.LayoutParams params =
                new RecyclerView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT, height);
        setLayoutParams(params);

        initImageView();
        initInfoLayout();
        initArrowView();
        addLineView();
    }

    private void initArrowView() {
        mArrowView = new VenvyImageView(mContext);
        mArrowView.setImageDrawable(VenvyResourceUtil.getDrawable(mContext, "img_scanner_btn_go"));
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        params.rightMargin = VenvyUIUtil.dip2px(mContext, 15);
        mArrowView.setLayoutParams(params);
        addView(mArrowView);
    }

    private void initImageView() {
        mImageView = new VenvyImageView(mContext);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mImgSize = VenvyUIUtil.dip2px(mContext, 79);
        LayoutParams params = new LayoutParams(mImgSize, mImgSize);
        mImageView.setLayoutParams(params);


        addView(mImageView);
    }

    private void addLineView() {
        View view = new View(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, VenvyUIUtil.dip2px(mContext, 1));
        params.gravity = Gravity.BOTTOM;
        view.setLayoutParams(params);
        view.setBackgroundColor(Color.parseColor("#EEE3D9"));
        addView(view);
    }

    private void initInfoLayout() {
        mInfoLayout = new FrameLayout(mContext);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin = VenvyUIUtil.dip2px(mContext, 95);

        mInfoLayout.setLayoutParams(params);
        mInfoLayout.addView(createItemNameView());
        mInfoLayout.addView(createItemCountView());

        addView(mInfoLayout);
    }

    private TextView createItemNameView() {
        mNameView = new TextView(mContext);
        LayoutParams params =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.topMargin = VenvyUIUtil.dip2px(mContext, 17);
        mNameView.setLayoutParams(params);
        mNameView.setTextColor(Color.BLACK);
        mNameView.setGravity(Gravity.CENTER);
        mNameView.setTextSize(16);

        return mNameView;
    }

    private TextView createItemCountView() {
        mCountView = new TextView(mContext);
        LayoutParams params =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.topMargin = VenvyUIUtil.dip2px(mContext, 42);
        mCountView.setTextColor(Color.GRAY);
        mCountView.setLayoutParams(params);
        mCountView.setGravity(Gravity.CENTER);
        mCountView.setTextSize(13);

        return mCountView;

    }
}
