package com.aula.playlist.repository;

import android.os.Handler;
import android.os.Looper;

import com.aula.playlist.model.Musica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Implementação em memória, usada apenas quando o google-services.json não existe.
 * Permite testar a interface localmente sem Firebase (não sincroniza entre aparelhos).
 */
public class MockPlaylistRepository implements PlaylistRepository {

    private final Handler main = new Handler(Looper.getMainLooper());
    private final List<Musica> dados = new ArrayList<>();
    private final List<MusicasListener> ouvintes = new ArrayList<>();
    private int proximoId = 1;

    @Override
    public Registro observar(MusicasListener listener) {
        ouvintes.add(listener);
        main.post(() -> listener.onMusicas(copiaOrdenada()));
        return () -> ouvintes.remove(listener);
    }

    @Override
    public void adicionar(Musica musica, ResultadoListener resultado) {
        musica.setId("mock-" + (proximoId++));
        musica.setCriadoEm(new Date());
        dados.add(musica);
        notificar();
        resultado.onSucesso();
    }

    @Override
    public void votar(String idMusica, ResultadoListener resultado) {
        for (Musica m : dados) {
            if (m.getId().equals(idMusica)) {
                m.setVotos(m.getVotos() + 1);
                notificar();
                resultado.onSucesso();
                return;
            }
        }
        resultado.onErro("Música não encontrada.");
    }

    @Override
    public void excluir(String idMusica, ResultadoListener resultado) {
        Iterator<Musica> it = dados.iterator();
        while (it.hasNext()) {
            if (it.next().getId().equals(idMusica)) {
                it.remove();
                notificar();
                resultado.onSucesso();
                return;
            }
        }
        resultado.onErro("Música não encontrada.");
    }

    private void notificar() {
        List<Musica> copia = copiaOrdenada();
        for (MusicasListener l : new ArrayList<>(ouvintes)) {
            l.onMusicas(copia);
        }
    }

    private List<Musica> copiaOrdenada() {
        List<Musica> copia = new ArrayList<>();
        for (Musica m : dados) {
            Musica c = new Musica(m.getTitulo(), m.getArtista(), m.getIndicadoPor());
            c.setId(m.getId());
            c.setVotos(m.getVotos());
            c.setCriadoEm(m.getCriadoEm());
            copia.add(c);
        }
        Collections.sort(copia, (a, b) -> Long.compare(b.getVotos(), a.getVotos()));
        return copia;
    }
}
