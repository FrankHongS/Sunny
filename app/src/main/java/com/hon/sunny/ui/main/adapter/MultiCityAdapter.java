package com.hon.sunny.ui.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
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
import com.hon.sunny.common.Constants;
import com.hon.sunny.common.util.SharedPreferenceUtil;
import com.hon.sunny.common.util.Util;
import com.hon.sunny.data.main.bean.Weather;

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

    public void setOnMultiCityClickListener(OnMultiCityClickListener onMultiCityClickListener) {
        this.onMultiCityClickListener = onMultiCityClickListener;
    }

    public MultiCityAdapter(List<Weather> weatherList) {
        mWeatherList = weatherList;
    }

    @Override
    public MultiCityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multi_city, parent, false);
        return new MultiCityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MultiCityViewHolder holder, int position) {

        holder.bind(mWeatherList.get(position));
        holder.itemView.setOnClickListener(v->{
            onMultiCityClickListener.onClick(mWeatherList.get(holder.getAdapterPosition()).city);
        });
        holder.itemView.setOnLongClickListener(v -> {
            onMultiCityClickListener.onLongClick(mWeatherList.get(holder.getAdapterPosition()).city);
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
        public void bind(Weather weather) {

            String weatherDesc;
            int code;
            CardCityUIHelper cardCityUIHelper = new CardCityUIHelper();

            if(Constants.UNKNOWN_CITY.equals(weather.status)){
                weatherDesc="未知";
                code=-1;
                mDialogCity.setText(Util.safeText(weather.city));
                mDialogTemp.setText("error");
            }else {
                weatherDesc=weather.now.txt;
                code = Integer.valueOf(weather.now.code);
                mDialogCity.setText(Util.safeText(weather.city));
                mDialogTemp.setText(String.format("%s℃", weather.now.tmp));
            }
            cardCityUIHelper.applyStatus(code, weather.city, mCardView);

            Glide.with(mContext)
                    .load(SharedPreferenceUtil.getInstance().getInt(weatherDesc, R.mipmap.none
                    ))
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            mDialogIcon.setImageBitmap(resource);
                            mDialogIcon.setColorFilter(Color.WHITE);
                        }
                    });

        }
    }

    public interface OnMultiCityClickListener {
        void onLongClick(String city);
        void onClick(String city);
    }
}

