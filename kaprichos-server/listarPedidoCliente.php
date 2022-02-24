<?php
include 'conexion.php';
$id_cliente = $_GET['id_cliente'];

$sql = "SELECT p.id_pedido, p.mesa, p.total, p.estado_cocina FROM pedido p WHERE id_cliente = $id_cliente";
$consultar = mysqli_query($conexion, $sql);

if(mysqli_num_rows($consultar) > 0){
    while($fila = mysqli_fetch_array($consultar)){
        $producto[] = array_map("utf8_encode",  $fila);
    }
}
echo json_encode($producto, JSON_UNESCAPED_UNICODE);
?>