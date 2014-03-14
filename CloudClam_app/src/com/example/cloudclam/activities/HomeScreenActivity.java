package com.example.cloudclam.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.cloudclam.R;
import com.example.cloudclam.helpers.DBAssistant;

public class HomeScreenActivity extends Activity implements OnClickListener{

	Button startScan,offDB,onDB,apkInfo,exit;
	DBAssistant dbAssist;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * Setup the UI components
		 */
		setContentView(R.layout.home_screen);
		setupButtons();
		/*
		 * Set up the Database
		 */
		this.setupOfflineDB();	
	}

	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.Bexit:
			finish();
			break;
		case R.id.Binfo:
			startActivity(new Intent(HomeScreenActivity.this,APKInfo.class));
			break;
		case R.id.Bofflinedb:
			startActivity(new Intent(HomeScreenActivity.this,OfflineDBView.class));
			break;
		case R.id.Bonlinedb:
			startActivity(new Intent(HomeScreenActivity.this,OnlineDBView.class));
			break;
		case R.id.BScan:
			startActivity(new Intent(HomeScreenActivity.this,HashScanner.class));
			break;
		}
		
	}
	
	
	private void setupButtons(){
		startScan = (Button)findViewById(R.id.BScan);
		offDB = (Button)findViewById(R.id.Bofflinedb);
		onDB = (Button)findViewById(R.id.Bonlinedb);
		exit = (Button)findViewById(R.id.Bexit);
		apkInfo = (Button)findViewById(R.id.Binfo);
		startScan.setOnClickListener(this);
		offDB.setOnClickListener(this);
		onDB.setOnClickListener(this);
		apkInfo.setOnClickListener(this);
		exit.setOnClickListener(this);
	}

	private void setupOfflineDB(){
		dbAssist = new DBAssistant(this);
		dbAssist.open();
		Runnable dbSetup = new Runnable(){
			public void run(){
						dbAssist.addEntry("000");
						dbAssist.addEntry("001");
						dbAssist.addEntry("a21");
						dbAssist.addEntry("4e5");
						dbAssist.close();
			}
		};
		Thread dbThread = new Thread(dbSetup);
		dbThread.start();
	}
	
}
