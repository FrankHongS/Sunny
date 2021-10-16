package com.hon.sunny.ui.main.viewholder;

import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hon.sunny.R;
import com.hon.sunny.ui.base.BaseViewHolder;
import com.hon.sunny.ui.main.adapter.ForecastAdapter;
import com.hon.sunny.vo.bean.main.ForecastEntity;
import com.hon.sunny.vo.bean.main.Weather;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Frank Hon on 2019/4/13 12:17 AM.
 * E-mail: frank_hon@foxmail.com
 * <p>
 * forecasts
 */
public class ForecastViewHolder extends BaseViewHolder<Weather> {

    private static final int FORECAST_DAY_COUNT = 3;
    @BindView(R.id.cv_forecast)
    CardView forecastCard;
    @BindView(R.id.rv_forecast)
    RecyclerView forecasts;
    private ForecastAdapter mForecastAdapter;
    private boolean isCollapsed = true;

    public ForecastViewHolder(View itemView) {
        super(itemView);

        mForecastAdapter = new ForecastAdapter();
        forecasts.setAdapter(mForecastAdapter);
        forecasts.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
    }

    @Override
    public void bind(Weather weather) {

        List<ForecastEntity> forecastList = new ArrayList<>();

        for (Weather.DailyForecastEntity entity : weather.dailyForecast) {
            ForecastEntity forecastEntity = new ForecastEntity(
                    weather.city,
                    entity.date,
                    entity.max,
                    entity.min,
                    entity.txtD,
                    entity.sc,
                    entity.dir,
                    entity.spd,
                    entity.pop
            );

            forecastList.add(forecastEntity);
        }
        List<ForecastEntity> trimmedForecastList = generateForecastList(forecastList);
        mForecastAdapter.swapDataList(trimmedForecastList);
        forecastCard.setOnClickListener(
                view -> {
                    if (forecastList.size() > FORECAST_DAY_COUNT) {
                        if (isCollapsed) {
                            isCollapsed = false;
                            mForecastAdapter.swapDataList(forecastList);
                        } else {
                            isCollapsed = true;
                            mForecastAdapter.swapDataList(trimmedForecastList);
                        }
                    }
                }
        );
    }

    private List<ForecastEntity> generateForecastList(List<ForecastEntity> forecastList) {
        int size = forecastList.size();
        return size > FORECAST_DAY_COUNT ? forecastList.subList(0, FORECAST_DAY_COUNT) : forecastList;
    }
}
