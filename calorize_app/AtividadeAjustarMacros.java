package com.gustavo.calorize;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.gustavo.calorize.banco.BancoDeDadosPrincipal; // Importe o banco de dados Room
import com.gustavo.calorize.banco.dao.DaoUsuario; // Importe o DAO
import com.gustavo.calorize.modelo.Usuario; // Importe  Usuario
import com.gustavo.calorize.utilidades.Constantes; // Para a chave do Intent

import java.util.concurrent.Executors; // Para operações de banco de dad

public class AtividadeAjustarMacros extends AppCompatActivity {


    private EditText etMetaCalorias;

    // Declaração dos campos para porcentagem
    private EditText etPercentCarbo;
    private EditText etPercentProteina;
    private EditText etPercentGordura;

    // Declaração dos campos para exibir as gramas calculadas
    private TextView tvGramasCarbo;
    private TextView tvGramasProteina;
    private TextView tvGramasGordura;
    private TextView tvPercentTotal;

    private Button btnSalvarMetas;
    private TextView tvTituloAtividade;

    private Usuario usuarioAtual;
    private DaoUsuario daoUsuario;

    // Botão de Voltar
    private ImageButton btnVoltar; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_ajustar_macros);

        // Inicializar o DAO do Room
        daoUsuario = BancoDeDadosPrincipal.obterBancoDeDados(this).daoUsuario();

        // Obter o objeto Usuario do Intent (passado pela Activity anterior)
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constantes.EXTRA_USUARIO_OBJETO)) {
            usuarioAtual = (Usuario) intent.getSerializableExtra(Constantes.EXTRA_USUARIO_OBJETO);
            if (usuarioAtual == null) {
                Toast.makeText(this, "Erro: Usuário não encontrado. Retornando para a tela principal.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, AtividadePrincipalInicial.class));
                finish();
                return;
            }
        } else {
            Toast.makeText(this, "Erro: Nenhum usuário foi passado. Retornando para a tela principal.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, AtividadePrincipalInicial.class));
            finish();
            return;
        }

        //  Conectar os elementos com o layout
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

        btnVoltar = findViewById(R.id.backArrowMacros);
        btnVoltar.setOnClickListener(v -> {
            Intent backIntent = new Intent(AtividadeAjustarMacros.this, AtividadePrincipal.class);
            backIntent.putExtra(Constantes.EXTRA_USUARIO_OBJETO, usuarioAtual);
            startActivity(backIntent);
            finish();
        });


        //  Preencher a  com os dados do usuário atual
        exibirDadosUsuarioEMetas();

        // Adicionar TextWatchers para recalcular em tempo real
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

        // TextWatcher para o campo de calorias
        etMetaCalorias.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                calcularEExibirMetas(); // Recalcula macros em gramas
            }
        });


        // Clique do botão Salvar Metas
        btnSalvarMetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarESalvarMetas()) {
                    // Se tudo deu certo
                    Intent intent = new Intent(AtividadeAjustarMacros.this, AtividadePrincipal.class);
                    intent.putExtra(Constantes.EXTRA_USUARIO_OBJETO, usuarioAtual);
                    startActivity(intent);
                    finish(); // Finaliza esta Activity
                }
            }
        });

        // Chama o cálculo inicial para exibir as macros iniciais
        calcularEExibirMetas();
    }

    // Metodo para exibir os dados do usuário e preencher os campos de edição
    private void exibirDadosUsuarioEMetas() {
        if (usuarioAtual != null) {
            tvTituloAtividade.setText("Defina suas Metas de Macronutrientes");

            // Preenche o campo de meta de calorias
            etMetaCalorias.setText(String.valueOf(usuarioAtual.getMetaDeCalorias()));

            int metaDeCalorias = usuarioAtual.getMetaDeCalorias();

            if (metaDeCalorias > 0) {
                // Usando getMetaProteinas(), getMetaGorduras(), getMetaCarboidratos()
                double carboPercent = (usuarioAtual.getMetaCarboidratos() * 4.0 / metaDeCalorias) * 100.0;
                double proteinaPercent = (usuarioAtual.getMetaProteinas() * 4.0 / metaDeCalorias) * 100.0;
                double gorduraPercent = (usuarioAtual.getMetaGorduras() * 9.0 / metaDeCalorias) * 100.0;

                etPercentCarbo.setText(String.format("%.0f", carboPercent));
                etPercentProteina.setText(String.format("%.0f", proteinaPercent));
                etPercentGordura.setText(String.format("%.0f", gorduraPercent));
            } else {
                // Se a meta de calorias for 0 ou não definida, zera as porcentagens
                etPercentCarbo.setText("0");
                etPercentProteina.setText("0");
                etPercentGordura.setText("0");
            }
        }
    }


    // Metodo para calcular e exibir calorias e macros
    private void calcularEExibirMetas() {
        int metaDeCalorias = 0;
        try {
            String sCalorias = etMetaCalorias.getText().toString();
            if (!sCalorias.isEmpty()) {
                metaDeCalorias = Integer.parseInt(sCalorias);
            }
        } catch (NumberFormatException e) {
        }

        double percentCarbo = obterDoubleDoEditText(etPercentCarbo);
        double percentProteina = obterDoubleDoEditText(etPercentProteina);
        double percentGordura = obterDoubleDoEditText(etPercentGordura);

        double somaPercent = percentCarbo + percentProteina + percentGordura;
        tvPercentTotal.setText(String.format("Total: %.0f%%", somaPercent));

        // Feedback visual para a soma
        // evitar problemas de ponto flutuante
        if (somaPercent > 100.001 || somaPercent < 99.999) {
            tvPercentTotal.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            btnSalvarMetas.setEnabled(false); // Desabilita o botão se a soma não for 100
        } else {
            tvPercentTotal.setTextColor(getResources().getColor(android.R.color.black));
            btnSalvarMetas.setEnabled(true); // Habilita o botão
        }

        // Calcular calorias de cada macro
        double caloriasCarbo = metaDeCalorias * (percentCarbo / 100.0);
        double caloriasProteina = metaDeCalorias * (percentProteina / 100.0);
        double caloriasGordura = metaDeCalorias * (percentGordura / 100.0);

        // Convertendo calorias para gramas
        // Se a meta de calorias for 0, as gramas também devem ser 0
        double gramasCarbo = (metaDeCalorias > 0) ? caloriasCarbo / 4.0 : 0.0;
        double gramasProteina = (metaDeCalorias > 0) ? caloriasProteina / 4.0 : 0.0;
        double gramasGordura = (metaDeCalorias > 0) ? caloriasGordura / 9.0 : 0.0;

        // Exibir gramas nos TextViews
        tvGramasCarbo.setText(String.format("%.1f", gramasCarbo) + "g");
        tvGramasProteina.setText(String.format("%.1f", gramasProteina) + "g");
        tvGramasGordura.setText(String.format("%.1f", gramasGordura) + "g");
    }

    private double obterDoubleDoEditText(EditText editText) {
        try {
            String texto = editText.getText().toString();
            if (texto.isEmpty()) {
                return 0.0;
            }
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // Valida e salva os macros no botão
    private boolean validarESalvarMetas() {
        String sCalorias = etMetaCalorias.getText().toString();
        double percentCarbo = obterDoubleDoEditText(etPercentCarbo);
        double percentProteina = obterDoubleDoEditText(etPercentProteina);
        double percentGordura = obterDoubleDoEditText(etPercentGordura);

        if (sCalorias.isEmpty() || sCalorias.equals("0")) { // Meta de calorias não pode ser zero
            Toast.makeText(this, "Por favor, defina uma meta de calorias válida (maior que 0).", Toast.LENGTH_LONG).show();
            return false;
        }

        int novasCalorias = 0;
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

        if (somaPercent < 99.999 || somaPercent > 100.001) { // Checagem com tolerância
            Toast.makeText(this, "A soma das porcentagens deve ser 100%. Total atual: " + String.format("%.0f%%", somaPercent), Toast.LENGTH_LONG).show();
            return false;
        }

        // Se tudo validado, atualize o objeto usuarioAtual com as novas metas
        usuarioAtual.setMetaDeCalorias(novasCalorias);

        // Calculaa as gramas com base nas novas porcentagens e meta de calorias
        double novasCarboidratosG = (novasCalorias * (percentCarbo / 100.0)) / 4.0;
        double novasProteinasG = (novasCalorias * (percentProteina / 100.0)) / 4.0;
        double novasGordurasG = (novasCalorias * (percentGordura / 100.0)) / 9.0;

        usuarioAtual.setMetaCarboidratos(novasCarboidratosG);
        usuarioAtual.setMetaProteinas(novasProteinasG);
        usuarioAtual.setMetaGorduras(novasGordurasG);

        // Salvar o Usuario atualizado no banco de dados Room (em uma thread separada)
        Executors.newSingleThreadExecutor().execute(() -> {
            daoUsuario.atualizar(usuarioAtual);

            runOnUiThread(() -> {
                Toast.makeText(AtividadeAjustarMacros.this, "Metas de macros e calorias salvas para " + usuarioAtual.getNome() + "!", Toast.LENGTH_SHORT).show();
            });
        });
        return true;
    }
}