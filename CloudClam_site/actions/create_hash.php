<?php

/*
Code to create a new hash
Product details are read from 
HTTP Post
Remember each hash has the following properties
1.hash_short : First 3 characters of the hash
2.hash_long : Full hash
3.category : Type of Malware
*/

//array for JSON response
$response = array();

//check for required fields
if(isset($_POST['hash_long']) && isset($_POST['category'])){
    $hash_long = $_POST['hash_long'];
    $hash_category = $_POST['category'];
    $hash_short = substr($hash_long,0,3);
    
    //include db_connect class
    require_once(__DIR__.'/../db/db_connect.php');
    
    //connect to db
    $db = new DB_CONNECT();
    
    //mysql inserting a new row
    $result = mysql_query("INSERT INTO hashtab(hash_short, hash_long, category) VALUES('$hash_short','$hash_long','$hash_category')");
    
    //check if row inserted
    if($result){
        //successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Product Successfully Created";
        
        //echoing JSON Response
        echo json_encode($response);
    }else{
        //failed to insert row
        $response["success"] = 0;
        $response["message"] = "Error inserting hash";
        echo json_encode($response);
    }
}else{
        $response["success"] = 0;
        $response["message"] = "Error missing params";
        echo json_encode($response);
}

/*
Message Response Format
{
    "success" : 1,
    "message" : "Productcreated"
}
*/

?>