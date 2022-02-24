<?php
include 'conexion.php';
$token = $_GET['token'];

$sql = "SELECT SUM(c.cantidad_carrito * c.total_carrito) AS total_carrito, COUNT(*) AS productos FROM carrito c WHERE c.token_user = '$token'";
$consultar = mysqli_query($conexion, $sql);

if(mysqli_num_rows($consultar) > 0){
    while($fila = mysqli_fetch_array($consultar)){
        $producto[] = array_map("utf8_encode",  $fila);
    }
}
echo json_encode($producto, JSON_UNESCAPED_UNICODE);
?>