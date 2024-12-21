<?php 
require_once 'db_connect.php';

$response = array();

if($_SERVER['REQUEST_METHOD']=='POST'){
    if(isset($_POST['id'])){
        $id = $_POST['id'];
        $all = $connect->query("DELETE FROM cars WHERE `cars`.`id` = $id");
        $response["success"] = true;
        
        echo json_encode($response);
    }
}
else{
$response["success"] = false;
$response["message"] = "Error.";

echo json_encode($response);
}

//header('Location: /index.php');
?>