package com.gymlog.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.gymlog.app.data.entidad.Serie;
import com.gymlog.app.data.relacion.ResumenSesion;

import java.util.List;

@Dao
public interface SerieDao {

    @Insert
    long insertar(Serie serie);

    @Delete
    void eliminar(Serie serie);

    /** Series que se han hecho de un ejercicio en la sesión actual. */
    @Query("SELECT * FROM series WHERE ejercicioId = :ejercicioId AND sesion = :sesion ORDER BY id")
    LiveData<List<Serie>> obtenerDeSesion(long ejercicioId, long sesion);

    /** Última serie registrada en una sesión anterior (para mostrar la referencia). */
    @Query("SELECT * FROM series WHERE ejercicioId = :ejercicioId AND sesion < :sesion "
            + "ORDER BY fecha DESC, id DESC LIMIT 1")
    Serie obtenerUltimaAnterior(long ejercicioId, long sesion);

    @Query("SELECT COUNT(*) AS totalSeries, COALESCE(SUM(peso * repeticiones), 0) AS volumen "
            + "FROM series WHERE sesion = :sesion")
    ResumenSesion obtenerResumen(long sesion);
}
