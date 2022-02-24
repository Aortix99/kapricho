package com.example.kapricho.cliente.fragments.clases;

public class ItemDetallePedidoCliente {
    String imagen;
    String descripcion;
    String cantidad;

    public ItemDetallePedidoCliente(){

    }

    public ItemDetallePedidoCliente(String imagen, String descripcion, String cantidad) {
        this.imagen = imagen;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }
}
