
package com.notiva.app;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {Usuario.class, Nota.class},
        version = 2,
        exportSchema = false
)
public abstract class NotivaDatabase extends RoomDatabase {

    // Operaciones de usuarios y notas
    public abstract UsuarioDao usuarioDao();
    public abstract NotaDao notaDao();

    // Migración de la versión 1 a la 2
    private static final Migration MIGRATION_1_2 =
            new Migration(1, 2) {

                @Override
                public void migrate(
                        @NonNull SupportSQLiteDatabase database) {

                    database.execSQL(
                            "CREATE TABLE IF NOT EXISTS `notas` (" +
                                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                    "`usuarioId` INTEGER NOT NULL, " +
                                    "`titulo` TEXT, " +
                                    "`contenido` TEXT, " +
                                    "`emocion` TEXT, " +
                                    "`fechaCreacion` INTEGER NOT NULL, " +
                                    "FOREIGN KEY(`usuarioId`) " +
                                    "REFERENCES `usuarios`(`id`) " +
                                    "ON UPDATE NO ACTION ON DELETE CASCADE)"
                    );

                    database.execSQL(
                            "CREATE INDEX IF NOT EXISTS " +
                                    "`index_notas_usuarioId` " +
                                    "ON `notas` (`usuarioId`)"
                    );
                }
            };

    // Instancia única de la base de datos
    private static volatile NotivaDatabase INSTANCIA;

    public static NotivaDatabase obtenerInstancia(
            Context contexto) {

        if (INSTANCIA == null) {

            synchronized (NotivaDatabase.class) {

                if (INSTANCIA == null) {

                    INSTANCIA = Room.databaseBuilder(
                                    contexto.getApplicationContext(),
                                    NotivaDatabase.class,
                                    "notiva_database"
                            )
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }

        return INSTANCIA;
    }
}
