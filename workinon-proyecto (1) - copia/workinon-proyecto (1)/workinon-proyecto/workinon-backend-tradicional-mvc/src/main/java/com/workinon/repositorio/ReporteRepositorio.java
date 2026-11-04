package com.workinon.repositorio;

import com.workinon.modelo.Reporte;

/**
 * REPOSITORIO — ReporteRepositorio
 * Los reportes solo se guardan (nadie los lista de vuelta desde la
 * página en esta versión), por eso es el repositorio más corto.
 */
public class ReporteRepositorio {

    public static void guardar(Reporte reporte) {
        // Los reportes no se guardan en tabla propia en esta versión
        // tradicional simplificada: se quedan en memoria para poder
        // mostrarlos/depurarlos durante la demo.
        AlmacenMemoria.reportes.add(reporte);
    }
}
