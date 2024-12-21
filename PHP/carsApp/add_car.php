<?
require_once 'db_connect.php';

$response = array();
// echo '<pre>';
// print_r($_SERVER);
// echo '</pre>';
if($_SERVER['REQUEST_METHOD']=='POST'){
    if(isset($_POST['owner_id']) && isset($_POST['info']) && isset($_POST['image'])){
        $owner_id = $_POST['owner_id'];
        $info = $_POST['info'];
        $image = $_POST['image'];        
        $url = 'images/'  . hash('sha256', 'img_' . rand($_POST['owner_id'], 15)) . '.bln';
        file_put_contents($url, $image);

        $all = $connect->query("INSERT INTO `cars` (`id`, `owner_id`, `info`, `image_src`) VALUES (NULL, '$owner_id', '$info', '$url')"); 
        if ($all) {
            $response["success"] = true;
            $response["message"] = "Объявление размещено";
            
            echo json_encode($response);            
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