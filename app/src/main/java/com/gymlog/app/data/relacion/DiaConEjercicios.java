package com.gymlog.app.data.relacion;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.gymlog.app.data.entidad.DiaEntrenamiento;
import com.gymlog.app.data.entidad.Ejercicio;

import java.util.List;

/** Relación 1 a N: un día con todos sus ejercicios. */
public class DiaConEjercicios {

    @Embedded
    public DiaEntrenamiento dia;

    @Relation(parentColumn = "id", entityColumn = "diaId")
    public List<Ejercicio> ejercicios;
}
