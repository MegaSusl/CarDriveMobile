<?php 
require_once 'db_connect.php';

$response = array();

if($_SERVER['REQUEST_METHOD'] == 'GET' && $_REQUEST['id'] && $_REQUEST['phone']){
        $userId = $_REQUEST['id'];
        $newPhone = $_REQUEST['phone'];
        $all = $connect->query("UPDATE `users` SET `phone` = $newPhone WHERE `users`.`id` = $userId;");        
        if ($all) {
            $response["success"] = true;
            $response["message"] = "Номер обновлён!";
            
            echo json_encode($response);            
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

//header('Location: /index.php');
?>