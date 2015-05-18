package me.relex.seamlessviewpagerheader.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import me.relex.seamlessviewpagerheader.R;

public class ListActivity extends ActionBarActivity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		findViewById(R.id.tv_list_activity).setOnClickListener(this);
		findViewById(R.id.tv_list_fragment).setOnClickListener(this);
		findViewById(R.id.tv_list_github).setOnClickListener(this);
	}


	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_list, menu);
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
	}*/

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.tv_list_activity:
				startActivity(new Intent(this,MainActivity.class));
				break;
			case R.id.tv_list_fragment:
				startActivity(new Intent(this,MainFragmentActivity.class));
				break;
			case  R.id.tv_list_github:
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("https://github.com/kaedea"));
				startActivity(intent);
				break;
		}
	}
}
