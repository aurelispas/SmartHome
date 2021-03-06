package com.geekytheory.SmartHome_App;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * Author: Mario Pérez Esteso Website: http://geekytheory.com Mail:
 * mario@geekytheory.com
 */

public class MyActivity extends SherlockFragmentActivity {

	private final int GET_JSON = 0;
	private final int UPDATE_DATABASE = 1;
	boolean refresh = false;
	SherlockFragmentActivity activity = this;
	MyCustomAdapter adapter = null;

	ArrayList<MyCustomAdapter> adapterList = new ArrayList<MyCustomAdapter>();

	ArrayList<ArrayList> matrixList = new ArrayList<ArrayList>(); // ArrayList
	// de
	// ArrayLists
	ArrayList<DeviceItem> deviceItemList = new ArrayList<DeviceItem>();

	TestFragmentAdapter mAdapter;
	ViewPager mPager;
	PageIndicator mIndicator;
	private static String[] CONTENT;

	ArrayList<String> titles = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AsyncLoadTasks.run(this, GET_JSON, 0, "", "");
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			// AsyncLoadTasks.run(this, GET_JSON, 0, "", "");
			Toast.makeText(
					this.activity,
					"Sorry for the inconveniences, but this function has not been implemented yet.",
					Toast.LENGTH_LONG).show();
			break;
		case R.id.menu_about:
			launchAbout();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void refreshView() {
		if (refresh) {
			Log.i("MATRIXSIZE", matrixList.size() + "");
			for (int i = 0; i < matrixList.size(); i++) {
				adapterList.add(new MyCustomAdapter(this, R.layout.custom_row,
						matrixList.get(i)));
			}
			tabTitles();

			setContentView(R.layout.simple_tabs);

			mAdapter = new TestFragmentAdapter(getSupportFragmentManager(),
					this);

			mPager = (ViewPager) findViewById(R.id.pager);
			mPager.setAdapter(mAdapter);

			mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
			mIndicator.setViewPager(mPager);
		}
	}

	public void launchAbout() {
		Intent i = new Intent(this, About.class);
		startActivity(i);
	}

	public void tabTitles() {
		int length_titles = titles.size();
		CONTENT = new String[length_titles];
		for (int i = 0; i < length_titles; i++) {
			CONTENT[i] = titles.get(i);
		}
	}

	private class MyCustomAdapter extends ArrayAdapter<DeviceItem> {

		private ArrayList<DeviceItem> customDeviceItemList;

		public MyCustomAdapter(Context context, int textViewResourceId,
				ArrayList<DeviceItem> deviceItemList) {
			super(context, textViewResourceId, deviceItemList);
			this.customDeviceItemList = new ArrayList<DeviceItem>();
			this.customDeviceItemList.addAll(deviceItemList);
			// Log.i("DEVICE_ITEM_LIST",
			// this.customDeviceItemList.get(0).title+"");

		}

		private class ViewHolder {
			Switch switchItem;
			TextView textItem, textItemTemp;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;

			// Log.v("ConvertView", String.valueOf(position));

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.custom_row, null);

				holder = new ViewHolder();
				holder.switchItem = (Switch) convertView
						.findViewById(R.id.on_off_switch);
				holder.textItem = (TextView) convertView
						.findViewById(R.id.tempItem);
				holder.textItemTemp = (TextView) convertView
						.findViewById(R.id.tempItem2);
				convertView.setTag(holder);

				holder.switchItem
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								Switch sw = (Switch) v;
								DeviceItem deviceItem = (DeviceItem) sw
										.getTag();
								deviceItem.setState(sw.isChecked());
								// Implement in background
								String value = (deviceItem.isOn()) ? "1" : "0";
								TaskHandler.run(deviceItem.getId(),
										deviceItem.getTitle(), value);
								Log.i("ITEM", deviceItem.getId() + "-"
										+ deviceItem.getTitle() + "-"
										+ deviceItem.isOn());

							}
						});
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			DeviceItem deviceItem = customDeviceItemList.get(position);
			if (deviceItem.getTitle().substring(0, 3).equals("LED")) {
				holder.switchItem.setText(deviceItem.getTitle());
				holder.switchItem.setChecked(deviceItem.isOn());
				holder.switchItem.setTag(deviceItem);
				holder.textItem.setVisibility(View.GONE);
				holder.textItemTemp.setVisibility(View.GONE);
			} else {
				holder.switchItem.setEnabled(false);
				holder.switchItem.setVisibility(View.GONE);
				// holder.textItem.setText(deviceItem.title);
				holder.textItemTemp.setText(deviceItem.title + " ºC");
				float temp = Float.parseFloat(deviceItem.title);
				if (temp > 28.00) {
					holder.textItemTemp.setTextColor(Color.RED);
				} else if (temp <= 28.00 && temp > 23.00) {
					holder.textItemTemp.setTextColor(Color.rgb(255, 71, 10));
				} else if (temp <= 23.00 && temp > 19.00) {
					holder.textItemTemp.setTextColor(Color.rgb(0, 153, 0));
				} else if (temp <= 19.00 && temp > 10.00) {
					holder.textItemTemp.setTextColor(Color.rgb(0, 0, 153));
				} else {
					holder.textItemTemp.setTextColor(Color.rgb(0, 153, 204));
				}
			}
			return convertView;
		}

	}

	class TestFragmentAdapter extends FragmentPagerAdapter {
		private int mCount = CONTENT.length;
		MyActivity mainActivity;

		public TestFragmentAdapter(FragmentManager fm, MyActivity mainActivity) {
			super(fm);
			this.mainActivity = mainActivity;
		}

		@Override
		public Fragment getItem(int position) {
			return TestFragment.newInstance(String.valueOf(position),
					this.mainActivity);
		}

		@Override
		public int getCount() {
			return mCount;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position];
		}

	}

	private static class TaskHandler extends
			AsyncTask<String, Integer, Boolean> {
		int id;
		String atr, value;

		public TaskHandler(int id, String atr, String value) {
			this.id = id;
			this.atr = atr;
			this.value = value;
		}

		protected Boolean doInBackground(final String... args) {
			JSONuse update = new JSONuse(id, atr, value);
			return true;
		}

		static void run(int id, String atr, String value) {
			new TaskHandler(id, atr, value).execute();
		}

	}

}
