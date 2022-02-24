<?php
include 'conexion.php';

date_default_timezone_set('America/Bogota');

$id_producto = $_POST['id_producto'];
$descripcion = $_POST['descripcion'];
$precio = $_POST['precio'];
$disponibilidad = $_POST['disponibilidad'];
$imagen = $_POST['imagen'];
$categoria = $_POST['categoria'];

$sql = "SELECT imagen FROM producto WHERE id_producto = '$id_producto'";
$consultar = mysqli_query($conexion, $sql);

$filename = "imgProductos/".date('m-d-Yh:i:sa', time()).".jpeg";

if (!file_exists("imgProductos/")) {
    mkdir("imgProductos/", 755, true);
}

if(mysqli_num_rows($consultar) > 0){

    $fila = mysqli_fetch_array($consultar);

    $foto = $fila["imagen"];
    
        if($imagen == "vacio"){
            $update2 = "UPDATE producto SET precio = '$precio', descripcion = '$descripcion', imagen = '$foto', existencia = '$disponibilidad', categoria = '$categoria' WHERE id_producto = '$id_producto'";
            $query2 = mysqli_query($conexion, $update2);

            if($query2){
                echo "actualizacion exitosa";
            } else {
                echo "error al actualizar 1";
            }

        } else {
            if($fila['imagen'] != 'imgProductos/vacio.png'){
                
                if(unlink($fila['imagen'])){
                    $upload = file_put_contents($filename, base64_decode($imagen));

                    if($upload){
                        $update3 = "UPDATE producto SET precio = '$precio', descripcion = '$descripcion', imagen = '$filename', existencia = '$disponibilidad', categoria = '$categoria' WHERE id_producto = '$id_producto'";
                        $query3 = mysqli_query($conexion, $update3);
            
                        if($query3){
                            echo "actualizacion exitosa";
                        } else {
                            echo "error al actualizar 2";
                        }
                    }

                } else {
                    echo "Error al actualizar imagen";
                }

            } else {
                $upload2 = file_put_contents($filename, base64_decode($imagen));
                if($upload2){
                    $update4 = "UPDATE producto SET precio = '$precio', descripcion = '$descripcion', imagen = '$filename', existencia = '$disponibilidad', categoria = '$categoria' WHERE id_producto = '$id_producto'";
                    $query4 = mysqli_query($conexion, $update4);
        
                    if($query4){
                        echo "actualizacion exitosa";
                    } else {
                        echo "error al actualizar 2";
                    }
                }
            }
        }
    
    
} 