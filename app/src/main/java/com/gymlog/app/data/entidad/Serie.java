package com.gymlog.app.data.entidad;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Serie realmente hecha. El campo "sesion" es la hora de inicio del entrenamiento y sirve
 * para agrupar las series de una misma sesión y compararlas con la anterior.
 */
@Entity(
        tableName = "series",
        foreignKeys = @ForeignKey(
                entity = Ejercicio.class,
                parentColumns = "id",
                childColumns = "ejercicioId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("ejercicioId"), @Index("sesion")})
public class Serie {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long ejercicioId;

    public long sesion;

    public double peso;

    public int repeticiones;

    public long fecha;

    public Serie() {
    }

    @Ignore
    public Serie(long ejercicioId, long sesion, double peso, int repeticiones) {
        this.ejercicioId = ejercicioId;
        this.sesion = sesion;
        this.peso = peso;
        this.repeticiones = repeticiones;
        this.fecha = System.currentTimeMillis();
    }
}
