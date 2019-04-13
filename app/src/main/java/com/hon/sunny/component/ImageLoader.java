package com.hon.sunny.component;

import android.content.Context;
import androidx.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class ImageLoader {

    public static void load(Context context, @DrawableRes int imageRes, ImageView view) {
        Glide.with(context.getApplicationContext()).load(imageRes).crossFade().into(view);
    }

    public static void clear(Context context) {
        Glide.get(context).clearMemory();
    }
}