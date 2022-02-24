<?php
include 'conexion.php';

date_default_timezone_set('America/Bogota');

$descripcion = $_POST['nombre'];
$precio = $_POST['precio'];
$disponibilidad = $_POST['existencia'];
$categoria = $_POST['categoria'];
$imagen = $_POST['imagen'];

$filename = "imgProductos/".date('m-d-Yh:i:sa', time()).".jpeg";

if (!file_exists("imgProductos/")) {
    mkdir("imgProductos/", 755, true);
}

if($imagen != "vacio"){
    $upload = file_put_contents($filename, base64_decode($imagen));
    if($upload){
        $sql = "INSERT INTO producto VALUES(0, '$precio', '$descripcion', '$filename', '$disponibilidad', '$categoria')";
        $consulta = mysqli_query($conexion, $sql);

        if($consulta){
            echo "ingreso exitoso";
        } else {
            echo "error al agregar producto";
        }
    } else {
        echo "Error al subir imagen";
    }
} else {
    $sql = "INSERT INTO producto VALUES(0, '$precio', '$descripcion', 'imgProductos/vacio.png', '$disponibilidad', '$categoria')";
    $consulta = mysqli_query($conexion, $sql);

    if($consulta){
        echo "ingreso exitoso";
    } else {
        echo "error al agregar producto";
    }
}
?>