<?php 
require_once 'db_connect.php';

$response = array();
// echo '<pre>';
// print_r($_SERVER);
// echo '</pre>';
if($_SERVER['REQUEST_METHOD']=='POST'){
    if(isset($_POST['login']) and isset($_POST['pass'])){
        $login = $_POST['login'];
        $pass = $_POST['pass'];
        $all = $connect->query("SELECT * FROM `users` WHERE `name` LIKE '$login' AND `password` LIKE '$pass'");
        if ($all) {
            $response["success"] = true;
            $response["message"] = "Welcome";
            $response["login"] = $login;
            while ($row = mysqli_fetch_array($all,MYSQLI_ASSOC)) {
            
            $response["id"] = $row["id"];
            $response["role"]=$row["role"];
        
            echo json_encode($response);
            }
        } else {
            $response["success"] = false;
            $response["message"] = "Oops! An error occurred.";
        
            echo json_encode($response);
        }
    }
}
else{
$response["success"] = false;
$response["message"] = "Error.";

echo json_encode($response);}

//header('Location: /index.php');
?>