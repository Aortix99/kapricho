package com.example.kapricho.cliente.fragments.clases;

public class ItemsHome {
    String id;
    String id_cliente;
    String descripcion;
    String precio;
    String disponibilidad;
    String imagen;

    public ItemsHome(){

    }

    public ItemsHome(String id, String id_cliente, String descripcion, String precio, String disponibilidad, String imagen) {
        this.id = id;
        this.id_cliente = id_cliente;
        this.descripcion = descripcion;
        this.precio = precio;
        this.disponibilidad = disponibilidad;
        this.imagen = imagen;
    }

    public String getId() {
        return id;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
