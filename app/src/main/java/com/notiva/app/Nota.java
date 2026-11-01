
package com.notiva.app;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "notas",

        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "id",
                childColumns = "usuarioId",
                onDelete = ForeignKey.CASCADE
        ),

        indices = {@Index("usuarioId")}
)
public class Nota {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int usuarioId;

    public String titulo;

    public String contenido;

    public String emocion;

    public long fechaCreacion;
}
