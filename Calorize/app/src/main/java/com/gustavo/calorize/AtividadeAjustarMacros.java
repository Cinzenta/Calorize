package com.gustavo.calorize;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log; // Adicionado para Log.e
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.gustavo.calorize.banco.BancoDeDadosPrincipal;
import com.gustavo.calorize.banco.dao.DaoUsuario;
import com.gustavo.calorize.modelo.Usuario;
import com.gustavo.calorize.utilidades.Constantes;

import java.util.Locale; // Import para Locale
import java.util.concurrent.Executors;

public class AtividadeAjustarMacros extends AppCompatActivity {

    private static final String TAG = "AtividadeAjustarMacros"; // TAG para logs

    private EditText etMetaCalorias;

    private EditText etPercentCarbo;
    private EditText etPercentProteina;
    private EditText etPercentGordura;

    private TextView tvGramasCarbo;
    private TextView tvGramasProteina;
    private TextView tvGramasGordura;
    private TextView tvPercentTotal;

    private Button btnSalvarMetas;
    private TextView tvTituloAtividade;

    private Usuario usuarioAtual;
    private DaoUsuario daoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_ajustar_macros);

        daoUsuario = BancoDeDadosPrincipal.obterBancoDeDados(this).daoUsuario();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constantes.EXTRA_USUARIO_OBJETO)) {
            usuarioAtual = (Usuario) intent.getSerializableExtra(Constantes.EXTRA_USUARIO_OBJETO);
            if (usuarioAtual == null) {
                Log.e(TAG, "Erro: Usuário não encontrado no Intent. Redirecionando.");
                Toast.makeText(this, "Erro: Usuário não encontrado. Retornando para a tela principal.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, AtividadePrincipalInicial.class));
                finish();
                return;
            }
        } else {
            Log.e(TAG, "Erro: Nenhum usuário foi passado no Intent. Redirecionando.");
            Toast.makeText(this, "Erro: Nenhum usuário foi passado. Retornando para a tela principal.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, AtividadePrincipalInicial.class));
            finish();
            return;
        }

        tvTituloAtividade = findViewById(R.id.tvTituloMacros);
        etMetaCalorias = findViewById(R.id.etMetaCalorias);

        etPercentCarbo = findViewById(R.id.etCarboPercent);
        etPercentProteina = findViewById(R.id.etProteinaPercent);
        etPercentGordura = findViewById(R.id.etGorduraPercent);

        tvGramasCarbo = findViewById(R.id.tvCarboGramas);
        tvGramasProteina = findViewById(R.id.tvProteinaGramas);
        tvGramasGordura = findViewById(R.id.tvGorduraGramas);
        tvPercentTotal = findViewById(R.id.tvTotalPercent);

        btnSalvarMetas = findViewById(R.id.btnSalvarMacros);

        // btnVoltar
        ImageButton btnVoltar = findViewById(R.id.backArrowMacros);
        btnVoltar.setOnClickListener(v -> {
            Intent backIntent = new Intent(AtividadeAjustarMacros.this, AtividadePrincipal.class);
            backIntent.putExtra(Constantes.EXTRA_USUARIO_OBJETO, usuarioAtual);
            startActivity(backIntent);
            finish();
        });

        exibirDadosUsuarioEMetas();

        TextWatcher observadorDeTextoMacro = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                calcularEExibirMetas();
            }
        };

        etPercentCarbo.addTextChangedListener(observadorDeTextoMacro);
        etPercentProteina.addTextChangedListener(observadorDeTextoMacro);
        etPercentGordura.addTextChangedListener(observadorDeTextoMacro);

        etMetaCalorias.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                calcularEExibirMetas();
            }
        });


        btnSalvarMetas.setOnClickListener(v -> {
            if (validarESalvarMetas()) {
                Intent intentSalvar = new Intent(AtividadeAjustarMacros.this, AtividadePrincipal.class);
                intentSalvar.putExtra(Constantes.EXTRA_USUARIO_OBJETO, usuarioAtual);
                startActivity(intentSalvar);
                finish();
            }
        });

        calcularEExibirMetas();
    }

    private void exibirDadosUsuarioEMetas() {
        if (usuarioAtual != null) {
            tvTituloAtividade.setText("Defina suas Metas de Macronutrientes");

            etMetaCalorias.setText(String.valueOf(usuarioAtual.getMetaDeCalorias()));

            int metaDeCalorias = usuarioAtual.getMetaDeCalorias();

            if (metaDeCalorias > 0) {
                double carboPercent = (usuarioAtual.getMetaCarboidratos() * 4.0 / metaDeCalorias) * 100.0;
                double proteinaPercent = (usuarioAtual.getMetaProteinas() * 4.0 / metaDeCalorias) * 100.0;
                double gorduraPercent = (usuarioAtual.getMetaGorduras() * 9.0 / metaDeCalorias) * 100.0;

                // Uso Locale.getDefault()
                etPercentCarbo.setText(String.format(Locale.getDefault(), "%.0f", carboPercent));
                etPercentProteina.setText(String.format(Locale.getDefault(), "%.0f", proteinaPercent));
                etPercentGordura.setText(String.format(Locale.getDefault(), "%.0f", gorduraPercent));
            } else {
                etPercentCarbo.setText("0");
                etPercentProteina.setText("0");
                etPercentGordura.setText("0");
            }
        }
    }

    private void calcularEExibirMetas() {
        int metaDeCalorias = 0;
        try {
            String sCalorias = etMetaCalorias.getText().toString();
            if (!sCalorias.isEmpty()) {
                metaDeCalorias = Integer.parseInt(sCalorias);
            }
        } catch (NumberFormatException e) {
            // Bloco catch vazio preenchido com Log.e
            Log.e(TAG, "Erro de formato de número para meta de calorias: " + e.getMessage());
        }

        double percentCarbo = obterDoubleDoEditText(etPercentCarbo);
        double percentProteina = obterDoubleDoEditText(etPercentProteina);
        double percentGordura = obterDoubleDoEditText(etPercentGordura);

        // Uso Locale.getDefault()
        tvPercentTotal.setText(String.format(Locale.getDefault(), "Total: %.0f%%", percentCarbo + percentProteina + percentGordura));

        double somaPercent = percentCarbo + percentProteina + percentGordura;

        if (somaPercent > 100.001 || somaPercent < 99.999) {
            tvPercentTotal.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            btnSalvarMetas.setEnabled(false);
        } else {
            tvPercentTotal.setTextColor(getResources().getColor(android.R.color.black));
            btnSalvarMetas.setEnabled(true);
        }

        double caloriasCarbo = metaDeCalorias * (percentCarbo / 100.0);
        double caloriasProteina = metaDeCalorias * (percentProteina / 100.0);
        double caloriasGordura = metaDeCalorias * (percentGordura / 100.0);

        double gramasCarbo = (metaDeCalorias > 0) ? caloriasCarbo / 4.0 : 0.0;
        double gramasProteina = (metaDeCalorias > 0) ? caloriasProteina / 4.0 : 0.0;
        double gramasGordura = (metaDeCalorias > 0) ? caloriasGordura / 9.0 : 0.0;

        // string para exibição
        tvGramasCarbo.setText(String.format(Locale.getDefault(), "%.1f", gramasCarbo) + "g");
        tvGramasProteina.setText(String.format(Locale.getDefault(), "%.1f", gramasProteina) + "g");
        tvGramasGordura.setText(String.format(Locale.getDefault(), "%.1f", gramasGordura) + "g");
    }

    private double obterDoubleDoEditText(EditText editText) {
        try {
            String texto = editText.getText().toString();
            if (texto.isEmpty()) {
                return 0.0;
            }
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Erro de formato de número ao obter double do EditText: " + e.getMessage());
            return 0.0;
        }
    }

    private boolean validarESalvarMetas() {
        String sCalorias = etMetaCalorias.getText().toString();
        double percentCarbo = obterDoubleDoEditText(etPercentCarbo);
        double percentProteina = obterDoubleDoEditText(etPercentProteina);
        double percentGordura = obterDoubleDoEditText(etPercentGordura);

        if (sCalorias.isEmpty() || sCalorias.equals("0")) {
            Toast.makeText(this, "Por favor, defina uma meta de calorias válida (maior que 0).", Toast.LENGTH_LONG).show();
            return false;
        }

        int novasCalorias;
        try {
            novasCalorias = Integer.parseInt(sCalorias);
            if (novasCalorias <= 0) {
                Toast.makeText(this, "A meta de calorias deve ser um valor positivo.", Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Meta de calorias inválida.", Toast.LENGTH_LONG).show();
            return false;
        }

        double somaPercent = percentCarbo + percentProteina + percentGordura;

        if (somaPercent < 99.999 || somaPercent > 100.001) {
            Toast.makeText(this, "A soma das porcentagens deve ser 100%. Total atual: " + String.format(Locale.getDefault(), "%.0f%%", somaPercent), Toast.LENGTH_LONG).show();
            return false;
        }

        usuarioAtual.setMetaDeCalorias(novasCalorias);

        double novasCarboidratosG = (novasCalorias * (percentCarbo / 100.0)) / 4.0;
        double novasProteinasG = (novasCalorias * (percentProteina / 100.0)) / 4.0;
        double novasGordurasG = (novasCalorias * (percentGordura / 100.0)) / 9.0;

        usuarioAtual.setMetaCarboidratos(novasCarboidratosG);
        usuarioAtual.setMetaProteinas(novasProteinasG);
        usuarioAtual.setMetaGorduras(novasGordurasG);

        Executors.newSingleThreadExecutor().execute(() -> {
            daoUsuario.atualizar(usuarioAtual);

            runOnUiThread(() -> Toast.makeText(AtividadeAjustarMacros.this, "Metas de macros e calorias salvas para " + usuarioAtual.getNome() + "!", Toast.LENGTH_SHORT).show());
        });
        return true;
    }
}
