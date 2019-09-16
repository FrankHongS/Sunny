package com.hon.sunny.ui.main.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hon.sunny.R;
import com.hon.sunny.base.BaseViewHolder;
import com.hon.sunny.utils.Constants;
import com.hon.sunny.utils.SharedPreferenceUtil;
import com.hon.sunny.utils.Util;
import com.hon.sunny.vo.bean.main.Weather;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class MultiCityAdapter extends RecyclerView.Adapter<MultiCityAdapter.MultiCityViewHolder> {
    private List<Weather> mWeatherList;
    private OnMultiCityClickListener onMultiCityClickListener = null;

    public MultiCityAdapter(List<Weather> weatherList) {
        mWeatherList = weatherList;
    }

    public void setOnMultiCityClickListener(OnMultiCityClickListener onMultiCityClickListener) {
        this.onMultiCityClickListener = onMultiCityClickListener;
    }

    @NonNull
    @Override
    public MultiCityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multi_city, parent, false);
        return new MultiCityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MultiCityViewHolder holder, int position) {

        holder.bind(mWeatherList.get(position));
        holder.itemView.setOnClickListener(v -> {
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

    public interface OnMultiCityClickListener {
        void onLongClick(String city);

        void onClick(String city);
    }

    class MultiCityViewHolder extends BaseViewHolder<Weather> {

        @BindView(R.id.dialog_city)
        TextView dialogCity;
        @BindView(R.id.dialog_icon)
        ImageView dialogIcon;
        @BindView(R.id.dialog_temp)
        TextView dialogTemp;
        @BindView(R.id.cardView)
        CardView cardView;

        public MultiCityViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(Weather weather) {

            String weatherDesc;
            int code;
            CardCityUIHelper cardCityUIHelper = new CardCityUIHelper();

            if (Constants.UNKNOWN_CITY.equals(weather.status)) {
                weatherDesc = "未知";
                code = -1;
                dialogCity.setText(Util.safeText(weather.city));
                dialogTemp.setText("error");
            } else {
                weatherDesc = weather.now.txt;
                code = Integer.valueOf(weather.now.code);
                dialogCity.setText(Util.safeText(weather.city));
                dialogTemp.setText(String.format("%s℃", weather.now.tmp));
            }
            cardCityUIHelper.applyStatus(code, weather.city, cardView);

            Glide.with(itemView.getContext())
                    .asBitmap()
                    .load(SharedPreferenceUtil.getInstance().getInt(weatherDesc, R.mipmap.none))
                    .into(new BitmapImageViewTarget(dialogIcon){
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            super.onResourceReady(resource, transition);
                            dialogIcon.setImageBitmap(resource);
                            dialogIcon.setColorFilter(Color.WHITE);
                        }
                    });

        }
    }
}

