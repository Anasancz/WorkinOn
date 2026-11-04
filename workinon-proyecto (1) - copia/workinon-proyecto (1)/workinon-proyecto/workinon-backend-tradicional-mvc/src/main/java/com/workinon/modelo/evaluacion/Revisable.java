package com.workinon.modelo.evaluacion;

/**
 * INTERFAZ — Revisable
 * Demuestra:
 *  - POLIMORFISMO MEDIANTE INTERFACES: Permite tratar de forma homogénea cualquier objeto que requiera revisión.
 */
public interface Revisable {
    boolean necesitaRevisionManual();
    void registrarObservaciones(String notas);
}
