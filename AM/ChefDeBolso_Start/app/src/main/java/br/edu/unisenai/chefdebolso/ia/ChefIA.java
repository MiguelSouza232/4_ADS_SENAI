package br.edu.unisenai.chefdebolso.ia;

import android.content.Context;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.ChatFutures;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerativeBackend;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import br.edu.unisenai.chefdebolso.Config;
import br.edu.unisenai.chefdebolso.model.Mensagem;

// Definir comportamento de uso da IA
public class ChefIA {

    // Definir interface para comportamento do chat
    public interface OuvinteConversa {
        void aoReceberPalavra(String resposta);
        void aoTerminar(String erro);
    }
    private Executor executorPrincipal;
    private GenerativeModelFutures modeloChat;
    private GenerativeModelFutures modeloReceita;

    private ChatFutures chat;
    private volatile Subscription assinaturaAtual;

    public ChefIA(Context contexto){
        executorPrincipal = ContextCompat.getMainExecutor(contexto);

        FirebaseAI ai = FirebaseAI.getInstance(GenerativeBackend.googleAI());

        //1° passo: definir o persona do chat
        Content instrucaoChef = new Content.Builder()
                .addText(Config.INSTRUCAO_CHEF)
                .build();
        GenerativeModel chef = ai.generativeModel(Config.MODELO,null,null,null,null,instrucaoChef);
        modeloChat = GenerativeModelFutures.from(chef);

        chat = modeloChat.startChat();
    }

    static List<Content> montarHistorico(List<Mensagem> conversa) {
        List<String> papeis = new ArrayList<>();
        List<StringBuilder> textos = new ArrayList<>();

        for (Mensagem m : conversa) {
            String papel;
            String texto;
            switch (m.getTipo()) {
                case Mensagem.USUARIO:
                    papel = "user";
                    texto = m.getTexto();
                    break;
                case Mensagem.CHEF:
                    papel = "model";
                    texto = m.getTexto();
                    break;
                case Mensagem.RECEITA:
                    papel = "model";
                    texto = m.getReceita().comoTexto();
                    break;
                default:
                    continue; // ERRO não entra no histórico
            }
            if (texto == null || texto.trim().isEmpty()) {
                continue;
            }
            int ultimo = papeis.size() - 1;
            if (ultimo >= 0 && papeis.get(ultimo).equals(papel)) {
                textos.get(ultimo).append("\n").append(texto);
            } else {
                papeis.add(papel);
                textos.add(new StringBuilder(texto));
            }
        }

        List<Content> historico = new ArrayList<>();
        for (int i = 0; i < papeis.size(); i++) {
            // Um Builder NOVO para cada turno. Reaproveitar o builder do "user"
            // para montar o "model" faz o chef esquecer quem disse o quê.
            historico.add(new Content.Builder()
                    .setRole(papeis.get(i))
                    .addText(textos.get(i).toString())
                    .build());
        }
        return historico;
    }

    public void conversar(String texto, OuvinteConversa ouvinte){
        Content mensagem = new Content.Builder()
                .setRole("user")
                .addText(texto)
                .build();

        Publisher<GenerativeModelFutures> fluxo = chat.sendMessage(mensagem);

        fluxo.subscribe(new Subscriber<GenerativeModelFutures>() {
            @Override
            public void onSubscribe(Subscription s) {
                assinaturaAtual = s;
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(GenerativeModelFutures resposta) {
                String texto = resposta.getText();
                if (texto == null || texto.isEmpty()) return;

                executorPrincipal.execute(() -> ouvinte.aoReceberPalavra(texto));

            }

            @Override
            public void onError(Throwable t) {
                Log.e("Gemini_Error", t.getMessage());
            }

            @Override
            public void onComplete() {
                assinaturaAtual = null;
                executorPrincipal.execute(() -> ouvinte.aoTerminar(null));
            }
        });

    }

}
