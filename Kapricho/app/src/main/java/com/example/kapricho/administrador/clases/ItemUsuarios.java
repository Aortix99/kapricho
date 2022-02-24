package com.example.kapricho.administrador.clases;

public class ItemUsuarios {
    String id;
    String nombre;
    String rol;

    public ItemUsuarios(){

    }

    public ItemUsuarios(String nombre, String rol, String id) {
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
