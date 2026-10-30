package com.gymlog.app.data.relacion;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.gymlog.app.data.entidad.DiaEntrenamiento;
import com.gymlog.app.data.entidad.Rutina;

import java.util.List;

/** Relación 1 a N: una rutina con todos sus días. */
public class RutinaConDias {

    @Embedded
    public Rutina rutina;

    @Relation(parentColumn = "id", entityColumn = "rutinaId")
    public List<DiaEntrenamiento> dias;
}
