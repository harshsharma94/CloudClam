<?php 

/*
List Entire Online Database
*/

//array for JSON response
$response = array();

//include db connect class
require_once(__DIR__.'/../db/db_connect.php');

//connecting to db
$db=new DB_CONNECT();

//get All hashes via sql
$result = mysql_query("SELECT *FROM hashtab") or die(mysql_error());

//check for empty result
if(mysql_num_rows($result) > 0){
    //looping through all results
    //hashes node
    $response["hashes"] = array();
    
    while($row= mysql_fetch_array($result)){
     //temp user array
        $hash=array();
        $hash["pid"] = $row["pid"];
        $hash["hash_short"] = $row["hash_short"];
        $hash["hash_long"] = $row["hash_long"];
        $hash["hash_category"] = $row["category"];
        $hash["created_at"] = $row["created_at"];
        $hash["updated_at"] = $row["updated_at"];
        
        //push singe hash into final response
        array_push($response["hashes"],$hash);
    }
    //success
    $response["success"] = 1;
 
    // echoing JSON response
    echo json_encode($response);
}else{
    // no hashes found
    $response["success"] = 0;
    $response["message"] = "No hashes found";
 
    // echo no users JSON
    echo json_encode($response);    
}

?>