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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hon.sunny.R;
import com.hon.sunny.ui.base.BaseViewHolder;
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

public class MultiCityAdapter extends ListAdapter<Weather, MultiCityAdapter.MultiCityViewHolder> {
    private OnMultiCityClickListener onMultiCityClickListener = null;

    private List<Weather> weatherList;

    public MultiCityAdapter() {
        super(new DiffUtil.ItemCallback<Weather>() {
            @Override
            public boolean areItemsTheSame(@NonNull Weather oldItem, @NonNull Weather newItem) {
                return oldItem.city.equals(newItem.city);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Weather oldItem, @NonNull Weather newItem) {
                return oldItem.now.txt.equals(newItem.now.txt) &&
                        oldItem.now.code.equals(newItem.now.code) &&
                        oldItem.now.tmp.equals(newItem.now.tmp);
            }
        });
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
    public void onBindViewHolder(@NonNull MultiCityViewHolder holder, int position) {
        Weather weather = getItem(position);
        holder.bind(weather);
        holder.itemView.setOnClickListener(v -> {
            onMultiCityClickListener.onClick(weather.city);
        });
        holder.itemView.setOnLongClickListener(v -> {
            onMultiCityClickListener.onLongClick(weather.city, holder.getLayoutPosition());
            return true;
        });
    }

    @Override
    public void submitList(@Nullable List<Weather> list) {
        super.submitList(list);
        this.weatherList = list;
    }

    public void insertItem(int position, Weather weather) {
        weatherList.add(position, weather);
        notifyItemInserted(position);
    }

    public Weather removeItem(int position){
        Weather weather=weatherList.remove(position);
        notifyItemRemoved(position);
        return weather;
    }

    public boolean isEmpty(){
        return weatherList.isEmpty();
    }

    public interface OnMultiCityClickListener {
        void onLongClick(String city, int position);

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

        MultiCityViewHolder(View itemView) {
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
                    .into(new BitmapImageViewTarget(dialogIcon) {
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

