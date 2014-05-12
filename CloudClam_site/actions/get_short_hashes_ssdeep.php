<?php 


//array for JSON response
$response = array();

//include db connect class
require_once(__DIR__.'/../db/db_connect.php');

//connecting to db
$db=new DB_CONNECT();

//get All hashes via sql
$result = mysql_query("SELECT hash_short FROM ssdeephashtab") or die(mysql_error());

//check for empty result
if(mysql_num_rows($result) > 0){
    //looping through all results
    //hashes node
    $response["hashes"] = array();
    
    while($row= mysql_fetch_array($result)){
     //temp user array
        $hash=array();
        $hash["hash_short"] = $row["hash_short"];

        //push single hash into final response
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