package br.edu.unisenai.chefdebolso.model;

import java.io.Serializable;

/**
 * Uma linha da conversa. Serializable para caber no Bundle quando a tela gira.
 *
 * O "tipo" decide de que lado e com que cor a bolha aparece (MensagemAdapter).
 */
public class Mensagem implements Serializable {

    public static final int USUARIO = 0;
    public static final int CHEF = 1;
    public static final int RECEITA = 2;
    public static final int ERRO = 3;

    private final int tipo;
    private String texto;
    private final Receita receita;

    private Mensagem(int tipo, String texto, Receita receita) {
        this.tipo = tipo;
        this.texto = texto;
        this.receita = receita;
    }

    public static Mensagem doUsuario(String texto) {
        return new Mensagem(USUARIO, texto, null);
    }

    //Começa vazia: o streaming vai preenchendo pedaço por pedaço.
    public static Mensagem doChef(String texto) {
        return new Mensagem(CHEF, texto, null);
    }

    public static Mensagem deReceita(Receita receita) {
        return new Mensagem(RECEITA, receita.getNome(), receita);
    }

    public static Mensagem deErro(String texto) {
        return new Mensagem(ERRO, texto, null);
    }

    public int getTipo() { return tipo; }

    public String getTexto() { return texto; }

    public Receita getReceita() { return receita; }

    // Usado pelo streaming: cada pedaço que chega é colado no final.
    public void acrescentar(String pedaco) {
        texto = (texto == null ? "" : texto) + pedaco;
    }

    public void substituirTexto(String novo) {
        texto = novo;
    }

    public boolean estaVazia() {
        return texto == null || texto.trim().isEmpty();
    }
}
