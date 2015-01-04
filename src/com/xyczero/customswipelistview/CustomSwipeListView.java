package com.xyczero.customswipelistview;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;
import android.widget.Scroller;

import com.xyczero.customlistview.R;

public class CustomSwipeListView extends ListView {
	private final String TAG = "com.xyczeo.CustomSwipeListView";

	private enum SwipeDirection {
		NONE, RIGHT, LEFT;
	}

	private final int MIN_VELOCITY = 500;// dips

	private Scroller mScroller;
	private SwipeDirection mSwipeDirection = SwipeDirection.NONE;

	private VelocityTracker mVelocityTracker;
	private int mMinimumVelocity;
	private int mMaximumVelocity;

	private View mItemMainView;

	private View mItemSwipeView;

	private Rect mTouchFrame;

	private boolean isClickItemSwipeView;

	private boolean isSwiping;

	private int mTouchSlop;

	private int mSelectedPosition = INVALID_POSITION;

	private float mDownMotionX;

	private float mDownMotionY;

	private int mScreenWidth;

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

	public void initCustomSwipeListView() {
		final Context context = getContext();
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		final float density = context.getResources().getDisplayMetrics().density;
		mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
		mMinimumVelocity = (int) (MIN_VELOCITY * density);
		mScroller = new Scroller(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			obtainVelocityTracker(ev);
			mDownMotionX = ev.getX();
			mDownMotionY = ev.getY();
			mSelectedPosition = INVALID_POSITION;
			mSelectedPosition = pointToPosition((int) mDownMotionX,
					(int) mDownMotionY);
			Log.d(TAG, "selectedPosition:" + mSelectedPosition);
			if (mSelectedPosition != INVALID_POSITION) {
				mItemMainView = getChildAt(
						mSelectedPosition - getFirstVisiblePosition())
						.findViewById(R.id.doc_layout);
				mItemSwipeView = getChildAt(
						mSelectedPosition - getFirstVisiblePosition())
						.findViewWithTag("com.xyc.customswipe.layout");
				if (isInSwipePosition((int) mDownMotionX, (int) mDownMotionY)) {
					isClickItemSwipeView = true;
				} else {
					isClickItemSwipeView = false;
				}
			}
			Log.d(TAG, "onInterceptTouchEvent:ACTION_DOWN" + "--"
					+ isClickItemSwipeView);
			break;
		case MotionEvent.ACTION_UP:
			if (mItemSwipeView.isShown() && isClickItemSwipeView) {
				mItemSwipeView.setVisibility(GONE);
				mItemMainView.scrollTo(0, 0);
			}
			recycleVelocityTracker();
		case MotionEvent.ACTION_CANCEL:
			recycleVelocityTracker();
			Log.d(TAG, "onInterceptTouchEvent:ACTION_UP OR ACTION_CANCEL"
					+ "--" + isClickItemSwipeView);
			break;
		default:
			return false;
		}
		return !isClickItemSwipeView;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
		final int x = (int) ev.getX();
		if (action == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
			return false;
		}
		if (mSelectedPosition != INVALID_POSITION) {
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				Log.d(TAG, "onTouchEvent:ACTION_DOWN");
				obtainVelocityTracker(ev);
				break;
			case MotionEvent.ACTION_MOVE:
				Log.d(TAG, "onTouchEvent:ACTION_MOVE");
				if (Math.abs(getScrollXVelocity()) > mMinimumVelocity
						|| (Math.abs(ev.getX() - mDownMotionX) > mTouchSlop && Math
								.abs(ev.getY() - mDownMotionY) < mTouchSlop)) {
					isSwiping = true;
				}
				if (isSwiping) {
					int deltaX = (int) mDownMotionX - x;
					mDownMotionX = x;
					mItemMainView.scrollBy(deltaX, 0);
				}
				break;
			case MotionEvent.ACTION_UP:
				Log.d(TAG, "onTouchEvent:ACTION_UP");
				if (isSwiping) {
					final int velocityX = getScrollXVelocity();
					if (velocityX > mMinimumVelocity) {
						// scrollBySwipeDirection(SwipeDirection.RIGHT);
					} else if (velocityX < -mMinimumVelocity) {
						scrollBySwipeDirection(SwipeDirection.LEFT);
					} else {
						scrollBySwipeDirection(SwipeDirection.NONE);
					}

					recycleVelocityTracker();
					if (mScroller.isFinished()) {
						isSwiping = false;
					}
				} else {
					// else if (Math.abs(ev.getX() - mDownMotionX) < 10
					// && Math.abs(ev.getY() - mDownMotionY) < 10
					// && mSelectedPosition != AdapterView.INVALID_POSITION) {
					// mListViewListener
					// .onPullListViewItemClickListener(slidePosition);
					// }
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

		if (isSwiping && mSelectedPosition != INVALID_POSITION) {
			if (mScroller.computeScrollOffset()) {
				// 让ListView item根据当前的滚动偏移量进行滚动
				mItemMainView.scrollTo(mScroller.getCurrX(),
						mScroller.getCurrY());

				postInvalidate();

				// 滚动动画结束的时候调用回调接口
				if (mScroller.isFinished()
						&& mSwipeDirection != SwipeDirection.RIGHT) {
					// if (mRemoveListener == null) {
					// throw new NullPointerException(
					// "RemoveListener is null, we should called setRemoveListener()");
					// }
					isSwiping = false;
					mItemSwipeView.setVisibility(VISIBLE);
					// itemView.scrollTo(0, 0);
					// mRemoveListener.removeItem(removeDirection,
					// slidePosition);
				}
			}
		}
		super.computeScroll();
	}

	private boolean isInSwipePosition(int x, int y) {
		Rect frame = mTouchFrame;
		if (frame == null) {
			mTouchFrame = new Rect();
			frame = mTouchFrame;
		}
		if (mItemSwipeView.getVisibility() == View.VISIBLE) {
			frame.set(mItemSwipeView.getLeft(), getChildAt(mSelectedPosition)
					.getTop(), mItemSwipeView.getRight(),
					getChildAt(mSelectedPosition).getBottom());
			if (frame.contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	private void obtainVelocityTracker(MotionEvent ev) {
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

	private int getScrollXVelocity() {
		mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
		int velocity = (int) mVelocityTracker.getXVelocity();
		return velocity;
	}

	private void scrollBySwipeDirection(SwipeDirection direction) {
		switch (direction) {
		case RIGHT:
			scrollToRight();
			break;
		case LEFT:
			scrollToLeft();
			break;
		case NONE:
			smoothScrollTo();
			break;
		default:
			break;
		}
	}

	private void scrollToRight() {
		final int delta = (mScreenWidth + mItemMainView.getScrollX());
		mScroller.startScroll(mItemMainView.getScrollX(), 0, -delta, 0,
				Math.abs(delta));
		postInvalidate(); // 刷新itemView
	}

	private void scrollToLeft() {
		final int delta = (mScreenWidth / 5 - mItemMainView.getScrollX());
		mScroller.startScroll(mItemMainView.getScrollX(), 0, delta, 0,
				Math.abs(delta));
		postInvalidate(); // 刷新itemView
	}

	private void smoothScrollTo() {
		// 如果向左滚动的距离大于屏幕的三分之一，就让其删除
		if (mItemMainView.getScrollX() >= mScreenWidth / 3) {
			scrollToLeft();
		} else if (mItemMainView.getScrollX() <= -mScreenWidth / 3) {
			scrollToRight();
		} else {
			// 滚回到原始位置,为了偷下懒这里是直接调用scrollTo滚动
			mItemMainView.scrollTo(0, 0);
			mItemSwipeView.setVisibility(GONE);
		}
	}
}
