package com.gymlog.app.ui.principal;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.gymlog.app.R;
import com.gymlog.app.data.relacion.RutinaConDias;
import com.gymlog.app.databinding.ItemRutinaBinding;

public class RutinaAdapter extends ListAdapter<RutinaConDias, RutinaAdapter.RutinaVH> {

    public interface Escucha {
        void alPulsar(RutinaConDias item);

        void alBorrar(RutinaConDias item);
    }

    private static final DiffUtil.ItemCallback<RutinaConDias> DIFF =
            new DiffUtil.ItemCallback<RutinaConDias>() {
                @Override
                public boolean areItemsTheSame(@NonNull RutinaConDias a, @NonNull RutinaConDias b) {
                    return a.rutina.id == b.rutina.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull RutinaConDias a, @NonNull RutinaConDias b) {
                    return a.rutina.nombre.equals(b.rutina.nombre) && a.dias.size() == b.dias.size();
                }
            };

    private final Escucha escucha;

    public RutinaAdapter(Escucha escucha) {
        super(DIFF);
        this.escucha = escucha;
    }

    @NonNull
    @Override
    public RutinaVH onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        ItemRutinaBinding binding =
                ItemRutinaBinding.inflate(LayoutInflater.from(padre.getContext()), padre, false);
        return new RutinaVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RutinaVH holder, int posicion) {
        RutinaConDias item = getItem(posicion);
        int dias = item.dias.size();

        holder.binding.nombreRutina.setText(item.rutina.nombre);
        holder.binding.resumenRutina.setText(holder.itemView.getResources()
                .getQuantityString(R.plurals.num_dias, dias, dias));

        holder.binding.getRoot().setOnClickListener(v -> escucha.alPulsar(item));
        holder.binding.botonBorrar.setOnClickListener(v -> escucha.alBorrar(item));
    }

    static class RutinaVH extends RecyclerView.ViewHolder {
        final ItemRutinaBinding binding;

        RutinaVH(ItemRutinaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
