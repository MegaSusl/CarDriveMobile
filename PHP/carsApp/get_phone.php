<?php 
require_once 'db_connect.php';

$response = array();

if($_SERVER['REQUEST_METHOD'] == 'GET' && $_REQUEST['id']){
        $id = $_REQUEST['id'];
        $all = $connect->query("SELECT `phone` FROM `users` WHERE `id` = $id");        
        if ($all) {
            $response["success"] = true;            
            $response['phone'] = mysqli_fetch_all($all)[0][0];
            
            echo json_encode($response, JSON_UNESCAPED_UNICODE);
        } else {
            $response["success"] = false;
            $response["message"] = "Oops! An error occurred.";
        
            echo json_encode($response);
        }
}
else{
$response["success"] = false;
$response["message"] = "Error.";

echo json_encode($response);}
?>