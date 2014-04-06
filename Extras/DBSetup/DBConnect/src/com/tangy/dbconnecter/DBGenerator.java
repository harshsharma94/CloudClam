package com.tangy.dbconnecter;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

public class DBGenerator {

	static ArrayList<String> files;
	public static void main(String args[]){
		//setup
		files = new ArrayList<String>();
		DBConfig myDBConnect = new DBConfig("hashtables", "3306", "tangy", "tangy");
		//create connection
		Connection con = myDBConnect.createConnection();
		myDBConnect.emptyTable(con, "md5_hashtab");
		if(con !=null){
			System.out.println("Connection opened");
		}
		File file = new File("D:\\Memes");
		files = myDBConnect.listfilz(file, files);
		String md5 = "",fName="";
		for(int i = 0 ; i < files.size();  i++){
			md5 = "";
			fName = "";
			try {
				md5 = myDBConnect.calculateMD5(files.get(i));
				fName = files.get(i).toString().
	    				substring(files.get(i).lastIndexOf("\\") + 1);
				myDBConnect.insertMD5(con, "md5_hashtab", i, fName, md5);
				System.out.println(fName + "    " + md5 + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		myDBConnect.closeConnection(con);
	}
}
