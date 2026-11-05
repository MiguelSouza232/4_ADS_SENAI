package br.edu.unisenai.chefdebolso.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Espelho do schema que mandamos para o Gemini
 */
public class Receita implements Serializable {

    private String nome;
    private int tempoMinutos;
    private String dificuldade;
    private List<String> ingredientes = new ArrayList<>();
    private List<String> passos = new ArrayList<>();
    private String dica;

    public String getNome() { return nome; }

    public int getTempoMinutos() { return tempoMinutos; }

    public String getDificuldade() { return dificuldade; }

    public List<String> getIngredientes() {
        return ingredientes == null ? new ArrayList<>() : ingredientes;
    }

    public List<String> getPassos() {
        return passos == null ? new ArrayList<>() : passos;
    }

    public String getDica() { return dica; }

    public boolean temDica() {
        return dica != null && !dica.trim().isEmpty();
    }


    public String comoTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append("Receita sugerida: ").append(nome)
          .append(" (").append(tempoMinutos).append(" min, ").append(dificuldade).append("). ");
        sb.append("Ingredientes: ").append(String.join(", ", getIngredientes())).append(". ");
        sb.append("Modo de preparo: ").append(String.join(" ", getPassos()));
        if (temDica()) {
            sb.append(" Dica: ").append(dica);
        }
        return sb.toString();
    }
}
