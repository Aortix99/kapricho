<?php
include 'conexion.php';
$token = $_POST['token'];

$sql = "DELETE FROM carrito WHERE carrito.token_user = '$token'";
$consultar = mysqli_query($conexion, $sql);

if($consultar){
    echo "exitoso";
} else {
    echo "error";
}

?>