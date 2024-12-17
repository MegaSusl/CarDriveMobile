<?php 
require_once 'db_connect.php';

$response = array();

if($_SERVER['REQUEST_METHOD']=='POST'){
    if(
        isset($_POST['login']) && isset($_POST['password']) && isset($_POST['phone'])
    ){
        $login = $_POST['login'];
        $phone = $_POST['phone'];
        $password = $_POST['password'];
        $check = $connect->query("SELECT * FROM `users` WHERE `phone` LIKE '$phone' OR `name` LIKE '$login'");
        if($check->num_rows==null){
        
        $all = $connect->query("INSERT INTO `users` (`id`, `name`, `phone`, `password`, `role`) VALUES (NULL, '$login', '$phone', '$password', '2');");
        if ($check->num_rows == 0) {
            $response["success"] = 1;
            $response["message"] = "Acc successfully created.";
        
            echo json_encode($response);
        } else{
            $response["success"] = false;
            $response["message"] = "Oops! An error occurred.";
        
            echo json_encode($response);
        }
        }
        else{
            $response["success"] = false;
            $response["message"] = "Such user is exists in the system";
        }
    }
}
else{
$response["success"] = false;
$response["message"] = "Error.";

echo json_encode($response);}

//header('Location: /index.php');
?>