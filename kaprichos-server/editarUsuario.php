<?php
include 'conexion.php';
$id_user = $_POST['id_usuario'];
$nombre = $_POST['nombre'];
$apellido = $_POST['apellido'];
$usuario = $_POST['usuario'];
$clave = $_POST['clave'];
$rol = $_POST['rol'];

$confirmacion = "SELECT * FROM usuarios WHERE usuario = '$usuario' AND NOT id_usuario = '$id_user'";
$consulta = mysqli_query($conexion, $confirmacion);

if(mysqli_num_rows($consulta) > 0){
    echo "usuario existe";
} else {
    $sql = "UPDATE usuarios SET nombre = '$nombre', apellido = '$apellido', usuario = '$usuario', clave = '$clave', rol = '$rol' WHERE id_usuario = '$id_user'";
    $ejecucion = mysqli_query($conexion, $sql);

    if($ejecucion){
        echo "actualizacion exitosa";
    } else {
        echo "error al actualizar";
    }
}

?>