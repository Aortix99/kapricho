<?php
include 'conexion.php';

$id_pedido = $_POST['id_pedido'];
$estado = $_POST['estado'];

if($estado == 'Cancelado'){
    $sql = "UPDATE pedido SET estado_cocina = '$estado', estado = '$estado', medio_de_pago = '$estado' WHERE id_pedido = '$id_pedido'";
    $consulta = mysqli_query($conexion, $sql);

    if($consulta){
        echo 'Actualizacion exitosa';
    } else {
        echo 'Error al actualizar';
    }
} else {
    $sql = "UPDATE pedido SET estado_cocina = '$estado' WHERE id_pedido = '$id_pedido'";
    $consulta = mysqli_query($conexion, $sql);

    if($consulta){
        echo 'Actualizacion exitosa';
    } else {
        echo 'Error al actualizar';
    }
}
?>