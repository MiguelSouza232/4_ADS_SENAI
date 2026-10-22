package com.aula.playlist.repository;

import com.aula.playlist.model.Musica;

import java.util.List;

/** Abstração da camada de dados da playlist (Firestore real ou Mock em memória). */
public interface PlaylistRepository {

    interface MusicasListener {
        void onMusicas(List<Musica> musicas);

        void onErro(String mensagem);
    }

    interface ResultadoListener {
        void onSucesso();

        void onErro(String mensagem);
    }

    /** Permite parar de ouvir as atualizações em tempo real. */
    interface Registro {
        void remover();
    }

    /** Escuta a playlist em tempo real, já ordenada por votos (maior para menor). */
    Registro observar(MusicasListener listener);

    void adicionar(Musica musica, ResultadoListener resultado);

    void votar(String idMusica, ResultadoListener resultado);

    void excluir(String idMusica, ResultadoListener resultado);
}
