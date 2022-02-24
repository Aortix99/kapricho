<?php
include 'conexion.php';
$id_pedido = $_POST['id_pedido'];
$medio = $_POST['medio_pago'];

$sql = "CALL cobro_pedido($id_pedido, '$medio')";
$query = mysqli_query($conexion, $sql);

if(mysqli_num_rows($query) > 0){
    echo "cobro exitoso";
} else {
    echo "error al cobrar";
}
?>