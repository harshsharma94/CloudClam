package com.example.cloudclam.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.cloudclam.R;
import com.example.cloudclam.helpers.JSONParser;

public class OnlineDBView extends Activity{
	private ProgressDialog pDialog;
	JSONParser jParser = new JSONParser();
	private static String url_fulldb = "http://10.0.3.2/CloudClam/actions/get_entiredb.php";
	
	//JSON NODE NAMES
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_ID = "pid";
	private static final String TAG_HASHSHORT = "hash_short";
	private static final String TAG_HASHLONG = "hash_long";
	private static final String TAG_HASH_CATEGORY = "hash_category";
	private static final String HASHES_ALL = "hashes";
	private static final String TAG_LOG = "CLOUDCLAMLOG";
	
	//db Json Array
	JSONArray hashlong_all = null;
	TextView tv_db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.onlinedb_view);
		tv_db = (TextView)findViewById(R.id.TVOnDb);
		new LoadAllHashes().execute();
	}
	
	/*
	 * Load All Hashes via AsyncTask
	 */
	
	class LoadAllHashes extends AsyncTask<String, String,String>{

		
		

		/*
		 * First show Progress Dialog before beggining the GET
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(OnlineDBView.this);
			pDialog.setMessage("Loading Database Please Wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/*
		 * Get Hashes via Background Thread
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			//Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			JSONObject json =  jParser.makeHttpRequest(url_fulldb, "GET", params);
			Log.d(TAG_LOG,json.toString());
			String onlineDB = "EmptyDB";
			try {
				int success = json.getInt(TAG_SUCCESS);
				if(success == 1){
					hashlong_all = json.getJSONArray(HASHES_ALL);
					Log.d(TAG_LOG,hashlong_all + "");
					//Loop thru all hashes
					onlineDB = "";
					for(int i = 0; i < hashlong_all.length();i++){
						JSONObject c = hashlong_all.getJSONObject(i);
						
						//Store each json item in variable
						String id = c.getString(TAG_ID);
						String hash_long = c.getString(TAG_HASHLONG);
						String hash_short = c.getString(TAG_HASHSHORT);
						String hash_category = c.getString(TAG_HASH_CATEGORY);
						onlineDB = onlineDB + "|\t\t" + id + "\t\t|\t\t" + 
								hash_long + "\t\t|\t\t" + hash_short + "\t\t|\t\t"
								 + hash_category + "\t\t|\n";
					}
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return onlineDB;
		}
		
		/*
		 * Dismiss Progress Bar
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(final String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			runOnUiThread(new Runnable(){
				public void run(){
					tv_db.setText(result);
			}
			});
		}
	}
}
