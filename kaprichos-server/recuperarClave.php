<?php
include 'conexion.php';

$correo = $_POST['correo'];

$sql_confirm = "SELECT usuario_cliente, clave_cliente FROM cliente WHERE correo_cliente = '$correo'";
$consultar = mysqli_query($conexion, $sql_confirm);

if(mysqli_num_rows($consultar) > 0){
    $datos = mysqli_fetch_array($consultar);
    $usuario = $datos["usuario_cliente"];
    $clave = $datos["clave_cliente"];

    echo "existe;".$usuario.";".$clave."";
} else {
    echo "error usuario";
}
?>