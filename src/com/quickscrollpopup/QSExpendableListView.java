package com.quickscrollpopup;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

public class QSExpendableListView extends ExpandableListView  {

	public interface OnQuickScrollListener{
		public void onDown();
		public void onScroll(MotionEvent e2, float distanceY);
		public void onFling(MotionEvent e2, float deltaVY);
		public void onScrollState(int scrollState, int firstVisiblePos);
	}
	private boolean isQuickScrollEnable = false;
	private QuickScrollPopup mQuickScrollPopup;
	private GestureDetector detector;
	
	public QSExpendableListView(Context context) {
		super(context);
	}

	public QSExpendableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public QSExpendableListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mQuickScrollPopup.setMesureDim(getMeasuredWidth(),getMeasuredHeight());
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		detector.onTouchEvent(ev);
		super.onTouchEvent(ev);
		return true;
	}
	
	public void setQuickScrollEnable(boolean enable){
		isQuickScrollEnable = enable;
		if(mQuickScrollPopup == null)
		mQuickScrollPopup = new QuickScrollPopup(getContext(), this);
		if(isQuickScrollEnable){
			setSmoothScrollbarEnabled(true);
		} else {
			setSmoothScrollbarEnabled(false);
		}
		detector = new GestureDetector(getContext(), new GestureListenerQS(mQuickScrollPopup));
		detector.setIsLongpressEnabled(false);
		setOnScrollListener(new OnScrollListener() {
			private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.e("Upendra", "OnScrollListener() scrollState:"+scrollState);
				mScrollState = scrollState;
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				mQuickScrollPopup.onScrollState(mScrollState, firstVisibleItem);
			}
		});
	}

	
	public boolean isQuickScrollEnable(){
		return isQuickScrollEnable;
	}
	
	
	private class GestureListenerQS implements OnGestureListener{
		private OnQuickScrollListener mQuickScrollListener;
		public GestureListenerQS(OnQuickScrollListener listener){
			mQuickScrollListener = listener;
		}
		
		@Override
		public boolean onDown(MotionEvent e) {
			mQuickScrollListener.onDown();
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			Log.e("Upendra", "onScroll() distanceY:"+distanceY);
			mQuickScrollListener.onScroll(e2,distanceY);
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.e("Upendra", "onFling() velocityY:"+velocityY);
			mQuickScrollListener.onFling(e2, (-velocityY)*1.0f);
			return true;
		}
		
	}
}
