<?php
include 'conexion.php';

$id_producto = $_POST['id_producto'];
$imagen = $_POST['imagen'];

$sql = "DELETE FROM producto WHERE id_producto = '$id_producto'";
$ejecucion = mysqli_query($conexion, $sql);

if($ejecucion){
    if($imagen == 'imgProductos/vacio.png'){
        echo "eliminacion exitosa";
    } else {
        unlink($imagen);
        echo "eliminacion exitosa";
    }
} else {
    echo "error al eliminar";
}
?>