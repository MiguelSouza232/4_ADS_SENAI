package com.aula.playlist.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.aula.playlist.model.Musica;
import com.aula.playlist.repository.FirestorePlaylistRepository;
import com.aula.playlist.repository.MockPlaylistRepository;
import com.aula.playlist.repository.PlaylistRepository;
import com.aula.playlist.util.Evento;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;

public class PlaylistViewModel extends ViewModel {

    private final PlaylistRepository repositorio;
    private final PlaylistRepository.Registro registro;

    private final MutableLiveData<List<Musica>> musicas = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> carregando = new MutableLiveData<>(true);
    private final MutableLiveData<Evento<String>> mensagem = new MutableLiveData<>();

    public PlaylistViewModel(PlaylistRepository repositorio) {
        this.repositorio = repositorio;

        // Atualização em tempo real: cada mudança no servidor chega aqui
        this.registro = repositorio.observar(new PlaylistRepository.MusicasListener() {
            @Override
            public void onMusicas(List<Musica> lista) {
                musicas.setValue(lista);
                carregando.setValue(false);
            }

            @Override
            public void onErro(String texto) {
                carregando.setValue(false);
                mensagem.setValue(new Evento<>(texto));
            }
        });
    }

    public LiveData<List<Musica>> getMusicas() {
        return musicas;
    }

    public LiveData<Boolean> getCarregando() {
        return carregando;
    }

    public LiveData<Evento<String>> getMensagem() {
        return mensagem;
    }

    public void adicionar(String titulo, String artista, String indicadoPor) {
        repositorio.adicionar(new Musica(titulo, artista, indicadoPor), resultado(null));
    }

    public void votar(Musica musica) {
        repositorio.votar(musica.getId(), resultado(null));
    }

    public void excluir(Musica musica) {
        repositorio.excluir(musica.getId(), resultado("Música removida"));
    }

    private PlaylistRepository.ResultadoListener resultado(String mensagemSucesso) {
        return new PlaylistRepository.ResultadoListener() {
            @Override
            public void onSucesso() {
                if (mensagemSucesso != null) {
                    mensagem.setValue(new Evento<>(mensagemSucesso));
                }
            }

            @Override
            public void onErro(String texto) {
                mensagem.setValue(new Evento<>(texto));
            }
        };
    }

    @Override
    protected void onCleared() {
        registro.remover();
    }

    /** Escolhe Firestore quando o Firebase foi configurado; senão usa o Mock em memória. */
    public static class Factory implements ViewModelProvider.Factory {

        private final Context appContext;

        public Factory(Context context) {
            this.appContext = context.getApplicationContext();
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            boolean firebaseConfigurado = !FirebaseApp.getApps(appContext).isEmpty();
            PlaylistRepository repo = firebaseConfigurado
                    ? new FirestorePlaylistRepository()
                    : new MockPlaylistRepository();
            return modelClass.cast(new PlaylistViewModel(repo));
        }
    }
}
