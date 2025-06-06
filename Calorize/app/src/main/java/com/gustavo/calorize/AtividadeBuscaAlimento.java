package com.gustavo.calorize;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gustavo.calorize.adaptador.AdaptadorAlimento;
import com.gustavo.calorize.api.cliente.ClienteRetrofit;
import com.gustavo.calorize.api.modelo.AlimentoApiModelo;
import com.gustavo.calorize.api.servico.ServicoApi;
import com.gustavo.calorize.banco.BancoDeDadosPrincipal;
import com.gustavo.calorize.banco.dao.DaoAlimento;
import com.gustavo.calorize.modelo.Alimento;
import com.gustavo.calorize.modelo.AlimentoConsumido;
import com.gustavo.calorize.utilidades.Constantes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AtividadeBuscaAlimento extends AppCompatActivity
        implements AdaptadorAlimento.OnItemClickListener, AdaptadorAlimento.OnItemLongClickListener {

    private static final String TAG = "AtividadeBuscaAlimento";

    private ImageButton btnVoltar;
    private EditText etBuscar;
    private Button btnCriarAlimentoPersonalizado;

    private RecyclerView recyclerPersonalizados;
    private AdaptadorAlimento adaptadorAlimentosPersonalizados;

    private RecyclerView recyclerApi;
    private AdaptadorAlimento adaptadorAlimentosApi;

    private TextView tituloAlimentosFrequentes;
    private TextView tituloAlimentosPersonalizados;

    private String tipoRefeicao;
    private long idUsuarioAtual;

    private DaoAlimento daoAlimento;
    private ServicoApi servicoApi;

    private ActivityResultLauncher<Intent> criarAlimentoLauncher;
    private ActivityResultLauncher<Intent> definirGramasLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_busca_alimento);

        // Inicializar Views
        btnVoltar = findViewById(R.id.backArrow);
        etBuscar = findViewById(R.id.edit_buscar);
        btnCriarAlimentoPersonalizado = findViewById(R.id.btn_refeicao_personalizada);

        // Inicializar os dois RecyclerViews
        recyclerPersonalizados = findViewById(R.id.recycler_personalizados);
        recyclerApi = findViewById(R.id.recycler_frequentes);

        // Inicializar títulos
        tituloAlimentosFrequentes = findViewById(R.id.alimentos_frequentes_titulo);
        tituloAlimentosPersonalizados = findViewById(R.id.alimentos_personalizados_titulo);

        // Inicialize o DAO (banco de dados local)
        daoAlimento = BancoDeDadosPrincipal.obterBancoDeDados(this).daoAlimento();

        // Inicialize a interface da API Retrofit
        servicoApi = ClienteRetrofit.obterCliente().create(ServicoApi.class);

        // Registrar os ActivityResultLaunchers
        setupActivityResultLaunchers();

        // Obter o tipo de refeição E o ID do usuário da Intent
        if (getIntent() != null) {
            tipoRefeicao = getIntent().getStringExtra(Constantes.EXTRA_REFEICAO_TIPO);
            idUsuarioAtual = getIntent().getLongExtra(Constantes.EXTRA_ID_USUARIO, -1L);

            if (tipoRefeicao == null || tipoRefeicao.isEmpty() || idUsuarioAtual == -1L) {
                Log.e(TAG, "Erro: Dados de refeição ou usuário ausentes. ID do usuário: " + idUsuarioAtual + ", Tipo de refeição: " + tipoRefeicao);
                Toast.makeText(this, "Erro: Dados de refeição ou usuário ausentes. Por favor, reinicie o app.", Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
        } else {
            Log.e(TAG, "Erro: Intent nula ao iniciar AtividadeBuscaAlimento.");
            Toast.makeText(this, "Erro interno. Por favor, tente novamente.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        // Configurar a seta de voltar
        btnVoltar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        // Configurar o botão "Criar Alimento Personalizado"
        btnCriarAlimentoPersonalizado.setOnClickListener(v -> {
            Intent intent = new Intent(AtividadeBuscaAlimento.this, AtividadeCriarAlimento.class);
            intent.putExtra(Constantes.EXTRA_ID_USUARIO, idUsuarioAtual);
            criarAlimentoLauncher.launch(intent);
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
                // Chama o metodo unificado de busca/carregamento
                fetchAndDisplayFoods(termoBusca.isEmpty() ? null : termoBusca);
            }
        });

        configurarRecyclerViews();

        // Carrega alimentos ao iniciar a tela (padrão + personalizados)
        // Chama o metodo unificado para carregar todos os alimentos (termoBusca nulo)
        fetchAndDisplayFoods(null);
    }

    // Configura os ActivityResultLaunchers
    private void setupActivityResultLaunchers() {
        criarAlimentoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Log.d(TAG, "Retorno de AtividadeCriarAlimento. Recarregando alimentos.");
                        String termoBuscaAtual = etBuscar.getText().toString().trim();
                        fetchAndDisplayFoods(termoBuscaAtual.isEmpty() ? null : termoBuscaAtual);
                        Toast.makeText(this, "Alimento personalizado criado com sucesso!", Toast.LENGTH_SHORT).show();
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        Log.d(TAG, "Criação de alimento personalizado cancelada.");
                        Toast.makeText(this, "Criação de alimento cancelada.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        definirGramasLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        AlimentoConsumido alimentoConsumido = (AlimentoConsumido) result.getData().getSerializableExtra(Constantes.RESULTADO_ALIMENTO_CONSUMIDO_OBJETO);

                        if (alimentoConsumido != null) {
                            Intent resultadoParaPrincipalIntent = new Intent();
                            resultadoParaPrincipalIntent.putExtra(Constantes.RESULTADO_ALIMENTO_CONSUMIDO_OBJETO, alimentoConsumido);
                            resultadoParaPrincipalIntent.putExtra(Constantes.EXTRA_REFEICAO_TIPO, tipoRefeicao);

                            setResult(RESULT_OK, resultadoParaPrincipalIntent);
                            finish();
                        } else {
                            Toast.makeText(this, "Erro ao obter alimento consumido da tela de gramas.", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "AlimentoConsumido nulo retornado de AtividadeDefinirGramas.");
                            setResult(RESULT_CANCELED);
                        }
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        Log.d(TAG, "Operação em AtividadeDefinirGramas cancelada. Permanece na BuscaAlimento.");
                    }
                }
        );
    }

    private void fetchAndDisplayFoods(@Nullable String searchTerm) {
        final boolean isSearching = (searchTerm != null && !searchTerm.isEmpty());
        Log.d(TAG, "Iniciando fetchAndDisplayFoods para termo: '" + (isSearching ? searchTerm : "VAZIO") + "'");

        // Busca no banco de dados local (alimentos personalizados do usuário)
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Alimento> localResults;
            if (isSearching) {
                localResults = daoAlimento.buscarAlimentosPorNome(searchTerm, idUsuarioAtual);
            } else {
                // Se não há termo de busca, carrega todos os alimentos comuns e personalizados
                localResults = daoAlimento.obterTodosAlimentosComunsEPersonalizados(idUsuarioAtual);
            }
            // Ordenar por nome
            Collections.sort(localResults, (a1, a2) -> a1.getNome().compareToIgnoreCase(a2.getNome()));
            Log.d(TAG, "Encontrados " + localResults.size() + " alimentos locais para '" + (isSearching ? searchTerm : "tudo") + "'.");


            // Chamada à API externa (alimentos da API)
            Call<List<AlimentoApiModelo>> apiCall;
            if (isSearching) {
                apiCall = servicoApi.buscarAlimentos(searchTerm);
            } else {
                apiCall = servicoApi.obterTodosAlimentos();
            }

            apiCall.enqueue(new Callback<List<AlimentoApiModelo>>() {
                @Override
                public void onResponse(@NonNull Call<List<AlimentoApiModelo>> call, @NonNull Response<List<AlimentoApiModelo>> response) {
                    List<Alimento> apiResultsConverted = new ArrayList<>();
                    if (response.isSuccessful() && response.body() != null) {
                        List<AlimentoApiModelo> alimentosApi = response.body();
                        for (AlimentoApiModelo apiModelo : alimentosApi) {
                            apiResultsConverted.add(new Alimento(
                                    apiModelo.getNome(),
                                    apiModelo.getPorcao(),
                                    apiModelo.getCalorias(),
                                    apiModelo.getCarboidratos(),
                                    apiModelo.getProteinas(),
                                    apiModelo.getGorduras(),
                                    0L // ID do usuário 0L para indicar que é um alimento padrão da API
                            ));
                        }
                        // Ordenar por nome
                        Collections.sort(apiResultsConverted, (a1, a2) -> a1.getNome().compareToIgnoreCase(a2.getNome()));
                        Log.d(TAG, "API retornou " + apiResultsConverted.size() + " alimentos para '" + (isSearching ? searchTerm : "geral") + "'.");
                    } else {
                        Log.e(TAG, "Erro na resposta da API para '" + (isSearching ? searchTerm : "geral") + "': Código " + response.code() + ", Mensagem: " + response.message());
                        try (ResponseBody errorBody = response.errorBody()) {
                            if (errorBody != null) {
                                Log.e(TAG, "Corpo do erro da API: " + errorBody.string());
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Erro ao ler errorBody: " + e.getMessage());
                        }
                        runOnUiThread(() -> Toast.makeText(AtividadeBuscaAlimento.this, "Erro ao buscar na API.", Toast.LENGTH_SHORT).show());
                    }

                    // Atualizar a UI nos respectivos adaptadores
                    runOnUiThread(() -> {
                        // Gerenciar visibilidade dos títulos
                        tituloAlimentosPersonalizados.setVisibility(localResults.isEmpty() ? View.GONE : View.VISIBLE);
                        tituloAlimentosFrequentes.setVisibility(apiResultsConverted.isEmpty() ? View.GONE : View.VISIBLE);

                        adaptadorAlimentosPersonalizados.atualizarDados(localResults);
                        adaptadorAlimentosApi.atualizarDados(apiResultsConverted);

                        Log.d(TAG, "UI Atualizada. Total de alimentos (API): " + apiResultsConverted.size() +
                                ", (Personalizados): " + localResults.size());

                        if (localResults.isEmpty() && apiResultsConverted.isEmpty()) {
                            if (isSearching) {
                                Toast.makeText(AtividadeBuscaAlimento.this, "Nenhum alimento encontrado para '" + searchTerm + "'", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AtividadeBuscaAlimento.this, "Nenhum alimento encontrado. Crie um novo!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<List<AlimentoApiModelo>> call, @NonNull Throwable t) {
                    Log.e(TAG, "Falha na requisição da API para '" + (isSearching ? searchTerm : "geral") + "': " + t.getMessage(), t);
                    // Se a API falhar, mostre apenas os resultados locais (na thread principal)
                    runOnUiThread(() -> {
                        tituloAlimentosFrequentes.setVisibility(View.GONE); // Esconde o título da API se falhar
                        tituloAlimentosPersonalizados.setVisibility(localResults.isEmpty() ? View.GONE : View.VISIBLE);
                        adaptadorAlimentosPersonalizados.atualizarDados(localResults);
                        adaptadorAlimentosApi.atualizarDados(new ArrayList<>()); // Limpa a lista da API
                        Toast.makeText(AtividadeBuscaAlimento.this, "Erro de conexão com a API. Mostrando apenas alimentos personalizados.", Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
    }

    @Override
    public void onItemClick(Alimento alimento) {
        Log.d(TAG, "Alimento clicado: " + alimento.getNome() + " (ID Local: " + alimento.getId() + ")");
        navegarParaAtividadeDefinirGramas(alimento);
    }

    @Override
    public void onItemLongClick(Alimento alimento) {
        Log.d(TAG, "Alimento clicado longamente: " + alimento.getNome());
        // Apenas permite excluir se for um alimento criado pelo usuário atual (idUsuario != 0L)
        if (alimento.getIdUsuario() == idUsuarioAtual && alimento.getIdUsuario() != 0L) {
            mostarCustomDialogo(alimento);
        } else {
            Toast.makeText(AtividadeBuscaAlimento.this, "Você não pode excluir alimentos padrão da API.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navegarParaAtividadeDefinirGramas(Alimento alimento) {
        Intent intent = new Intent(AtividadeBuscaAlimento.this, AtividadeDefinirGramas.class);
        intent.putExtra(Constantes.EXTRA_ALIMENTO_SELECIONADO_OBJETO, alimento);
        intent.putExtra(Constantes.EXTRA_REFEICAO_TIPO, tipoRefeicao);
        definirGramasLauncher.launch(intent); //
    }

    private void configurarRecyclerViews() {
        // Configuração para alimentos personalizados
        recyclerPersonalizados.setLayoutManager(new GridLayoutManager(this, 3));
        adaptadorAlimentosPersonalizados = new AdaptadorAlimento(new ArrayList<>(), this, this);
        recyclerPersonalizados.setAdapter(adaptadorAlimentosPersonalizados);

        // Configuração para alimentos da API
        recyclerApi.setLayoutManager(new GridLayoutManager(this, 3));
        adaptadorAlimentosApi = new AdaptadorAlimento(new ArrayList<>(), this, this);
        recyclerApi.setAdapter(adaptadorAlimentosApi);
    }

    private void mostarCustomDialogo(Alimento alimento) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Alimento Personalizado")
                .setMessage("Tem certeza que deseja excluir o alimento '" + alimento.getNome() + "'?\nEsta ação não pode ser desfeita.")
                .setPositiveButton("Excluir", (dialog, which) -> deletarAlimentoPersonalizado(alimento))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deletarAlimentoPersonalizado(Alimento alimento) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                daoAlimento.deletar(alimento);
                Log.d(TAG, "Alimento personalizado excluído: " + alimento.getNome() + " (ID: " + alimento.getId() + ")");
                runOnUiThread(() -> {
                    Toast.makeText(AtividadeBuscaAlimento.this, "'" + alimento.getNome() + "' excluído com sucesso.", Toast.LENGTH_SHORT).show();
                    String termoBuscaAtual = etBuscar.getText().toString().trim();
                    // Re-executa a busca para atualizar a UI após a exclusão
                    fetchAndDisplayFoods(termoBuscaAtual.isEmpty() ? null : termoBuscaAtual);
                });
            } catch (Exception e) {
                Log.e(TAG, "Erro ao excluir alimento personalizado: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(AtividadeBuscaAlimento.this, "Erro ao excluir alimento. Consulte o log.", Toast.LENGTH_LONG).show());
            }
        });
    }
}
