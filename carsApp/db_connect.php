<?php
define('DB_HOST','localhost');
define('DB_USER','root');
define('DB_PASSWORD','96325874k');
define('DB_NAME','carApp');

$connect = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME);
if(!$connect){
    die('error connect to database!');
}
$connect->set_charset('utf8');

?>