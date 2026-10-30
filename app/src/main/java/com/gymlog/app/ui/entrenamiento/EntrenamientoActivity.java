package com.gymlog.app.ui.entrenamiento;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gymlog.app.R;
import com.gymlog.app.data.entidad.Ejercicio;
import com.gymlog.app.data.entidad.Serie;
import com.gymlog.app.databinding.ActivityEntrenamientoBinding;
import com.gymlog.app.ui.GymViewModel;
import com.gymlog.app.util.Animaciones;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Entrenamiento en vivo: se va ejercicio por ejercicio, se registra cada serie
 * y después de cada una arranca el temporizador de descanso.
 */
public class EntrenamientoActivity extends AppCompatActivity {

    public static final String EXTRA_DIA_ID = "dia_id";
    public static final String EXTRA_DIA_NOMBRE = "dia_nombre";

    private static final String ESTADO_SESION = "sesion";
    private static final String ESTADO_INDICE = "indice";
    private static final double PASO_PESO = 2.5;
    private static final long TIEMPO_EXTRA_MS = 15_000;

    private ActivityEntrenamientoBinding binding;
    private GymViewModel vm;
    private SerieAdapter adaptadorSeries;

    private List<Ejercicio> ejercicios = new ArrayList<>();
    private int indice = 0;
    private long sesion;
    private int seriesHechas = 0;
    private LiveData<List<Serie>> fuenteSeries;

    // Estado del temporizador de descanso
    private CountDownTimer temporizador;
    private long totalMs;
    private long restanteMs;
    private long ultimoSegundoAvisado = -1;
    private boolean urgente = false;
    private boolean enDescanso = false;
    private boolean cambiandoEjercicio = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long diaId = getIntent().getLongExtra(EXTRA_DIA_ID, -1);
        if (diaId == -1) {
            finish();
            return;
        }

        binding = ActivityEntrenamientoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        vm = new ViewModelProvider(this).get(GymViewModel.class);

        // La sesión se identifica por su hora de inicio; se conserva si se recrea la pantalla
        if (savedInstanceState != null) {
            sesion = savedInstanceState.getLong(ESTADO_SESION, System.currentTimeMillis());
            indice = savedInstanceState.getInt(ESTADO_INDICE, 0);
        } else {
            sesion = System.currentTimeMillis();
        }

        binding.tituloDia.setText(getIntent().getStringExtra(EXTRA_DIA_NOMBRE));
        configurarLista();
        configurarBotones();

        vm.obtenerEjercicios(diaId).observe(this, lista -> {
            if (!ejercicios.isEmpty()) {
                return; // Solo se cargan una vez al abrir la pantalla
            }
            if (lista.isEmpty()) {
                finish();
                return;
            }
            ejercicios = lista;
            if (indice >= ejercicios.size()) {
                indice = 0;
            }
            cargarEjercicio();
        });
    }

    private void configurarLista() {
        adaptadorSeries = new SerieAdapter(serie -> vm.eliminarSerie(serie));
        binding.listaSeries.setLayoutManager(new LinearLayoutManager(this));
        binding.listaSeries.setAdapter(adaptadorSeries);
    }

    private void configurarBotones() {
        binding.botonSalir.setOnClickListener(v -> finish());

        binding.botonPesoMas.setOnClickListener(v -> cambiarPeso(PASO_PESO));
        binding.botonPesoMenos.setOnClickListener(v -> cambiarPeso(-PASO_PESO));
        binding.botonRepsMas.setOnClickListener(v -> cambiarReps(1));
        binding.botonRepsMenos.setOnClickListener(v -> cambiarReps(-1));

        binding.botonRegistrar.setOnClickListener(v -> registrarSerie());

        binding.botonMasTiempo.setOnClickListener(v -> {
            if (enDescanso) {
                long nuevo = restanteMs + TIEMPO_EXTRA_MS;
                totalMs = Math.max(totalMs, nuevo);
                arrancarTemporizador(nuevo);
            }
        });
        binding.botonSaltar.setOnClickListener(v -> {
            cancelarDescanso();
            mostrarPanelDescanso(false);
        });

        binding.botonAnterior.setOnClickListener(v -> {
            if (indice > 0) {
                cambiarEjercicio(indice - 1, false);
            }
        });
        binding.botonSiguiente.setOnClickListener(v -> {
            if (indice < ejercicios.size() - 1) {
                cambiarEjercicio(indice + 1, true);
            } else {
                finalizar();
            }
        });
    }

    // ------------------------------------------------------------------
    // Ejercicio actual
    // ------------------------------------------------------------------

    /** Pinta el ejercicio actual y conecta sus series y su referencia anterior. */
    private void cargarEjercicio() {
        Ejercicio actual = ejercicios.get(indice);

        binding.nombreEjercicio.setText(actual.nombre);
        binding.contadorEjercicios.setText(
                getString(R.string.contador_ejercicios, indice + 1, ejercicios.size()));
        binding.progresoGeneral.setProgressCompat((indice + 1) * 100 / ejercicios.size(), true);
        binding.botonAnterior.setEnabled(indice > 0);
        binding.botonSiguiente.setText(
                indice == ejercicios.size() - 1 ? R.string.finalizar : R.string.siguiente);

        reiniciarZonaAccion();

        if (fuenteSeries != null) {
            fuenteSeries.removeObservers(this);
        }
        fuenteSeries = vm.obtenerSeriesSesion(actual.id, sesion);
        fuenteSeries.observe(this, this::alCambiarSeries);

        // Rellena peso y repeticiones con lo que se hizo la última vez
        final int indiceSolicitado = indice;
        vm.obtenerReferencia(actual.id, sesion, ultima -> {
            if (isDestroyed() || indiceSolicitado != indice) {
                return;
            }
            if (ultima != null) {
                binding.referencia.setText(getString(R.string.ultima_vez,
                        FormatoPeso.dar(ultima.peso), ultima.repeticiones));
                binding.entradaPeso.setText(FormatoPeso.dar(ultima.peso));
                binding.entradaReps.setText(String.valueOf(ultima.repeticiones));
            } else {
                binding.referencia.setText(R.string.primera_vez);
                binding.entradaPeso.setText("0");
                binding.entradaReps.setText("10");
            }
        });
    }

    /** Cambia de ejercicio deslizando el contenido hacia un lado. */
    private void cambiarEjercicio(int nuevoIndice, boolean haciaAdelante) {
        if (cambiandoEjercicio) {
            return;
        }
        cambiandoEjercicio = true;
        float desplazamiento = 80f * getResources().getDisplayMetrics().density;
        float salida = haciaAdelante ? -desplazamiento : desplazamiento;

        binding.contenido.animate()
                .alpha(0f)
                .translationX(salida)
                .setDuration(150)
                .withEndAction(() -> {
                    indice = nuevoIndice;
                    cargarEjercicio();
                    binding.contenido.setTranslationX(-salida);
                    binding.contenido.animate()
                            .alpha(1f)
                            .translationX(0f)
                            .setDuration(200)
                            .withEndAction(() -> cambiandoEjercicio = false)
                            .start();
                })
                .start();
    }

    private void alCambiarSeries(List<Serie> series) {
        adaptadorSeries.submitList(series);
        seriesHechas = series.size();
        binding.textoSinSeries.setVisibility(series.isEmpty() ? View.VISIBLE : View.GONE);

        Ejercicio actual = ejercicios.get(indice);
        if (seriesHechas < actual.seriesObjetivo) {
            binding.etiquetaSerie.setText(
                    getString(R.string.serie_de, seriesHechas + 1, actual.seriesObjetivo));
        } else {
            binding.etiquetaSerie.setText(R.string.objetivo_cumplido);
        }
    }

    // ------------------------------------------------------------------
    // Registro de series
    // ------------------------------------------------------------------

    private void registrarSerie() {
        Animaciones.pulsar(binding.botonRegistrar);

        double peso = leerPeso();
        int repeticiones = leerReps();
        if (repeticiones <= 0) {
            binding.entradaReps.setError(getString(R.string.error_reps));
            return;
        }

        Ejercicio actual = ejercicios.get(indice);
        vm.guardarSerie(new Serie(actual.id, sesion, peso, repeticiones));

        boolean cumpleObjetivo = seriesHechas + 1 == actual.seriesObjetivo;
        boolean esUltimoEjercicio = indice == ejercicios.size() - 1;

        if (cumpleObjetivo && esUltimoEjercicio) {
            // Última serie del entrenamiento: no hace falta descansar
            vibrar(200);
            Snackbar.make(binding.getRoot(), R.string.todo_completado, Snackbar.LENGTH_LONG).show();
        } else {
            iniciarDescanso(actual.descansoSegundos);
        }
    }

    private void cambiarPeso(double diferencia) {
        double nuevo = Math.max(0, leerPeso() + diferencia);
        binding.entradaPeso.setText(FormatoPeso.dar(nuevo));
    }

    private void cambiarReps(int diferencia) {
        int nuevo = Math.max(1, leerReps() + diferencia);
        binding.entradaReps.setText(String.valueOf(nuevo));
    }

    private double leerPeso() {
        String texto = textoDe(binding.entradaPeso).replace(',', '.');
        try {
            return texto.isEmpty() ? 0 : Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int leerReps() {
        try {
            return Integer.parseInt(textoDe(binding.entradaReps));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String textoDe(EditText campo) {
        Editable texto = campo.getText();
        return texto == null ? "" : texto.toString().trim();
    }

    // ------------------------------------------------------------------
    // Temporizador de descanso
    // ------------------------------------------------------------------

    private void iniciarDescanso(int segundos) {
        if (segundos <= 0) {
            return;
        }
        totalMs = segundos * 1000L;
        arrancarTemporizador(totalMs);
        mostrarPanelDescanso(true);
    }

    private void arrancarTemporizador(long duracionMs) {
        if (temporizador != null) {
            temporizador.cancel();
        }
        enDescanso = true;
        restanteMs = duracionMs;
        ultimoSegundoAvisado = -1;
        binding.anilloDescanso.setMax((int) totalMs);
        actualizarReloj(duracionMs);

        temporizador = new CountDownTimer(duracionMs, 100) {
            @Override
            public void onTick(long restante) {
                restanteMs = restante;
                actualizarReloj(restante);
            }

            @Override
            public void onFinish() {
                restanteMs = 0;
                actualizarReloj(0);
                alTerminarDescanso();
            }
        }.start();
    }

    private void actualizarReloj(long milisegundos) {
        long segundos = (milisegundos + 999) / 1000; // se redondea hacia arriba
        binding.relojDescanso.setText(String.format(Locale.getDefault(),
                "%02d:%02d", segundos / 60, segundos % 60));
        binding.anilloDescanso.setProgress((int) milisegundos);

        // En los últimos 5 segundos cambia de color, late y vibra
        boolean ultimosSegundos = segundos <= 5 && milisegundos > 0;
        if (ultimosSegundos != urgente) {
            urgente = ultimosSegundos;
            int color = ContextCompat.getColor(this, urgente ? R.color.secundario : R.color.primario);
            binding.anilloDescanso.setIndicatorColor(color);
            binding.relojDescanso.setTextColor(
                    ContextCompat.getColor(this, urgente ? R.color.secundario : R.color.texto));
        }
        if (ultimosSegundos && segundos != ultimoSegundoAvisado) {
            ultimoSegundoAvisado = segundos;
            Animaciones.latir(binding.relojDescanso);
            vibrar(40);
        }
    }

    private void alTerminarDescanso() {
        enDescanso = false;
        vibrar(400);
        mostrarPanelDescanso(false);
        Snackbar.make(binding.getRoot(), R.string.descanso_terminado, Snackbar.LENGTH_SHORT).show();
    }

    private void cancelarDescanso() {
        if (temporizador != null) {
            temporizador.cancel();
            temporizador = null;
        }
        enDescanso = false;
    }

    /** Alterna entre el botón de registrar serie y el panel con la cuenta atrás. */
    private void mostrarPanelDescanso(boolean mostrar) {
        if (mostrar) {
            binding.botonRegistrar.setVisibility(View.GONE);
            Animaciones.aparecer(binding.panelDescanso);
        } else {
            binding.panelDescanso.setVisibility(View.GONE);
            Animaciones.aparecer(binding.botonRegistrar);
        }
    }

    /** Deja la zona de acción en su estado inicial al cambiar de ejercicio. */
    private void reiniciarZonaAccion() {
        cancelarDescanso();
        binding.panelDescanso.animate().cancel();
        binding.botonRegistrar.animate().cancel();
        binding.panelDescanso.setVisibility(View.GONE);
        binding.botonRegistrar.setVisibility(View.VISIBLE);
        binding.botonRegistrar.setAlpha(1f);
        binding.botonRegistrar.setTranslationY(0f);
    }

    @SuppressWarnings("deprecation")
    private void vibrar(long milisegundos) {
        Vibrator vibrador = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrador != null && vibrador.hasVibrator()) {
            vibrador.vibrate(VibrationEffect.createOneShot(
                    milisegundos, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    // ------------------------------------------------------------------
    // Final del entrenamiento
    // ------------------------------------------------------------------

    private void finalizar() {
        cancelarDescanso();
        vm.obtenerResumen(sesion, resumen -> {
            if (isFinishing() || isDestroyed()) {
                return;
            }
            long minutos = Math.max(1, (System.currentTimeMillis() - sesion) / 60_000);
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.entrenamiento_terminado)
                    .setMessage(getString(R.string.resumen,
                            resumen.totalSeries, FormatoPeso.dar(resumen.volumen), minutos))
                    .setCancelable(false)
                    .setPositiveButton(R.string.cerrar, (dialogo, cual) -> finish())
                    .show();
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle salida) {
        super.onSaveInstanceState(salida);
        salida.putLong(ESTADO_SESION, sesion);
        salida.putInt(ESTADO_INDICE, indice);
    }

    @Override
    protected void onDestroy() {
        cancelarDescanso();
        super.onDestroy();
    }
}
