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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aula.playlist.adapter.MusicaAdapter;
import com.aula.playlist.databinding.ActivityMainBinding;
import com.aula.playlist.databinding.TelaMusicaBinding;
import com.aula.playlist.model.Musica;
import com.aula.playlist.util.Evento;
import com.aula.playlist.viewmodel.PlaylistViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;


public class MainActivity extends AppCompatActivity implements MusicaAdapter.AcaoListener {

    private ActivityMainBinding binding;
    private PlaylistViewModel viewModel;
    private MusicaAdapter adapter;

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
        configurarLista();
        configurarViewModel();
    }

    // ---------------------------------------------------------------------------------
    // Lista + atualização em tempo real
    // ---------------------------------------------------------------------------------

    private void configurarLista() {
        adapter = new MusicaAdapter(this);
        binding.listaMusicas.setLayoutManager(new LinearLayoutManager(this));
        binding.listaMusicas.setAdapter(adapter);
    }

    private void configurarViewModel() {
        viewModel = new ViewModelProvider(this, new PlaylistViewModel.Factory(this))
                .get(PlaylistViewModel.class);

        // RF5/RF6: a lista chega ordenada e se atualiza sozinha
        viewModel.getMusicas().observe(this, lista -> {
            adapter.submitList(lista);
            atualizarEstadoVazio();
        });

        viewModel.getCarregando().observe(this, carregando -> {
            binding.progressCarregando.setVisibility(
                    Boolean.TRUE.equals(carregando) ? View.VISIBLE : View.GONE);
            atualizarEstadoVazio();
        });

        // Feedbacks (ex.: "Música removida", falhas de conexão)
        viewModel.getMensagem().observe(this, (Evento<String> evento) -> {
            if (evento == null) return;
            String texto = evento.consumir();
            if (texto != null) {
                Snackbar.make(binding.getRoot(), texto, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    // RF2: mensagem central quando a playlist está vazia
    private void atualizarEstadoVazio() {
        List<Musica> lista = viewModel.getMusicas().getValue();
        boolean carregando = Boolean.TRUE.equals(viewModel.getCarregando().getValue());
        boolean vazia = lista == null || lista.isEmpty();
        binding.txtVazio.setVisibility(vazia && !carregando ? View.VISIBLE : View.GONE);
    }

    // ---------------------------------------------------------------------------------
    // Cliques do adapter (RF7, RF8, RF9)
    // ---------------------------------------------------------------------------------

    @Override
    public void onVotar(Musica musica) {
        viewModel.votar(musica);
    }

    @Override
    public void onExcluir(Musica musica) {
        if (!souDono(musica)) {
            Toast.makeText(this, "Essa música é de " + musica.getIndicadoPor(),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Excluir")
                .setMessage("Tirar \"" + musica.getTitulo() + "\" da playlist?")
                .setNegativeButton("Não", null)
                .setPositiveButton("Excluir", (d, w) -> viewModel.excluir(musica))
                .show();
    }

    private boolean souDono(Musica musica) {
        return meuNome != null && meuNome.equals(musica.getIndicadoPor());
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
                    viewModel.adicionar(titulo, artista, meuNome);
                })
                .show();
    }
}
