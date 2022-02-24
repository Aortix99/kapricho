<?php
include 'conexion.php';
$token = $_GET['token'];

$sql = "SELECT p.id_producto, p.descripcion, c.cantidad_carrito, c.total_carrito AS p_unitario, (c.cantidad_carrito * c.total_carrito) AS total_pagar, p.existencia ,p.imagen FROM carrito c INNER JOIN producto p ON p.id_producto = c.id_producto_carrito WHERE c.token_user = '$token'";
$consultar = mysqli_query($conexion, $sql);

if(mysqli_num_rows($consultar) > 0){
    while($fila = mysqli_fetch_array($consultar)){
        $producto[] = array_map("utf8_encode",  $fila);
    }
}
echo json_encode($producto, JSON_UNESCAPED_UNICODE);
?>