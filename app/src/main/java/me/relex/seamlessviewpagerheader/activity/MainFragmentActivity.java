package me.relex.seamlessviewpagerheader.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.ActionBarActivity;
import me.relex.seamlessviewpagerheader.R;
import me.relex.seamlessviewpagerheader.fragment.MainFragment;
import me.relex.seamlessviewpagerheader.tools.ScrollableFragmentListener;
import me.relex.seamlessviewpagerheader.tools.ScrollableListener;


public class MainFragmentActivity extends ActionBarActivity implements ScrollableFragmentListener {

	public static final String FRAGMENT_MAIN = "FRAGMENT_MAIN";
	public static SparseArrayCompat<ScrollableListener> mScrollableListenerArrays = new SparseArrayCompat<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_fragment);
		Fragment fragment = new MainFragment();
		getSupportFragmentManager().beginTransaction()
				.add(R.id.container, fragment, FRAGMENT_MAIN)
				.commit();
	}

	@Override
	public void onFragmentAttached(ScrollableListener listener, int position) {
		mScrollableListenerArrays.put(position, listener);
	}

	@Override
	public void onFragmentDetached(ScrollableListener listener, int position) {
		mScrollableListenerArrays.remove(position);
	}

	public SparseArrayCompat<ScrollableListener> getScrollableListeners(){
		return mScrollableListenerArrays;
	}




/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	*//**
	 * A placeholder fragment containing a simple view.
	 *//**//*
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
		                         Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
	}*/
}
