<?php 
require_once 'db_connect.php';?>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <link rel="stylesheet" href="/css/style.css">
    <title>Portfolio Base</title>
</head>
<body>
    
<br>
Добавить пользователя:
    <form action="/add.php" method=POST>
        <input type="text" name="name" placeholder="Имя"><br>
        <input type="text" name="phone"  placeholder="Номер телефона">
        <input type="submit" name="submit" value="Добавить">
    </form>
        
    <br>
Удалить пользователя с айди:
    <form action="/delete.php" method=POST>
        <input type="text" name="id" placeholder="Айди">
        <input type="submit" name="submit" value="Удалить">
    </form>
    <br>
    Изменить имя пользователя с айди:
    <form action="/update_name.php" method=POST>
        <input type="text" name="id" placeholder="Айди"><br>
        <input type="text" name="name" placeholder="Имя">
        <input type="submit" name="submit" value="Изменить">
    </form>
    <br>
    Изменить телефон пользователя с айди:
    <form action="/update_phone.php" method=POST>
        <input type="text" name="id" placeholder="Айди"><br>
        <input type="text" name="phone" placeholder="телефон">
        <input type="submit" name="submit" value="Изменить">
    </form>






</body>
</html>