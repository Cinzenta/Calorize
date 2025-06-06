package com.gustavo.calorize;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gustavo.calorize.banco.BancoDeDadosPrincipal;
import com.gustavo.calorize.banco.dao.DaoAlimento;
import com.gustavo.calorize.modelo.Alimento;
import com.gustavo.calorize.utilidades.Constantes;

import java.util.concurrent.Executors;

public class AtividadeCriarAlimento extends AppCompatActivity {

    private EditText etNomeAlimento, etPorcao, etCalorias, etProteinas, etCarboidratos, etGorduras;
    private Button btnCriarAlimento;
    private ImageButton btnVoltar;

    private DaoAlimento daoAlimento;
    private long idUsuarioAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_criar_alimento);

        // Inicializar Views
        etNomeAlimento = findViewById(R.id.edit_digitar_nome);
        etPorcao = findViewById(R.id.edit_gramas_base);
        etCalorias = findViewById(R.id.edit_calorias);
        etProteinas = findViewById(R.id.edit_proteina);
        etCarboidratos = findViewById(R.id.edit_carboidrato);
        etGorduras = findViewById(R.id.edit_gordura);
        btnCriarAlimento = findViewById(R.id.btn_salvar_alimento);
        btnVoltar = findViewById(R.id.backArrow);

        // Inicialize o DAO
        daoAlimento = BancoDeDadosPrincipal.obterBancoDeDados(this).daoAlimento();

        // Obter o ID do usuário da Intent
        if (getIntent() != null) {
            idUsuarioAtual = getIntent().getLongExtra(Constantes.EXTRA_ID_USUARIO, -1L);
            if (idUsuarioAtual == -1L) {
                Log.e("AtividadeCriarAlimento", "Erro: ID do usuário ausente.");
                Toast.makeText(this, "Erro: ID do usuário ausente.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        } else {
            Log.e("AtividadeCriarAlimento", "Erro: Intent nula ao iniciar.");
            Toast.makeText(this, "Erro: Intent nula.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnVoltar.setOnClickListener(v -> finish());

        btnCriarAlimento.setOnClickListener(v -> criarNovoAlimento());
    }

    private void criarNovoAlimento() {
        String nome = etNomeAlimento.getText().toString().trim();
        String porcaoStr = etPorcao.getText().toString().trim();
        String caloriasStr = etCalorias.getText().toString().trim();
        String proteinasStr = etProteinas.getText().toString().trim();
        String carboidratosStr = etCarboidratos.getText().toString().trim();
        String gordurasStr = etGorduras.getText().toString().trim();

        if (nome.isEmpty() || porcaoStr.isEmpty() || caloriasStr.isEmpty() || proteinasStr.isEmpty() || carboidratosStr.isEmpty() || gordurasStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {


            double calorias = Double.parseDouble(caloriasStr);


            double proteinas = Double.parseDouble(proteinasStr);
            double carboidratos = Double.parseDouble(carboidratosStr);
            double gorduras = Double.parseDouble(gordurasStr);

            // Cria o objeto Alimento

            Alimento novoAlimento = new Alimento(nome, porcaoStr, calorias, proteinas, carboidratos, gorduras, idUsuarioAtual);

            // Insere o novo alimento no banco de dados
            Executors.newSingleThreadExecutor().execute(() -> {
                daoAlimento.insert(novoAlimento);
                Log.d("AtividadeCriarAlimento", "Alimento personalizado criado e inserido: " + novoAlimento.getNome());

                // Retorna o resultado para AtividadeBuscaAlimento na UI
                runOnUiThread(() -> {
                    Intent resultadoIntent = new Intent();
                    setResult(RESULT_OK);
                    finish();
                });
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valores numéricos inválidos para calorias/macros.", Toast.LENGTH_SHORT).show();
            Log.e("AtividadeCriarAlimento", "Erro de formato numérico: " + e.getMessage());
        }
    }
}