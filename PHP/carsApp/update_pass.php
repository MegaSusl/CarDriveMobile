<?php 
require_once 'db_connect.php';

$response = array();

if($_SERVER['REQUEST_METHOD'] == 'POST' && $_REQUEST['id'] && $_REQUEST['pass_old'] && $_REQUEST['pass_new']){
        $userId = $_REQUEST['id'];
        $pass_old = $_REQUEST['pass_old'];
        $pass_new = $_REQUEST['pass_new'];
        $all = $connect->query("UPDATE `users` SET `password` = $pass_new WHERE `users`.`id` = $userId AND `users`.`password` = '$pass_old'");
        if ($all) {
            $response["success"] = true;
            $response["message"] = "Пароль обновлён!";
            
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