package com.gymlog.app.util;

import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.gymlog.app.R;
import com.gymlog.app.databinding.DialogEjercicioBinding;
import com.gymlog.app.databinding.DialogTextoBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.function.Consumer;

/** Diálogos comunes de la aplicación. Hay que pasarles siempre una Activity como contexto. */
public final class Dialogos {

    private Dialogos() {
    }

    public interface AlGuardarEjercicio {
        void guardar(String nombre, int series, int descansoSegundos);
    }

    /** Pide un texto corto (nombre de rutina o de día). No se cierra si está vacío. */
    public static void pedirTexto(Context contexto, @StringRes int titulo, @StringRes int pista,
                                  Consumer<String> alAceptar) {
        DialogTextoBinding b = DialogTextoBinding.inflate(LayoutInflater.from(contexto));
        b.campoTexto.setHint(contexto.getString(pista));

        AlertDialog dialogo = new MaterialAlertDialogBuilder(contexto)
                .setTitle(titulo)
                .setView(b.getRoot())
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.guardar, null)
                .create();
        mostrarConTeclado(dialogo);

        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String texto = textoDe(b.entradaTexto);
            if (texto.isEmpty()) {
                b.campoTexto.setError(contexto.getString(R.string.error_nombre_vacio));
                return;
            }
            alAceptar.accept(texto);
            dialogo.dismiss();
        });
    }

    /** Formulario para añadir un ejercicio: nombre, series y descanso. */
    public static void pedirEjercicio(Context contexto, AlGuardarEjercicio alGuardar) {
        DialogEjercicioBinding b = DialogEjercicioBinding.inflate(LayoutInflater.from(contexto));
        b.entradaSeries.setText("4");
        b.entradaDescanso.setText("90");

        AlertDialog dialogo = new MaterialAlertDialogBuilder(contexto)
                .setTitle(R.string.nuevo_ejercicio)
                .setView(b.getRoot())
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.guardar, null)
                .create();
        mostrarConTeclado(dialogo);

        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nombre = textoDe(b.entradaNombre);
            if (nombre.isEmpty()) {
                b.campoNombre.setError(contexto.getString(R.string.error_nombre_vacio));
                return;
            }
            int series = limitar(entero(b.entradaSeries, 4), 1, 20);
            int descanso = limitar(entero(b.entradaDescanso, 90), 0, 600);
            alGuardar.guardar(nombre, series, descanso);
            dialogo.dismiss();
        });
    }

    /** Pregunta de confirmación antes de borrar algo. */
    public static void confirmar(Context contexto, String mensaje, Runnable alConfirmar) {
        new MaterialAlertDialogBuilder(contexto)
                .setMessage(mensaje)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.borrar, (d, cual) -> alConfirmar.run())
                .show();
    }

    private static void mostrarConTeclado(AlertDialog dialogo) {
        if (dialogo.getWindow() != null) {
            dialogo.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        dialogo.show();
    }

    private static String textoDe(EditText campo) {
        Editable texto = campo.getText();
        return texto == null ? "" : texto.toString().trim();
    }

    private static int entero(EditText campo, int porDefecto) {
        try {
            return Integer.parseInt(textoDe(campo));
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }

    private static int limitar(int valor, int minimo, int maximo) {
        return Math.max(minimo, Math.min(maximo, valor));
    }
}
