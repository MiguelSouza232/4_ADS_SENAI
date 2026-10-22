package com.aula.playlist.util;

/** Envolve uma mensagem para que seja consumida uma única vez (ex.: Snackbar após rotação). */
public class Evento<T> {

    private final T conteudo;
    private boolean consumido = false;

    public Evento(T conteudo) {
        this.conteudo = conteudo;
    }

    /** Retorna o conteúdo na primeira chamada e null nas seguintes. */
    public T consumir() {
        if (consumido) return null;
        consumido = true;
        return conteudo;
    }
}
