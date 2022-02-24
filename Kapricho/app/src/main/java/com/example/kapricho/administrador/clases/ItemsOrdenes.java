package com.example.kapricho.administrador.clases;

public class ItemsOrdenes {
    String noOrden;
    String mesa;
    String nombre_cli;
    String id_cliente;
    String total;
    String estatus;
    String medido_pago;

    public ItemsOrdenes(){

    }

    public ItemsOrdenes(String noOrden, String mesa, String nombre_cli, String id_cliente, String total, String estatus,
                        String medido_pago) {
        this.noOrden = noOrden;
        this.mesa = mesa;
        this.nombre_cli = nombre_cli;
        this.id_cliente = id_cliente;
        this.total = total;
        this.estatus = estatus;
        this.medido_pago = medido_pago;
    }

    public String getNoOrden() {
        return noOrden;
    }

    public void setNoOrden(String noOrden) {
        this.noOrden = noOrden;
    }

    public String getMesa() {
        return mesa;
    }

    public void setMesa(String mesa) {
        this.mesa = mesa;
    }

    public String getNombre_cli() {
        return nombre_cli;
    }

    public void setNombre_cli(String nombre_cli) {
        this.nombre_cli = nombre_cli;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getMedido_pago() {
        return medido_pago;
    }

    public void setMedido_pago(String medido_pago) {
        this.medido_pago = medido_pago;
    }


}
