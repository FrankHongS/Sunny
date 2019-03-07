package com.hon.sunny.ui.city.view.expandrecycleview;

import android.view.ViewGroup;

import com.hon.sunny.R;
import com.hon.sunny.Sunny;

import zlc.season.practicalrecyclerview.AbstractAdapter;
import zlc.season.practicalrecyclerview.AbstractViewHolder;
import zlc.season.practicalrecyclerview.ItemType;

/**
 * Created by Frank on 2017/11/19.
 * E-mail:frank_hon@foxmail.com
 */

public class GridAdapter extends AbstractAdapter<ItemType,AbstractViewHolder>{

    private OnCityHintItemClickListener mOnCityHintItemClickListener;

    public GridAdapter(){
        String[] cityList= Sunny.getAppContext().getResources().getStringArray(R.array.city_hint);
        for(String city:cityList){
            GridBean bean=new GridBean(city);
            add(bean);
        }
    }

    @Override
    protected AbstractViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new GridViewHolder(parent,mOnCityHintItemClickListener);
    }

    @Override
    protected void onNewBindViewHolder(AbstractViewHolder holder, int position) {
        ((GridViewHolder)holder).setData((GridBean) get(position));
    }

    public void setOnCityHintItemClickListener(OnCityHintItemClickListener listener){
        this.mOnCityHintItemClickListener=listener;
    }

    public interface OnCityHintItemClickListener{
        void onCityHintItemClick(String cityName);
    }
}
