package com.workinon.modelo.evaluacion;

/**
 * INTERFAZ — Notificable
 * Demuestra:
 *  - INTERFACES EN JAVA: Define métodos que las clases deben implementar obligatoriamente.
 */
public interface Notificable {
    String generarMensajeNotificacion();
    boolean enviarNotificacion(String destinatario);
}
