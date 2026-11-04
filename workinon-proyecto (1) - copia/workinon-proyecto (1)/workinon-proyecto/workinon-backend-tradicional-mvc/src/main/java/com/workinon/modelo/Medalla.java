package com.workinon.modelo;

public class Medalla {
    private int idMedalla;
    private String nombre;

    public Medalla(int idMedalla, String nombre) {
        this.idMedalla = idMedalla;
        this.nombre = nombre;
    }

    public int getIdMedalla() {
        return idMedalla;
    }

    public String getNombre() {
        return nombre;
    }
}
