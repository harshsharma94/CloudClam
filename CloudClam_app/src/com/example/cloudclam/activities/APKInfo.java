package com.example.cloudclam.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleAdapter;

import com.example.cloudclam.R;

public class APKInfo extends ListActivity{
	
	private final String TAG="APKINFOLOG";
	ArrayList<HashMap<String, String>> apkList;
	private SimpleAdapter apks;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apk_info);
		apkList = new ArrayList<HashMap<String,String>>();
		new APKInfoLoader().execute();
	}
	
	private void setupLV(){
		apks = new SimpleAdapter(APKInfo.this, apkList,R.layout.apk_info_listitem, new String[]{"line1","line2"}, new int[]{R.id.text1,R.id.text2});
		setListAdapter(apks);
	}
	
	private void listallApks(){
		final PackageManager pm = getPackageManager();
		//get a list of installed apps.
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		int i = 0;
		for (ApplicationInfo packageInfo : packages) {
			HashMap<String, String> map =  new HashMap<String, String>();
			try {
				map.put("line1", packageInfo.loadLabel(getPackageManager()).toString());
				map.put("line2", packageInfo.packageName);
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				map.put("line1", "Something went wrong");
				map.put("line2", "Something went Wrong");
			}
			apkList.add(map);
			//apks.notifyDataSetChanged();
			i++;
			/*
			Log.d(TAG,"Name : " + packageInfo.loadLabel(getPackageManager()).toString());
		    Log.d(TAG, "Installed package :" + packageInfo.packageName);
		    Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
		    */
		}
		// the getLaunchIntentForPackage returns an intent that you can use with startActivity() 
	}
	
	class APKInfoLoader extends AsyncTask<String, String, String>{
		private ProgressDialog pDialog;
		
		

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(APKInfo.this);
			pDialog.setMessage("Loading APK List..");
			pDialog.setCancelable(false);
			pDialog.setIndeterminate(false);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			listallApks();
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			runOnUiThread(new Runnable(){
				public void run(){
					setupLV();
			}
			});
		}
	}


}
