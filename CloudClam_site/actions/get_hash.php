<?php

//Get Hash details using Short Hash

//array for JSON response
$response=array();

//include db connect class
require_once(__DIR__.'/../db/db_connect.php');

//connecting to db
$db=new DB_CONNECT();

//check for GET data
if(isset($_GET["hash_short"]) && isset($_GET["hash_long"])){
    $hash_short = $_GET['hash_short'];
    $hash_long_act = $_GET["hash_long"];
    
    //get long hash options from short hash
    $query = mysql_query("SELECT * FROM hashtab WHERE hash_short = '$hash_short' ORDER BY pid DESC");
    
    if(!empty($query)){
        //check for empty result
        if(mysql_num_rows($query) > 0){
            $query_responses = array();
            //First Get Matching Results from DB
            //Now check whether hash_long_act equals any one of the responses
            $response["detected"] = 0;
            while($result = mysql_fetch_array($query, MYSQL_ASSOC)){
                if($result["hash_long"] == $hash_long_act){
                    $response["detected"] = 1;
                    $response["hash_category"] = $result["category"];
                }
                
            }
                            
            //success
            $response["success"] = 1;

            //echo JSON response
            echo json_encode($response);
            
            
        }else{
            
            //no such hash found
            $response["success"] = 1;
            $response["message"] = "No Hash Found";
            
            echo json_encode($response);
        }
    }else{
        //no such hash found
            $response["success"] = 0;
            $response["message"] = "No Hash Found";
            
            echo json_encode($response);
    }
}else{
//no such hash found
            $response["success"] = 0;
            $response["message"] = "Error in Field";
            
            echo json_encode($response);       
}

?>