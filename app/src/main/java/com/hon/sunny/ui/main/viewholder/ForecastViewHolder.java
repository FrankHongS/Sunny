package com.hon.sunny.ui.main.viewholder;

import android.util.Log;
import android.view.View;

import com.hon.sunny.R;
import com.hon.sunny.base.BaseViewHolder;
import com.hon.sunny.data.main.bean.ForecastEntity;
import com.hon.sunny.data.main.bean.Weather;
import com.hon.sunny.ui.main.adapter.ForecastAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Bind;

/**
 * Created by Frank Hon on 2019/4/13 12:17 AM.
 * E-mail: frank_hon@foxmail.com
 *
 *  forecasts
 */
public class ForecastViewHolder extends BaseViewHolder<Weather> {

    @Bind(R.id.cv_forecast)
    CardView forecastCard;
    @Bind(R.id.rv_forecast)
    RecyclerView forecasts;

    private ForecastAdapter mForecastAdapter;

    private boolean isCollapsed=true;

    private static final int FORECAST_DAY_COUNT = 3;

    public ForecastViewHolder(View itemView) {
        super(itemView);

        mForecastAdapter=new ForecastAdapter();
        forecasts.setAdapter(mForecastAdapter);
        forecasts.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
    }

    @Override
    public void bind(Weather weather) {

        List<ForecastEntity> forecastList=new ArrayList<>();

        if(weather.dailyForecast!=null){
            for(Weather.DailyForecastEntity entity:weather.dailyForecast){
                ForecastEntity forecastEntity=new ForecastEntity(
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
        }
        List<ForecastEntity> trimmedForecastList=generateForecastList(forecastList);

        mForecastAdapter.swapDataList(trimmedForecastList);

        forecastCard.setOnClickListener(
                view->{
                    if(forecastList.size()>FORECAST_DAY_COUNT){
                        if(isCollapsed){
                            isCollapsed=false;
                            mForecastAdapter.swapDataList(forecastList);
                        }else {
                            isCollapsed=true;
                            mForecastAdapter.swapDataList(trimmedForecastList);
                        }
                    }
                }
        );
    }

    private List<ForecastEntity> generateForecastList(List<ForecastEntity> forecastList){
        int size=forecastList.size();
        return size>FORECAST_DAY_COUNT?forecastList.subList(0,FORECAST_DAY_COUNT):forecastList;
    }
}
