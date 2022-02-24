<?php
include 'conexion.php';

$nombre = $_POST['nombre'];
$apellido = $_POST['apellido'];
$usuario = $_POST['usuario'];
$clave = $_POST['clave'];
$rol = $_POST['rol'];

$confirmacion = "SELECT * FROM usuarios WHERE usuario = '$usuario'";
$consulta = mysqli_query($conexion, $confirmacion);

if(mysqli_num_rows($consulta) > 0){
    echo "usuario existe";
} else {
    $sql = "INSERT INTO usuarios VALUES (0, '$nombre', '$apellido', '$usuario', '$clave', '$rol')";
    $ejecucion = mysqli_query($conexion, $sql);

    if($ejecucion){
        echo "ingreso exitoso";
    } else {
        echo "error al agregar usuario";
    }
}
?>