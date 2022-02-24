<?php
include 'conexion.php';
$id_pedido = $_POST['id_pedido'];

$sql = "CALL anular_venta($id_pedido)";
$query = mysqli_query($conexion, $sql);

if(mysqli_num_rows($query) > 0){
    echo "anulacion exitoso";
} else {
    echo "error al anular";
}
?>