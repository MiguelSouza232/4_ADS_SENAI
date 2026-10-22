package com.aula.playlist.repository;

import com.aula.playlist.model.Musica;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class FirestorePlaylistRepository implements PlaylistRepository {

    private static final String COLECAO = "playlist";

    private final CollectionReference colecao =
            FirebaseFirestore.getInstance().collection(COLECAO);

    // Desempate: mais votadas primeiro; entre iguais, a mais antiga primeiro
    // (documento recém-criado ainda sem data do servidor vai para o fim).
    private static final Comparator<Musica> ORDEM = (a, b) -> {
        int porVotos = Long.compare(b.getVotos(), a.getVotos());
        if (porVotos != 0) return porVotos;
        Date da = a.getCriadoEm();
        Date db = b.getCriadoEm();
        if (da == null && db == null) return 0;
        if (da == null) return 1;
        if (db == null) return -1;
        return da.compareTo(db);
    };

    @Override
    public Registro observar(MusicasListener listener) {
        ListenerRegistration registro = colecao
                .orderBy("votos", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, erro) -> {
                    if (erro != null) {
                        listener.onErro("Não foi possível carregar a playlist. Verifique a conexão.");
                        return;
                    }
                    if (snapshot == null) return;

                    List<Musica> lista = new ArrayList<>(snapshot.toObjects(Musica.class));
                    Collections.sort(lista, ORDEM);
                    listener.onMusicas(lista);
                });
        return registro::remove;
    }

    @Override
    public void adicionar(Musica musica, ResultadoListener resultado) {
        colecao.add(musica)
                .addOnSuccessListener(ref -> resultado.onSucesso())
                .addOnFailureListener(e ->
                        resultado.onErro("Não foi possível adicionar a música."));
    }

    @Override
    public void votar(String idMusica, ResultadoListener resultado) {
        // increment(1) soma no servidor, evitando perda de votos simultâneos
        colecao.document(idMusica)
                .update("votos", FieldValue.increment(1))
                .addOnSuccessListener(v -> resultado.onSucesso())
                .addOnFailureListener(e ->
                        resultado.onErro("Não foi possível registrar o voto."));
    }

    @Override
    public void excluir(String idMusica, ResultadoListener resultado) {
        colecao.document(idMusica)
                .delete()
                .addOnSuccessListener(v -> resultado.onSucesso())
                .addOnFailureListener(e ->
                        resultado.onErro("Não foi possível remover a música."));
    }
}
