package com.quickscrollpopup;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.OverScroller;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quickscrollpopup.QSExpendableListView.OnQuickScrollListener;


public class QuickScrollPopup implements OnQuickScrollListener{
	private Context mContext;
	private ExpandableListView mListView;
	private RelativeLayout mParentLayout;
	
	private int mLWidth;
	private int mLHeight;
	private int mMinScroll;
	private int mMaxScroll;
	private int mTotScroll;
	private int mMaxOverScroll;
	private int mTotOverScroll;
	private int mActionBarHeight;
	private int mTop;
	private float mCurScroll;
	private float scrollY;
	private ViewGroup mScrollContainer;
	private TextView tvQSPopuptext;
	private BaseExpandableListAdapter mExpandableListAdapter;
	private int mCurPosition = 0; 
	LayoutInflater inflater;
	private int totGrounpCount;
	private int totChildCount;
	private int totItems;
	private int[] childCount;
	private int[] itemsGroupPos;
	
	private static int FIXED_POPUP_WIDTH = 200;
	private static int FIXED_POPUP_HEIGHT = 35;
	private int groupItemHeight, childItemHeight;
	private int totContentHeight;
	private QSFlinger flinger;
	private OverScroller mScroller;
	public QuickScrollPopup(Context context,ExpandableListView listView){
		mContext = context;
		mListView = listView;
		mExpandableListAdapter = (BaseExpandableListAdapter) mListView.getExpandableListAdapter();
		mParentLayout = (RelativeLayout) mListView.getParent();
		flinger = new QSFlinger(new Handler());
		inflater = LayoutInflater.from(mContext);
		totGrounpCount = mExpandableListAdapter.getGroupCount();
		childCount = new int[totGrounpCount];
		for(int i=0;i<totGrounpCount;i++){
			totChildCount += mExpandableListAdapter.getChildrenCount(i);
			childCount[i] = mExpandableListAdapter.getChildrenCount(i);
		}
		totItems = totGrounpCount + totChildCount;
		itemsGroupPos = new int[totItems];
		initilize();
	}
	
	private void initilize(){
		ActionBar acBar = ((Activity)mContext).getActionBar();
		if(acBar != null){
			mActionBarHeight = acBar.getHeight();
		}
		mTop = mListView.getTop();
		mCurScroll = mTop;
		scrollY = mTop;
		mScrollContainer = (ViewGroup) inflater.inflate(R.layout.quickscroll_popup, null);
		tvQSPopuptext = (TextView) mScrollContainer.findViewById(R.id.quickscroll_textview);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(FIXED_POPUP_WIDTH, FIXED_POPUP_HEIGHT);
		params.setMargins(0, (int)scrollY, 0, 0);
		mParentLayout.addView(mScrollContainer, params);
		mParentLayout.bringChildToFront(mScrollContainer);
		mScroller = new OverScroller(mContext);
	}

	@Override
	public void onDown() {
		flinger.endFling();
		mMinScroll = mTop;
		mMaxScroll = mListView.getMeasuredHeight() - FIXED_POPUP_HEIGHT;
		mTotScroll = mMaxScroll - mMinScroll;
		mTotOverScroll = mMaxOverScroll - mMinScroll;
	}

	@Override
	public void onScroll(MotionEvent e,float deltaY) {
		
		mCurScroll +=deltaY;
		if(mCurScroll < mTop){
			mCurScroll = mTop;
		}
		if(mCurScroll > mMaxOverScroll){
			mCurScroll = mMaxOverScroll;
		}
		
		float per = mCurScroll/mTotOverScroll;
		
		scrollY  = per*mTotScroll;
		
		if(scrollY < mMinScroll){
			scrollY = mMinScroll;
		}
		
		if(scrollY > mMaxScroll){
			scrollY = mMaxScroll;
		}
		Log.e("Upendra", "Popup scrollY:"+scrollY);
		int position = mListView.pointToPosition((int)e.getX(),(int)scrollY);
		if(position != ListView.INVALID_POSITION){
			mCurPosition = getGroupPos(position);//itemsGroupPos[position];
		}
		updateScrollContainerParams();
	}

	@Override
	public void onScrollState(int scrollState, int firstVisiblePos) {
		Log.e("Upendra", "onScrollState firstVisiblePos:"+firstVisiblePos);
		if(scrollState == OnScrollListener.SCROLL_STATE_FLING){
			flinger.start(firstVisiblePos, flinger);
		}
	}
	
	@Override
	public void onFling(MotionEvent e, float deltaVY) {
//		flinger.start(deltaVY, flinger);
	}

	public void updateQuickPopupText(){
		String txt = (String) mExpandableListAdapter.getGroup(mCurPosition);
		tvQSPopuptext.setText(txt);
	}
	public void setMesureDim(int width, int height) {
		mLWidth = width;
		mLHeight = height;
		View view = mExpandableListAdapter.getGroupView(0, true, null, mListView);
		view.measure(mLWidth, 0);
		groupItemHeight = view.getMeasuredHeight();
		view = mExpandableListAdapter.getChildView(0, 0, false, null, mListView);
		view.measure(mLWidth, 0);
		childItemHeight = view.getMeasuredHeight();
		totContentHeight = totGrounpCount * groupItemHeight;
		for(int i=0;i<totGrounpCount;i++){
			totContentHeight += childCount[i]*childItemHeight;
		}
		totContentHeight += mListView.getDividerHeight()*totItems - mListView.getDividerHeight();
		mMaxOverScroll = totContentHeight - mLHeight - FIXED_POPUP_HEIGHT;
		Log.e("Upendra", "Popup mMaxOverScroll:"+mMaxOverScroll);
		updateScrollContainerParams();
	}

	private int getGroupPos(int itemPos){
		int count = 0;
		for(int i=0;i<totGrounpCount;i++){
			count++;
			count += childCount[i];
			if(itemPos < count){
				return i;
			}
		}
		
		return ListView.INVALID_POSITION;
	}
	private void updateScrollContainerParams() {
		updateQuickPopupText();
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) mScrollContainer.getLayoutParams();
		params.setMargins(mLWidth - FIXED_POPUP_WIDTH , (int)scrollY, 0, 0);
	}
	
	private class QSFlinger implements Runnable{

		private int curVisiblePos;
		private int INC_VAL = 10;
		Handler mHandler;
		public QSFlinger(Handler handler){
			mHandler = handler;
		}
		public void start(int pos, Runnable runnable){
			curVisiblePos = pos + 1;
			mHandler.post(runnable);
		}
		
		public void endFling(){
			mHandler.removeCallbacks(this);
		}
		
		@Override
		public void run() {
			
//			mCurScroll += velY;
			int visitedItemCount =  curVisiblePos - 1;
			int groupPos = getGroupPos(curVisiblePos - 1) + 1;
			if(visitedItemCount > 0){
				mCurScroll = groupPos *groupItemHeight + (visitedItemCount > 0 ? ((visitedItemCount - groupPos)* childItemHeight): 0);
				mCurScroll += mListView.getDividerHeight()*curVisiblePos - mListView.getDividerHeight();
			}else{
				mCurScroll = 0;
			}
			if(mCurScroll < mTop){
				mCurScroll = mTop;
			}
			if(mCurScroll > mMaxOverScroll){
				mCurScroll = mMaxOverScroll;
			}
			
			float per = mCurScroll/mTotOverScroll;
			float prevScrollY = scrollY;
			scrollY  = per*mTotScroll;
			
			if(scrollY < mMinScroll){
				scrollY = mMinScroll;
			}
			
			if(scrollY > mMaxScroll){
				scrollY = mMaxScroll;
			}
			ObjectAnimator objAnim = ObjectAnimator.ofFloat(mScrollContainer, "y", mScrollContainer.getY(),
					scrollY);
			objAnim.setDuration(100);
			
			objAnim.addListener(new AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationRepeat(Animator animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animator animation) {
					Log.e("Upendra", "Popup scrollY:"+scrollY);
					int position = mListView.pointToPosition(100,(int)scrollY);
					if(position != ListView.INVALID_POSITION){
						mCurPosition = getGroupPos(position);
					}
					updateScrollContainerParams();
					
					mScrollContainer.postInvalidate();							
				}
				
				@Override
				public void onAnimationCancel(Animator animation) {
					// TODO Auto-generated method stub
					
				}
			});
//			objAnim.start();
			Log.e("Upendra", "Popup scrollY:"+scrollY);
			int position = mListView.pointToPosition(100,(int)scrollY);
			if(position != ListView.INVALID_POSITION){
				mCurPosition = getGroupPos(position);
			}
			updateScrollContainerParams();
			
			mScrollContainer.postInvalidate();		
		}
		
	}
	
}
