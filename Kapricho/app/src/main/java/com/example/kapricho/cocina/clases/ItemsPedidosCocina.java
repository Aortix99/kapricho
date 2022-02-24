package com.example.kapricho.cocina.clases;

public class ItemsPedidosCocina {
    String NoPedido;
    String Mesa;
    String NombreCliente;
    String estadoPedido;
    String correo;
    String total;

    public ItemsPedidosCocina(){

    }

    public ItemsPedidosCocina(String noPedido, String mesa, String nombreCliente, String estadoPedido, String correo, String total) {
        this.NoPedido = noPedido;
        this.Mesa = mesa;
        this.NombreCliente = nombreCliente;
        this.estadoPedido = estadoPedido;
        this.correo = correo;
        this.total = total;
    }

    public String getNoPedido() {
        return NoPedido;
    }

    public void setNoPedido(String noPedido) {
        this.NoPedido = noPedido;
    }

    public String getMesa() {
        return Mesa;
    }

    public void setMesa(String mesa) {
        this.Mesa = mesa;
    }

    public String getNombreCliente() {
        return NombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.NombreCliente = nombreCliente;
    }

    public String getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(String estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
