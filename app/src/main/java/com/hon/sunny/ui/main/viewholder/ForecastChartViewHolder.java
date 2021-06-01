package com.hon.sunny.ui.main.viewholder;

import android.view.View;

import com.hon.simplechartview.ChartEntity;
import com.hon.simplechartview.ChartView;
import com.hon.sunny.R;
import com.hon.sunny.ui.base.BaseViewHolder;
import com.hon.sunny.utils.Util;
import com.hon.sunny.vo.bean.main.Weather;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Frank Hon on 2019/4/21 1:11 AM.
 * E-mail: frank_hon@foxmail.com
 */
public class ForecastChartViewHolder extends BaseViewHolder<Weather> {

    @BindView(R.id.chart_forecast)
    ChartView chartView;

    public ForecastChartViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(Weather weather) {

        List<ChartEntity> chartEntities = new ArrayList<>();

        List<String> labelList = new ArrayList<>();

        List<Float> maxDataList = new ArrayList<>();
        List<Float> minDataList = new ArrayList<>();
        for (Weather.DailyForecastEntity entity : weather.dailyForecast) {
            maxDataList.add(Float.valueOf(entity.max));
            minDataList.add(Float.valueOf(entity.min));
            try {
                labelList.add(Util.dayForWeek(entity.date));
            } catch (ParseException e) {
                labelList.add(entity.date);
            }
        }
        ChartEntity maxChartEntity = new ChartEntity(maxDataList, itemView.getResources().getColor(R.color.maxLineColor), itemView.getResources().getColor(R.color.chartDotColor));
        ChartEntity minChartEntity = new ChartEntity(minDataList, itemView.getResources().getColor(R.color.minLineColor), itemView.getResources().getColor(R.color.chartDotColor));

        chartEntities.add(maxChartEntity);
        chartEntities.add(minChartEntity);


        chartView.setChartData(chartEntities, labelList);
    }
}
