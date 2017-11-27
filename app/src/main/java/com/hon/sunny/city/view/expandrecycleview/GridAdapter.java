package com.hon.sunny.city.view.expandrecycleview;

import android.view.ViewGroup;

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
        GridBean bean1=new GridBean("北京");
        GridBean bean2=new GridBean("上海");
        GridBean bean3=new GridBean("广州");
        GridBean bean4=new GridBean("深圳");
        add(bean1);
        add(bean2);
        add(bean3);
        add(bean4);
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
