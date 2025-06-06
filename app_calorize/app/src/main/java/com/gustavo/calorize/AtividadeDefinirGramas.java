package com.gustavo.calorize;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gustavo.calorize.modelo.Alimento;
import com.gustavo.calorize.modelo.AlimentoConsumido;
import com.gustavo.calorize.utilidades.Constantes;

import java.util.Calendar;
import java.util.Locale;

public class AtividadeDefinirGramas extends AppCompatActivity {

    private ImageButton btnVoltar;
    private EditText editDigitarGramas;
    private Button btnAdicionar;
    private TextView textTituloNutricional;
    private TextView textValorCalorias;
    private TextView textValorProteinas;
    private TextView textValorCarboidrato;
    private TextView textValorGordura;

    private Alimento alimentoOriginal;
    private String tipoRefeicao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_definir_gramas);

        // Inicializar Views
        btnVoltar = findViewById(R.id.backArrow);
        editDigitarGramas = findViewById(R.id.edit_digitar_gramas);
        btnAdicionar = findViewById(R.id.btn_adicionar);
        textTituloNutricional = findViewById(R.id.text_titulo_nutricional);
        textValorCalorias = findViewById(R.id.text_valor_calorias);
        textValorProteinas = findViewById(R.id.text_valor_proteinas);
        textValorCarboidrato = findViewById(R.id.text_valor_carboidrato);
        textValorGordura = findViewById(R.id.text_valor_gordura);

        // Receber dados da Intent
        Intent intent = getIntent();
        if (intent != null) {
            alimentoOriginal = (Alimento) intent.getSerializableExtra(Constantes.EXTRA_ALIMENTO_SELECIONADO_OBJETO);
            tipoRefeicao = intent.getStringExtra(Constantes.EXTRA_REFEICAO_TIPO);

            if (alimentoOriginal == null || tipoRefeicao == null || tipoRefeicao.isEmpty()) {
                Log.e("AtividadeDefinirGramas", "Erro: Dados do alimento ou refeição não encontrados. Alimento: " + (alimentoOriginal != null ? alimentoOriginal.getNome() : "NULO") + ", Tipo Refeição: " + tipoRefeicao);
                Toast.makeText(this, "Erro: Dados do alimento ou refeição não encontrados.", Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED); // Retorna cancelado se os dados estiverem ausentes
                finish();
                return;
            }
        } else {
            Log.e("AtividadeDefinirGramas", "Erro: Nenhuma Intent passada ao iniciar AtividadeDefinirGramas.");
            Toast.makeText(this, "Erro interno. Por favor, tente novamente.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED); // Retorna cancelado se não houver Intent
            finish();
            return;
        }

        // Exibir informações nutricionais por 100g inicialmente
        exibirInformacoesAlimentoOriginal();

        // TextWatcher para recalcular macros em tempo real
        editDigitarGramas.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                recalcularMacros(s.toString());
            }
        });

        // Configurar botão de Voltar
        btnVoltar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        // Configura botão adcionar
        btnAdicionar.setOnClickListener(v -> {
            String gramasStr = editDigitarGramas.getText().toString();
            if (gramasStr.isEmpty()) {
                Toast.makeText(AtividadeDefinirGramas.this, "Por favor, digite a quantidade em gramas.", Toast.LENGTH_SHORT).show();
                return;
            }

            double gramas;
            try {
                gramas = Double.parseDouble(gramasStr);
                if (gramas <= 0) {
                    Toast.makeText(AtividadeDefinirGramas.this, "A quantidade deve ser maior que zero.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(AtividadeDefinirGramas.this, "Quantidade inválida. Use apenas números.", Toast.LENGTH_SHORT).show();
                Log.e("AtividadeDefinirGramas", "Erro de formato numérico para gramas: " + gramasStr, e);
                return;
            }

            //Recalcular os valores finais para o AlimentoConsumido
            double fator = gramas / 100.0;

            double caloriasTotais = alimentoOriginal.getCalorias() * fator;
            double proteinasTotais = alimentoOriginal.getProteinas() * fator;
            double carboidratosTotais = alimentoOriginal.getCarboidratos() * fator;
            double gordurasTotais = alimentoOriginal.getGorduras() * fator;

            AlimentoConsumido alimentoConsumido = new AlimentoConsumido(
                    0L, // usuarioId - da atividade principal
                    alimentoOriginal.getId(), // alimentoOriginalId - ID do Alimento do banco de dados
                    alimentoOriginal.getNome(),
                    caloriasTotais,
                    proteinasTotais,
                    carboidratosTotais,
                    gordurasTotais,
                    gramas,
                    tipoRefeicao,
                    Calendar.getInstance().getTimeInMillis() //  para zerar no final do dia
            );

            //  Verifique se o objeto está sendo criado corretamente
            Log.d("AtividadeDefinirGramas", "AlimentoConsumido criado: " + alimentoConsumido.toString());

            // Prepara a Intent para retornar o AlimentoConsumido para a AtividadeBuscaAlimento
            Intent resultIntent = new Intent();
            resultIntent.putExtra(Constantes.RESULTADO_ALIMENTO_CONSUMIDO_OBJETO, alimentoConsumido);
            resultIntent.putExtra(Constantes.EXTRA_REFEICAO_TIPO, tipoRefeicao);

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void exibirInformacoesAlimentoOriginal() {
        if (alimentoOriginal == null) {
            textTituloNutricional.setText("Alimento não disponível");
            textValorCalorias.setText("0 kcal");
            textValorProteinas.setText("0.0 g");
            textValorCarboidrato.setText("0.0 g");
            textValorGordura.setText("0.0 g");
            return;
        }
        textTituloNutricional.setText(String.format(Locale.getDefault(), "Informação Nutricional de %s\n(por 100g)", alimentoOriginal.getNome()));
        textValorCalorias.setText(String.format(Locale.getDefault(), "%.0f kcal", (double) alimentoOriginal.getCalorias()));
        textValorProteinas.setText(String.format(Locale.getDefault(), "%.1f g", alimentoOriginal.getProteinas()));
        textValorCarboidrato.setText(String.format(Locale.getDefault(), "%.1f g", alimentoOriginal.getCarboidratos()));
        textValorGordura.setText(String.format(Locale.getDefault(), "%.1f g", alimentoOriginal.getGorduras()));
    }

    private void recalcularMacros(String gramasStr) {
        if (alimentoOriginal == null) {
            return;
        }

        double gramas;
        try {
            gramas = Double.parseDouble(gramasStr);
        } catch (NumberFormatException e) {
            exibirInformacoesAlimentoOriginal(); // Se o campo estiver vazio ou inválido, mostre os valores por 100g
            return;
        }

        if (gramas <= 0) {
            exibirInformacoesAlimentoOriginal(); // Se a quantidade for zero ou negativa, mostre os valores por 100g
            return;
        }

        // Calcula os novos valores
        double caloriasCalculadas = (alimentoOriginal.getCalorias() / 100.0) * gramas;
        double proteinasCalculadas = (alimentoOriginal.getProteinas() / 100.0) * gramas;
        double carboidratosCalculados = (alimentoOriginal.getCarboidratos() / 100.0) * gramas;
        double gordurasCalculadas = (alimentoOriginal.getGorduras() / 100.0) * gramas;

        // Atualiza os TextViews com os novos valores
        textTituloNutricional.setText(String.format(Locale.getDefault(), "Informação Nutricional de %s\n(para %.0f g)", alimentoOriginal.getNome(), gramas));
        textValorCalorias.setText(String.format(Locale.getDefault(), "%.0f kcal", caloriasCalculadas));
        textValorProteinas.setText(String.format(Locale.getDefault(), "%.1f g", proteinasCalculadas));
        textValorCarboidrato.setText(String.format(Locale.getDefault(), "%.1f g", carboidratosCalculados));
        textValorGordura.setText(String.format(Locale.getDefault(), "%.1f g", gordurasCalculadas));
    }
}