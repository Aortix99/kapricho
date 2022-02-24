<?php
include 'conexion.php';
$id_pedido = $_GET['id_pedido'];

$sql = "SELECT p.descripcion, dt.cantidad, dt.total,(dt.cantidad * dt.total) AS precio_total, f.estado_cocina, p.imagen FROM pedido f INNER JOIN detalle_pedido dt ON f.id_pedido = dt.id_pedido INNER JOIN producto p ON dt.id_producto = p.id_producto WHERE f.id_pedido = $id_pedido";

$consultar = mysqli_query($conexion, $sql);

if(mysqli_num_rows($consultar) > 0){
    while($fila = mysqli_fetch_array($consultar)){
        $producto[] = array_map("utf8_encode",  $fila);
    }
}
echo json_encode($producto, JSON_UNESCAPED_UNICODE);
?>