package com.hon.sunny.ui.city.view.expandrecycleview;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hon.sunny.R;

import java.util.List;

import zlc.season.practicalrecyclerview.AbstractAdapter;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

/**
 * Created by Frank on 2017/9/4.
 * E-mail:frank_hon@foxmail.com
 */

class ParentViewHolder extends AbstractViewHolder<ParentBean> implements View.OnClickListener {
    private TextView mText;
    private ImageView mImageView;

    private ParentBean parent;
    private List<ChildBean> child;

    private ExpandAdapter mAdapter;
    private ExpandAdapter.OnItemClickListener mOnItemClickListener;

    private String mZone;

    ParentViewHolder(ExpandAdapter.OnItemClickListener onItemClickListener, AbstractAdapter adapter, ViewGroup parent) {
        super(parent, R.layout.parent_item);
        mText = (TextView) itemView.findViewById(R.id.text);
        mText.setOnClickListener(this);
        mImageView = (ImageView) itemView.findViewById(R.id.image);
        mImageView.setOnClickListener(this);
        mAdapter = (ExpandAdapter) adapter;
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void setData(ParentBean data) {
        mText.setText(data.text);
        child = data.mChild;
        parent = data;
        mZone = data.zone;
        if (child == null || child.size() == 0) {
            mImageView.setVisibility(View.INVISIBLE);
        } else {
            mImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image:
                if (child != null && child.size() != 0 && View.VISIBLE == mImageView.getVisibility()) {
                    if (parent.isExpand) {
                        mAdapter.removeBack(getAdapterPosition(), child.size());
                        parent.isExpand = false;
                        mImageView.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    } else {
                        mAdapter.insertAllBack(getAdapterPosition(), child);
                        parent.isExpand = true;
                        mImageView.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    }
                }
                break;
            case R.id.text:
                if (mOnItemClickListener != null) {
                    String city;
                    if (mZone != null && !TextUtils.isEmpty(mZone)) {
                        city = mZone;
                    } else {
                        city = mText.getText().toString();
                    }
                    mOnItemClickListener.onItemClick(city);
                }
                break;
            default:
                break;
        }
    }
}

