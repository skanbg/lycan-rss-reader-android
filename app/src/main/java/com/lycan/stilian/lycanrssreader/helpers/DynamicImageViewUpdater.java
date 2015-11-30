package com.lycan.stilian.lycanrssreader.helpers;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.lycan.stilian.lycanrssreader.tasks.constants.ACTION_TYPE;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.IUpdateable;

import java.lang.ref.WeakReference;

public class DynamicImageViewUpdater implements IUpdateable {
    private final WeakReference<ImageView> mContext;

    public DynamicImageViewUpdater(ImageView context) {
        this.mContext = new WeakReference<>(context);
    }

    @Override
    public void updateData(Object data, ACTION_TYPE actionType) {
        if (this.mContext != null) {
            ImageView imageView = mContext.get();

            if (imageView != null) {
                if (data != null) {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap((Bitmap)data);
                }
//                else {
//                    Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.placeholder);
//                    imageView.setImageDrawable(placeholder);
//                }
            }
        }
    }
}
