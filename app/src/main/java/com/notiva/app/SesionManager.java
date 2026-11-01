package com.notiva.app;

import android.content.Context;
import android.content.SharedPreferences;

public class SesionManager {

    private static final String ARCHIVO = "notiva_sesion";

    private static final String CLAVE_ID = "USUARIO_ID";
    private static final String CLAVE_NOMBRE = "USUARIO_NOMBRE";

    private final SharedPreferences preferencias;

    public SesionManager(Context contexto) {

        preferencias = contexto.getSharedPreferences(
                ARCHIVO,
                Context.MODE_PRIVATE
        );
    }

    // Guardar una sesión después del login
    public void iniciarSesion(int usuarioId, String nombre) {

        preferencias.edit()
                .putInt(CLAVE_ID, usuarioId)
                .putString(CLAVE_NOMBRE, nombre)
                .apply();
    }

    // Comprobar si existe una sesión guardada
    public boolean haySesion() {

        return obtenerUsuarioId() > 0;
    }

    // Recuperar el identificador del usuario
    public int obtenerUsuarioId() {

        return preferencias.getInt(CLAVE_ID, -1);
    }

    // Recuperar el nombre del usuario
    public String obtenerNombre() {

        return preferencias.getString(CLAVE_NOMBRE, "");
    }

    // Cerrar sesión sin borrar las notas
    public void cerrarSesion() {

        preferencias.edit()
                .remove(CLAVE_ID)
                .remove(CLAVE_NOMBRE)
                .apply();
    }
}
