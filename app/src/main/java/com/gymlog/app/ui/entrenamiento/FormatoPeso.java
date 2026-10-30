package com.gymlog.app.ui.entrenamiento;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Da formato a los pesos: "60" en vez de "60.0" y "62.5" cuando hay decimales. */
final class FormatoPeso {

    private static final DecimalFormat FORMATO =
            new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.US));

    private FormatoPeso() {
    }

    static String dar(double valor) {
        return FORMATO.format(valor);
    }
}
