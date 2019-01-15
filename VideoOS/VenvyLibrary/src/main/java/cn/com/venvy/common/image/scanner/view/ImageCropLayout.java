package cn.com.venvy.common.image.scanner.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.venvy.common.image.crop.CropImageView;
import cn.com.venvy.common.interf.IWidgetClickListener;
import cn.com.venvy.common.utils.VenvyResourceUtil;
import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * 图片裁剪类
 * Created by mac on 17/12/18.
 */

public class ImageCropLayout extends FrameLayout {
    private ImageView mBackBtn;
    private TextView mCompleteCropBtn;
    private CropImageView mCropImageView;
    private Context mContext;
    private int mTopLayoutHeight = 0;

    public ImageCropLayout(@NonNull Context context) {
        super(context);
        mContext = context;
        mTopLayoutHeight = VenvyUIUtil.dip2px(context, 44);

        setBackgroundColor(Color.BLACK);

        initTopView();
        initCropImageView();

        LayoutParams params = new LayoutParams(VenvyUIUtil.getScreenWidth(context), VenvyUIUtil.getScreenHeight(context));
        setLayoutParams(params);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    private void initTopView() {
        FrameLayout topLayout = new FrameLayout(mContext);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, VenvyUIUtil.dip2px(mContext, 44));
        topLayout.setLayoutParams(params);
        topLayout.setBackgroundColor(Color.parseColor("#2896F0"));

        initBackView();
        initOkCropBtn();

        topLayout.addView(mBackBtn);
        topLayout.addView(mCompleteCropBtn);
        topLayout.addView(createTitleView());

        addView(topLayout);

    }

    private View createTitleView() {
        TextView titleView = new TextView(mContext);
        titleView.setText("图片上传");
        titleView.setTextSize(16);
        titleView.setTextColor(Color.WHITE);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;

        titleView.setLayoutParams(params);

        return titleView;

    }

    private void initBackView() {
        mBackBtn = new ImageView(mContext);
        mBackBtn.setClickable(true);
        mBackBtn.setImageDrawable(VenvyResourceUtil.getDrawable(mContext, "img_scanner_btn_back"));
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, mTopLayoutHeight);
        params.leftMargin = VenvyUIUtil.dip2px(mContext, 10);

        mBackBtn.setLayoutParams(params);

    }


    private void initOkCropBtn() {
        mCompleteCropBtn = new TextView(mContext);
        mCompleteCropBtn.setClickable(true);
        mCompleteCropBtn.setText("上传");
        mCompleteCropBtn.setTextColor(Color.WHITE);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int margin = VenvyUIUtil.dip2px(mContext, 10);
        params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        params.rightMargin = margin;

        mCompleteCropBtn.setLayoutParams(params);

    }

    private void initCropImageView() {
        mCropImageView = new CropImageView(mContext);
        mCropImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mCropImageView.setFixedAspectRatio(true);
        mCropImageView.setAdjustViewBounds(true);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.topMargin = mTopLayoutHeight;
        mCropImageView.setLayoutParams(params);

        addView(mCropImageView);
    }

    public void setCropImage(@NonNull String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        mCropImageView.setImageBitmap(bitmap);
    }

    public void setCropCancelListener(final OnClickListener cancelClickListener) {
        mBackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelClickListener != null) {
                    cancelClickListener.onClick(v);
                }
            }
        });
    }

    public void setCropCompleteListener(final IWidgetClickListener<Bitmap> cropCompleteListener) {
        mCompleteCropBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cropCompleteListener != null) {
                    cropCompleteListener.onClick(mCropImageView.getCroppedImage());
                }
            }
        });
    }
}
