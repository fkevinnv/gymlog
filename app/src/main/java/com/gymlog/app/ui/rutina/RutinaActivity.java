package com.gymlog.app.ui.rutina;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gymlog.app.R;
import com.gymlog.app.data.entidad.Ejercicio;
import com.gymlog.app.data.relacion.DiaConEjercicios;
import com.gymlog.app.databinding.ActivityRutinaBinding;
import com.gymlog.app.ui.GymViewModel;
import com.gymlog.app.ui.entrenamiento.EntrenamientoActivity;
import com.gymlog.app.util.Animaciones;
import com.gymlog.app.util.Dialogos;

import java.util.List;

/** Muestra los días de una rutina y permite añadir ejercicios o empezar a entrenar. */
public class RutinaActivity extends AppCompatActivity {

    public static final String EXTRA_RUTINA_ID = "rutina_id";
    public static final String EXTRA_RUTINA_NOMBRE = "rutina_nombre";

    private ActivityRutinaBinding binding;
    private GymViewModel vm;
    private DiaAdapter adaptador;
    private long rutinaId;
    private boolean primeraCarga = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rutinaId = getIntent().getLongExtra(EXTRA_RUTINA_ID, -1);
        if (rutinaId == -1) {
            finish();
            return;
        }

        binding = ActivityRutinaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.tituloRutina.setText(getIntent().getStringExtra(EXTRA_RUTINA_NOMBRE));
        binding.botonAtras.setOnClickListener(v -> finish());

        vm = new ViewModelProvider(this).get(GymViewModel.class);

        adaptador = new DiaAdapter(new DiaAdapter.Escucha() {
            @Override
            public void alEmpezar(DiaConEjercicios item) {
                empezarEntrenamiento(item);
            }

            @Override
            public void alAnadirEjercicio(DiaConEjercicios item) {
                Dialogos.pedirEjercicio(RutinaActivity.this,
                        (nombre, series, descanso) ->
                                vm.crearEjercicio(item.dia.id, nombre, series, descanso));
            }

            @Override
            public void alBorrarDia(DiaConEjercicios item) {
                Dialogos.confirmar(RutinaActivity.this,
                        getString(R.string.confirmar_borrar_dia, item.dia.nombre),
                        () -> vm.eliminarDia(item.dia));
            }

            @Override
            public void alBorrarEjercicio(Ejercicio ejercicio) {
                Dialogos.confirmar(RutinaActivity.this,
                        getString(R.string.confirmar_borrar_ejercicio, ejercicio.nombre),
                        () -> vm.eliminarEjercicio(ejercicio));
            }
        });
        binding.listaDias.setLayoutManager(new LinearLayoutManager(this));
        binding.listaDias.setAdapter(adaptador);

        vm.obtenerDias(rutinaId).observe(this, this::mostrarDias);

        binding.fabNuevoDia.setOnClickListener(v -> {
            Animaciones.pulsar(v);
            Dialogos.pedirTexto(this, R.string.nuevo_dia, R.string.nombre_dia,
                    nombre -> vm.crearDia(rutinaId, nombre));
        });
        Animaciones.crecer(binding.fabNuevoDia, 400);
    }

    private void mostrarDias(List<DiaConEjercicios> lista) {
        binding.estadoVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        adaptador.submitList(lista, () -> {
            if (primeraCarga && !lista.isEmpty()) {
                binding.listaDias.scheduleLayoutAnimation();
                primeraCarga = false;
            }
        });
    }

    private void empezarEntrenamiento(DiaConEjercicios item) {
        if (item.ejercicios.isEmpty()) {
            Toast.makeText(this, R.string.aviso_sin_ejercicios, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, EntrenamientoActivity.class);
        intent.putExtra(EntrenamientoActivity.EXTRA_DIA_ID, item.dia.id);
        intent.putExtra(EntrenamientoActivity.EXTRA_DIA_NOMBRE, item.dia.nombre);
        startActivity(intent, ActivityOptionsCompat
                .makeCustomAnimation(this, R.anim.entrar_derecha, R.anim.salir_izquierda)
                .toBundle());
    }
}
