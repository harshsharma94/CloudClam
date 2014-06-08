package com.example.cloudclam.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.cloudclam.R;
import com.example.cloudclam.helpers.DBAssistant;
import com.example.cloudclam.helpers.JSONParser;

public class HomeScreenActivity extends Activity implements OnClickListener
{

	Button startScan, apkInfo, exit, startDeepScan;
	DBAssistant dbAssist;
	JSONParser jParser = new JSONParser();
	private static final String TAG_SUCCESS = "success";
	// Url for Cloud Testing
	 private static String url_short_hashes =
	 "http://172.16.6.88/CloudClam/actions/get_short_hashes.php";
	 private static String url_short_hashes_ssdeep =
	 "http://172.16.6.88/CloudClam/actions/get_short_hashes_ssdeep.php";
	// Url for Emulator Testing
	//private static String url_short_hashes = "http://10.0.2.2/CloudClam/actions/get_short_hashes.php";
	//private static String url_short_hashes_ssdeep = "http://10.0.2.2/CloudClam/actions/get_short_hashes_ssdeep.php";
	// Url for wifi testing
	 //private static String url_short_hashes =
	 //"http://192.168.221.1/CloudClam/actions/get_short_hashes.php";
	 //private static String url_short_hashes_ssdeep =
	 //"http://192.168.221.1/CloudClam/actions/get_short_hashes_ssdeep.php";
	JSONArray shortHashes = null;
	SharedPreferences preferences;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		/*
		 * Setup the UI components
		 */
		setContentView(R.layout.home_screen);
		setupButtons();
		/*
		 * Set up the Database
		 */
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		if (!preferences.getBoolean("firstStart", false))
		{
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("firstStart", true);
			editor.commit();
			this.setupOfflineDB();
		}

	}

	private boolean isNetworkAvailable()
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.Bexit:
			finish();
			break;
		case R.id.Binfo:
			startActivity(new Intent(HomeScreenActivity.this, APKInfo.class));
			break;
		/*
		 * case R.id.Bofflinedb: startActivity(new
		 * Intent(HomeScreenActivity.this,OfflineDBView.class)); break; case
		 * R.id.Bonlinedb: startActivity(new
		 * Intent(HomeScreenActivity.this,OnlineDBView.class)); break;
		 */
		case R.id.BScan:
			if (isNetworkAvailable())
			{
				Bundle scanHolder = new Bundle();
				scanHolder.putString("scanType", "md5");
				Intent scanActivity = new Intent(HomeScreenActivity.this,
						HashScanner.class);
				scanActivity.putExtra("scanHolder", scanHolder);
				startActivity(scanActivity);
				break;
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Unable to Connect.",
						Toast.LENGTH_SHORT).show();
			}
		case R.id.BDScan:
			if (isNetworkAvailable())
			{
				Bundle scanHolder2 = new Bundle();
				scanHolder2.putString("scanType", "ssdeep");
				Intent scanActivity2 = new Intent(HomeScreenActivity.this,
						HashScanner.class);
				scanActivity2.putExtra("scanHolder", scanHolder2);
				startActivity(scanActivity2);
				break;
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Unable to Connect.",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	private void setupButtons()
	{
		startScan = (Button) findViewById(R.id.BScan);
		startDeepScan = (Button) findViewById(R.id.BDScan);
		exit = (Button) findViewById(R.id.Bexit);
		apkInfo = (Button) findViewById(R.id.Binfo);
		startScan.setOnClickListener(this);
		apkInfo.setOnClickListener(this);
		exit.setOnClickListener(this);
		startDeepScan.setOnClickListener(this);
	}

	class LoadOfflineDB extends AsyncTask<String, String, String>
	{
		private ProgressDialog pDialog;

		protected void onPreExecute()
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(HomeScreenActivity.this);
			pDialog.setMessage("Updating");
			pDialog.setCancelable(false);
			pDialog.setIndeterminate(false);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... param)
		{

			// TODO Auto-generated method stub
			// SharedPreferences.Editor editor = preferences.edit();
			// editor.putBoolean("firstStart", false);
			// editor.commit();

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			JSONObject json = jParser.makeHttpRequest(url_short_hashes, "GET",
					params);

			Log.d("All Hashes", json.toString());

			try
			{
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1)
				{
					shortHashes = json.getJSONArray("hashes");

					for (int i = 0; i < shortHashes.length(); i++)
					{
						JSONObject c = shortHashes.getJSONObject(i);
						Log.d("OFFLINE DB ENTRIES", c.getString("hash_short"));
						dbAssist.addEntry(c.getString("hash_short"));
					}
				}
				else
				{
					Log.d("CONNECTIVITY", "Connection Problem");
				}
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			json = jParser.makeHttpRequest(url_short_hashes_ssdeep, "GET",
					params);

			Log.d("All Hashes SSDEEP", json.toString());

			try
			{
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1)
				{
					shortHashes = json.getJSONArray("hashes");

					for (int i = 0; i < shortHashes.length(); i++)
					{
						JSONObject c = shortHashes.getJSONObject(i);
						Log.d("OFFLINE DB ENTRIES SSDEEP",
								c.getString("hash_short"));
						dbAssist.addEntry(c.getString("hash_short"));
					}
				}
				else
				{
					Log.d("CONNECTIVITY", "Connection Problem");
				}
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;

		}

		@Override
		protected void onPostExecute(String result)
		{

			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();

		}

	}

	private void setupOfflineDB()
	{
		dbAssist = new DBAssistant(this);
		dbAssist.open();

		/*
		 * Runnable dbSetup = new Runnable() { public void run() {
		 * 
		 * /* dbAssist.addEntry("000"); dbAssist.addEntry("001");
		 * dbAssist.addEntry("a21"); dbAssist.addEntry("4fb");
		 * dbAssist.addEntry("529"); dbAssist.addEntry("192");
		 * 
		 * //firstStart = preferences.getBoolean("firstStart", true); //if
		 * (firstStart) //{
		 * 
		 * //} } };
		 * 
		 * Thread dbThread = new Thread(dbSetup); dbThread.start();
		 */

		// firstStart = preferences.getBoolean("firstStart", true);
		// if (firstStart)
		// {
		new LoadOfflineDB().execute();
		// }

		// dbAssist.close();
	}

}
