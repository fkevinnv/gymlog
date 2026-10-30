package com.gymlog.app.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.gymlog.app.data.dao.DiaDao;
import com.gymlog.app.data.dao.EjercicioDao;
import com.gymlog.app.data.dao.RutinaDao;
import com.gymlog.app.data.dao.SerieDao;
import com.gymlog.app.data.entidad.DiaEntrenamiento;
import com.gymlog.app.data.entidad.Ejercicio;
import com.gymlog.app.data.entidad.Rutina;
import com.gymlog.app.data.entidad.Serie;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {Rutina.class, DiaEntrenamiento.class, Ejercicio.class, Serie.class},
        version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RutinaDao rutinaDao();

    public abstract DiaDao diaDao();

    public abstract EjercicioDao ejercicioDao();

    public abstract SerieDao serieDao();

    /** Las escrituras no se pueden hacer en el hilo principal, así que usamos este ejecutor. */
    public static final ExecutorService EJECUTOR = Executors.newFixedThreadPool(2);

    private static volatile AppDatabase instancia;

    public static AppDatabase obtener(Context contexto) {
        if (instancia == null) {
            synchronized (AppDatabase.class) {
                if (instancia == null) {
                    instancia = Room.databaseBuilder(
                            contexto.getApplicationContext(),
                            AppDatabase.class,
                            "gymlog.db").build();
                }
            }
        }
        return instancia;
    }
}
