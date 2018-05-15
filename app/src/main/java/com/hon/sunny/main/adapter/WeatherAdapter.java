package com.hon.sunny.main.adapter;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
import com.hon.sunny.data.main.bean.Weather;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.Bind;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class WeatherAdapter extends AnimRecyclerViewAdapter {
    private static String TAG = WeatherAdapter.class.getSimpleName();

    private Context mContext;

    private static final int TYPE_ONE = 0;
    private static final int TYPE_TWO = 1;
    private static final int TYPE_THREE = 2;
    private static final int TYPE_FOUR = 3;

//    @IntDef({TYPE_ONE,TYPE_TWO,TYPE_THREE,TYPE_FOUR})
//    @Retention(RetentionPolicy.SOURCE)//?? todo
//    private @interface WeatherType{}

    private Weather mWeatherData;

    public WeatherAdapter(Weather weatherData) {
        this.mWeatherData = weatherData;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == WeatherAdapter.TYPE_ONE) {
            return WeatherAdapter.TYPE_ONE;
        }
        if (position == WeatherAdapter.TYPE_TWO) {
            return WeatherAdapter.TYPE_TWO;
        }
        if (position == WeatherAdapter.TYPE_THREE) {
            return WeatherAdapter.TYPE_THREE;
        }
        if (position == WeatherAdapter.TYPE_FOUR) {
            return WeatherAdapter.TYPE_FOUR;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        switch (viewType) {
            case TYPE_ONE:
                return new NowWeatherViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.item_temperature, parent, false));
            case TYPE_TWO:
                return new HoursWeatherViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.item_hour_info, parent, false));
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        switch (itemType) {
            case TYPE_ONE:
                ((NowWeatherViewHolder) holder).bind(mWeatherData);
                break;
            case TYPE_TWO:
                ((HoursWeatherViewHolder) holder).bind(mWeatherData);
                break;
            case TYPE_THREE:
                ((SuggestionViewHolder) holder).bind(mWeatherData);
                break;
            case TYPE_FOUR:
                ((ForecastViewHolder) holder).bind(mWeatherData);
                break;
            default:
                break;
        }
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

        protected void bind(Weather weather) {
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
     * 当日小时预告
     */
    private class HoursWeatherViewHolder extends BaseViewHolder<Weather> {
        private LinearLayout itemHourInfoLayout;
        private TextView[] mClock = new TextView[mWeatherData.hourlyForecast.size()];
        private TextView[] mTemp = new TextView[mWeatherData.hourlyForecast.size()];
        private TextView[] mHumidity = new TextView[mWeatherData.hourlyForecast.size()];
        private TextView[] mWind = new TextView[mWeatherData.hourlyForecast.size()];


        HoursWeatherViewHolder(View itemView) {
            super(itemView);

            itemHourInfoLayout = (LinearLayout) itemView.findViewById(R.id.item_hour_info_linearlayout);

            for (int i = 0; i < mWeatherData.hourlyForecast.size(); i++) {
                View view = View.inflate(mContext, R.layout.item_hour_info_line, null);
                mClock[i] = (TextView) view.findViewById(R.id.one_clock);
                mTemp[i] = (TextView) view.findViewById(R.id.one_temp);
                mHumidity[i] = (TextView) view.findViewById(R.id.one_humidity);
                mWind[i] = (TextView) view.findViewById(R.id.one_wind);
                itemHourInfoLayout.addView(view);
            }
        }

        protected void bind(Weather weather) {

            try {
                for (int i = 0; i < weather.hourlyForecast.size(); i++) {

                    //s.subString(s.length-3,s.length);
                    //第一个参数是开始截取的位置，第二个是结束位置。
                    String mDate = weather.hourlyForecast.get(i).date;
                    mClock[i].setText(
                            mDate.substring(mDate.length() - 5, mDate.length()));
                    mTemp[i].setText(
                            String.format("%s℃", weather.hourlyForecast.get(i).tmp));
                    mHumidity[i].setText(
                            String.format("%s%%", weather.hourlyForecast.get(i).hum)
                    );
                    mWind[i].setText(
                            String.format("%sKm/h", weather.hourlyForecast.get(i).spd)
                    );
                }
            } catch (Exception e) {
                PLog.e(e.toString());
            }
        }
    }

    /**
     * 当日建议
     */
    class SuggestionViewHolder extends BaseViewHolder<Weather> {
        @Bind(R.id.cloth_brief)
        TextView clothBrief;
        @Bind(R.id.cloth_txt)
        TextView clothTxt;
        @Bind(R.id.sport_brief)
        TextView sportBrief;
        @Bind(R.id.sport_txt)
        TextView sportTxt;
        @Bind(R.id.travel_brief)
        TextView travelBrief;
        @Bind(R.id.travel_txt)
        TextView travelTxt;
        @Bind(R.id.flu_brief)
        TextView fluBrief;
        @Bind(R.id.flu_txt)
        TextView fluTxt;

        SuggestionViewHolder(View itemView) {
            super(itemView);
        }

        protected void bind(Weather weather) {
            try {
                clothBrief.setText(String.format("穿衣指数---%s", weather.lifestyle.get(0).brf));
                clothTxt.setText(weather.lifestyle.get(0).txt);

                sportBrief.setText(String.format("运动指数---%s", weather.lifestyle.get(1).brf));
                sportTxt.setText(weather.lifestyle.get(1).txt);

                travelBrief.setText(String.format("旅游指数---%s", weather.lifestyle.get(2).brf));
                travelTxt.setText(weather.lifestyle.get(2).txt);

                fluBrief.setText(String.format("感冒指数---%s", weather.lifestyle.get(3).brf));
                fluTxt.setText(weather.lifestyle.get(3).txt);
            } catch (Exception e) {
                PLog.e(e.toString());
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

        protected void bind(Weather weather) {
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

