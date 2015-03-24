/*
 *  COPYRIGHT NOTICE  
 *  Copyright (C) 2015, xyczero <xiayuncheng1991@gmail.com>
 *  
 *  	http://www.xyczero.com/
 *   
 *  @license under the Apache License, Version 2.0 
 *
 *  @file    CustomSwipeListView.java
 *  @brief   Custom Swipe ListView
 *  
 *  @version 1.0     
 *  @author  xyczero
 *  @date    2015/01/12    
 */

package com.xyczero.customswipelistview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * A view that shows items in a vertically scrolling list. The items come from
 * the {@link com.xyczero.customswipelistview.CustomSwipeBaseAdapter} associated with this view.
 *
 * @author xyczero
 */
public class CustomSwipeListView extends ListView {
    private static final String TAG = "com.xyczeo.customswipelistview";

    /**
     * Indicates the tag of the adapter's itemMainView.
     */
    public static final String ITEMMAIN_LAYOUT_TAG = "com.xyczeo.customswipelistview.itemmainlayout";

    /**
     * Indicates the tag of the adapter's swipeLeftView.
     */
    public static final String ITEMSWIPE_LAYOUT_TAG = "com.xyczeo.customswipelistview.swipeleftlayout";

    /**
     * The unit is dip per second.
     */
    private static final int MIN_VELOCITY = 500;

    private static final int MINIMUM_SWIPEITEM_TRIGGER_DELTAX = 5;

    /**
     * Touch mode of swipe.
     */
    private static final int TOUCH_SWIPE_RIGHT = 1;
    private static final int TOUCH_SWIPE_LEFT = 2;
    private static final int TOUCH_SWIPE_AUTO = 3;
    private static final int TOUCH_SWIPE_NONE = 4;

    /**
     * Current touch mode of swipe;
     */
    private int mCurTouchSwipeMode;

    /**
     * Rectangle used for hit testing children.
     */
    private Rect mTouchFrame;

    private Scroller mScroller;

    private int mScreenWidth;

    private int mTouchSlop;

    private VelocityTracker mVelocityTracker;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    /**
     * Control the animation execution time.
     */
    private final static int DEFAULT_DURATION = 250;
    private int mAnimationLeftDuration = DEFAULT_DURATION;
    private int mAnimationRightDuration = DEFAULT_DURATION;

    /**
     * The view that is shown in front of the listview by the position which the
     * finger point to currently; It indicates a general item view of the
     * listview;
     */
    private View mCurItemMainView;

    /**
     * The view that is currently hidden in behind of {@link #mCurItemMainView}
     * by the position which the finger point to currently .It indicates a view
     * which might been shown when in the mode of {@link #TOUCH_SWIPE_LEFT} ;
     */
    private View mCurItemSwipeView;

    /**
     * Same as {@link #mCurItemMainView} except that it was the last position
     * which the finger pointed to;
     */
    private View mLastItemMainView;

    /**
     * Same as {@link #mCurItemSwipeView} except that it was the last position
     * which the finger pointed to;
     */
    private View mLastItemSwipeView;

    /**
     * True if {@link #mLastItemSwipeView} is visible.
     */
    private boolean isItemSwipeViewVisible;

    /**
     * True if clicking the position of {@link #mCurItemSwipeView}. Indicates
     * whether the listview will intercept the distribution of the touch event;
     */
    private boolean isClickItemSwipeView;

    /**
     * True if triggering the swipe touch mode. Indicates whether trigger the
     * swipe touch mode.
     */
    private boolean isSwiping;

    /**
     * Used to record the accumulation in the direction of X before determining
     * whether perform the swipe action.
     */
    private float mAccumAbsDeltaX;

    /**
     * Used to record the accumulation in the direction of Y before determining
     * whether perform the swipe action
     */
    private float mAccumAbsDeltaY;

    /**
     * Used to track the position that is pointed to.
     */
    private int mCurSelectedPosition;

    /**
     * Used to track the position that was pointed to.
     */
    private int mLastSelectedPosition;

    /**
     * Used to track the X coordinate when the first finger down to.
     */
    private float mDownMotionX;

    /**
     * Used to track the Y coordinate when the first finger down to.
     */
    private float mDownMotionY;

    /**
     * Control whether enable the {@link #isSwiping}.
     */
    private boolean mEnableJudgeSwiping;

    /**
     * Control whether enable the {@link #TOUCH_SWIPE_RIGHT}.
     */
    private boolean mEnableSwipeItemRight = true;

    /**
     * Control whether enable the {@link #TOUCH_SWIPE_LEFT}.
     */
    private boolean mEnableSwipeItemLeft = true;

    /**
     * the minimum delta in x coordinate that whether triggers the
     * {@link #TOUCH_SWIPE_LEFT}.
     */
    private int mSwipeItemLeftTriggerDeltaX;

    /**
     * the minimum delta in x coordinate that whether triggers the
     * {@link #TOUCH_SWIPE_RIGHT}.
     */
    private int mSwipeItemRightTriggerDeltaX;

    /**
     * The listener that receives notifications when an item is removed in
     * {@link #TOUCH_SWIPE_RIGHT}.
     */
    private RemoveItemCustomSwipeListener mRemoveItemCustomSwipeListener;

    public CustomSwipeListView(Context context) {
        super(context);
        initCustomSwipeListView();
    }

    public CustomSwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCustomSwipeListView();
    }

    public CustomSwipeListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initCustomSwipeListView();
    }

    private void initCustomSwipeListView() {
        final Context context = getContext();
        final ViewConfiguration configuration = ViewConfiguration.get(context);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        // set minimum velocity according to the MIN_VELOCITY.
        mMinimumVelocity = CustomSwipeUtils
                .convertDptoPx(context, MIN_VELOCITY);
        mScreenWidth = CustomSwipeUtils.getScreenWidth(context);
        mScroller = new Scroller(context);
        initSwipeItemTriggerDeltaX();

        // set default value.
        mCurTouchSwipeMode = TOUCH_SWIPE_NONE;
        mCurSelectedPosition = INVALID_POSITION;
    }

    private void initSwipeItemTriggerDeltaX() {
        mSwipeItemLeftTriggerDeltaX = mScreenWidth / 3;
        mSwipeItemRightTriggerDeltaX = -mScreenWidth / 3;
    }

    private int getItemSwipeViewWidth(View itemSwipeView) {
        if (itemSwipeView != null)
            return mCurItemSwipeView.getLayoutParams().width;
        else
            return Integer.MAX_VALUE;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // just response single finger action.
        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownMotionX = ev.getX();
                mDownMotionY = ev.getY();
                mCurSelectedPosition = INVALID_POSITION;
                mCurSelectedPosition = pointToPosition((int) mDownMotionX,
                        (int) mDownMotionY);
                Log.d(TAG, "selectedPosition:" + mCurSelectedPosition);

                if (mCurSelectedPosition != INVALID_POSITION) {
                    mCurItemMainView = getChildAt(
                            mCurSelectedPosition - getFirstVisiblePosition())
                            .findViewWithTag(ITEMMAIN_LAYOUT_TAG);
                    mCurItemSwipeView = getChildAt(
                            mCurSelectedPosition - getFirstVisiblePosition())
                            .findViewWithTag(ITEMSWIPE_LAYOUT_TAG);
                    isClickItemSwipeView = isInSwipePosition((int) mDownMotionX,
                            (int) mDownMotionY);
                }

                Log.d(TAG, "onInterceptTouchEvent:ACTION_DOWN" + "--"
                        + isClickItemSwipeView);
                break;
            case MotionEvent.ACTION_UP:
                // clear data and give initial value
                if (isClickItemSwipeView) {
                    mCurItemSwipeView.setVisibility(GONE);
                    mCurItemMainView.scrollTo(0, 0);
                    mLastItemMainView = null;
                    mLastItemSwipeView = null;
                    isItemSwipeViewVisible = false;
                }
                recycleVelocityTracker();
                Log.d(TAG, "onInterceptTouchEvent:ACTION_UP" + "--"
                        + isClickItemSwipeView);
                break;
            case MotionEvent.ACTION_CANCEL:
                recycleVelocityTracker();
                Log.d(TAG, "onInterceptTouchEvent:ACTION_CANCEL" + "--"
                        + isClickItemSwipeView);
                break;
            default:
                return false;
        }
        // Return true and don't intercept the touch event if clicking the
        // itemswipeview.
        return !isClickItemSwipeView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Just response single finger action.
        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        final int x = (int) ev.getX();

        if (action == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
            ev.setAction(MotionEvent.ACTION_CANCEL);
            return super.onTouchEvent(ev);
        }

        //if the next action_move is coming after the second action_down
        //when the ItemSwipeView is still swiping with the first action,
        //it will not allow the following actions until the ItemSwipeView has finished the swiping.
        if (mEnableJudgeSwiping && isSwiping) {
            ev.setAction(MotionEvent.ACTION_CANCEL);
            return super.onTouchEvent(ev);
        }

        if (mCurSelectedPosition != INVALID_POSITION) {
            addVelocityTrackerMotionEvent(ev);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "onTouchEvent:ACTION_DOWN");
                    // If there is a itemswipeview and then don't click it
                    // by the next down action,it will first return to original
                    // state and cancel to response the following actions.
                    if (isItemSwipeViewVisible) {
                        if (!isClickItemSwipeView) {
                            mLastItemSwipeView.setVisibility(GONE);
                            mLastItemMainView.scrollTo(0, 0);
                        }
                        isItemSwipeViewVisible = false;
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        return super.onTouchEvent(ev);
                    }
                    mEnableJudgeSwiping = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "onTouchEvent:ACTION_MOVE");
                    mVelocityTracker.getYVelocity();
                    // This is a remedial action in case of the finger clicking down
                    // quickly again after TOUCH_SWIPE_LEFT.
                    // At that moment the mScroller may not finish so
                    // isItemSwipeViewVisible is still false when the finger clicks
                    // down.
                    if (isItemSwipeViewVisible) {
                        if (!isClickItemSwipeView) {
                            mLastItemSwipeView.setVisibility(GONE);
                            mLastItemMainView.scrollTo(0, 0);
                        }
                        isItemSwipeViewVisible = false;
                    }

                    // determine whether perform the swipe action for the whole touch event.
                    if (mEnableJudgeSwiping) {
                        mAccumAbsDeltaX = mAccumAbsDeltaX
                                + Math.abs(ev.getX() - mDownMotionX);
                        mAccumAbsDeltaY = mAccumAbsDeltaY
                                + Math.abs(ev.getY() - mDownMotionY);
                        if (mAccumAbsDeltaY >= mTouchSlop) {
                            isSwiping = false;
                            mEnableJudgeSwiping = false;
                        } else if (mAccumAbsDeltaX >= mTouchSlop) {
                            isSwiping = true;
                            mEnableJudgeSwiping = false;
                        }
                    } else {
                        mAccumAbsDeltaX = 0;
                        mAccumAbsDeltaY = 0;
                    }

                    if (isSwiping) {
                        int deltaX = (int) mDownMotionX - x;
                        if (deltaX > 0 && mEnableSwipeItemLeft || deltaX < 0
                                && mEnableSwipeItemRight) {
                            mDownMotionX = x;
                            mCurItemMainView.scrollBy(deltaX, 0);
                        }
                        // if super.onTouchEvent() that been called there,it might
                        // lead to the specified item out of focus due to
                        // it might call itemClick function in the sliding.
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "onTouchEvent:ACTION_UP");
                    if (isSwiping) {
                        // Record the old view and position
                        mLastItemMainView = mCurItemMainView;
                        mLastItemSwipeView = mCurItemSwipeView;
                        mLastSelectedPosition = mCurSelectedPosition;

                        final int velocityX = getScrollXVelocity();
                        if (velocityX > mMinimumVelocity) {
                            scrollByTouchSwipeMode(TOUCH_SWIPE_RIGHT, -mScreenWidth);
                        } else if (velocityX < -mMinimumVelocity) {
                            scrollByTouchSwipeMode(TOUCH_SWIPE_LEFT,
                                    getItemSwipeViewWidth(mLastItemSwipeView));
                        } else {
                            scrollByTouchSwipeMode(TOUCH_SWIPE_AUTO,
                                    Integer.MIN_VALUE);
                        }

                        recycleVelocityTracker();
                        // TODO:To be optimized for not calling computeScroll
                        // function.
                        if (mScroller.isFinished()) {
                            isSwiping = false;
                        }

                        // prevent to trigger OnItemClick by transverse sliding
                        // distance too slow or too small OnItemClick events when in
                        // swipe mode.
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        return super.onTouchEvent(ev);
                    }
                    break;
                default:
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (isSwiping && mLastSelectedPosition != INVALID_POSITION) {
            if (mScroller.computeScrollOffset()) {
                mLastItemMainView.scrollTo(mScroller.getCurrX(),
                        mScroller.getCurrY());
                postInvalidate();

                //when triggering the action_down,AbListview will call the abortAnimation function.
                //Then the mFinished will be true;
                //Other one, the computeScroll is callback by onDraw().
                if (mScroller.isFinished()) {
                    isSwiping = false;
                    switch (mCurTouchSwipeMode) {
                        case TOUCH_SWIPE_LEFT:
                            // show itemswipeview
                            mLastItemSwipeView.setVisibility(VISIBLE);
                            isItemSwipeViewVisible = true;
                            break;
                        case TOUCH_SWIPE_RIGHT:
                            if (mRemoveItemCustomSwipeListener == null) {
                                throw new NullPointerException(
                                        "RemoveItemCustomSwipeListener is null, we should called setRemoveItemCustomSwipeListener()");
                            }
                            // Callback
                            mRemoveItemCustomSwipeListener
                                    .onRemoveItemListener(mLastSelectedPosition);

                            // Before the view in the selected position is
                            // deleted,it needs to return to original state because
                            // the next position will be setted in this position.
                            mLastItemMainView.scrollTo(0, 0);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        super.computeScroll();
    }

    /**
     * True if clicking in the itemswipeview position.
     *
     * @param x the x coordinate which gets in the down action
     * @param y the y coordinate which gets in the down action
     * @return
     */
    private boolean isInSwipePosition(int x, int y) {
        Rect frame = mTouchFrame;
        if (frame == null) {
            mTouchFrame = new Rect();
            frame = mTouchFrame;
        }
        // The premise is that the itemswipeview is visible.
        if (isItemSwipeViewVisible) {
            frame.set(
                    mLastItemSwipeView.getLeft(),
                    getChildAt(mLastSelectedPosition - getFirstVisiblePosition())
                            .getTop(),
                    mLastItemSwipeView.getRight(),
                    getChildAt(mLastSelectedPosition - getFirstVisiblePosition())
                            .getBottom());
            if (frame.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    private void addVelocityTrackerMotionEvent(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * Get the velocity in the direction of x coordinate per second.
     *
     * @return
     */
    private int getScrollXVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return velocity;
    }

    /**
     * @param touchSwipeMode the swipe mode{@link #mCurTouchSwipeMode}
     * @param targetDelta    The target delta in the direction of x coordinate that will be
     *                       sliding by ignoring the delta that has been sliding.
     */
    private void scrollByTouchSwipeMode(int touchSwipeMode, int targetDelta) {
        mCurTouchSwipeMode = touchSwipeMode;
        switch (touchSwipeMode) {
            case TOUCH_SWIPE_RIGHT:
                scrollByTartgetDelta(targetDelta, mAnimationRightDuration);
            case TOUCH_SWIPE_LEFT:
                scrollByTartgetDelta(targetDelta, mAnimationLeftDuration);
                break;
            case TOUCH_SWIPE_AUTO:
                scrollByAuto();
                break;
            default:
                break;
        }
    }

    /**
     * Calculate the actual delta in the direction of x coordinate by taking the
     * delta that has been sliding into consideration.
     *
     * @param targetDelta       The target delta in the direction of x coordinate that will be
     *                          sliding by ignoring the delta that has been sliding.
     * @param animationDuration Animation execution time.
     */
    private void scrollByTartgetDelta(final int targetDelta,
                                      int animationDuration) {
        final int itemMainScrollX = mLastItemMainView.getScrollX();
        final int actualDelta = (targetDelta - itemMainScrollX);
        mScroller.startScroll(itemMainScrollX, 0, actualDelta, 0,
                animationDuration);
        postInvalidate();
    }

    /**
     * Determine whether meet the trigger condition according to the delta that
     * has been sliding when the x velocity doesn't meet the trigger condition.
     */
    private void scrollByAuto() {
        final int itemMainScrollX = mLastItemMainView.getScrollX();
        if (itemMainScrollX >= mSwipeItemLeftTriggerDeltaX) {
            scrollByTouchSwipeMode(TOUCH_SWIPE_LEFT,
                    getItemSwipeViewWidth(mLastItemSwipeView));
        } else if (itemMainScrollX <= mSwipeItemRightTriggerDeltaX) {
            scrollByTouchSwipeMode(TOUCH_SWIPE_RIGHT, -mScreenWidth);
        } else {
            // Return to original state due to not meet the conditions.
            // TODO:To be optimized for not calling computeScroll function.
            mLastItemMainView.scrollTo(0, 0);
            mLastItemSwipeView.setVisibility(GONE);
            isItemSwipeViewVisible = false;
        }
    }

    /**
     * set the animation time in swiping left
     *
     * @param duration millisecond
     */
    public void setAnimationLeftDuration(int duration) {
        mAnimationRightDuration = duration;
    }

    /**
     * set the animation time in swiping right
     *
     * @param duration millisecond
     */
    public void setAnimationRightDuration(int duration) {
        mAnimationLeftDuration = duration;
    }

    public void setSwipeItemLeftEnable(boolean enable) {
        mEnableSwipeItemLeft = enable;
    }

    public void setSwipeItemRightEnable(boolean enable) {
        mEnableSwipeItemRight = enable;
    }

    public void setSwipeItemRightTriggerDeltaX(int dipDeltaX) {
        if (dipDeltaX < MINIMUM_SWIPEITEM_TRIGGER_DELTAX)
            return;
        final int pxDeltaX = CustomSwipeUtils.convertDptoPx(getContext(),
                dipDeltaX);
        setSwipeItemTriggerDeltaX(TOUCH_SWIPE_RIGHT, pxDeltaX);
    }

    public void setSwipeItemLeftTriggerDeltaX(int dipDeltaX) {
        if (dipDeltaX < MINIMUM_SWIPEITEM_TRIGGER_DELTAX)
            return;
        final int pxDeltaX = CustomSwipeUtils.convertDptoPx(getContext(),
                dipDeltaX);
        setSwipeItemTriggerDeltaX(TOUCH_SWIPE_LEFT, pxDeltaX);
    }

    private void setSwipeItemTriggerDeltaX(int touchMode, int pxDeltaX) {
        switch (touchMode) {
            case TOUCH_SWIPE_RIGHT:
                mSwipeItemRightTriggerDeltaX = pxDeltaX <= mScreenWidth ? -pxDeltaX
                        : -mScreenWidth;
                break;
            case TOUCH_SWIPE_LEFT:
                mSwipeItemLeftTriggerDeltaX = pxDeltaX <= mScreenWidth ? pxDeltaX
                        : mScreenWidth;
                break;
            default:
                break;
        }
    }

    /**
     * Register a callback to be invoked when an item in this Listview has been
     * removed in {@link #TOUCH_SWIPE_RIGHT}.
     *
     * @param removeItemCustomSwipeListener
     */
    public void setRemoveItemCustomSwipeListener(
            RemoveItemCustomSwipeListener removeItemCustomSwipeListener) {
        mRemoveItemCustomSwipeListener = removeItemCustomSwipeListener;
    }

    /**
     * Interface definition for a callback to be invoked when an item in this
     * Listview has been removed in {@link #TOUCH_SWIPE_RIGHT}.
     */
    public interface RemoveItemCustomSwipeListener {

        /**
         * Callback method to be invoked when an item in this Listview has been
         * removed.
         *
         * @param selectedPostion the position which has been removed.
         */
        void onRemoveItemListener(int selectedPostion);
    }
}
