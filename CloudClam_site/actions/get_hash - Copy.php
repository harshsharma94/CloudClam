<?php

//Get Hash details using Short Hash

//array for JSON response
$response=array();

//include db connect class
require_once(__DIR__.'/../db/db_connect.php');

//connecting to db
$db=new DB_CONNECT();

//check for post data
if(isset($_GET["hash_short"])){
    $hash_short = $_GET['hash_short'];
    
    //get hash details from short hash
    $query = mysql_query("SELECT * FROM hashtab WHERE hash_short = $hash_short ORDER BY pid DESC");
    $results = array();
    /*
    while($line = mysql_fetch_array($result, MYSQL_ASSOC)){
        $results[] = $line;
        echo $results[0];
}
*/
    
    
    if(!empty($query)){
        //check for empty result
        if(mysql_num_rows($query) > 0){
            $response["hash"] = array();
            while($result = mysql_fetch_array($query, MYSQL_ASSOC)){
            $hash = array();
            $hash["hash_pid"] = $result["pid"];
            $hash["hash_short"] = $result["hash_short"];
            $hash["hash_long"] = $result["hash_long"];
            $hash["hash_category"] = $result["category"];
            $hash["created_at"] = $result["created_at"];
            $hash["updated_at"] = $result["updated_at"];
            
            //success
            $response["success"] = 1;
            
            //user response
            array_push($response["hash"],$hash);//push the hash array in response
            }
            //echo JSON response
            echo json_encode($response);
        }else{
            
            //no such hash found
            $response["success"] = 0;
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