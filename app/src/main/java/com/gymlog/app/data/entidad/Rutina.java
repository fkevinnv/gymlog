package com.gymlog.app.data.entidad;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/** Una rutina agrupa varios días de entrenamiento (ej. "Fuerza 4 días"). */
@Entity(tableName = "rutinas")
public class Rutina {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String nombre = "";

    public long fechaCreacion;

    public Rutina() {
    }

    @Ignore
    public Rutina(@NonNull String nombre) {
        this.nombre = nombre;
        this.fechaCreacion = System.currentTimeMillis();
    }
}
