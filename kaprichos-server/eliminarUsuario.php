<?php
include 'conexion.php';

$id_user = $_POST['id_usuario'];

$sql = "DELETE FROM usuarios WHERE id_usuario = '$id_user'";
$ejecucion = mysqli_query($conexion, $sql);

if($ejecucion){
    echo "eliminacion exitosa";
} else {
    echo "error al eliminar";
}
?>