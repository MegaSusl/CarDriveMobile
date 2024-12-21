<?php 
require_once 'db_connect.php';

$response = array();

if($_SERVER['REQUEST_METHOD']=='POST'){
        $all = $connect->query("SELECT `id`, `owner_id`, `info` FROM `cars`");        
        if ($all) {
            $response["success"] = true;
            $response["message"] = "cars list";
            $response["list"] = array();
            $rows = mysqli_fetch_all($all);
            foreach ($rows as $row) {
                $list = array();
                $owner_id = $row["1"];
                $phone = mysqli_fetch_all($connect->query("SELECT `phone` FROM `users` WHERE `id` = $owner_id"))[0][0];
                $list["id"] = $row["0"];
                $list["info"] = $row["2"];
                $list["phone"] = $phone;
            array_push($response["list"], $list);
            }
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

//header('Location: /index.php');
?>