package com.notiva.app;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UsuarioDao {

    // Guardar un usuario nuevo
    @Insert
    long insertar(Usuario usuario);

    // Buscar un usuario por su correo
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    Usuario buscarPorCorreo(String correo);

    // Buscar un usuario por su ID
    @Query("SELECT * FROM usuarios WHERE id = :usuarioId LIMIT 1")
    Usuario buscarPorId(int usuarioId);

}
