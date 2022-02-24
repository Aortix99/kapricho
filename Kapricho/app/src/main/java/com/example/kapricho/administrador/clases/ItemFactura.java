package com.example.kapricho.administrador.clases;

public class ItemFactura {
    String producto;
    String cantidad;
    String total;
    String unit;

    public ItemFactura(){

    }

    public ItemFactura(String producto, String cantidad, String total, String unit) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.total = total;
        this.unit = unit;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
