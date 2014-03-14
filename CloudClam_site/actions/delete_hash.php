<?

/*
Following code is to
delete a hash using 
long hash
*/

$response = array();

if(isset($_POST['hash_long'])){
    $hash_long = $_POST['hash_long'];
    
    //include db connect class
    require_once(__DIR__.'/../db/db_connect.php');

    //connecting to db
    $db=new DB_CONNECT();
    
    //mysql update row with matched hash_long
    $result = mysql_query("DELETE FROM hashtab WHERE hash_long = $hash_long");
    
    //check if rows deleted or not
    if(mysql_affected_rows() > 0){
        //successfully updated
        $response["success"] = 1;
        $response["message"] = "Hash $hash_long has been deleted!";
        
        echo json_encode($response);
    }else{
        //no hash found
        $response["success"] = 0;
        $response["message"] = "Hash not Found";
        echo json_encode($response);
    }
}else{
        $response["success"] = 0;
        $response["message"] = "Problem in Deleting";
        echo json_encode($response);
}

?>