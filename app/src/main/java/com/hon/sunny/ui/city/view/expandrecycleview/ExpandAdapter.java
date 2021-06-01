package com.hon.sunny.ui.city.view.expandrecycleview;

import android.view.ViewGroup;

import zlc.season.practicalrecyclerview.AbstractAdapter;
import zlc.season.practicalrecyclerview.AbstractViewHolder;
import zlc.season.practicalrecyclerview.ItemType;

/**
 * Created by Frank on 2017/9/4.
 * E-mail:frank_hon@foxmail.com
 */

public class ExpandAdapter extends AbstractAdapter<ItemType, AbstractViewHolder> {

    private OnItemClickListener mOnItemClickListener;

    @Override
    protected AbstractViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == RecyclerItemType.PARENT.getValue()) {
            return new ParentViewHolder(mOnItemClickListener, this, parent);
        } else if (viewType == RecyclerItemType.CHILD.getValue()) {
            return new ChildViewHolder(mOnItemClickListener, parent);
        }
        return null;
    }

    @Override
    protected void onNewBindViewHolder(AbstractViewHolder holder, int position) {
        if (holder instanceof ParentViewHolder) {
            ((ParentViewHolder) holder).setData((ParentBean) get(position));
        } else if (holder instanceof ChildViewHolder) {
            ((ChildViewHolder) holder).setData((ChildBean) get(position));
        }
    }

    @Override
    public void clear() {
        super.clear();
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(String text);
    }
}

