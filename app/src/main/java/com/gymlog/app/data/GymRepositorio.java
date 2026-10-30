package com.gymlog.app.data;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.gymlog.app.data.dao.DiaDao;
import com.gymlog.app.data.dao.EjercicioDao;
import com.gymlog.app.data.dao.RutinaDao;
import com.gymlog.app.data.dao.SerieDao;
import com.gymlog.app.data.entidad.DiaEntrenamiento;
import com.gymlog.app.data.entidad.Ejercicio;
import com.gymlog.app.data.entidad.Rutina;
import com.gymlog.app.data.entidad.Serie;
import com.gymlog.app.data.relacion.DiaConEjercicios;
import com.gymlog.app.data.relacion.ResumenSesion;
import com.gymlog.app.data.relacion.RutinaConDias;

import java.util.List;
import java.util.function.Consumer;

/** Único punto de acceso a los datos. Las consultas con LiveData ya van en segundo plano. */
public class GymRepositorio {

    private final RutinaDao rutinaDao;
    private final DiaDao diaDao;
    private final EjercicioDao ejercicioDao;
    private final SerieDao serieDao;
    private final Handler hiloPrincipal = new Handler(Looper.getMainLooper());

    public GymRepositorio(Application aplicacion) {
        AppDatabase bd = AppDatabase.obtener(aplicacion);
        rutinaDao = bd.rutinaDao();
        diaDao = bd.diaDao();
        ejercicioDao = bd.ejercicioDao();
        serieDao = bd.serieDao();
    }

    // ----- Rutinas -----

    public LiveData<List<RutinaConDias>> obtenerRutinas() {
        return rutinaDao.obtenerRutinasConDias();
    }

    public void crearRutina(String nombre) {
        AppDatabase.EJECUTOR.execute(() -> rutinaDao.insertar(new Rutina(nombre)));
    }

    public void eliminarRutina(Rutina rutina) {
        AppDatabase.EJECUTOR.execute(() -> rutinaDao.eliminar(rutina));
    }

    // ----- Días -----

    public LiveData<List<DiaConEjercicios>> obtenerDias(long rutinaId) {
        return diaDao.obtenerDiasConEjercicios(rutinaId);
    }

    public void crearDia(long rutinaId, String nombre) {
        AppDatabase.EJECUTOR.execute(() -> diaDao.insertar(new DiaEntrenamiento(rutinaId, nombre)));
    }

    public void eliminarDia(DiaEntrenamiento dia) {
        AppDatabase.EJECUTOR.execute(() -> diaDao.eliminar(dia));
    }

    // ----- Ejercicios -----

    public LiveData<List<Ejercicio>> obtenerEjercicios(long diaId) {
        return ejercicioDao.obtenerPorDia(diaId);
    }

    public void crearEjercicio(long diaId, String nombre, int series, int descansoSegundos) {
        AppDatabase.EJECUTOR.execute(() ->
                ejercicioDao.insertar(new Ejercicio(diaId, nombre, series, descansoSegundos)));
    }

    public void eliminarEjercicio(Ejercicio ejercicio) {
        AppDatabase.EJECUTOR.execute(() -> ejercicioDao.eliminar(ejercicio));
    }

    // ----- Series -----

    public LiveData<List<Serie>> obtenerSeriesSesion(long ejercicioId, long sesion) {
        return serieDao.obtenerDeSesion(ejercicioId, sesion);
    }

    public void guardarSerie(Serie serie) {
        AppDatabase.EJECUTOR.execute(() -> serieDao.insertar(serie));
    }

    public void eliminarSerie(Serie serie) {
        AppDatabase.EJECUTOR.execute(() -> serieDao.eliminar(serie));
    }

    /** Busca la última serie de una sesión anterior y devuelve el resultado en el hilo principal. */
    public void obtenerReferencia(long ejercicioId, long sesion, Consumer<Serie> resultado) {
        AppDatabase.EJECUTOR.execute(() -> {
            Serie serie = serieDao.obtenerUltimaAnterior(ejercicioId, sesion);
            hiloPrincipal.post(() -> resultado.accept(serie));
        });
    }

    public void obtenerResumen(long sesion, Consumer<ResumenSesion> resultado) {
        AppDatabase.EJECUTOR.execute(() -> {
            ResumenSesion resumen = serieDao.obtenerResumen(sesion);
            hiloPrincipal.post(() -> resultado.accept(resumen));
        });
    }
}
