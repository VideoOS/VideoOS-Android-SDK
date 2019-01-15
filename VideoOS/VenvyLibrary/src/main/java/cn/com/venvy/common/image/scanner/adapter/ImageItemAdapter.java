package cn.com.venvy.common.image.scanner.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.com.venvy.common.image.VenvyImageInfo;
import cn.com.venvy.common.image.VenvyImageView;
import cn.com.venvy.common.interf.IWidgetClickListener;
import cn.com.venvy.common.utils.VenvyResourceUtil;
import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * Created by mac on 18/2/24.
 */

public class ImageItemAdapter extends
        RecyclerViewCursorAdapter<RecyclerView.ViewHolder> {
    public IWidgetClickListener<String> mOnItemClickListener;
    private int imgHeight = 0;
    private Drawable placeHolderDrawable;

    public ImageItemAdapter(@NonNull Context context) {
        super(null);
        imgHeight = VenvyUIUtil.dip2px(context, 100);
        placeHolderDrawable = VenvyResourceUtil.getDrawable(context, "img_scanner_no");
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
        GridViewHolder gridViewHolder = (GridViewHolder) holder;
        final String imageUrl = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));

        gridViewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onClick(imageUrl);
                }
            }
        });

        VenvyImageInfo venvyImageInfo =
                new VenvyImageInfo.Builder().setUrl(imageUrl)
                        .setResizeWidth(70)
                        .setResizeHeight(70)
                        .setPlaceHolderImage(placeHolderDrawable)
                        .isLocalMedia(true).build();
        gridViewHolder.mImageView.loadImage(venvyImageInfo);
    }

    @Override
    protected int getItemViewType(int position, Cursor cursor) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        VenvyImageView view = new VenvyImageView(parent.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imgHeight);
        view.setLayoutParams(params);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new GridViewHolder(view);
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {

        final VenvyImageView mImageView;


        GridViewHolder(View itemView) {
            super(itemView);
            mImageView = (VenvyImageView) itemView;
        }
    }
}
