package com.hon.sunny.city.view.expandrecycleview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hon.sunny.R;

import zlc.season.practicalrecyclerview.AbstractViewHolder;

/**
 * Created by Frank on 2017/9/4.
 * E-mail:frank_hon@foxmail.com
 */

class ChildViewHolder extends AbstractViewHolder<ChildBean> implements View.OnClickListener{

    private TextView mText;
    private Context mContext;

    private ExpandAdapter.OnItemClickListener mOnItemClickListener;

    ChildViewHolder(ExpandAdapter.OnItemClickListener onItemClickListener,ViewGroup parent) {
        super(parent, R.layout.child_item);
        mText=(TextView)itemView.findViewById(R.id.text);
        mText.setOnClickListener(this);
        mContext = parent.getContext();
        mOnItemClickListener=onItemClickListener;
    }

    @Override
    public void setData(ChildBean data) {
        mText.setText(String.valueOf(data.text));
    }

    @Override
    public void onClick(View view) {
        if(mOnItemClickListener!=null)
            mOnItemClickListener.onItemClick(mText.getText().toString());
    }
}
