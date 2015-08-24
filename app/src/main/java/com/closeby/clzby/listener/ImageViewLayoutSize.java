package com.closeby.clzby.listener;

import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by iGold on 6/17/15.
 */
public class ImageViewLayoutSize implements ViewTreeObserver.OnGlobalLayoutListener {

    private ImageView imageView;
    private OnLayoutSizeListener listener;

    public ImageViewLayoutSize(ImageView imageView, OnLayoutSizeListener listener) {
        this.imageView = imageView;
        this.listener = listener;
    }

    public void setOnListener(OnLayoutSizeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onGlobalLayout() {
        int width = this.imageView.getWidth();
        int height = this.imageView.getHeight();

        if (this.listener != null) {
            listener.onGetSize(width, height);
        }
    }
}