<?php
include 'conexion.php';

$usuario = $_GET['usuario'];
$clave = $_GET['clave'];

$sql = "SELECT * FROM usuarios WHERE usuario = '$usuario' AND clave = '$clave'";
$consultar = mysqli_query($conexion, $sql);

$cliente = "SELECT * FROM cliente WHERE usuario_cliente = '$usuario' AND clave_cliente = '$clave'";
$consultar_cliente = mysqli_query($conexion, $cliente);


if(mysqli_num_rows($consultar) > 0){
    $fila = mysqli_fetch_array($consultar);
    $producto[] = array_map("utf8_encode",  $fila);
    echo json_encode($producto, JSON_UNESCAPED_UNICODE);
} else if(mysqli_num_rows($consultar_cliente) > 0){
    $fila = mysqli_fetch_array($consultar_cliente);
    $producto[] = array_map("utf8_encode", $fila);
    echo json_encode($producto, JSON_UNESCAPED_UNICODE);
}

?>