<?php
include 'conexion.php';

$nombre = $_POST['nombre'];
$apellido = $_POST['apellido'];
$usuario = $_POST['usuario'];
$correo = $_POST['correo'];
$clave = $_POST['clave'];
$rol = $_POST['rol'];

$confirmacion = "SELECT * FROM cliente WHERE usuario_cliente = '$usuario'";
$consulta = mysqli_query($conexion, $confirmacion);

if(mysqli_num_rows($consulta) > 0){
    echo "usuario existe";
} else {
    $sql = "INSERT INTO cliente VALUES (0, '$nombre', '$apellido', '$usuario', '$clave', '$correo', '$rol')";
    $ejecucion = mysqli_query($conexion, $sql);

    if($ejecucion){
        echo "ingreso exitoso";
    } else {
        echo "error al agregar usuario";
    }
}
?>