package com.hon.sunny.ui.main.adapter;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.hon.sunny.R;
import com.hon.sunny.Sunny;
import com.hon.sunny.ui.base.BaseViewHolder;
import com.hon.sunny.ui.common.AnimatorAdapter;
import com.hon.sunny.ui.main.viewholder.ForecastChartViewHolder;
import com.hon.sunny.ui.main.viewholder.ForecastViewHolder;
import com.hon.sunny.ui.main.viewholder.HoursWeatherViewHolder;
import com.hon.sunny.ui.main.viewholder.SuggestionViewHolder;
import com.hon.sunny.utils.Constants;
import com.hon.sunny.utils.SharedPreferenceUtil;
import com.hon.sunny.utils.Util;
import com.hon.sunny.vo.bean.main.Weather;

import butterknife.BindView;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */
@SuppressWarnings("all")
public class WeatherAdapter extends AnimatorAdapter<BaseViewHolder> {

    private static final int NOW_WEATHER = 0;
    private static final int HOURS_WEATHER = 1;
    private static final int SUGGESTION = 2;
    private static final int FORECAST = 3;
    private static final int FORECAST_CHART = 4;
    private static String TAG = WeatherAdapter.class.getSimpleName();
    private Weather mWeatherData;

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case NOW_WEATHER:
                return new NowWeatherViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_now_temperature, parent, false));
            case HOURS_WEATHER:
                return new HoursWeatherViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hour_info, parent, false));
            case SUGGESTION:
                return new SuggestionViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false));
            case FORECAST:
                return new ForecastViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast, parent, false));
            case FORECAST_CHART:
                return new ForecastChartViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast_chart, parent, false)
                );
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.bind(mWeatherData);
        if (PreferenceManager.getDefaultSharedPreferences(Sunny.getAppContext())
                .getBoolean(Constants.ANIM_START, true)) {
            addItemAnimation(holder);
        }
    }

    @Override
    public int getItemCount() {
        return mWeatherData != null && mWeatherData.status != null ? 5 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setWeather(Weather weather) {
        this.mWeatherData = weather;
        changeData();// similar to notifyDataChanged
    }

    /**
     * 当前天气情况
     */
    class NowWeatherViewHolder extends BaseViewHolder<Weather> {

        @BindView(R.id.iv_now_weather)
        ImageView weatherIcon;
        @BindView(R.id.tv_now_temp)
        TextView tempNow;
        @BindView(R.id.tv_now_temp_max)
        TextView tempMax;
        @BindView(R.id.tv_now_temp_min)
        TextView tempMin;
        @BindView(R.id.tv_now_air_pm25)
        TextView tempPm;
        @BindView(R.id.tv_now_air_quality)
        TextView tempQuality;
        @BindView(R.id.tv_now_aqi)
        TextView tempAqi;

        NowWeatherViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(Weather weather) {
            if (weather.now != null)
                tempNow.setText(String.format("%s℃", weather.now.tmp));
            if (weather.dailyForecast != null) {

                tempMax.setText(
                        String.format("↑ %s ℃", weather.dailyForecast.get(0).max));
                tempMin.setText(
                        String.format("↓ %s ℃", weather.dailyForecast.get(0).min));

            }

            tempPm.setText(String.format("PM2.5: %s μg/m³", Util.safeText(weather.pm25)));
            tempQuality.setText(Util.safeText("空气质量: ", weather.quality));
            tempAqi.setText(Util.safeText("AQI: ", weather.aqi));

            if (weather.now != null)
                Glide.with(itemView.getContext())
                        .load(SharedPreferenceUtil.getInstance().getInt(weather.now.txt, R.mipmap.none))
                        .into(weatherIcon);
        }
    }

}