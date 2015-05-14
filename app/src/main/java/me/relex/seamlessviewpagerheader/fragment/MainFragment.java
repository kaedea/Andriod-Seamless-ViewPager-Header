package me.relex.seamlessviewpagerheader.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import me.relex.seamlessviewpagerheader.R;
import me.relex.seamlessviewpagerheader.activity.MainFragmentActivity;
import me.relex.seamlessviewpagerheader.tools.ScrollableListener;
import me.relex.seamlessviewpagerheader.tools.ViewPagerHeaderHelper;
import me.relex.seamlessviewpagerheader.widget.SlidingTabLayout;
import me.relex.seamlessviewpagerheader.widget.TouchCallbackLayout;

/**
 * Created by kaede on 2015/5/13.
 */
public class MainFragment extends Fragment implements TouchCallbackLayout.TouchEventListener,
		ViewPagerHeaderHelper.OnViewPagerTouchListener {

	private static final long  DEFAULT_DURATION = 300L;
	private static final float DEFAULT_DAMPING  = 1.5f;


	private ViewPager             mViewPager;
	private View                  mHeaderLayoutView;
	private ViewPagerHeaderHelper mViewPagerHeaderHelper;

	private int mTouchSlop;
	private int mTabHeight;
	private int mHeaderHeight;

	private Interpolator mInterpolator = new DecelerateInterpolator();


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_main, container, false);

		mTouchSlop = ViewConfiguration.get(this.getActivity()).getScaledTouchSlop();
		mTabHeight = getResources().getDimensionPixelSize(R.dimen.tabs_height);
		mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.viewpager_header_height);

		mViewPagerHeaderHelper = new ViewPagerHeaderHelper(this.getActivity(), this);

		TouchCallbackLayout touchCallbackLayout = (TouchCallbackLayout) view.findViewById(R.id.layout);
		touchCallbackLayout.setTouchEventListener(this);

		mHeaderLayoutView = view.findViewById(R.id.header);

		SlidingTabLayout slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.tabs);
		//slidingTabLayout.setDistributeEvenly(true);

		mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
		mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));

		slidingTabLayout.setViewPager(mViewPager);

		ViewCompat.setTranslationY(mViewPager, mHeaderHeight);
		return view;
	}

	@Override
	public boolean onLayoutInterceptTouchEvent(MotionEvent event) {

		return mViewPagerHeaderHelper.onLayoutInterceptTouchEvent(event,
				mTabHeight + mHeaderHeight);
	}

	@Override
	public boolean onLayoutTouchEvent(MotionEvent event) {
		return mViewPagerHeaderHelper.onLayoutTouchEvent(event);
	}

	@Override
	public boolean isViewBeingDragged(MotionEvent event) {
		MainFragmentActivity activity = (MainFragmentActivity) getActivity();
		ScrollableListener listener = activity.getScrollableListeners().valueAt(mViewPager.getCurrentItem());
		if (listener==null)return true;
		return listener.isViewBeingDragged(event);
	}

	@Override
	public void onMoveStarted(float y) {

	}

	@Override
	public void onMove(float y, float yDx) {
		float headerTranslationY = ViewCompat.getTranslationY(mHeaderLayoutView) + yDx;
		if (headerTranslationY >= 0) { // pull end
			headerExpand(0L);

			//Log.d("kaede", "pull end");
			if (countPullEnd >= 1) {
				if (countPullEnd == 1) {
					downtime = SystemClock.uptimeMillis();
					simulateTouchEvent(mViewPager, downtime, SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 250, y + mHeaderHeight);
				}
				simulateTouchEvent(mViewPager, downtime, SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, 250, y + mHeaderHeight);
			}
			countPullEnd++;

		} else if (headerTranslationY <= -mHeaderHeight) { // push end
			headerFold(0L);

			//Log.d("kaede", "push end");
			//Log.d("kaede", "kaede onMove y="+y+",yDx="+yDx+",headerTranslationY="+headerTranslationY);
			if (countPushEnd >= 1) {
				if (countPushEnd == 1) {
					downtime = SystemClock.uptimeMillis();
					simulateTouchEvent(mViewPager, downtime, SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 250, y + mHeaderHeight);
				}
				simulateTouchEvent(mViewPager, downtime, SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, 250, y + mHeaderHeight);
			}
			countPushEnd++;

		} else {

			//Log.d("kaede", "ing");
	    	/*if(!isHasDispatchDown3){
        	simulateTouchEvent(mViewPager,SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 250, y+mHeaderHeight);
        	isHasDispatchDown3=true;
        	}*/

			ViewCompat.animate(mHeaderLayoutView)
					.translationY(headerTranslationY)
					.setDuration(0)
					.start();
			ViewCompat.animate(mViewPager)
					.translationY(headerTranslationY + mHeaderHeight)
					.setDuration(0)
					.start();
		}
	}

	long downtime     = -1;
	int  countPushEnd = 0, countPullEnd = 0;

	private void simulateTouchEvent(View dispatcher, long downTime, long eventTime, int action, float x, float y) {
		MotionEvent motionEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), action, x, y, 0);
		try {
			dispatcher.dispatchTouchEvent(motionEvent);
		} catch (Throwable e) {
			Log.e("kaede", "simulateTouchEvent error: " + e.toString());
		} finally {
			motionEvent.recycle();
		}
	}

	@Override
	public void onMoveEnded(boolean isFling, float flingVelocityY) {

		//Log.d("kaede", "move end");
		countPushEnd = countPullEnd = 0;

		float headerY = ViewCompat.getTranslationY(mHeaderLayoutView); // 0到负数
		if (headerY == 0 || headerY == -mHeaderHeight) {
			return;
		}

		if (mViewPagerHeaderHelper.getInitialMotionY() - mViewPagerHeaderHelper.getLastMotionY()
				< -mTouchSlop) {  // pull > mTouchSlop = expand
			headerExpand(headerMoveDuration(true, headerY, isFling, flingVelocityY));
		} else if (mViewPagerHeaderHelper.getInitialMotionY()
				- mViewPagerHeaderHelper.getLastMotionY()
				> mTouchSlop) { // push > mTouchSlop = fold
			headerFold(headerMoveDuration(false, headerY, isFling, flingVelocityY));
		} else {
			if (headerY > -mHeaderHeight / 2f) {  // headerY > header/2 = expand
				headerExpand(headerMoveDuration(true, headerY, isFling, flingVelocityY));
			} else { // headerY < header/2= fold
				headerFold(headerMoveDuration(false, headerY, isFling, flingVelocityY));
			}
		}
	}

	private long headerMoveDuration(boolean isExpand, float currentHeaderY, boolean isFling,
	                                float velocityY) {

		long defaultDuration = DEFAULT_DURATION;

		if (isFling) {

			float distance = isExpand ? Math.abs(mHeaderHeight) - Math.abs(currentHeaderY)
					: Math.abs(currentHeaderY);
			velocityY = Math.abs(velocityY) / 1000;

			defaultDuration = (long) (distance / velocityY * DEFAULT_DAMPING);

			defaultDuration =
					defaultDuration > DEFAULT_DURATION ? DEFAULT_DURATION : defaultDuration;
		}

		return defaultDuration;
	}

	private void headerFold(long duration) {
		ViewCompat.animate(mHeaderLayoutView)
				.translationY(-mHeaderHeight)
				.setDuration(duration)
				.setInterpolator(mInterpolator)
				.start();

		ViewCompat.animate(mViewPager).translationY(0).
				setDuration(duration).setInterpolator(mInterpolator).start();

		mViewPagerHeaderHelper.setHeaderExpand(false);
	}

	private void headerExpand(long duration) {
		ViewCompat.animate(mHeaderLayoutView)
				.translationY(0)
				.setDuration(duration)
				.setInterpolator(mInterpolator)
				.start();

		ViewCompat.animate(mViewPager)
				.translationY(mHeaderHeight)
				.setDuration(duration)
				.setInterpolator(mInterpolator)
				.start();
		mViewPagerHeaderHelper.setHeaderExpand(true);
	}



	private class ViewPagerAdapter extends FragmentPagerAdapter {

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {

			if (position == 3) {
				return ScrollViewFragment.newInstance(position);
			}
			return ListViewFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return getString(R.string.tab_country);
				case 1:
					return getString(R.string.tab_continent);
				case 2:
					return getString(R.string.tab_city);
				case 3:
					return getString(R.string.tab_scroll_view);
			}

			return "";
		}
	}
}
