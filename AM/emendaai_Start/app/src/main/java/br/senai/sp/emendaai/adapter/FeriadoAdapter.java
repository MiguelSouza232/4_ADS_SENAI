package br.senai.sp.emendaai.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.senai.sp.emendaai.R;
import br.senai.sp.emendaai.model.Feriado;
import br.senai.sp.emendaai.util.Datas;

public class FeriadoAdapter extends RecyclerView.Adapter<FeriadoAdapter.FeriadoViewHolder> {

    public interface OnFeriadoClickListener {
        void onFeriadoClick(Feriado feriado);
    }

    private final List<Feriado> listaFeriado;
    private final OnFeriadoClickListener listener;

    public FeriadoAdapter(List<Feriado> listaFeriado, OnFeriadoClickListener listener) {
        this.listaFeriado = listaFeriado;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FeriadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View card = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feriado, parent, false);
        return new FeriadoViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull FeriadoViewHolder holder, int position) {
        Feriado item = listaFeriado.get(position);

        holder.txtDia.setText(Datas.dia(item.getData()));
        holder.txtMes.setText(Datas.mesCurto(item.getData()));
        holder.txtNome.setText(item.getNome());
        holder.txtSemana.setText(Datas.diaDaSemana(item.getData()));
        holder.txtContagem.setText(Datas.contagem(item.getData()));

        boolean ehEmenda = Datas.ehEmenda(item.getData());
        holder.txtSelo.setVisibility(ehEmenda ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFeriadoClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaFeriado.size();
    }

    public class FeriadoViewHolder extends RecyclerView.ViewHolder {
        TextView txtDia, txtMes, txtNome, txtSemana, txtContagem, txtSelo;

        public FeriadoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDia = itemView.findViewById(R.id.txtDia);
            txtMes = itemView.findViewById(R.id.txtMes);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtSemana = itemView.findViewById(R.id.txtSemana);
            txtContagem = itemView.findViewById(R.id.txtContagem);
            txtSelo = itemView.findViewById(R.id.txtSelo);
        }
    }
}