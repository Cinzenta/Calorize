package com.gustavo.calorize;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gustavo.calorize.adaptador.AdaptadorAlimento;
import com.gustavo.calorize.banco.dao.DaoAlimento;
import com.gustavo.calorize.banco.BancoDeDadosPrincipal;
import com.gustavo.calorize.modelo.Alimento;
import com.gustavo.calorize.modelo.AlimentoConsumido;
import com.gustavo.calorize.utilidades.Constantes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

// IMPLEMENTA AMBAS AS INTERFACES DO ADAPTADOR
public class AtividadeBuscaAlimento extends AppCompatActivity
        implements AdaptadorAlimento.OnItemClickListener, AdaptadorAlimento.OnItemLongClickListener {

    private ImageButton btnVoltar;
    private EditText etBuscar;
    private RecyclerView recyclerPersonalizados;
    private Button btnCriarAlimentoPersonalizado;

    private String tipoRefeicao;
    private long idUsuarioAtual;

    private DaoAlimento daoAlimento;
    private AdaptadorAlimento adaptadorAlimento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_busca_alimento);

        // Inicializar Views
        btnVoltar = findViewById(R.id.backArrow);
        etBuscar = findViewById(R.id.edit_buscar);
        recyclerPersonalizados = findViewById(R.id.recycler_personalizados);
        btnCriarAlimentoPersonalizado = findViewById(R.id.btn_refeicao_personalizada);

        // Inicialize o DAO
        daoAlimento = BancoDeDadosPrincipal.obterBancoDeDados(this).daoAlimento();

        // Obter o tipo de refeição E o ID do usuário da Intent
        if (getIntent() != null) {
            tipoRefeicao = getIntent().getStringExtra(Constantes.EXTRA_REFEICAO_TIPO);
            idUsuarioAtual = getIntent().getLongExtra(Constantes.EXTRA_ID_USUARIO, -1L);

            if (tipoRefeicao == null || tipoRefeicao.isEmpty() || idUsuarioAtual == -1L) {
                Log.e("AtividadeBuscaAlimento", "Erro: Dados de refeição ou usuário ausentes. ID do usuário: " + idUsuarioAtual + ", Tipo de refeição: " + tipoRefeicao);
                Toast.makeText(this, "Erro: Dados de refeição ou usuário ausentes. Por favor, reinicie o app.", Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED); // Retorna cancelado se dados estiverem faltando
                finish();
                return;
            }
        } else {
            Log.e("AtividadeBuscaAlimento", "Erro: Intent nula ao iniciar AtividadeBuscaAlimento.");
            Toast.makeText(this, "Erro interno. Por favor, tente novamente.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED); // Retorna cancelado
            finish();
            return;
        }

        // Configurar a seta de voltar
        btnVoltar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED); // Se o usuário apenas voltar, cancele a operação
            finish();
        });

        // Configurar o botão "Criar Alimento Personalizado"
        btnCriarAlimentoPersonalizado.setOnClickListener(v -> {
            Intent intent = new Intent(AtividadeBuscaAlimento.this, AtividadeCriarAlimento.class);
            // Ao criar alimento, passamos o ID do usuário para que o alimento seja vinculado a ele
            intent.putExtra(Constantes.EXTRA_ID_USUARIO, idUsuarioAtual);
            startActivityForResult(intent, Constantes.CODIGO_REQUISICAO_CRIAR_ALIMENTO);
        });

        // Configurar a barra de busca
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String termoBusca = s.toString().trim();
                if (!termoBusca.isEmpty()) {
                    buscarAlimentos(termoBusca);
                } else {
                    // Se a busca estiver vazia, mostre novamente os alimentos personalizados do usuário
                    carregarEExibirAlimentosPersonalizadosEComuns();
                }
            }
        });

        configurarRecyclerViews();

        // Carregar alimentos ao iniciar a tela
        carregarEExibirAlimentosPersonalizadosEComuns();
    }

    private void buscarAlimentos(String termoBusca) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Este metodo DaoAlimento.buscarAlimentosPorNome()
            List<Alimento> alimentosEncontrados = daoAlimento.buscarAlimentosPorNome(termoBusca, idUsuarioAtual);
            runOnUiThread(() -> {
                if (alimentosEncontrados != null && !alimentosEncontrados.isEmpty()) {
                    adaptadorAlimento.atualizarDados(alimentosEncontrados);
                    Log.d("AtividadeBuscaAlimento", "Busca '" + termoBusca + "' encontrou: " + alimentosEncontrados.size() + " alimentos.");
                } else {
                    adaptadorAlimento.atualizarDados(new ArrayList<>());
                    Log.d("AtividadeBuscaAlimento", "Busca '" + termoBusca + "' não encontrou alimentos.");
                    Toast.makeText(AtividadeBuscaAlimento.this, "Nenhum alimento encontrado para '" + termoBusca + "'", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // IMPLEMENTAÇÃO DO MeTODO onItemClick DA INTERFACE (Clique normal)
    @Override
    public void onItemClick(Alimento alimento) {
        // Este metodo é chamado quando um item de alimento é clicado normalmente no RecyclerView
        Log.d("AtividadeBuscaAlimento", "Alimento clicado: " + alimento.getNome());
        navegarParaAtividadeDefinirGramas(alimento);
    }

    // IMPLEMENTAÇÃO DO MeTODO onItemLongClick DA INTERFACE (Clique longo)
    @Override
    public void onItemLongClick(Alimento alimento) {
        Log.d("AtividadeBuscaAlimento", "Alimento clicado longamente: " + alimento.getNome());
        if (alimento.getIdUsuario() == idUsuarioAtual && alimento.getIdUsuario() != 0L) { // Verifica se é um alimento personalizado
            showDeleteCustomFoodDialog(alimento);
        } else {
            Toast.makeText(AtividadeBuscaAlimento.this, "Você não pode excluir alimentos padrão ou de outros usuários.", Toast.LENGTH_SHORT).show();
        }
    }

    // Metodo para ir ate AtividadeDefinirGramas
    private void navegarParaAtividadeDefinirGramas(Alimento alimento) {
        Intent intent = new Intent(AtividadeBuscaAlimento.this, AtividadeDefinirGramas.class);
        intent.putExtra(Constantes.EXTRA_ALIMENTO_SELECIONADO_OBJETO, alimento);
        intent.putExtra(Constantes.EXTRA_REFEICAO_TIPO, tipoRefeicao); // Passa o tipo de refeição
        startActivityForResult(intent, Constantes.CODIGO_REQUISICAO_DEFINIR_GRAMAS); // USA CONSTANTES.JAVA
    }

    private void configurarRecyclerViews() {
        // Configuração do RecyclerView para alimentos personalizados
        recyclerPersonalizados.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adaptadorAlimento = new AdaptadorAlimento(new ArrayList<>(), this, this);
        recyclerPersonalizados.setAdapter(adaptadorAlimento);
    }

    private void carregarEExibirAlimentosPersonalizadosEComuns() {
        if (idUsuarioAtual == -1L) {
            Log.e("AtividadeBuscaAlimento", "Não foi possível carregar alimentos: ID do usuário é -1.");
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            // Este metodo e para carregar alimentos  personalizados do usuário atual.
            List<Alimento> alimentos = daoAlimento.obterTodosAlimentosComunsEPersonalizados(idUsuarioAtual);
            runOnUiThread(() -> {
                if (alimentos != null && !alimentos.isEmpty()) {
                    adaptadorAlimento.atualizarDados(alimentos);
                    Log.d("AtividadeBuscaAlimento", "Alimentos padrão e personalizados carregados: " + alimentos.size() + " para o usuário " + idUsuarioAtual);
                } else {
                    adaptadorAlimento.atualizarDados(new ArrayList<>());
                    Log.d("AtividadeBuscaAlimento", "Nenhum alimento padrão ou personalizado encontrado para o usuário " + idUsuarioAtual);
                    Toast.makeText(AtividadeBuscaAlimento.this, "Nenhum alimento encontrado. Crie um novo!", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    // meTODO para exibir um diálogo para confirmar a exclusão de um alimento personalizado
    private void showDeleteCustomFoodDialog(Alimento alimento) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Alimento Personalizado")
                .setMessage("Tem certeza que deseja excluir o alimento '" + alimento.getNome() + "'?\nEsta ação não pode ser desfeita.")
                .setPositiveButton("Excluir", (dialog, which) -> deletarAlimentoPersonalizado(alimento))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Deleta o alimento personalizado do banco de dados e atualiza a UI
    private void deletarAlimentoPersonalizado(Alimento alimento) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                daoAlimento.deletar(alimento);
                Log.d("AtividadeBuscaAlimento", "Alimento personalizado excluído: " + alimento.getNome() + " (ID: " + alimento.getId() + ")");
                runOnUiThread(() -> {
                    Toast.makeText(AtividadeBuscaAlimento.this, "'" + alimento.getNome() + "' excluído com sucesso.", Toast.LENGTH_SHORT).show();
                    String termoBuscaAtual = etBuscar.getText().toString().trim();
                    if (!termoBuscaAtual.isEmpty()) {
                        buscarAlimentos(termoBuscaAtual);
                    } else {
                        carregarEExibirAlimentosPersonalizadosEComuns();
                    }
                });
            } catch (Exception e) {
                Log.e("AtividadeBuscaAlimento", "Erro ao excluir alimento personalizado: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(AtividadeBuscaAlimento.this, "Erro ao excluir alimento. Consulte o log.", Toast.LENGTH_LONG).show());
            }
        });
    }


    // recebe ps dadpos
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // RETORNO DE ATIVIDADEDEFINIRGRAMAS
        if (requestCode == Constantes.CODIGO_REQUISICAO_DEFINIR_GRAMAS) {
            if (resultCode == RESULT_OK && data != null) {
                AlimentoConsumido alimentoConsumido = (AlimentoConsumido) data.getSerializableExtra(Constantes.RESULTADO_ALIMENTO_CONSUMIDO_OBJETO);

                if (alimentoConsumido != null) {
                    Intent resultadoParaPrincipalIntent = new Intent();
                    resultadoParaPrincipalIntent.putExtra(Constantes.RESULTADO_ALIMENTO_CONSUMIDO_OBJETO, alimentoConsumido);
                    resultadoParaPrincipalIntent.putExtra(Constantes.EXTRA_REFEICAO_TIPO, tipoRefeicao);

                    setResult(RESULT_OK, resultadoParaPrincipalIntent);
                    finish(); // Fecha AtividadeBuscaAlimento e retorna para AtividadePrincipal
                } else {
                    Toast.makeText(this, "Erro ao obter alimento consumido da tela de gramas.", Toast.LENGTH_SHORT).show();
                    Log.e("AtividadeBuscaAlimento", "AlimentoConsumido nulo retornado de AtividadeDefinirGramas.");
                    setResult(RESULT_CANCELED); // Retorna cancelado se o objeto for nulo
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("AtividadeBuscaAlimento", "Operação em AtividadeDefinirGramas cancelada. Permanece na BuscaAlimento.");
            }
        }

        else if (requestCode == Constantes.CODIGO_REQUISICAO_CRIAR_ALIMENTO) { // USA CONSTANTES.JAVA
            if (resultCode == RESULT_OK) {
                Log.d("AtividadeBuscaAlimento", "Retorno de AtividadeCriarAlimento. Recarregando alimentos personalizados.");
                String termoBuscaAtual = etBuscar.getText().toString().trim();
                if (!termoBuscaAtual.isEmpty()) {
                    buscarAlimentos(termoBuscaAtual);
                } else {
                    carregarEExibirAlimentosPersonalizadosEComuns();
                }
                Toast.makeText(this, "Alimento personalizado criado com sucesso!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("AtividadeBuscaAlimento", "Criação de alimento personalizado cancelada.");
                Toast.makeText(this, "Criação de alimento cancelada.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}