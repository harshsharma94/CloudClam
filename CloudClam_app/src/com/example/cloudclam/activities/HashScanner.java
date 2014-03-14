package com.example.cloudclam.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cloudclam.R;
import com.example.cloudclam.helpers.DBAssistant;
import com.example.cloudclam.helpers.JSONParser;

public class HashScanner extends Activity{

	ProgressBar scanProgress;
	TextView scanInd,fileInd,malwareInd;
	String[][] apkInfo;
	DBAssistant myDB;
	ArrayList<String> malwares;
	
	//Web Request Vars
	JSONParser jParser = new JSONParser();
	private static String url_checkhash = "http://10.0.3.2/CloudClam/actions/get_hash.php";
	
	//JSON NODE NAMES
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_HASHSHORT = "hash_short";
	private static final String TAG_HASHLONG = "hash_long";
	private static final String TAG_HASH_CATEGORY = "hash_category";
	private static final String TAG_HASH_DETECTED = "detected";
	private static final String TAG_LOG = "CLOUDCLAMLOG";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hash_scan);
		setupVariables();
		new APKSourceLoad().execute();
	}
	
	private void setupVariables(){
		scanProgress = (ProgressBar)findViewById(R.id.PBar);
		fileInd = (TextView)findViewById(R.id.tvFileInd);
		malwareInd = (TextView)findViewById(R.id.TVSecurity);
		malwares = new ArrayList<String>();
		myDB = new DBAssistant(this);
	}

	class APKSourceLoad extends AsyncTask<String, String, String>{
		private ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(HashScanner.this);
			pDialog.setMessage("Initializing");
			pDialog.setCancelable(false);
			pDialog.setIndeterminate(false);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			apkInfo = listAllApkSources();
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			scanProgress.setMax(apkInfo.length);
			scanProgress.setIndeterminate(false);
			new SearchMD5Hash().execute();
		}
		
		private String[][] listAllApkSources(){
			final PackageManager pm = getPackageManager();
			//get a list of installed apps.
			List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
			String[][] apkSources = new String[packages.size()][2];
			int i=0;
			for(ApplicationInfo packageInfo : packages){
				apkSources[i][0] = packageInfo.sourceDir;
				apkSources[i++][1] = packageInfo.loadLabel(getPackageManager()).toString();
			}
			return apkSources;
		}
	}
	
	private String calculateMD5(String path) throws IOException{
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			File f = new File(path);
			FileInputStream fis = new FileInputStream(f);
			byte[] dataBytes = new byte[1024];
			int nread = 0;
			while((nread=fis.read(dataBytes))!= -1)
			{
				md.update(dataBytes, 0, nread);
			}
			
			byte[] mdbytes = md.digest();
			
			StringBuffer hexString = new StringBuffer();
	    	for (int i=0;i<mdbytes.length;i++) {
	    		String hex=Integer.toHexString(0xff & mdbytes[i]);
	   	     	if(hex.length()==1) hexString.append('0');
	   	     	hexString.append(hex);
	    	}
	    	fis.close();
	    	return hexString.toString();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	class SearchMD5Hash extends AsyncTask<String, Integer, String>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			doScan();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			fileInd.setText("Complete");
			if(malwares.size() > 0){
				malwareInd.setText("Malware in the following Apps\n");
				for(int i=0;i < malwares.size();i++){
					malwareInd.setText(malwareInd.getText() + malwares.get(i) + "\n");
				}
				malwareInd.setTextColor(getResources().getColor(R.color.Red));
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			scanProgress.setProgress(values[0]);
			//Update UI
			fileInd.setText(apkInfo[values[0]][1]);
		}
		
		
		private void doScan(){
			//Initialization
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myDB.open();
			
			
			for(int i=0;i<apkInfo.length;i++){
				//Calculate MD5
				try {
					String md5 = calculateMD5(apkInfo[i][0]);
					String md5_short = md5.substring(0, 3);
					Log.d(TAG_LOG,md5);
					if(myDB.searchForHash(md5_short)){
						Log.d(TAG_LOG,"POSSIBLE CATCH");
						// SEND GET Request
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair(TAG_HASHSHORT, md5_short));
						params.add(new BasicNameValuePair(TAG_HASHLONG, md5));
						JSONObject json =  jParser.makeHttpRequest(url_checkhash, "GET", params);
						Log.d(TAG_LOG,json.toString());
						
						//Receive Response to GET
						try {
							int success = json.getInt(TAG_SUCCESS);
							if(success == 1){
								int detected = json.getInt(TAG_HASH_DETECTED);
								
								if(detected == 1){
								String category = json.getString(TAG_HASH_CATEGORY);
								Log.d(TAG_LOG, category);
								malwares.add(apkInfo[i][1]);
								}//else no one cares
								else{
									Log.d(TAG_LOG,"Not a Problem");
								}
							}else{
								Log.d(TAG_LOG,"Connection Problem");
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}else{
						
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				publishProgress(i);
				
			}
			
		}
		
		
	}
	
	
}
