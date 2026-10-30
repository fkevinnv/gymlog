package com.gymlog.app.data.entidad;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** Un día de la rutina (ej. "Pecho y tríceps"). Si se borra la rutina, se borran sus días. */
@Entity(
        tableName = "dias",
        foreignKeys = @ForeignKey(
                entity = Rutina.class,
                parentColumns = "id",
                childColumns = "rutinaId",
                onDelete = ForeignKey.CASCADE),
        indices = @Index("rutinaId"))
public class DiaEntrenamiento {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long rutinaId;

    @NonNull
    public String nombre = "";

    public DiaEntrenamiento() {
    }

    @Ignore
    public DiaEntrenamiento(long rutinaId, @NonNull String nombre) {
        this.rutinaId = rutinaId;
        this.nombre = nombre;
    }
}
