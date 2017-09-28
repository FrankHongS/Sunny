package com.hon.sunny.modules.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hon.sunny.R;
import com.hon.sunny.base.BaseViewHolder;
import com.hon.sunny.common.PLog;
import com.hon.sunny.common.util.SharedPreferenceUtil;
import com.hon.sunny.common.util.Util;
import com.hon.sunny.component.RxBus;
import com.hon.sunny.modules.main.domain.ChangeCityEvent;
import com.hon.sunny.modules.main.domain.Weather;

import java.util.List;

import butterknife.Bind;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class MultiCityAdapter extends RecyclerView.Adapter<MultiCityAdapter.MultiCityViewHolder> {
    private Context mContext;
    private List<Weather> mWeatherList;
    private OnMultiCityClickListener onMultiCityClickListener = null;

    public void setOnMultiCityLongClick(OnMultiCityClickListener onMultiCityClickListener) {
        this.onMultiCityClickListener = onMultiCityClickListener;
    }

    public MultiCityAdapter(List<Weather> weatherList) {
        mWeatherList = weatherList;
    }

    @Override
    public MultiCityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new MultiCityViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multi_city, parent, false));
    }

    @Override
    public void onBindViewHolder(MultiCityViewHolder holder, int position) {

        holder.bind(mWeatherList.get(position));
        holder.itemView.setOnClickListener(v->{
            onMultiCityClickListener.onClick(mWeatherList.get(holder.getAdapterPosition()).basic.city);
        });
        holder.itemView.setOnLongClickListener(v -> {
            onMultiCityClickListener.onLongClick(mWeatherList.get(holder.getAdapterPosition()).basic.city);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mWeatherList.size();
    }

    public boolean isEmpty() {
        return 0 == mWeatherList.size();
    }

    class MultiCityViewHolder extends BaseViewHolder<Weather> {

        @Bind(R.id.dialog_city)
        TextView mDialogCity;
        @Bind(R.id.dialog_icon)
        ImageView mDialogIcon;
        @Bind(R.id.dialog_temp)
        TextView mDialogTemp;
        @Bind(R.id.cardView)
        CardView mCardView;

        public MultiCityViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bind(Weather weather) {

            try {
                mDialogCity.setText(Util.safeText(weather.basic.city));
                mDialogTemp.setText(String.format("%s℃", weather.now.tmp));
            } catch (NullPointerException e) {
                PLog.e(e.getMessage());
            }

            Glide.with(mContext)
                    .load(SharedPreferenceUtil.getInstance().getInt(weather.now.cond.txt, R.mipmap.none
                    ))
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            mDialogIcon.setImageBitmap(resource);
                            mDialogIcon.setColorFilter(Color.WHITE);
                        }
                    });

            int code = Integer.valueOf(weather.now.cond.code);
            CardCityUIHelper cardCityUIHelper = new CardCityUIHelper();
            cardCityUIHelper.applyStatus(code, weather.basic.city, mCardView);

            PLog.d(weather.now.cond.txt + " " + weather.now.cond.code);
        }
    }

    public interface OnMultiCityClickListener {
        void onLongClick(String city);
        void onClick(String city);
    }
}

