package com.gymlog.app.util;

import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/** Animaciones pequeñas y reutilizables para dar vida a la interfaz. */
public final class Animaciones {

    private Animaciones() {
    }

    /** Efecto de "pulsación": se encoge un poco y rebota. */
    public static void pulsar(View vista) {
        vista.animate().cancel();
        vista.animate()
                .setStartDelay(0)
                .scaleX(0.92f)
                .scaleY(0.92f)
                .setDuration(80)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> vista.animate()
                        .setStartDelay(0)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(160)
                        .setInterpolator(new OvershootInterpolator())
                        .start())
                .start();
    }

    /** Se agranda un momento. Se usa en la cuenta atrás de los últimos segundos. */
    public static void latir(View vista) {
        vista.animate().cancel();
        vista.animate()
                .setStartDelay(0)
                .scaleX(1.15f)
                .scaleY(1.15f)
                .setDuration(120)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> vista.animate()
                        .setStartDelay(0)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(180)
                        .setInterpolator(new OvershootInterpolator())
                        .start())
                .start();
    }

    /** Aparece desde cero hasta su tamaño con un pequeño rebote (botones flotantes). */
    public static void crecer(View vista, long retrasoMs) {
        vista.animate().cancel();
        vista.setScaleX(0f);
        vista.setScaleY(0f);
        vista.animate()
                .setStartDelay(retrasoMs)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(350)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    /** Muestra la vista con un fundido y un ligero desplazamiento hacia arriba. */
    public static void aparecer(View vista) {
        vista.animate().cancel();
        vista.setAlpha(0f);
        vista.setTranslationY(40f);
        vista.setVisibility(View.VISIBLE);
        vista.animate()
                .setStartDelay(0)
                .alpha(1f)
                .translationY(0f)
                .setDuration(250)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }
}
