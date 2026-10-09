package com.aula.playlist.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.playlist.databinding.ItemMusicaBinding;
import com.aula.playlist.model.Musica;

import java.util.Objects;

/**
 * Recebe a lista pronta e devolve os cliques de votar e excluir para a MainActivity.
 * Não conversa com o Firestore.
 */
public class MusicaAdapter extends ListAdapter<Musica, MusicaAdapter.MusicaViewHolder> {

    public interface AcaoListener {
        void onVotar(Musica musica);

        void onExcluir(Musica musica);
    }

    private static final DiffUtil.ItemCallback<Musica> DIFF = new DiffUtil.ItemCallback<Musica>() {
        @Override
        public boolean areItemsTheSame(@NonNull Musica antiga, @NonNull Musica nova) {
            return Objects.equals(antiga.getId(), nova.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Musica antiga, @NonNull Musica nova) {
            return antiga.getVotos() == nova.getVotos()
                    && Objects.equals(antiga.getTitulo(), nova.getTitulo())
                    && Objects.equals(antiga.getArtista(), nova.getArtista())
                    && Objects.equals(antiga.getIndicadoPor(), nova.getIndicadoPor());
        }
    };

    private final AcaoListener listener;

    public MusicaAdapter(AcaoListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    @NonNull
    @Override
    public MusicaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMusicaBinding binding = ItemMusicaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MusicaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicaViewHolder holder, int position) {
        holder.vincular(getItem(position));
    }

    class MusicaViewHolder extends RecyclerView.ViewHolder {

        private final ItemMusicaBinding binding;

        MusicaViewHolder(ItemMusicaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.btnVotar.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onVotar(getItem(pos));
                }
            });

            binding.btnExcluir.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onExcluir(getItem(pos));
                }
            });
        }

        void vincular(Musica musica) {
            binding.txtTitulo.setText(musica.getTitulo());
            binding.txtArtista.setText(musica.getArtista());
            binding.txtIndicadoPor.setText("indicada por " + musica.getIndicadoPor());
            binding.txtVotos.setText(String.valueOf(musica.getVotos()));
        }
    }
}
