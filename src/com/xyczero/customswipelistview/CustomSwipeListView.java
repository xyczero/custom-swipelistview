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
	private static final String TAG = "com.xyczeo.customswipelistview";
	public static final String ITEMSWIPE_LAYOUT_TAG = "com.xyczeo.customswipelistview.itemswipelayout";

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

	private View mOldItemMainView;

	private View mOldItemSwipeView;

	private Rect mTouchFrame;

	private boolean isItemSwipeViewVisible;

	private boolean isClickItemSwipeView;

	private boolean isSwiping;

	private int mTouchSlop;

	private int mSelectedPosition = INVALID_POSITION;

	private float mDownMotionX;

	private float mDownMotionY;

	private int mScreenWidth;

	private RemoveItemCustomSwipeListener mRemoveItemCustomSwipeListener;

	private boolean mEnableSiwpeItemRight = true;

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
		getLastVisiblePosition();
		getCount();
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
						.findViewWithTag(ITEMSWIPE_LAYOUT_TAG);
				isClickItemSwipeView = isInSwipePosition((int) mDownMotionX,
						(int) mDownMotionY);
			}

			Log.d(TAG, "onInterceptTouchEvent:ACTION_DOWN" + "--"
					+ isClickItemSwipeView);
			break;
		case MotionEvent.ACTION_UP:
			if (isClickItemSwipeView) {
				mItemSwipeView.setVisibility(GONE);
				mItemMainView.scrollTo(0, 0);
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
				if (isItemSwipeViewVisible) {
					if (!isClickItemSwipeView && mOldItemMainView != null
							&& mOldItemSwipeView != null) {
						mOldItemSwipeView.setVisibility(GONE);
						mOldItemMainView.scrollTo(0, 0);
					}
					isItemSwipeViewVisible = false;
					ev.setAction(MotionEvent.ACTION_CANCEL);
					return super.onTouchEvent(ev);
				}
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
						scrollBySwipeDirection(SwipeDirection.RIGHT);
					} else if (velocityX < -mMinimumVelocity) {
						scrollBySwipeDirection(SwipeDirection.LEFT);
					} else {
						scrollBySwipeDirection(SwipeDirection.NONE);
					}

					recycleVelocityTracker();
					if (mScroller.isFinished()) {
						isSwiping = false;
					}
					// 防止因横向滑动过慢或距离过小导致出发OnItemClick事件
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

		if (isSwiping && mSelectedPosition != INVALID_POSITION) {
			if (mScroller.computeScrollOffset()) {
				mItemMainView.scrollTo(mScroller.getCurrX(),
						mScroller.getCurrY());

				postInvalidate();
				if (mScroller.isFinished()) {
					switch (mSwipeDirection) {
					case LEFT:
						isSwiping = false;
						mItemSwipeView.setVisibility(VISIBLE);
						isItemSwipeViewVisible = true;
						mOldItemMainView = mItemMainView;
						mOldItemSwipeView = mItemSwipeView;
						break;
					case RIGHT:
						if (mRemoveItemCustomSwipeListener == null) {
							throw new NullPointerException(
									"RemoveItemCustomSwipeListener is null, we should called setRemoveItemCustomSwipeListener()");
						}
						mItemMainView.scrollTo(0, 0);
						mRemoveItemCustomSwipeListener
								.onRemoveItem(mSelectedPosition);
						break;
					default:
						break;
					}
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
		if (isItemSwipeViewVisible) {
			frame.set(mItemSwipeView.getLeft(),
					getChildAt(mSelectedPosition - getFirstVisiblePosition())
							.getTop(), mItemSwipeView.getRight(),
					getChildAt(mSelectedPosition - getFirstVisiblePosition())
							.getBottom());
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
		mSwipeDirection = direction;
		switch (direction) {
		case RIGHT:
			if (mEnableSiwpeItemRight)
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
			scrollBySwipeDirection(SwipeDirection.LEFT);
		} else if (mItemMainView.getScrollX() <= -mScreenWidth / 3) {
			scrollBySwipeDirection(SwipeDirection.RIGHT);
		} else {
			// 滚回到原始位置,为了偷下懒这里是直接调用scrollTo滚动
			mItemMainView.scrollTo(0, 0);
			mItemSwipeView.setVisibility(GONE);
			isItemSwipeViewVisible = false;
		}
	}

	public void setSiwpeItemRightEnable(boolean enable) {
		mEnableSiwpeItemRight = enable;
	}

	public void setRemoveItemCustomSwipeListener(
			RemoveItemCustomSwipeListener removeItemCustomSwipeListener) {
		mRemoveItemCustomSwipeListener = removeItemCustomSwipeListener;
	}

	public interface RemoveItemCustomSwipeListener {
		void onRemoveItem(int selectedPostion);
	}
}
