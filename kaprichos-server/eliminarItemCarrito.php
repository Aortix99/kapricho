<?php
include 'conexion.php';
$token = $_POST['token'];
$id_producto = $_POST['id_producto'];

$sql = "DELETE FROM carrito WHERE id_producto_carrito = '$id_producto' AND token_user = '$token'";
$consulta = mysqli_query($conexion, $sql);

if($consulta){
    echo "exitoso";
} else {
    echo "error";
}
?>