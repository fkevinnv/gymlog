package com.gymlog.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.gymlog.app.data.entidad.Ejercicio;

import java.util.List;

@Dao
public interface EjercicioDao {

    @Insert
    long insertar(Ejercicio ejercicio);

    @Delete
    void eliminar(Ejercicio ejercicio);

    @Query("SELECT * FROM ejercicios WHERE diaId = :diaId ORDER BY id")
    LiveData<List<Ejercicio>> obtenerPorDia(long diaId);
}
