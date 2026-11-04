package com.workinon.modelo;

public class Perfil {
    private int idPerfil;
    private String nombre;

    public Perfil(int idPerfil, String nombre) {
        this.idPerfil = idPerfil;
        this.nombre = nombre;
    }

    public int getIdPerfil() {
        return idPerfil;
    }

    public String getNombre() {
        return nombre;
    }
}
