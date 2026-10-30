package com.gymlog.app.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gymlog.app.data.GymRepositorio;
import com.gymlog.app.data.entidad.DiaEntrenamiento;
import com.gymlog.app.data.entidad.Ejercicio;
import com.gymlog.app.data.entidad.Rutina;
import com.gymlog.app.data.entidad.Serie;
import com.gymlog.app.data.relacion.DiaConEjercicios;
import com.gymlog.app.data.relacion.ResumenSesion;
import com.gymlog.app.data.relacion.RutinaConDias;

import java.util.List;
import java.util.function.Consumer;

/** ViewModel compartido por las tres pantallas. Solo reenvía las peticiones al repositorio. */
public class GymViewModel extends AndroidViewModel {

    private final GymRepositorio repositorio;
    private final LiveData<List<RutinaConDias>> rutinas;

    public GymViewModel(@NonNull Application aplicacion) {
        super(aplicacion);
        repositorio = new GymRepositorio(aplicacion);
        rutinas = repositorio.obtenerRutinas();
    }

    public LiveData<List<RutinaConDias>> obtenerRutinas() {
        return rutinas;
    }

    public void crearRutina(String nombre) {
        repositorio.crearRutina(nombre);
    }

    public void eliminarRutina(Rutina rutina) {
        repositorio.eliminarRutina(rutina);
    }

    public LiveData<List<DiaConEjercicios>> obtenerDias(long rutinaId) {
        return repositorio.obtenerDias(rutinaId);
    }

    public void crearDia(long rutinaId, String nombre) {
        repositorio.crearDia(rutinaId, nombre);
    }

    public void eliminarDia(DiaEntrenamiento dia) {
        repositorio.eliminarDia(dia);
    }

    public LiveData<List<Ejercicio>> obtenerEjercicios(long diaId) {
        return repositorio.obtenerEjercicios(diaId);
    }

    public void crearEjercicio(long diaId, String nombre, int series, int descansoSegundos) {
        repositorio.crearEjercicio(diaId, nombre, series, descansoSegundos);
    }

    public void eliminarEjercicio(Ejercicio ejercicio) {
        repositorio.eliminarEjercicio(ejercicio);
    }

    public LiveData<List<Serie>> obtenerSeriesSesion(long ejercicioId, long sesion) {
        return repositorio.obtenerSeriesSesion(ejercicioId, sesion);
    }

    public void guardarSerie(Serie serie) {
        repositorio.guardarSerie(serie);
    }

    public void eliminarSerie(Serie serie) {
        repositorio.eliminarSerie(serie);
    }

    public void obtenerReferencia(long ejercicioId, long sesion, Consumer<Serie> resultado) {
        repositorio.obtenerReferencia(ejercicioId, sesion, resultado);
    }

    public void obtenerResumen(long sesion, Consumer<ResumenSesion> resultado) {
        repositorio.obtenerResumen(sesion, resultado);
    }
}
