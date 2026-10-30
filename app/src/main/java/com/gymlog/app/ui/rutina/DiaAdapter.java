package com.gymlog.app.ui.rutina;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.gymlog.app.R;
import com.gymlog.app.data.entidad.Ejercicio;
import com.gymlog.app.data.relacion.DiaConEjercicios;
import com.gymlog.app.databinding.ItemDiaBinding;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class DiaAdapter extends ListAdapter<DiaConEjercicios, DiaAdapter.DiaVH> {

    public interface Escucha {
        void alEmpezar(DiaConEjercicios item);

        void alAnadirEjercicio(DiaConEjercicios item);

        void alBorrarDia(DiaConEjercicios item);

        void alBorrarEjercicio(Ejercicio ejercicio);
    }

    private static final DiffUtil.ItemCallback<DiaConEjercicios> DIFF =
            new DiffUtil.ItemCallback<DiaConEjercicios>() {
                @Override
                public boolean areItemsTheSame(@NonNull DiaConEjercicios a, @NonNull DiaConEjercicios b) {
                    return a.dia.id == b.dia.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull DiaConEjercicios a, @NonNull DiaConEjercicios b) {
                    return a.dia.nombre.equals(b.dia.nombre)
                            && mismosEjercicios(a.ejercicios, b.ejercicios);
                }
            };

    private static boolean mismosEjercicios(List<Ejercicio> a, List<Ejercicio> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            Ejercicio x = a.get(i);
            Ejercicio y = b.get(i);
            if (x.id != y.id
                    || !x.nombre.equals(y.nombre)
                    || x.seriesObjetivo != y.seriesObjetivo
                    || x.descansoSegundos != y.descansoSegundos) {
                return false;
            }
        }
        return true;
    }

    private final Escucha escucha;

    public DiaAdapter(Escucha escucha) {
        super(DIFF);
        this.escucha = escucha;
    }

    @NonNull
    @Override
    public DiaVH onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        ItemDiaBinding binding =
                ItemDiaBinding.inflate(LayoutInflater.from(padre.getContext()), padre, false);
        return new DiaVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaVH holder, int posicion) {
        DiaConEjercicios item = getItem(posicion);
        Context contexto = holder.itemView.getContext();

        // La relación de Room no garantiza el orden, así que lo fijamos por id
        List<Ejercicio> ejercicios = new ArrayList<>(item.ejercicios);
        ejercicios.sort((a, b) -> Long.compare(a.id, b.id));
        int total = ejercicios.size();

        holder.binding.nombreDia.setText(item.dia.nombre);
        holder.binding.resumenDia.setText(contexto.getResources()
                .getQuantityString(R.plurals.num_ejercicios, total, total));
        holder.binding.sinEjercicios.setVisibility(total == 0 ? View.VISIBLE : View.GONE);

        // Un chip por ejercicio; la X lo elimina
        holder.binding.chipsEjercicios.removeAllViews();
        for (Ejercicio ejercicio : ejercicios) {
            Chip chip = new Chip(contexto);
            chip.setText(contexto.getString(R.string.chip_ejercicio,
                    ejercicio.nombre, ejercicio.seriesObjetivo));
            chip.setTextColor(ContextCompat.getColor(contexto, R.color.texto));
            chip.setChipBackgroundColorResource(R.color.superficie_alta);
            chip.setChipStrokeWidth(0f);
            chip.setCloseIconVisible(true);
            chip.setCloseIconTintResource(R.color.texto_suave);
            chip.setOnCloseIconClickListener(v -> escucha.alBorrarEjercicio(ejercicio));
            holder.binding.chipsEjercicios.addView(chip);
        }

        holder.binding.botonBorrarDia.setOnClickListener(v -> escucha.alBorrarDia(item));
        holder.binding.botonAnadir.setOnClickListener(v -> escucha.alAnadirEjercicio(item));
        holder.binding.botonEmpezar.setOnClickListener(v -> escucha.alEmpezar(item));
    }

    static class DiaVH extends RecyclerView.ViewHolder {
        final ItemDiaBinding binding;

        DiaVH(ItemDiaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
