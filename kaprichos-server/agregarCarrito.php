<?php
include 'conexion.php';

$id_producto = $_POST['id_producto'];
$token = $_POST['token_user'];
$cantidad = $_POST['cantidad'];

$confirm = "SELECT * FROM carrito WHERE id_producto_carrito = '$id_producto' AND token_user = '$token'";
$consulta_conf = mysqli_query($conexion, $confirm);

if(mysqli_num_rows($consulta_conf) > 0){
    echo "item agregado";
} else {

    $sql = "CALL add_carrito($id_producto, '$cantidad', '$token')";
    $consulta = mysqli_query($conexion, $sql);

    if($consulta){
        echo "Agregado correctamente";
    } else { 
        echo "Error al agregar";
    }

}

?>