<?php
include 'conexion.php';
date_default_timezone_set('America/Bogota');
$fecha = date('Y-m-d');
$sql = "SELECT COUNT(*) AS pedidos FROM `pedido` WHERE estado_cocina = 'Recibido'";
$consulta = mysqli_query($conexion, $sql);

if(mysqli_num_rows($consulta) > 0){
    while($fila = mysqli_fetch_array($consulta)){
        $producto[] = array_map("utf8_encode",  $fila);
    }
}
echo json_encode($producto, JSON_UNESCAPED_UNICODE);
?>