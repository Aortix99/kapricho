<?php
include 'conexion.php';

$sql = "SELECT * FROM producto";
$consultar = mysqli_query($conexion, $sql);

if(mysqli_num_rows($consultar) > 0){
    while($fila = mysqli_fetch_array($consultar)){
        $producto[] = array_map("utf8_encode",  $fila);
    }
}
echo json_encode($producto, JSON_UNESCAPED_UNICODE);
?>