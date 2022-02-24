<?php
include 'conexion.php';
$id_user = $_POST['id_usuario'];
$nombre = $_POST['nombre'];
$apellido = $_POST['apellido'];
$usuario = $_POST['usuario'];
$correo = $_POST['correo'];
$clave = $_POST['clave'];
$rol = $_POST['rol'];

$confirmacion = "SELECT * FROM cliente WHERE usuario_cliente = '$usuario' AND NOT Id_cliente = '$id_user'";
$consulta = mysqli_query($conexion, $confirmacion);

if(mysqli_num_rows($consulta) > 0){
    echo "usuario existe";
} else {
    $sql = "UPDATE cliente SET nombre_cliente = '$nombre', apellido_cliente = '$apellido', usuario_cliente = '$usuario', clave_cliente = '$clave', rol = '$rol', correo_cliente = '$correo' WHERE Id_cliente = '$id_user'";
    $ejecucion = mysqli_query($conexion, $sql);

    if($ejecucion){
        echo "actualizacion exitosa";
    } else {
        echo "error al actualizar";
    }
}

?>