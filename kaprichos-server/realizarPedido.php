<?php
include 'conexion.php';
$token = $_POST['token'];
$mesa = $_POST['mesa'];

$sql_id = "SELECT Id_cliente FROM cliente WHERE usuario_cliente = '$token'";
$consulta_id = mysqli_query($conexion, $sql_id);

if(mysqli_num_rows($consulta_id) > 0){
    $id_array = mysqli_fetch_array($consulta_id);
    $id = $id_array['Id_cliente'];

    $sql = "CALL procesar_pedido($id, $id, '$token', $mesa)";
    $consultar = mysqli_query($conexion, $sql);

    if($consultar){
        echo "exitoso";
    } else { 
        echo "error";
    }
} else {
    echo "Error al encontrar id";
}
?>