package com.hon.sunny.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hon.sunny.R;
import com.hon.sunny.base.BaseViewHolder;
import com.hon.sunny.ui.main.viewholder.ForecastChartViewHolder;
import com.hon.sunny.utils.PLog;
import com.hon.sunny.utils.SharedPreferenceUtil;
import com.hon.sunny.component.AnimRecyclerViewAdapter;
import com.hon.sunny.component.ImageLoader;
import com.hon.sunny.data.main.bean.Weather;
import com.hon.sunny.ui.main.viewholder.ForecastViewHolder;
import com.hon.sunny.ui.main.viewholder.HoursWeatherViewHolder;
import com.hon.sunny.ui.main.viewholder.SuggestionViewHolder;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import butterknife.Bind;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

@SuppressWarnings("all")
public class WeatherAdapter extends AnimRecyclerViewAdapter<BaseViewHolder> {

    private static String TAG = WeatherAdapter.class.getSimpleName();

    private static final int NOW_WEATHER = 0;
    private static final int HOURS_WEATHER = 1;
    private static final int SUGGESTION = 2;
    private static final int FORECAST = 3;
    private static final int FORECAST_CHART = 4;

    private Weather mWeatherData;

    public WeatherAdapter(Weather weatherData) {
        this.mWeatherData = weatherData;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case NOW_WEATHER:
                return new NowWeatherViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_temperature, parent, false));
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
        if (SharedPreferenceUtil.getInstance().getMainAnim()) {
            showItemAnim(holder.itemView, position);
        }
    }

    @Override
    public int getItemCount() {
        return mWeatherData.status != null ? 5 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /**
     * 当前天气情况
     */
    class NowWeatherViewHolder extends BaseViewHolder<Weather> {

        @Bind(R.id.weather_icon)
        ImageView weatherIcon;
        @Bind(R.id.temp_flu)
        TextView tempFlu;
        @Bind(R.id.temp_max)
        TextView tempMax;
        @Bind(R.id.temp_min)
        TextView tempMin;
        @Bind(R.id.temp_pm)
        TextView tempPm;
        @Bind(R.id.temp_quality)
        TextView tempQuality;
        @Bind(R.id.cardView)
        CardView cardView;

        NowWeatherViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(Weather weather) {
            if (weather.now != null)
                tempFlu.setText(String.format("%s℃", weather.now.tmp));
            if (weather.dailyForecast != null) {

                tempMax.setText(
                        String.format("↑ %s ℃", weather.dailyForecast.get(0).max));
                tempMin.setText(
                        String.format("↓ %s ℃", weather.dailyForecast.get(0).min));

            }

//                tempPm.setText(String.format("PM2.5: %s μg/m³", Util.safeText(weather.aqi.city.pm25)));
//                tempQuality.setText(Util.safeText("空气质量： ", weather.aqi.city.qlty));服务端不支持
            if (weather.now != null)
                ImageLoader.load(itemView.getContext(),
                        SharedPreferenceUtil.getInstance().getInt(weather.now.txt, R.mipmap.none),
                        weatherIcon);
        }
    }

}