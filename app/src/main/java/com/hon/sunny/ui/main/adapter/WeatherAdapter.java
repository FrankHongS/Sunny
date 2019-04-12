package com.hon.sunny.ui.main.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hon.sunny.R;
import com.hon.sunny.base.BaseViewHolder;
import com.hon.sunny.common.PLog;
import com.hon.sunny.common.util.SharedPreferenceUtil;
import com.hon.sunny.common.util.Util;
import com.hon.sunny.component.AnimRecyclerViewAdapter;
import com.hon.sunny.component.ImageLoader;
import com.hon.sunny.data.main.bean.HourInfoEntity;
import com.hon.sunny.data.main.bean.SuggestionEntity;
import com.hon.sunny.data.main.bean.Weather;
import com.hon.sunny.ui.main.viewholder.HoursWeatherViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

@SuppressWarnings("all")
public class WeatherAdapter extends AnimRecyclerViewAdapter<BaseViewHolder> {

    private static String TAG = WeatherAdapter.class.getSimpleName();

    private Context mContext;

    private static final int TYPE_ONE = 0;
    private static final int TYPE_TWO = 1;
    private static final int TYPE_THREE = 2;
    private static final int TYPE_FOUR = 3;

    private Weather mWeatherData;

    public WeatherAdapter(Weather weatherData) {
        this.mWeatherData = weatherData;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        switch (viewType) {
            case TYPE_ONE:
                return new NowWeatherViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.item_temperature, parent, false));
            case TYPE_TWO:
                return new HoursWeatherViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.item_hour_info_01, parent, false));
            case TYPE_THREE:
                return new SuggestionViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.item_suggestion, parent, false));
            case TYPE_FOUR:
                return new ForecastViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.item_forecast, parent, false));
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
        return mWeatherData.status != null ? 4 : 0;
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
            try {
                tempFlu.setText(String.format("%s℃", weather.now.tmp));
                tempMax.setText(
                        String.format("↑ %s ℃", weather.dailyForecast.get(0).max));
                tempMin.setText(
                        String.format("↓ %s ℃", weather.dailyForecast.get(0).min));

//                tempPm.setText(String.format("PM2.5: %s μg/m³", Util.safeText(weather.aqi.city.pm25)));
//                tempQuality.setText(Util.safeText("空气质量： ", weather.aqi.city.qlty));TODO
                ImageLoader.load(itemView.getContext(),
                        SharedPreferenceUtil.getInstance().getInt(weather.now.txt, R.mipmap.none),
                        weatherIcon);
            } catch (Exception e) {
                PLog.e(TAG, e.toString());
            }
        }
    }

    /**
     * suggestions
     */
    class SuggestionViewHolder extends BaseViewHolder<Weather> {

        @Bind(R.id.rv_suggestions)
        RecyclerView suggestions;

        SuggestionViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(Weather weather) {

            List<SuggestionEntity> suggestionList=new ArrayList<>();

            if(weather.lifestyle!=null){
                for(int i=0;i<weather.lifestyle.size();i++){
                    Weather.LifestyleEntity lifestyleEntity=weather.lifestyle.get(i);
                    SuggestionEntity suggestionEntity=null;
                    switch (i){
                        case 0:
                            suggestionEntity=new SuggestionEntity(R.drawable.icon_cloth,
                                    String.format(itemView.getResources().getString(R.string.weather_suggesetion_clothes),
                                            lifestyleEntity.brf),
                                    lifestyleEntity.txt);
                            break;
                        case 1:
                            suggestionEntity=new SuggestionEntity(R.drawable.icon_sport,
                                    String.format(itemView.getResources().getString(R.string.weather_suggesetion_sports),
                                            lifestyleEntity.brf),
                                    lifestyleEntity.txt);
                            break;
                        case 2:
                            suggestionEntity=new SuggestionEntity(R.drawable.icon_flu,
                                    String.format(itemView.getResources().getString(R.string.weather_suggesetion_illness),
                                            lifestyleEntity.brf),
                                    lifestyleEntity.txt);
                            break;
                        case 3:
                            suggestionEntity=new SuggestionEntity(R.drawable.icon_travel,
                                    String.format(itemView.getResources().getString(R.string.weather_suggesetion_travel),
                                            lifestyleEntity.brf),
                                    lifestyleEntity.txt);
                            break;
                        default:
                            break;
                    }

                    if(suggestionEntity!=null)
                        suggestionList.add(suggestionEntity);
                }

                suggestions.setAdapter(new SuggestionAdapter(suggestionList));
                suggestions.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            }

        }
    }

    /**
     * 未来天气
     */
    class ForecastViewHolder extends BaseViewHolder<Weather> {
        private LinearLayout forecastLinear;
        private TextView[] forecastDate = new TextView[mWeatherData.dailyForecast.size()];
        private TextView[] forecastTemp = new TextView[mWeatherData.dailyForecast.size()];
        private TextView[] forecastTxt = new TextView[mWeatherData.dailyForecast.size()];
        private ImageView[] forecastIcon = new ImageView[mWeatherData.dailyForecast.size()];

        ForecastViewHolder(View itemView) {
            super(itemView);
            forecastLinear = (LinearLayout) itemView.findViewById(R.id.forecast_linear);
            for (int i = 0; i < mWeatherData.dailyForecast.size(); i++) {
                View view = View.inflate(mContext, R.layout.item_forecast_line, null);
                forecastDate[i] = (TextView) view.findViewById(R.id.forecast_date);
                forecastTemp[i] = (TextView) view.findViewById(R.id.forecast_temp);
                forecastTxt[i] = (TextView) view.findViewById(R.id.forecast_txt);
                forecastIcon[i] = (ImageView) view.findViewById(R.id.forecast_icon);
                forecastLinear.addView(view);
            }
        }

        @Override
        public void bind(Weather weather) {
            try {
                //今日 明日
                forecastDate[0].setText("今日");
                forecastDate[1].setText("明日");
                for (int i = 0; i < weather.dailyForecast.size(); i++) {
                    if (i > 1) {
                        try {
                            forecastDate[i].setText(
                                    Util.dayForWeek(weather.dailyForecast.get(i).date));
                        } catch (Exception e) {
                            PLog.e(e.toString());
                        }
                    }
                    ImageLoader.load(mContext,
                            SharedPreferenceUtil.getInstance().getInt(weather.dailyForecast.get(i).txtD, R.mipmap.none),
                            forecastIcon[i]);
                    forecastTemp[i].setText(
                            String.format("%s℃ - %s℃",
                                    weather.dailyForecast.get(i).min,
                                    weather.dailyForecast.get(i).max));
                    forecastTxt[i].setText(
                            String.format("%s。 %s %s %s km/h。 降水几率 %s%%。",
                                    weather.dailyForecast.get(i).txtD,
                                    weather.dailyForecast.get(i).sc,
                                    weather.dailyForecast.get(i).dir,
                                    weather.dailyForecast.get(i).spd,
                                    weather.dailyForecast.get(i).pop));
                }
            } catch (Exception e) {
                PLog.e(e.toString());
            }
        }
    }
}

