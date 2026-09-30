package br.senai.sp.emendaai;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.senai.sp.emendaai.api.BrasilApiService;
import br.senai.sp.emendaai.model.Endereco;
import br.senai.sp.emendaai.model.Feriado;
import br.senai.sp.emendaai.util.Datas;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetalheFeriadoActivity extends AppCompatActivity {

    private static final String TAG = "DetalheFeriadoActivity";

    private EditText campoCep;
    private ProgressBar progressoCep;
    private TextView txtResultadoCep;
    private Button btnCompartilhar;
    private Button btnBuscarCep;
    private TextView txtNomeFeriado;
    private TextView txtDataFeriado;
    private TextView txtContagemFeriado;
    private TextView txtAvisoEmenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_feriado);

        txtNomeFeriado = findViewById(R.id.txtNomeFeriado);
        txtDataFeriado = findViewById(R.id.txtDataFeriado);
        txtContagemFeriado = findViewById(R.id.txtContagemFeriado);
        txtAvisoEmenda = findViewById(R.id.txtAvisoEmenda);

        campoCep = findViewById(R.id.campoCep);
        progressoCep = findViewById(R.id.progressoCep);
        txtResultadoCep = findViewById(R.id.txtResultadoCep);
        btnCompartilhar = findViewById(R.id.btnCompartilhar);
        btnBuscarCep = findViewById(R.id.btnBuscarCep);

        btnBuscarCep.setOnClickListener(v -> buscarCep());

        Feriado feriado = (Feriado) getIntent().getSerializableExtra("feriado");
        if (feriado != null) {
            preencherDetalhes(feriado);
        }
    }

    private void preencherDetalhes(Feriado feriado) {
        txtNomeFeriado.setText(feriado.getNome());
        txtDataFeriado.setText(Datas.porExtenso(feriado.getData()));
        txtContagemFeriado.setText(Datas.contagem(feriado.getData()));

        boolean emenda = Datas.ehEmenda(feriado.getData());
        txtAvisoEmenda.setVisibility(emenda ? View.VISIBLE : View.GONE);
    }

    private void buscarCep() {
        String cepInformado = campoCep.getText().toString().trim();

        if (cepInformado.length() != 8 || !cepInformado.matches("\\d+")) {
            Toast.makeText(this, R.string.erro_cep_curto, Toast.LENGTH_SHORT).show();
            return;
        }

        btnBuscarCep.setEnabled(false);
        progressoCep.setVisibility(View.VISIBLE);
        txtResultadoCep.setVisibility(View.GONE);
        btnCompartilhar.setEnabled(false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://brasilapi.com.br/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BrasilApiService api = retrofit.create(BrasilApiService.class);
        Call<Endereco> requisicao = api.buscarCep(cepInformado);

        requisicao.enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                btnBuscarCep.setEnabled(true);
                progressoCep.setVisibility(View.GONE);

                if (response.code() == 404) {
                    Toast.makeText(DetalheFeriadoActivity.this, R.string.erro_cep_inexistente, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    Endereco endereco = response.body();
                    String mensagem = endereco.getStreet() + ", "
                            + endereco.getNeighborhood() + " - "
                            + endereco.getCity() + "/" + endereco.getState();
                    mostrarEndereco(mensagem);
                    return;
                }

                Toast.makeText(DetalheFeriadoActivity.this,
                        "Erro ao buscar CEP (código " + response.code() + ")",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable throwable) {
                Log.e(TAG, "Falha ao buscar CEP", throwable);
                btnBuscarCep.setEnabled(true);
                progressoCep.setVisibility(View.GONE);
                Toast.makeText(DetalheFeriadoActivity.this, R.string.msg_erro_rede, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarEndereco(String endereco) {
        txtResultadoCep.setText(endereco);
        txtResultadoCep.setVisibility(View.VISIBLE);
        btnCompartilhar.setEnabled(true);
    }

}
