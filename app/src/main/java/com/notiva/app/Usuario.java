package com.notiva.app;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "usuarios",
        indices = {@Index(value = {"correo"}, unique = true)}
)
public class Usuario {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nombre;

    public String correo;

    public String passwordHash;

    public String passwordSalt;
}
