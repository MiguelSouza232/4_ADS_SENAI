package br.edu.unisenai.chefdebolso.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.ai.type.Content;

import java.util.ArrayList;
import java.util.List;

import br.edu.unisenai.chefdebolso.R;
import br.edu.unisenai.chefdebolso.databinding.ItemMensagemBinding;
import br.edu.unisenai.chefdebolso.model.Mensagem;

public class MensagemAdapter extends RecyclerView.Adapter<MensagemAdapter.MensagemHolder> {

    private List<Mensagem> mensagens = new ArrayList<>();

    @NonNull
    @Override
    public MensagemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensagem,parent, false);
        return new MensagemHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MensagemHolder holder, int position) {
        holder.ligar(mensagens.get(position));
    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }



    public class MensagemHolder extends RecyclerView.ViewHolder {
        private TextView tvTexto;
        private LinearLayout bolha;

        public MensagemHolder(@NonNull View itemView) {
            super(itemView);
            tvTexto = itemView.findViewById(R.id.tvTexto);
            bolha = itemView.findViewById(R.id.bolha);
        }

        void ligar(Mensagem msg){
            //obter a posição do BULLET de conversa
            Context c = itemView.getContext();
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) bolha.getLayoutParams();

            // Regra: texto do usuario a DIREIRA , demais a ESQUERDA
            lp.gravity = msg.getTipo() == Mensagem.USUARIO ? Gravity.RIGHT : Gravity.LEFT;
            bolha.setLayoutParams(lp);

            tvTexto.setAlpha(1f);
            tvTexto.setTextColor(ContextCompat.getColor(c,R.color.texto));

            switch (msg.getTipo()){
                case Mensagem.USUARIO:
                    bolha.setBackgroundResource(R.drawable.bg_bolha_usuario);
                    tvTexto.setTextColor(ContextCompat.getColor(c,R.color.branco));
                    tvTexto.setText(msg.getTexto());
                    break;
                default:
                    bolha.setBackgroundResource(R.drawable.bg_bolha_chef);
                    if (msg.estaVazia()) {
                        tvTexto.setText("Digitando...");
                        tvTexto.setAlpha(0.6f);
                    } else {
                        tvTexto.setText(msg.getTexto());
                    }
                    break;

            }

        }
    }

    // Métodos para manipular a LISTA de MENSAGENS
    public int adicionar(Mensagem m) {
        mensagens.add(m);
        int posicao = mensagens.size() - 1;
        notifyItemInserted(posicao);
        return posicao;
    }

    public void atualizar(Mensagem m) {
        int posicao = mensagens.indexOf(m);
        if (posicao >= 0) {
            notifyItemChanged(posicao);
        }
    }

    public void substituir(Mensagem antiga, Mensagem nova) {
        int posicao = mensagens.indexOf(antiga);
        if (posicao >= 0) {
            mensagens.set(posicao, nova);
            notifyItemChanged(posicao);
        }
    }

    public void substituirTudo(List<Mensagem> novas) {
        int antes = mensagens.size();
        mensagens.clear();
        notifyItemRangeRemoved(0, antes);
        mensagens.addAll(novas);
        notifyItemRangeInserted(0, mensagens.size());
    }

    public void limpar() {
        int antes = mensagens.size();
        mensagens.clear();
        notifyItemRangeRemoved(0, antes);
    }

    public List<Mensagem> getMensagens() {
        return mensagens;
    }
    // FIM
}
