package cn.com.venvy.common.image.scanner.view;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

import cn.com.venvy.common.image.scanner.adapter.ImageFloderAdapter;
import cn.com.venvy.common.image.scanner.adapter.ImageItemAdapter;
import cn.com.venvy.common.image.scanner.bean.ImageFolderBean;
import cn.com.venvy.common.image.scanner.interf.IImageMediaCallback;
import cn.com.venvy.common.image.scanner.model.ImageFolderModel;
import cn.com.venvy.common.image.scanner.model.ImageModel;
import cn.com.venvy.common.interf.IWidgetClickListener;
import cn.com.venvy.common.utils.VenvyFileUtil;
import cn.com.venvy.common.utils.VenvyIOUtils;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyResourceUtil;
import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * Created by mac on 17/12/12.
 */

public class ImageScannerDialogLayout extends FrameLayout implements IImageMediaCallback {


    private FrameLayout mTopLayout;
    private RecyclerView mListView;

    private TextView mCancelView;
    private int mWidth;
    private int mHeight;
    private int mTopLayoutHeight;
    private Context mContext;
    TextView mTitleView;
    //图片裁减布局
    private ImageCropLayout mImageCropLayout;
    public IWidgetClickListener<String> mCropImageResultListener;
    public IWidgetClickListener mDismissDialogListener;

    private RecyclerView mGridView;
    private ImageItemAdapter imageItemAdapter;
    private ImageModel imageModel;

    private ImageFloderAdapter imageFloderAdapter;
    private ImageFolderModel imageFolderModel;

    public ImageScannerDialogLayout(@NonNull Context context) {
        super(context);

        mWidth = VenvyUIUtil.getScreenWidth(context);
        mTopLayoutHeight = VenvyUIUtil.dip2px(context, 44);
        mHeight = VenvyUIUtil.getScreenHeight(context);
        setBackgroundColor(Color.BLACK);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(params);
        mContext = context;
        initTopView();

        initListView();
    }

    private void initCropImageLayout() {
        mImageCropLayout = new ImageCropLayout(mContext);
        mImageCropLayout.setCropCancelListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageCropLayout.removeAllViews();
                removeView(mImageCropLayout);
                mImageCropLayout = null;
            }
        });

        mImageCropLayout.setCropCompleteListener(new IWidgetClickListener<Bitmap>() {
            @Override
            public void onClick(@Nullable Bitmap bitmap) {

                String sdCardDir = VenvyFileUtil.getCachePath(mContext) + "/cropImages/";
                File dirFile = new File(sdCardDir);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }

                String imgName = System.currentTimeMillis() + ".jpg";
                File file = new File(sdCardDir, imgName);

                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    removeAllViews();

                    mImageCropLayout.removeAllViews();
                    mImageCropLayout = null;
                    imageModel.onDestroy();
                    imageModel = null;
                    mGridView = null;
                    imageItemAdapter = null;
                    mListView = null;
                    imageFolderModel.onDestroy();
                    imageFolderModel = null;
                    imageFloderAdapter = null;

                    if (mCropImageResultListener != null) {
                        mCropImageResultListener.onClick(file.getAbsolutePath());
                    }

                } catch (Exception e) {
                    VenvyLog.i("crop image error");
                } finally {
                    VenvyIOUtils.close(outputStream);
                }

            }
        });
        addView(mImageCropLayout);
    }

    private void initTopView() {
        mTopLayout = new FrameLayout(mContext);
        LayoutParams params = new LayoutParams(mWidth, mTopLayoutHeight);
        params.gravity = Gravity.TOP;
        mTopLayout.setLayoutParams(params);
        mTopLayout.setBackgroundColor(Color.parseColor("#2896F0"));

        mTopLayout.addView(createBackView());
        mTopLayout.addView(createTitleView());

        initCancelView();
        mTopLayout.addView(mCancelView);
        addView(mTopLayout);
    }

    public void initCancelView() {
        mCancelView = new TextView(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        params.rightMargin = VenvyUIUtil.dip2px(mContext, 16);
        mCancelView.setLayoutParams(params);
        mCancelView.setTextColor(Color.WHITE);
        mCancelView.setGravity(Gravity.CENTER);
        mCancelView.setText("取消");
        mCancelView.setTextSize(18);
        mCancelView.setVisibility(GONE);
        mCancelView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllViews();
                if (imageModel != null) {
                    imageModel.onDestroy();
                    imageModel = null;
                }
                mGridView = null;
                imageItemAdapter = null;
                mListView = null;
                if (imageFolderModel != null) {
                    imageFolderModel.onDestroy();
                    imageFolderModel = null;
                }
                imageFloderAdapter = null;
                if (mDismissDialogListener != null) {
                    mDismissDialogListener.onClick(null);
                }
            }
        });
    }

    private View createTitleView() {
        mTitleView = new TextView(mContext);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        mTitleView.setLayoutParams(params);
        mTitleView.setTextColor(Color.WHITE);
        mTitleView.setGravity(Gravity.CENTER);
        mTitleView.setText("选择相册");
        mTitleView.setTextSize(20);
        return mTitleView;

    }

    private View createBackView() {
        ImageView backView = new ImageView(mContext);
        backView.setImageDrawable(VenvyResourceUtil.getDrawable(mContext, "img_scanner_btn_back"));
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        params.leftMargin = VenvyUIUtil.dip2px(mContext, 10);

        backView.setLayoutParams(params);

        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGridView != null) {
                    removeView(mGridView);
                    imageModel.onDestroy();
                    imageModel = null;
                    mGridView = null;
                    imageItemAdapter = null;
                    mListView.setVisibility(VISIBLE);
                    mCancelView.setVisibility(GONE);
                    mTitleView.setText("选择相册");
                } else {//关闭diaolog

                    removeAllViews();
                    if (imageModel != null) {
                        imageModel.onDestroy();
                        imageModel = null;
                    }
                    mGridView = null;
                    imageItemAdapter = null;
                    mListView = null;
                    if (imageFolderModel != null) {
                        imageFolderModel.onDestroy();
                        imageFolderModel = null;
                    }
                    imageFloderAdapter = null;
                    if (mDismissDialogListener != null) {
                        mDismissDialogListener.onClick(null);
                    }
                }

            }
        });
        return backView;

    }


    private void initGridView() {
        if (mGridView == null) {
            mGridView = new RecyclerView(mContext);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, mHeight);
            params.topMargin = mTopLayoutHeight;
            mGridView.setLayoutParams(params);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
            mGridView.addItemDecoration(new GridItemDecoration(getContext()));
            mGridView.setLayoutManager(gridLayoutManager);
            imageItemAdapter = new ImageItemAdapter(mContext);
            imageItemAdapter.mOnItemClickListener = new IWidgetClickListener<String>() {
                @Override
                public void onClick(@Nullable String uri) {
                    if (mImageCropLayout == null) {
                        initCropImageLayout();
                    }
                    mImageCropLayout.setCropImage(uri);
                }
            };
            mGridView.setAdapter(imageItemAdapter);
            addView(mGridView);

        }

    }


    private void initListView() {


        mListView = new RecyclerView(mContext);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(mContext);
        layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setLayoutManager(layoutmanager);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = mTopLayoutHeight;
        mListView.setLayoutParams(params);
        mListView.setBackgroundColor(Color.WHITE);

        imageFloderAdapter = new ImageFloderAdapter(mContext);
        imageFloderAdapter.mOnItemClickListener = new IWidgetClickListener<ImageFolderBean>() {
            @Override
            public void onClick(ImageFolderBean imageFolderBean) {
                initGridView();

                imageModel = new ImageModel();
                imageModel.onCreate(mContext, ImageScannerDialogLayout.this);
                imageModel.load(imageFolderBean);

                mListView.setVisibility(GONE);
                mTitleView.setText("选择图片");
                mCancelView.setVisibility(VISIBLE);
            }
        };
        mListView.setAdapter(imageFloderAdapter);

        imageFolderModel = new ImageFolderModel();
        imageFolderModel.onCreate(mContext, new IImageMediaCallback() {
            @Override
            public void onImageLoad(Cursor cursor) {
                imageFloderAdapter.swapCursor(cursor);
            }

            @Override
            public void onImageReset() {
                imageFloderAdapter.swapCursor(null);
            }
        });
        imageFolderModel.loadImageFolders();

        addView(mListView);
    }

    @Override
    public void onImageLoad(Cursor cursor) {
        imageItemAdapter.swapCursor(cursor);
    }

    @Override
    public void onImageReset() {
        imageItemAdapter.swapCursor(null);
    }
}
