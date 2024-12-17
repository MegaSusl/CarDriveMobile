<?php 
require_once 'db_connect.php';

$response = array();

if($_SERVER['REQUEST_METHOD']=='GET'){
        $all = $connect->query("SELECT `id`, `image_src` FROM `cars`");
        if ($all) {
            $response["success"] = true;
            $response["message"] = "cars imgs";
            $response["list"] = array();
            $rows = mysqli_fetch_all($all);
            foreach ($rows as $row) {
                $list = array();
                $list["id"] = $row["0"];
                $list["image"] = file_get_contents($row["1"]);
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
$response["message"] = $_SERVER;

echo json_encode($response);}

//header('Location: /index.php');
?>