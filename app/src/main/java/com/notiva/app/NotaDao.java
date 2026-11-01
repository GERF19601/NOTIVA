
package com.notiva.app;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotaDao {

    // CREATE: crear una nota
    @Insert
    long insertar(Nota nota);

    // READ: consultar las notas de un usuario
    @Query("SELECT * FROM notas " +
            "WHERE usuarioId = :usuarioId " +
            "ORDER BY fechaCreacion DESC")
    List<Nota> obtenerPorUsuario(int usuarioId);

    // UPDATE: modificar una nota del usuario
    @Query("UPDATE notas " +
            "SET titulo = :titulo, " +
            "contenido = :contenido, " +
            "emocion = :emocion " +
            "WHERE id = :notaId AND usuarioId = :usuarioId")
    int actualizar(
            int notaId,
            int usuarioId,
            String titulo,
            String contenido,
            String emocion
    );


    @Query("SELECT * FROM notas " +
            "WHERE id = :notaId AND usuarioId = :usuarioId " +
            "LIMIT 1")
    Nota buscarPorId(int notaId, int usuarioId);


    // DELETE: eliminar una nota del usuario
    @Query("DELETE FROM notas " +
            "WHERE id = :notaId AND usuarioId = :usuarioId")
    int eliminar(int notaId, int usuarioId);
}
