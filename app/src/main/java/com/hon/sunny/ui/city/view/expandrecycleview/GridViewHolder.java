package com.hon.sunny.ui.city.view.expandrecycleview;

import android.view.ViewGroup;
import android.widget.Button;

import com.hon.sunny.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

/**
 * Created by Frank on 2017/11/19.
 * E-mail:frank_hon@foxmail.com
 */

public class GridViewHolder extends AbstractViewHolder<GridBean>{

    @Bind(R.id.btn_city_hint)
    Button cityHint;

    private GridAdapter.OnCityHintItemClickListener mOnCityHintItemClickListener;

    public GridViewHolder(ViewGroup parent, GridAdapter.OnCityHintItemClickListener listener){
        super(parent, R.layout.item_city_hint);
        ButterKnife.bind(this,itemView);

        this.mOnCityHintItemClickListener=listener;
    }

    @Override
    public void setData(GridBean data) {
        cityHint.setText(data.cityName);
    }

    @OnClick(R.id.btn_city_hint)
    public void onClick(){
        if(mOnCityHintItemClickListener!=null)
            mOnCityHintItemClickListener.onCityHintItemClick(cityHint.getText().toString());
    }
}
