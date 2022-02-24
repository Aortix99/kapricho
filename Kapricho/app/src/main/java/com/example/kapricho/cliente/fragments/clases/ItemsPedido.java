package com.example.kapricho.cliente.fragments.clases;

public class ItemsPedido {
    String id_pedido;
    String mesa;
    String estado;
    String total;

    public ItemsPedido(){

    }

    public ItemsPedido(String id_pedido, String mesa, String estado, String total) {
        this.id_pedido = id_pedido;
        this.mesa = mesa;
        this.estado = estado;
        this.total = total;
    }

    public String getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(String id_pedido) {
        this.id_pedido = id_pedido;
    }

    public String getMesa() {
        return mesa;
    }

    public void setMesa(String mesa) {
        this.mesa = mesa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
