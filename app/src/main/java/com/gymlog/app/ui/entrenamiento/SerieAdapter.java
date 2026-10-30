package com.gymlog.app.ui.entrenamiento;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.gymlog.app.R;
import com.gymlog.app.data.entidad.Serie;
import com.gymlog.app.databinding.ItemSerieBinding;

/** Series registradas hoy para el ejercicio que se está viendo. */
public class SerieAdapter extends ListAdapter<Serie, SerieAdapter.SerieVH> {

    public interface Escucha {
        void alBorrar(Serie serie);
    }

    private static final DiffUtil.ItemCallback<Serie> DIFF = new DiffUtil.ItemCallback<Serie>() {
        @Override
        public boolean areItemsTheSame(@NonNull Serie a, @NonNull Serie b) {
            return a.id == b.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Serie a, @NonNull Serie b) {
            return a.peso == b.peso && a.repeticiones == b.repeticiones;
        }
    };

    private final Escucha escucha;

    public SerieAdapter(Escucha escucha) {
        super(DIFF);
        this.escucha = escucha;
    }

    @NonNull
    @Override
    public SerieVH onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        ItemSerieBinding binding =
                ItemSerieBinding.inflate(LayoutInflater.from(padre.getContext()), padre, false);
        return new SerieVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SerieVH holder, int posicion) {
        Serie serie = getItem(posicion);
        holder.binding.detalleSerie.setText(holder.itemView.getContext().getString(
                R.string.detalle_serie, FormatoPeso.dar(serie.peso), serie.repeticiones));
        holder.binding.botonBorrarSerie.setOnClickListener(v -> escucha.alBorrar(serie));
    }

    static class SerieVH extends RecyclerView.ViewHolder {
        final ItemSerieBinding binding;

        SerieVH(ItemSerieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
