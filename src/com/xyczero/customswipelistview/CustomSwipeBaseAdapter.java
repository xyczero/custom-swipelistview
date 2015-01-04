package com.xyczero.customswipelistview;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
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
		RelativeLayout rL = new RelativeLayout(context);
		rL.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, itemView
				.getLayoutParams().height));

		rL.addView(itemView);

		View swipeView = newSwipeView(context, position, parent);
		if (swipeView == null)
			return rL;
		RelativeLayout lL = new RelativeLayout(context);
		lL.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, itemView
				.getLayoutParams().height));
		lL.setHorizontalGravity(Gravity.END);
		lL.setBackgroundColor(mContext.getResources().getColor(
				android.R.color.transparent));
		swipeView.setTag("com.xyc.customswipe.layout");
		swipeView.setVisibility(View.GONE);
		change(swipeView);
		lL.addView(swipeView);
		rL.addView(lL);
		return rL;
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

	public void change(View view) {
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View innerView = ((ViewGroup) view).getChildAt(i);
				change(innerView);
			}
		}
	}
}
