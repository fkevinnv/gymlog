package com.gymlog.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.gymlog.app.data.entidad.DiaEntrenamiento;
import com.gymlog.app.data.relacion.DiaConEjercicios;

import java.util.List;

@Dao
public interface DiaDao {

    @Insert
    long insertar(DiaEntrenamiento dia);

    @Delete
    void eliminar(DiaEntrenamiento dia);

    @Transaction
    @Query("SELECT * FROM dias WHERE rutinaId = :rutinaId ORDER BY id")
    LiveData<List<DiaConEjercicios>> obtenerDiasConEjercicios(long rutinaId);
}
