<?php
include 'conexion.php';

$usuario = $_GET['usuario'];

$sql = "SELECT id_usuario, nombre, apellido FROM usuarios WHERE usuario = '$usuario'";
$consultar = mysqli_query($conexion, $sql);

if(mysqli_num_rows($consultar) > 0){
    $fila = mysqli_fetch_array($consultar);
    $producto[] = array_map("utf8_encode",  $fila);
    echo json_encode($producto, JSON_UNESCAPED_UNICODE);
}
?>