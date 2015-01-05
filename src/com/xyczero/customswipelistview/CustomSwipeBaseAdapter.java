package com.xyczero.customswipelistview;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

public abstract class CustomSwipeBaseAdapter extends BaseAdapter {
    private Context mContext;

    public CustomSwipeBaseAdapter(Context context) {
        mContext = context;
    }

    public abstract void bindItemView(View parentView, Context context,
            int position);

    public abstract void bindSwipeLeftView(View parentView, Context context,
            int position);

    private void bindView(View parentView, Context context, int position) {
        bindItemView(parentView, context, position);
        bindSwipeLeftView(parentView, context, position);
    }

    public abstract View newItemView(Context context, int position,
            ViewGroup parent);

    public abstract View newSwipeView(Context context, int position,
            ViewGroup parent);

    private View newView(Context context, int position, ViewGroup parent) {
        View itemView = newItemView(context, position, parent);
        if (itemView == null)
            throw new IllegalStateException("the itemView can't be null!");

        RelativeLayout itemLayout = new RelativeLayout(context);
        itemLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                itemView.getLayoutParams().height));
        itemLayout.addView(itemView);

        View swipeView = newSwipeView(context, position, parent);
        if (swipeView == null)
            return itemLayout;

        swipeView.setTag(CustomSwipeListView.ITEMSWIPE_LAYOUT_TAG);
        swipeView.setVisibility(View.GONE);

        RelativeLayout itemSwipeLayout = new RelativeLayout(context);
        itemSwipeLayout.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, itemView.getLayoutParams().height));
        itemSwipeLayout.setHorizontalGravity(Gravity.END);
        itemSwipeLayout.setBackgroundColor(mContext.getResources().getColor(
                android.R.color.transparent));
        itemSwipeLayout.addView(swipeView);
        itemLayout.addView(itemSwipeLayout);

        return itemLayout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = newView(mContext, position, parent);
        } else {
            v = convertView;
        }
        bindView(v, mContext, position);
        return v;
    }
}
