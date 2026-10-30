package com.gymlog.app.data.entidad;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** Ejercicio planificado dentro de un día, con las series objetivo y el descanso entre series. */
@Entity(
        tableName = "ejercicios",
        foreignKeys = @ForeignKey(
                entity = DiaEntrenamiento.class,
                parentColumns = "id",
                childColumns = "diaId",
                onDelete = ForeignKey.CASCADE),
        indices = @Index("diaId"))
public class Ejercicio {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long diaId;

    @NonNull
    public String nombre = "";

    public int seriesObjetivo;

    public int descansoSegundos;

    public Ejercicio() {
    }

    @Ignore
    public Ejercicio(long diaId, @NonNull String nombre, int seriesObjetivo, int descansoSegundos) {
        this.diaId = diaId;
        this.nombre = nombre;
        this.seriesObjetivo = seriesObjetivo;
        this.descansoSegundos = descansoSegundos;
    }
}
