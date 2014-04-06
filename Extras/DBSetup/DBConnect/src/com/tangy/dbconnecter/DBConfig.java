package com.tangy.dbconnecter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBConfig {
	String url,dbName,driver,uName,pWord;
	
	public DBConfig(String db,String port,String username,String password){
		url = "jdbc:mysql://127.0.0.1:3306/";
		dbName = db;
		driver = "com.mysql.jdbc.Driver";
		uName = username;
		pWord = password;
	}
	
	public Connection createConnection(){
		Connection con = null;
		try {
			Class.forName(driver).newInstance();
			con = DriverManager.getConnection(url + dbName,"tangy","tangy");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;
	}
	
	public void insertMD5(Connection con,String tablename,int id,String fileName,String md5){
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.execute("INSERT INTO " + tablename + " VALUES (" + id + ",'" + fileName + "','" + md5 + "');");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeConnection(Connection con){
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void emptyTable(Connection con,String tableName){
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.execute("TRUNCATE " + "md5_hashtab;");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
		//Recursive Algo to iterate thru all files and folders
			public ArrayList<String> listfilz(File file,ArrayList<String> paths) {
			    File[] list_of_files = file.listFiles();
			    System.out.println("PATH" +file.getPath());
			    for (int i = 0; i < list_of_files.length; i++) {
			    	if(list_of_files[i].isDirectory()){
			    		//go recursive if its a directory
			    		System.out.println("FOL : " + list_of_files[i]);
			    		paths = listfilz(list_of_files[i],paths);
			    	}else{
			    		//its a file then right?
			    		System.out.println("FILE: " + list_of_files[i]);
			    		/*fName = list_of_files[i].toString().
			    				substring(list_of_files[i].toString().lastIndexOf("\\"),list_of_files[i].toString().lastIndexOf("."));
			    				*/
			    		paths.add(list_of_files[i].toString());
			    	}
			    }
				return paths;
			 }
			
			public String calculateMD5(String path) throws IOException{
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


}
