<?php
include 'conexion.php';
$id_venta = $_GET['id_pedido'];
$id_cliente = $_GET['id_cliente'];

$query_factura = "SELECT f.id_pedido, DATE_FORMAT(f.fecha, '%d/%m/%Y') AS fecha,
DATE_FORMAT(f.fecha, '%H:%i:%s') AS hora, f.estado, f.medio_de_pago,f.total,
                                    cl.Id_cliente, cl.nombre_cliente, cl.apellido_cliente, cl.correo_cliente
                                    FROM pedido f
                                    INNER JOIN cliente cl
                                    ON f.id_cliente = cl.Id_cliente
                                    WHERE f.id_pedido = '$id_venta' AND f.id_cliente = '$id_cliente' AND f.estado != 10;";

$consultar = mysqli_query($conexion, $query_factura);

if(mysqli_num_rows($consultar) > 0){
    while($fila = mysqli_fetch_array($consultar)){
        $producto[] = array_map("utf8_encode",  $fila);
    }
}
echo json_encode($producto, JSON_UNESCAPED_UNICODE);
?>