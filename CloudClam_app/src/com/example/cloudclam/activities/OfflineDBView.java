package com.example.cloudclam.activities;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.cloudclam.R;
import com.example.cloudclam.helpers.DBAssistant;

public class OfflineDBView extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offlinedb_view);
		DBAssistant db = new DBAssistant(this);
		db.open();
		String myDB = db.returnDB();
		TextView dbView = (TextView)findViewById(R.id.TVOffDb);
		dbView.setText(myDB);
		db.close();
	}
	

}
