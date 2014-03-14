<?php

/*
Whenever you want to connect to mysql database
 and do some operations use the db_connect.php class as
 $db = new DB_CONNECT();
 */


/**
* Class file to connect to database
*/
class DB_CONNECT{

	//Constructor
	function __construct(){
		//connecting to db
		$this->connect();
	}

	//Destructor
	function __destruct(){
		//connecting to db
		$this->close();
	}

	/*
	*	Function to connect with database
	*/		
	function connect(){
		//import database connection variables
		require_once __DIR__. '/db_config.php';

		//Connect to mysql db
		$con = mysql_connect(DB_SERVER,DB_USER,DB_PASSWORD) or die(mysql_error());

		//Selecting database
		$db = mysql_select_db(DB_DATABASE) or die(mysql_error());///////CHECK

		//return connection cursor
		return $con;
	}

	/*
	* Function to close db connection
	*/
	function close(){
		//closing db connection
		mysql_close();
	}
}

?>