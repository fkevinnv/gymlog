package com.gymlog.app.ui.principal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gymlog.app.R;
import com.gymlog.app.data.entidad.Rutina;
import com.gymlog.app.data.relacion.RutinaConDias;
import com.gymlog.app.databinding.ActivityMainBinding;
import com.gymlog.app.ui.GymViewModel;
import com.gymlog.app.ui.rutina.RutinaActivity;
import com.gymlog.app.util.Animaciones;
import com.gymlog.app.util.Dialogos;

import java.util.List;

/** Pantalla principal: lista de rutinas. */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private GymViewModel vm;
    private RutinaAdapter adaptador;
    private boolean primeraCarga = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this).get(GymViewModel.class);

        adaptador = new RutinaAdapter(new RutinaAdapter.Escucha() {
            @Override
            public void alPulsar(RutinaConDias item) {
                abrirRutina(item.rutina);
            }

            @Override
            public void alBorrar(RutinaConDias item) {
                Dialogos.confirmar(MainActivity.this,
                        getString(R.string.confirmar_borrar_rutina, item.rutina.nombre),
                        () -> vm.eliminarRutina(item.rutina));
            }
        });
        binding.listaRutinas.setLayoutManager(new LinearLayoutManager(this));
        binding.listaRutinas.setAdapter(adaptador);

        vm.obtenerRutinas().observe(this, this::mostrarRutinas);

        binding.fabNuevaRutina.setOnClickListener(v -> {
            Animaciones.pulsar(v);
            Dialogos.pedirTexto(this, R.string.nueva_rutina, R.string.nombre_rutina, vm::crearRutina);
        });
        Animaciones.crecer(binding.fabNuevaRutina, 400);
    }

    private void mostrarRutinas(List<RutinaConDias> lista) {
        binding.estadoVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        adaptador.submitList(lista, () -> {
            // La animación de entrada solo se reproduce la primera vez que hay datos
            if (primeraCarga && !lista.isEmpty()) {
                binding.listaRutinas.scheduleLayoutAnimation();
                primeraCarga = false;
            }
        });
    }

    private void abrirRutina(Rutina rutina) {
        Intent intent = new Intent(this, RutinaActivity.class);
        intent.putExtra(RutinaActivity.EXTRA_RUTINA_ID, rutina.id);
        intent.putExtra(RutinaActivity.EXTRA_RUTINA_NOMBRE, rutina.nombre);
        startActivity(intent, ActivityOptionsCompat
                .makeCustomAnimation(this, R.anim.entrar_derecha, R.anim.salir_izquierda)
                .toBundle());
    }
}
