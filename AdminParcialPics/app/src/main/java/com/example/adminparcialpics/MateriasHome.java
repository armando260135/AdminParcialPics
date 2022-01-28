package com.example.adminparcialpics;

public class MateriasHome {

    private String nombre;
    private int foto;
    private String codigo;
    public MateriasHome(){}

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public MateriasHome(String nombre, int foto, String codigo) {
        this.nombre = nombre;
        this.foto = foto;
        this.codigo = codigo;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getFoto() {
        return foto;
    }

    public void setFoto(int foto) {
        this.foto = foto;
    }
}
