<?php
include 'conexion.php';

$fecha = $_GET['fecha'];

$sql = "SELECT SUM(total) AS recaudo FROM `pedido` WHERE fecha LIKE '%$fecha%' AND estado = 'Pagado'";
$consulta = mysqli_query($conexion, $sql);

if(mysqli_num_rows($consulta) > 0){
    while($fila = mysqli_fetch_array($consulta)){
        $producto[] = array_map("utf8_encode",  $fila);
    }
}
echo json_encode($producto, JSON_UNESCAPED_UNICODE);
?>