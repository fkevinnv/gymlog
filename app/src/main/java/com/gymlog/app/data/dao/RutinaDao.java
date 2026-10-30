package com.gymlog.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.gymlog.app.data.entidad.Rutina;
import com.gymlog.app.data.relacion.RutinaConDias;

import java.util.List;

@Dao
public interface RutinaDao {

    @Insert
    long insertar(Rutina rutina);

    @Delete
    void eliminar(Rutina rutina);

    @Transaction
    @Query("SELECT * FROM rutinas ORDER BY fechaCreacion DESC")
    LiveData<List<RutinaConDias>> obtenerRutinasConDias();
}
