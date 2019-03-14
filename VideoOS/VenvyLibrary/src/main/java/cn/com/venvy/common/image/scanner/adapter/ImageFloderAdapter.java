package cn.com.venvy.common.image.scanner.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import cn.com.venvy.common.image.VenvyImageInfo;
import cn.com.venvy.common.image.VenvyImageView;
import cn.com.venvy.common.image.scanner.bean.ImageFolderBean;
import cn.com.venvy.common.image.scanner.view.ImageScannerListItemView;
import cn.com.venvy.common.interf.IWidgetClickListener;
import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * Created by mac on 17/12/13.
 */

//public class ImageFloderAdapter extends ImageScannerAdapter<ImageFolderBean> {
//    public ImageFloderAdapter(Context context, List datas) {
//        super(context, datas);
//    }
//
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ImageScannerListItemViewHolder viewHolder = ImageScannerListItemViewHolder.get(mContext, convertView, position);
//        ImageFolderBean imageFolderBean = getItem(position);
//        viewHolder.setImageByUrl(1, imageFolderBean.firstImagePath);
//        viewHolder.setText(2, imageFolderBean.floderName);
//        viewHolder.setText(3, imageFolderBean.count + "张");
//        return viewHolder.getConvertView();
//    }
//}
public class ImageFloderAdapter extends
        RecyclerViewCursorAdapter<RecyclerView.ViewHolder> {
    public IWidgetClickListener<ImageFolderBean> mOnItemClickListener;
    private int height;

    public ImageFloderAdapter(Context context) {
        super(null);
        height = VenvyUIUtil.dip2px(context, 80);
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
        ListViewHolder listViewHolder = (ListViewHolder) holder;
        final ImageFolderBean imageFolderBean = ImageFolderBean.valueOf(cursor);
        ViewGroup viewGroup = listViewHolder.mItemView;
        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onClick(imageFolderBean);
                }
            }
        });
        listViewHolder.mNameView.setText(imageFolderBean.floderName);
        listViewHolder.mCountView.setText("" + imageFolderBean.count);
        VenvyImageInfo venvyImageInfo =
                new VenvyImageInfo.Builder()
                        .setResizeWidth(70)
                        .setResizeHeight(70)
                        .setUrl(imageFolderBean.firstImagePath)
                        .isLocalMedia(true).build();
        listViewHolder.mImageView.loadImage(venvyImageInfo);
    }

    @Override
    protected int getItemViewType(int position, Cursor cursor) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageScannerListItemView imageScannerListItemView = new ImageScannerListItemView(parent.getContext(), height);
        return new ImageFloderAdapter.ListViewHolder(imageScannerListItemView);
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {

        final ImageScannerListItemView mItemView;
        final VenvyImageView mImageView;
        final TextView mNameView;
        final TextView mCountView;

        ListViewHolder(ImageScannerListItemView itemView) {
            super(itemView);
            mItemView = (ImageScannerListItemView) itemView;
            mImageView = mItemView.mImageView;
            mNameView = mItemView.mNameView;
            mCountView = mItemView.mCountView;
        }
    }
}
