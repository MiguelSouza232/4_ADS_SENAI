package com.aula.playlist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aula.playlist.adapter.MusicaAdapter;
import com.aula.playlist.databinding.ActivityMainBinding;
import com.aula.playlist.databinding.TelaMusicaBinding;
import com.aula.playlist.model.Musica;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;


public class MainActivity extends AppCompatActivity implements MusicaAdapter.AcaoListener {

    private ActivityMainBinding binding;
    private MusicaAdapter adapter;

    private CollectionReference colecao;
    private ListenerRegistration registro;

    private String meuNome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(binding.toolbar);

        // Carregar dados do usuario
        carregarNome();

        // Ação do botão de adicionar
        binding.fabAdicionar.setOnClickListener(v -> mostrarDialogoNovaMusica());


        // SUA IMPLEMENTAÇÃO
        colecao = FirebaseFirestore.getInstance().collection("playlist");
        configurarLista();
        escutarPlaylist();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Para de escutar o Firestore quando a tela é fechada
        if (registro != null) {
            registro.remove();
        }
    }

    // ---------------------------------------------------------------------------------
    // Lista em tempo real (RF2, RF5, RF6)
    // ---------------------------------------------------------------------------------

    private void configurarLista() {
        adapter = new MusicaAdapter(this);
        binding.listaMusicas.setLayoutManager(new LinearLayoutManager(this));
        binding.listaMusicas.setAdapter(adapter);
    }

    private void escutarPlaylist() {
        // orderBy: da mais votada para a menos votada
        registro = colecao
                .orderBy("votos", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, erro) -> {
                    if (erro != null) {
                        mostrarMensagem("Não foi possível carregar a playlist.");
                        return;
                    }
                    if (snapshot == null) return;

                    // Atualiza sozinho sempre que alguém adiciona, vota ou exclui
                    adapter.submitList(snapshot.toObjects(Musica.class));

                    // RF2: mensagem central quando a playlist está vazia
                    binding.txtVazio.setVisibility(snapshot.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    // ---------------------------------------------------------------------------------
    // Cliques do adapter (RF7, RF8, RF9)
    // ---------------------------------------------------------------------------------

    @Override
    public void onVotar(Musica musica) {
        // RF7: o servidor soma 1 (sem perder votos simultâneos)
        colecao.document(musica.getId())
                .update("votos", FieldValue.increment(1))
                .addOnFailureListener(e -> mostrarMensagem("Não foi possível registrar o voto."));
    }

    @Override
    public void onExcluir(Musica musica) {
        // RF8: só quem indicou pode excluir
        if (meuNome == null || !meuNome.equals(musica.getIndicadoPor())) {
            Toast.makeText(this, "Essa música é de " + musica.getIndicadoPor(),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // RF9: confirmação
        new AlertDialog.Builder(this)
                .setTitle("Excluir")
                .setMessage("Tirar \"" + musica.getTitulo() + "\" da playlist?")
                .setNegativeButton("Não", null)
                .setPositiveButton("Excluir", (d, w) -> excluirMusica(musica))
                .show();
    }

    private void excluirMusica(Musica musica) {
        colecao.document(musica.getId())
                .delete()
                .addOnSuccessListener(v -> mostrarMensagem("Música removida"))
                .addOnFailureListener(e -> mostrarMensagem("Não foi possível remover a música."));
    }

    private void mostrarMensagem(String texto) {
        Snackbar.make(binding.getRoot(), texto, Snackbar.LENGTH_SHORT).show();
    }


    private void carregarNome() {
        SharedPreferences localNome = getSharedPreferences("nomeUsuario", MODE_PRIVATE);
        meuNome = localNome.getString("nomeUsuario", null);

        if (meuNome != null && !meuNome.equals("")) {
            Toast.makeText(this, "Bem-vindo(a) " + meuNome, Toast.LENGTH_LONG).show();
            return;
        }

        // Ativar a caixa de texto para digitar o nome
        EditText campo = new EditText(this);
        campo.setHint("Seu nome");

        new AlertDialog.Builder(this)
                .setTitle("Quem é você")
                .setMessage("Vai aparecer ao lado das músicas que voce indicar.")
                .setView(campo)
                .setCancelable(false)
                .setPositiveButton("Pronto",(dialogInterface, i) -> {
                    meuNome = campo.getText().toString();
                    localNome.edit().putString("nomeUsuario", meuNome).apply();

                }).show();

    }


    private void mostrarDialogoNovaMusica() {
        TelaMusicaBinding form = TelaMusicaBinding.inflate(getLayoutInflater());

        new AlertDialog.Builder(this)
                .setTitle("Indicar música")
                .setView(form.getRoot())
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Adicionar", (d, w) -> {
                    String titulo = String.valueOf(form.campoTitulo.getText()).trim();
                    String artista = String.valueOf(form.campoArtista.getText()).trim();

                    if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(artista)) {
                        Toast.makeText(this, "Preencha título e artista", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // RF3/RF4: grava na coleção "playlist" (votos = 0, criadoEm pelo servidor)
                    colecao.add(new Musica(titulo, artista, meuNome))
                            .addOnFailureListener(e ->
                                    mostrarMensagem("Não foi possível adicionar a música."));
                })
                .show();
    }
}
