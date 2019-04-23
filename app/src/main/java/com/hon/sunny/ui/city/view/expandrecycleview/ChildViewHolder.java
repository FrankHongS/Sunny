package com.hon.sunny.ui.city.view.expandrecycleview;

import android.view.ViewGroup;
import android.widget.TextView;

import com.hon.sunny.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

/**
 * Created by Frank on 2017/9/4.
 * E-mail:frank_hon@foxmail.com
 */

class ChildViewHolder extends AbstractViewHolder<ChildBean>{
    @Bind(R.id.text)
    TextView mText;

    private ExpandAdapter.OnItemClickListener mOnItemClickListener;

    ChildViewHolder(ExpandAdapter.OnItemClickListener onItemClickListener,ViewGroup parent) {
        super(parent, R.layout.child_item);
        ButterKnife.bind(this,itemView);
        mOnItemClickListener=onItemClickListener;
    }

    @Override
    public void setData(ChildBean data) {
        mText.setText(data.text);
    }

    @OnClick(R.id.text)
    public void onClick() {
        if(mOnItemClickListener!=null)
            mOnItemClickListener.onItemClick(mText.getText().toString());
    }
}
