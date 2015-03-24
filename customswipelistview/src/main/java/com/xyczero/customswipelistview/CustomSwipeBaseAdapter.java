/*
 *  COPYRIGHT NOTICE  
 *  Copyright (C) 2015, xyczero <xiayuncheng1991@gmail.com>
 *  
 *  	http://www.xyczero.com/
 *   
 *  @license under the Apache License, Version 2.0 
 *
 *  @file    CustomSwipeBaseAdapter.java
 *  @brief   Custom Swipe Abstract Adapter
 *  
 *  @version 1.0     
 *  @author  xyczero
 *  @date    2015/01/12    
 */

package com.xyczero.customswipelistview;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Extended {@link android.widget.BaseAdapter} that for an {@link android.widget.Adapter} that can be used in
 * both {@link CustomSwipeListView} (by implementing the specialized
 * {@link OnUndoActionListener} interface.
 *
 * @param <T> The data type which is to be shown.
 * @author xyczero
 */
public abstract class CustomSwipeBaseAdapter<T> extends BaseAdapter implements
        OnUndoActionListener {

    /**
     * Default value represents the invalid position.
     */
    private static final int INVALID_POSITION = -1;

    protected Context mContext;

    /**
     * Desgined to store the data which has been deleted. Can been used to
     * Restore the data when executing undo action.
     */
    private T mObjectDeleted;

    /**
     * Contains the list of objects that represent the data of the adapter which
     * is extended the CustomSwipeBaseAdapter.
     */
    private List<T> mObjects;

    /**
     * Records the position that the object has been deleted.
     */
    private int mHasDeletedPosition;

    /**
     * Indicates the undo action has been exectued when is true,otherwise is
     * false.
     */
    private boolean undoAnimationEnable;

    /**
     * Constructor
     *
     * @param context The current context.
     */
    public CustomSwipeBaseAdapter(Context context) {
        mContext = context;
        mObjects = new ArrayList<T>();
    }

    /**
     * remove the specified object by the position.
     *
     * @param position
     */
    public T removeItemByPosition(int position) {
        if (position < mObjects.size() && position != INVALID_POSITION) {
            mObjectDeleted = mObjects.remove(position);
            mHasDeletedPosition = position;
            notifyDataSetChanged();
            return mObjectDeleted;
        } else {
            throw new IndexOutOfBoundsException("The position is invalid!");
        }
    }

    /**
     * Adds the specified object at the end of {@link #mObjects} and init the
     * {@link #mHasDeletedPosition} for {@link #INVALID_POSITION}
     *
     * @param object
     */
    public void addAdapterData(T object) {
        if (object != null) {
            mObjects.add(object);
            mHasDeletedPosition = INVALID_POSITION;
        }
        notifyDataSetChanged();
    }

    /**
     * Adds the specified list to {@link #mObjects} and init the
     * {@link #mHasDeletedPosition} for {@link #INVALID_POSITION}
     *
     * @param objects
     */
    public void setAdapterData(List<T> objects) {
        if (objects != null) {
            mObjects = objects;
            mHasDeletedPosition = INVALID_POSITION;
        }
        notifyDataSetChanged();
    }

    /**
     * set the animation {@link anim} to the specified view by
     * {@link #mHasDeletedPosition} when the specified view exectuing the undo
     * action.
     *
     * @param itemView
     * @param undoPosition
     */
    private void setItemUndoActionAnimation(View itemView, int undoPosition) {
        if (undoPosition == mHasDeletedPosition && undoAnimationEnable) {
            itemView.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.undodialog_push_right_in));
            clearDeletedObject();
        } else {
            itemView.clearAnimation();
        }
    }

    protected List<T> getAdapterData() {
        return mObjects;
    }

    private void clearDeletedObject() {
        mObjectDeleted = null;
        undoAnimationEnable = false;
        mHasDeletedPosition = INVALID_POSITION;
    }

    /**
     * Bind an existing item view to the data pointed to by position.
     *
     * @param view     Existing item view, returned earlier by {@link #newItemView}
     * @param context  Interface to application's global information
     * @param position The position from which to get the data.
     */
    protected abstract void bindItemView(View view, Context context,
                                         int position);

    /**
     * Bind an existing swipe left view to the data pointed to by position.
     * SwipeLeftView is indicating the menu that will been shown when scroll
     * left the item view.
     *
     * @param view     Existing item view, returned earlier by {@link #newItemView}
     * @param context  Interface to application's global information
     * @param position The position from which to get the data.
     */
    protected abstract void bindSwipeLeftView(View view, Context context,
                                              int position);

    private void bindView(View view, Context context, int position) {
        bindItemView(view, context, position);
        bindSwipeLeftView(view, context, position);
    }

    /**
     * Makes a new item view to hold the data pointed to by position.
     *
     * @param context  Interface to application's global information
     * @param position The position from which to get the data.
     * @param parent   The View to which the new view is attached to
     */
    protected abstract View newItemView(Context context, int position,
                                        ViewGroup parent);

    /**
     * Makes a new swipe left view hold the data pointed to by position.
     * SwipeLeftView is indicating the menu that will been shown when scroll
     * left the item view.
     *
     * @param context  Interface to application's global information
     * @param position The position from which to get the data.
     * @param parent   The View to which the new view is attached to
     */
    protected abstract View newSwipeLeftView(Context context, int position,
                                             ViewGroup parent);

    /**
     * Makes a new view that combines itemView and swipeLeftView to hold the
     * data pointed to by position.
     *
     * @param context  Interface to application's global information
     * @param position The position from which to get the data.
     * @param parent   The View to which the new view is attached to
     * @return
     */
    private View newView(Context context, int position, ViewGroup parent) {
        // get a new itemView and init it.
        View itemView = newItemView(context, position, parent);
        if (itemView == null)
            throw new IllegalStateException("the itemView can't be null!");

        // set a tag for the itemMainView which is used in CustomSwipeListView.
        itemView.setTag(CustomSwipeListView.ITEMMAIN_LAYOUT_TAG);
        // itemLayout indicates the outermost layout of the new view.
        RelativeLayout itemLayout = new RelativeLayout(context);
        itemLayout.setLayoutParams(new AbsListView.LayoutParams(
                itemView.getLayoutParams().width, itemView.getLayoutParams().height));
        itemLayout.addView(itemView);

        // get a new swipeLeftView and init it.
        View swipeLeftView = newSwipeLeftView(context, position, parent);
        if (swipeLeftView == null)
            return itemLayout;

        // set a tag for the swipeLeftView which is used in CustomSwipeListView.
        swipeLeftView.setTag(CustomSwipeListView.ITEMSWIPE_LAYOUT_TAG);
        swipeLeftView.setVisibility(View.GONE);

        // itemSwipeLeftLayout indicates the layout of the swipeLeftView.
        RelativeLayout itemSwipeLeftLayout = new RelativeLayout(context);
        itemSwipeLeftLayout.setLayoutParams(new AbsListView.LayoutParams(
                itemView.getLayoutParams().width, itemView.getLayoutParams().height));
        itemSwipeLeftLayout.setHorizontalGravity(Gravity.END);
        itemSwipeLeftLayout.setBackgroundColor(mContext.getResources()
                .getColor(android.R.color.transparent));
        itemSwipeLeftLayout.addView(swipeLeftView);
        itemLayout.addView(itemSwipeLeftLayout);
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
        // must ensure that the height of the CustemListview is a certain
        // value(like wrap_content is forbidden),otherwise the undo animation
        // will appear unexpectedly.
        setItemUndoActionAnimation(v, position);
        return v;
    }

    /**
     * Implments the {@link #noExecuteUndoAction} in
     * {@link OnUndoActionListener}.
     */
    @Override
    public void noExecuteUndoAction() {
        if (!undoAnimationEnable) {
            clearDeletedObject();
        }
    }

    /**
     * Implments the {@link #executeUndoAction} in {@link OnUndoActionListener}.
     */
    @Override
    public void executeUndoAction() {
        if (mHasDeletedPosition <= mObjects.size()
                && mHasDeletedPosition != INVALID_POSITION) {
            mObjects.add(mHasDeletedPosition, mObjectDeleted);
            undoAnimationEnable = true;
            notifyDataSetChanged();
        }
    }
}
