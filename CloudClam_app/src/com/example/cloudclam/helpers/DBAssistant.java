package com.example.cloudclam.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBAssistant {
	final static String DB_NAME = "HASHDB";
	final static String DB_TABLE = "hashtab";
	final static String HASH_ID = "_id"; 
	final static String HASH_SHORT = "hash_short";
	
	private SQLiteDatabase myDB;
	private Context context;
	private DBHelper dbHelper;
	
	public DBAssistant(Context c){
		context = c;
		dbHelper = new DBHelper(c, DB_NAME, null, 1);
	}
	
	public DBAssistant open() throws SQLException{
		myDB = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() throws SQLException{
		myDB.close();
	}
	
	public void addEntry(String hash_short){
		ContentValues cv = new ContentValues();
		cv.put(HASH_SHORT, hash_short);
		myDB.insert(DB_TABLE, null, cv);
	}
	
	public void delEntry(String id){
		String where = HASH_ID + "=?";
		myDB.delete(DB_TABLE, where, new String[]{id});
	}
	
	public boolean searchForHash(String hash_short){
		Log.d("HASHTOSEARCH",hash_short);
		Cursor c = myDB.query(DB_TABLE, null, HASH_SHORT + "=?", new String[]{hash_short}, null,null,null);
		if(c.getCount() < 1){
			c.close();
			Log.d("DB_UPDATER","MD5 not found");
			return false;
		}else{
			//Now search that hash online
			return true;
		}
	}

	public String returnDB(){
		String[] cols = new String[]{HASH_ID,HASH_SHORT};
		Cursor c = myDB.query(DB_TABLE, cols, null, null, null, null, null);
		String completeDB = "";
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
			completeDB = completeDB + "\t|\t" + c.getInt(c.getColumnIndex(HASH_ID)) + "\t|\t" 
			+ c.getString(c.getColumnIndex(HASH_SHORT)) + "|\n";
		}
		return completeDB;
	}

//DBHelper	
private class DBHelper extends SQLiteOpenHelper{

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE " + DB_TABLE + 
				"( " + HASH_ID + " integer primary key autoincrement," + 
				HASH_SHORT + " text not null);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("");
	}
	
}


}