<?php
include 'conexion.php';

$sql = "SELECT f.id_pedido,f.total, f.fecha, cl.nombre_cliente, cl.apellido_cliente,cl.Id_cliente,cl.correo_cliente, f.estado, f.total, f.mesa, f.medio_de_pago, f.estado_cocina FROM pedido f INNER JOIN cliente cl ON f.id_cliente = cl.Id_cliente WHERE f.estado_cocina = 'Recibido' OR f.estado_cocina = 'En preparacion' ORDER BY f.id_pedido ASC";

$consultar = mysqli_query($conexion, $sql);

if(mysqli_num_rows($consultar) > 0){
    while($fila = mysqli_fetch_array($consultar)){
        $producto[] = array_map("utf8_encode",  $fila);
    }
}
echo json_encode($producto, JSON_UNESCAPED_UNICODE);
?>