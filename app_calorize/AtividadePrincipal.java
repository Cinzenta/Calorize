package com.gustavo.calorize;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import android.graphics.drawable.ClipDrawable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog; // Import necessário para AlertDialog
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

import com.gustavo.calorize.modelo.Usuario;
import com.gustavo.calorize.modelo.AlimentoConsumido;
import com.gustavo.calorize.utilidades.Constantes;
import com.gustavo.calorize.banco.BancoDeDadosPrincipal;
import com.gustavo.calorize.banco.dao.DaoUsuario;
import com.gustavo.calorize.banco.dao.DaoAlimentoConsumido;

import java.util.ArrayList; // Import para ArrayList
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AtividadePrincipal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tvSaudacaoUsuario;
    private TextView tvCaloriasMacas;
    private TextView tvSumarioProteina;
    private TextView tvSumarioCarboidrato;
    private TextView tvSumarioGordura;

    private ImageView ivMacaCheia;
    private ImageView ivMacaContorno;
    private ClipDrawable macaCheiaClipDrawable;

    private TextView tvAlimentosCafe;
    private TextView tvAlimentosAlmoco;
    private TextView tvAlimentosJantar;
    private TextView tvAlimentosLanche;

    private TextView tvQuantidadeCafe;
    private TextView tvQuantidadeAlmoco;
    private TextView tvQuantidadeJantar;
    private TextView tvQuantidadeLanche;

    // Listas para armazenar os AlimentosConsumidos por refeição
    private List<AlimentoConsumido> alimentosCafe = new ArrayList<>();
    private List<AlimentoConsumido> alimentosAlmoco = new ArrayList<>();
    private List<AlimentoConsumido> alimentosJantar = new ArrayList<>();
    private List<AlimentoConsumido> alimentosLanche = new ArrayList<>();

    private Usuario usuarioLogado;

    private DrawerLayout layoutGaveta;
    private NavigationView visaoNavegacao;
    private ImageButton btnMenuGaveta;
    private ImageButton btnVoltar;

    private DaoUsuario daoUsuario;
    private DaoAlimentoConsumido daoAlimentoConsumido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_principal);

        // Inicializa DAOs
        daoUsuario = BancoDeDadosPrincipal.obterBancoDeDados(this).daoUsuario();
        daoAlimentoConsumido = BancoDeDadosPrincipal.obterBancoDeDados(this).daoAlimentoConsumido();

        // Inicializa Views
        tvSaudacaoUsuario = findViewById(R.id.text_ola_usuario);
        tvCaloriasMacas = findViewById(R.id.text_calorias_maca);
        tvSumarioProteina = findViewById(R.id.text_proteina_sumario);
        tvSumarioCarboidrato = findViewById(R.id.text_carboidrato_sumario);
        tvSumarioGordura = findViewById(R.id.text_gordura_sumario);
        ivMacaCheia = findViewById(R.id.image_maca_cheia);
        ivMacaContorno = findViewById(R.id.image_maca_contorno);


        tvAlimentosCafe = findViewById(R.id.text_alimento_cafe);
        tvAlimentosAlmoco = findViewById(R.id.text_alimento_almoco);
        tvAlimentosJantar = findViewById(R.id.text_alimento_jantar);
        tvAlimentosLanche = findViewById(R.id.text_alimento_lanche);

        tvQuantidadeCafe = findViewById(R.id.text_quantidade_cafe);
        tvQuantidadeAlmoco = findViewById(R.id.text_quantidade_almoco);
        tvQuantidadeJantar = findViewById(R.id.text_quantidade_jantar);
        tvQuantidadeLanche = findViewById(R.id.text_quantidade_lanche);

        // Long click para remover alimentos
        tvAlimentosCafe.setOnLongClickListener(v -> {
            showRemoveFoodDialog(alimentosCafe, Constantes.REFEICAO_CAFE_DA_MANHA);
            return true;
        });
        tvAlimentosAlmoco.setOnLongClickListener(v -> {
            showRemoveFoodDialog(alimentosAlmoco, Constantes.REFEICAO_ALMOCO);
            return true;
        });
        tvAlimentosJantar.setOnLongClickListener(v -> {
            showRemoveFoodDialog(alimentosJantar, Constantes.REFEICAO_JANTAR);
            return true;
        });
        tvAlimentosLanche.setOnLongClickListener(v -> {
            showRemoveFoodDialog(alimentosLanche, Constantes.REFEICAO_LANCHE);
            return true;
        });


        // Recupera o usuário logado passado pela Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constantes.EXTRA_USUARIO_OBJETO)) {
            usuarioLogado = (Usuario) intent.getSerializableExtra(Constantes.EXTRA_USUARIO_OBJETO);
            if (usuarioLogado == null) {
                handleUserNotFound(); // Trata caso de usuário nulo
                return;
            }
            Log.d("AtividadePrincipal", "Usuário logado ID: " + usuarioLogado.getId() + ", Nome: " + usuarioLogado.getNome());
        } else {
            handleUserNotFound(); // Trata caso de Intent sem usuário
            return;
        }

        // Configura o botão de voltar
        btnVoltar = findViewById(R.id.backArrow);
        btnVoltar.setOnClickListener(v -> onBackPressed());

        // Configura o menu lateral (Drawer Navigation)
        layoutGaveta = findViewById(R.id.drawer_layout);
        visaoNavegacao = findViewById(R.id.nav_view);
        btnMenuGaveta = findViewById(R.id.btn_menu_drawer);

        btnMenuGaveta.setOnClickListener(v -> layoutGaveta.openDrawer(GravityCompat.START));
        visaoNavegacao.setNavigationItemSelectedListener(this); // Associa o listener

        // Atualiza o nome de usuário no cabeçalho do menu lateral
        View headerView = visaoNavegacao.getHeaderView(0);
        TextView tvCabecalhoUsuario = headerView.findViewById(R.id.nav_header_username);
        if (usuarioLogado != null) {
            tvCabecalhoUsuario.setText(usuarioLogado.getNome());
        }
        if (ivMacaCheia.getDrawable() instanceof ClipDrawable) {
            macaCheiaClipDrawable = (ClipDrawable) ivMacaCheia.getDrawable();
        } else {
            Log.e("AtividadePrincipal", "Drawable da maçã cheia não é um ClipDrawable. Verifique maca_cheia_progresso.xml.");
        }

        // Configura os botões de adicionar alimento para cada refeição
        configurarBotaoAdicionarAlimento(R.id.card_cafe_da_manha, R.id.btn_add_cafe, Constantes.REFEICAO_CAFE_DA_MANHA);
        configurarBotaoAdicionarAlimento(R.id.card_almoco, R.id.btn_add_almoco, Constantes.REFEICAO_ALMOCO);
        configurarBotaoAdicionarAlimento(R.id.card_jantar, R.id.btn_add_jantar, Constantes.REFEICAO_JANTAR);
        configurarBotaoAdicionarAlimento(R.id.card_lanche, R.id.btn_add_lanche, Constantes.REFEICAO_LANCHE);

        // A primeira carga de dados será feita no onResume()
    }

    private void handleUserNotFound() {
        Log.e("AtividadePrincipal", "Usuário não encontrado ou não passado na Intent. Redirecionando para AtividadePrincipalInicial.");
        Toast.makeText(this, "Erro: Usuário não logado. Redirecionando.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, AtividadePrincipalInicial.class);
        startActivity(intent);
        finish();
    }


    private void configurarBotaoAdicionarAlimento(int cardId, int buttonId, String tipoRefeicao) {
        View cardView = findViewById(cardId);
        ImageButton botaoAdicionar = cardView.findViewById(buttonId);
        botaoAdicionar.setOnClickListener(v -> {
            if (usuarioLogado == null || usuarioLogado.getId() == 0L) {
                Log.e("AtividadePrincipal", "Erro: usuarioLogado é NULO ou ID inválido ao tentar adicionar alimento.");
                Toast.makeText(AtividadePrincipal.this, "Erro: Usuário inválido. Por favor, reinicie.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(AtividadePrincipal.this, AtividadeBuscaAlimento.class);
            intent.putExtra(Constantes.EXTRA_REFEICAO_TIPO, tipoRefeicao);
            intent.putExtra(Constantes.EXTRA_ID_USUARIO, usuarioLogado.getId());
            Log.d("AtividadePrincipal", "Iniciando AtividadeBuscaAlimento para '" + tipoRefeicao + "' com ID do usuário: " + usuarioLogado.getId());

            startActivityForResult(intent, Constantes.CODIGO_REQUISICAO_ADICIONAR_ALIMENTO);
        });
    }

    private void carregarDadosEAtualizarUI() {
        if (usuarioLogado == null || usuarioLogado.getId() == 0L) {
            Log.e("AtividadePrincipal", "Usuário não logado ou ID inválido para carregar dados. Ignorando.");
            return;
        }

        tvSaudacaoUsuario.setText("Olá " + usuarioLogado.getNome());

        // Limpa os campos antes de carregar novos dados
        tvAlimentosCafe.setText("");
        tvQuantidadeCafe.setText("0 kcal");
        tvAlimentosAlmoco.setText("");
        tvQuantidadeAlmoco.setText("0 kcal");
        tvAlimentosJantar.setText("");
        tvQuantidadeJantar.setText("0 kcal");
        tvAlimentosLanche.setText("");
        tvQuantidadeLanche.setText("0 kcal");

        // Limpa as listas de alimentos antes de preenchê-las novamente
        alimentosCafe.clear();
        alimentosAlmoco.clear();
        alimentosJantar.clear();
        alimentosLanche.clear();

        long inicioDoDia = getStartOfDayMillis();
        long fimDoDia = getEndOfDayMillis();

        Executors.newSingleThreadExecutor().execute(() -> {
            // Recarregar o usuário do DB para ter os dados mais atualizados
            Usuario usuarioAtualizadoDoDB = daoUsuario.obterUsuarioPorId(usuarioLogado.getId());
            if (usuarioAtualizadoDoDB != null) {
                usuarioLogado = usuarioAtualizadoDoDB; // Atualiza a instância local do usuário
                Log.d("AtividadePrincipal", "Usuário recarregado do DB. Meta Calorias: " + usuarioLogado.getMetaDeCalorias());
            } else {
                Log.e("AtividadePrincipal", "Erro: Usuário não encontrado no DB ao recarregar dados. ID: " + usuarioLogado.getId());
                runOnUiThread(() -> Toast.makeText(AtividadePrincipal.this, "Erro ao carregar usuário. Redirecionando.", Toast.LENGTH_LONG).show());
                return;
            }

            List<AlimentoConsumido> alimentosConsumidosDoDia = daoAlimentoConsumido.obterAlimentosConsumidosDoDia(usuarioLogado.getId(), inicioDoDia, fimDoDia);
            Log.d("AtividadePrincipal", "Alimentos consumidos hoje: " + alimentosConsumidosDoDia.size());

            double totalCaloriasConsumidas = 0;
            double totalProteinasConsumidas = 0;
            double totalCarboidratosConsumidas = 0;
            double totalGordurasConsumidas = 0;

            final StringBuilder cafeTextBuilder = new StringBuilder();
            final StringBuilder almocoTextBuilder = new StringBuilder();
            final StringBuilder jantarTextBuilder = new StringBuilder();
            final StringBuilder lancheTextBuilder = new StringBuilder();

            final double[] cafeCalTotal = {0};
            final double[] almocoCalTotal = {0};
            final double[] jantarCalTotal = {0};
            final double[] lancheCalTotal = {0};

            for (AlimentoConsumido ac : alimentosConsumidosDoDia) {
                totalCaloriasConsumidas += ac.getCaloriasConsumidas();
                totalProteinasConsumidas += ac.getProteinasConsumidas();
                totalCarboidratosConsumidas += ac.getCarboidratosConsumidas();
                totalGordurasConsumidas += ac.getGordurasConsumidas();

                String entrada = String.format(Locale.getDefault(), "%s (%.0f kcal)", ac.getNomeAlimento(), ac.getCaloriasConsumidas());


                switch (ac.getTipoRefeicao()) {
                    case Constantes.REFEICAO_CAFE_DA_MANHA:
                        if (cafeTextBuilder.length() > 0) cafeTextBuilder.append("\n"); // Adiciona quebra de linha se já houver texto
                        cafeTextBuilder.append(entrada);
                        cafeCalTotal[0] += ac.getCaloriasConsumidas();
                        alimentosCafe.add(ac); // Adiciona à lista de café
                        break;
                    case Constantes.REFEICAO_ALMOCO:
                        if (almocoTextBuilder.length() > 0) almocoTextBuilder.append("\n");
                        almocoTextBuilder.append(entrada);
                        almocoCalTotal[0] += ac.getCaloriasConsumidas();
                        alimentosAlmoco.add(ac); // Adiciona à lista de almoço
                        break;
                    case Constantes.REFEICAO_JANTAR:
                        if (jantarTextBuilder.length() > 0) jantarTextBuilder.append("\n");
                        jantarTextBuilder.append(entrada);
                        jantarCalTotal[0] += ac.getCaloriasConsumidas();
                        alimentosJantar.add(ac); // Adiciona à lista de jantar
                        break;
                    case Constantes.REFEICAO_LANCHE:
                        if (lancheTextBuilder.length() > 0) lancheTextBuilder.append("\n");
                        lancheTextBuilder.append(entrada);
                        lancheCalTotal[0] += ac.getCaloriasConsumidas();
                        alimentosLanche.add(ac); // Adiciona à lista de lanche
                        break;
                }
            }

            // Variáveis finais
            final double finalTotalCaloriasConsumidas = totalCaloriasConsumidas;
            final double finalTotalProteinasConsumidas = totalProteinasConsumidas;
            final double finalTotalCarboidratosConsumidas = totalCarboidratosConsumidas;
            final double finalTotalGordurasConsumidas = totalGordurasConsumidas;

            runOnUiThread(() -> {
                // Atualiza os TextViews com os alimentos de cada refeição
                tvAlimentosCafe.setText(cafeTextBuilder.toString());
                tvQuantidadeCafe.setText(String.format(Locale.getDefault(), "%.0f kcal", cafeCalTotal[0]));
                tvAlimentosAlmoco.setText(almocoTextBuilder.toString());
                tvQuantidadeAlmoco.setText(String.format(Locale.getDefault(), "%.0f kcal", almocoCalTotal[0]));
                tvAlimentosJantar.setText(jantarTextBuilder.toString());
                tvQuantidadeJantar.setText(String.format(Locale.getDefault(), "%.0f kcal", jantarCalTotal[0]));
                tvAlimentosLanche.setText(lancheTextBuilder.toString());
                tvQuantidadeLanche.setText(String.format(Locale.getDefault(), "%.0f kcal", lancheCalTotal[0]));

                // Atualiza os resumos totais de calorias/macros
                int metaCalorias = usuarioLogado.getMetaDeCalorias();
                double metaProteinas = usuarioLogado.getMetaProteinas();
                double metaCarboidratos = usuarioLogado.getMetaCarboidratos();
                double metaGorduras = usuarioLogado.getMetaGorduras();

                tvCaloriasMacas.setText(String.format(Locale.getDefault(), "%.0f/%d", finalTotalCaloriasConsumidas, metaCalorias));
                atualizarPreenchimentoMaca((int) finalTotalCaloriasConsumidas, metaCalorias);

                tvSumarioProteina.setText(String.format(Locale.getDefault(), "P: %.1f/%.0f g", finalTotalProteinasConsumidas, metaProteinas));
                tvSumarioCarboidrato.setText(String.format(Locale.getDefault(), "C: %.1f/%.0f g", finalTotalCarboidratosConsumidas, metaCarboidratos));
                tvSumarioGordura.setText(String.format(Locale.getDefault(), "G: %.1f/%.0f g", finalTotalGordurasConsumidas, metaGorduras));

                Log.d("AtividadePrincipal", "UI Atualizada. Calorias: " + finalTotalCaloriasConsumidas + ", Proteinas: " + finalTotalProteinasConsumidas);
            });
        });
    }

    private void atualizarPreenchimentoMaca(int caloriasConsumidas, int metaCalorias) {
        if (macaCheiaClipDrawable == null) {
            Log.w("AtividadePrincipal", "macaClipDrawable é nulo. Não foi possível atualizar o preenchimento da maçã.");
            return;
        }

        double progresso = 0.0;
        if (metaCalorias > 0) {
            progresso = (double) caloriasConsumidas / metaCalorias;
        }

        // Garante que o progresso esteja entre 0.0 e 1.0 (0% a 100%)
        progresso = Math.max(0.0, Math.min(progresso, 1.0));

        // Converte o progresso (0.0 a 1.0) para o nível do ClipDrawable (0 a 10000)
        int nivel = (int) (progresso * 10000);

        macaCheiaClipDrawable.setLevel(nivel);
        Log.d("AtividadePrincipal", "Maca preenchida: Nível " + nivel + " (progresso: " + String.format("%.2f", progresso) + ")");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Garante que os dados são recarregados e a UI atualizada sempre que a Activity volta ao foco
        carregarDadosEAtualizarUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //  RETORNO BUSCAALIMENTO
        if (requestCode == Constantes.CODIGO_REQUISICAO_ADICIONAR_ALIMENTO) {
            if (resultCode == RESULT_OK && data != null) {
                AlimentoConsumido alimentoConsumido = (AlimentoConsumido) data.getSerializableExtra(Constantes.RESULTADO_ALIMENTO_CONSUMIDO_OBJETO);
                String tipoRefeicaoRecebido = data.getStringExtra(Constantes.EXTRA_REFEICAO_TIPO);

                if (alimentoConsumido != null && usuarioLogado != null && tipoRefeicaoRecebido != null) {
                    Log.d("AtividadePrincipal", "AlimentoConsumido recebido de BuscaAlimento: " + alimentoConsumido.toString());
                    Log.d("AtividadePrincipal", "Tipo Refeição recebido de BuscaAlimento: " + tipoRefeicaoRecebido);

                    // Garante que o tipo de refeição e o ID do usuário no objeto estão corretos
                    alimentoConsumido.setTipoRefeicao(tipoRefeicaoRecebido);
                    alimentoConsumido.setUsuarioId(usuarioLogado.getId());
                    alimentoConsumido.setTimestamp(System.currentTimeMillis()); // Define o yime atual

                    Executors.newSingleThreadExecutor().execute(() -> {
                        try {
                            long idAlimentoConsumido = daoAlimentoConsumido.inserir(alimentoConsumido);
                            Log.d("AtividadePrincipal", "AlimentoConsumido inserido no DB com ID: " + idAlimentoConsumido);

                            runOnUiThread(() -> {
                                Toast.makeText(AtividadePrincipal.this, "Adicionado: " + alimentoConsumido.getNomeAlimento() + " (" + (int) alimentoConsumido.getCaloriasConsumidas() + " kcal) ao " + alimentoConsumido.getTipoRefeicao(), Toast.LENGTH_SHORT).show();
                                // O onResume() já fará o trabalho de recarregar a UI automaticamente
                            });
                        } catch (Exception e) {
                            Log.e("AtividadePrincipal", "Erro durante a inserção do alimento consumido: " + e.getMessage(), e);
                            runOnUiThread(() -> Toast.makeText(AtividadePrincipal.this, "Erro ao salvar alimento. Consulte o log.", Toast.LENGTH_LONG).show());
                        }
                    });
                } else {
                    Log.e("AtividadePrincipal", "Dados de retorno incompletos de AtividadeBuscaAlimento.");
                    Toast.makeText(this, "Erro: Não foi possível adicionar o alimento. Dados incompletos.", Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("AtividadePrincipal", "Operação de adicionar alimento cancelada.");
                Toast.makeText(this, "Adição de alimento cancelada.", Toast.LENGTH_SHORT).show();
            }
        }
        //  RETORNO AJUSTARMACROS
        else if (requestCode == Constantes.CODIGO_REQUISICAO_DEFINIR_METAS) {
            if (resultCode == RESULT_OK) {
                Log.d("AtividadePrincipal", "Retorno de AtividadeAjustarMacros. onResume() irá recarregar os dados.");
                Toast.makeText(this, "Metas atualizadas com sucesso!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("AtividadePrincipal", "Ajuste de metas cancelado.");
                Toast.makeText(this, "Ajuste de metas cancelado.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_delete_user) {
            exibirDialogoConfirmacaoExclusao();
        } else if (id == R.id.nav_edit_macros) {
            navegarParaAtividadeAjustarMacros();
        }

        layoutGaveta.closeDrawer(GravityCompat.START);
        return true;
    }

    private void navegarParaAtividadeAjustarMacros() {
        if (usuarioLogado == null) {
            Toast.makeText(this, "Erro: Usuário não logado.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(AtividadePrincipal.this, AtividadeAjustarMacros.class);
        intent.putExtra(Constantes.EXTRA_USUARIO_OBJETO, usuarioLogado);
        startActivityForResult(intent, Constantes.CODIGO_REQUISICAO_DEFINIR_METAS);
    }

    private void exibirDialogoConfirmacaoExclusao() {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Usuário")
                .setMessage("Tem certeza que deseja excluir o usuário " + usuarioLogado.getNome() + "? Esta ação não pode ser desfeita.")
                .setPositiveButton("Excluir", (dialog, which) -> excluirUsuarioAtual())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void excluirUsuarioAtual() {
        if (usuarioLogado != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                daoUsuario.deletar(usuarioLogado);
                runOnUiThread(() -> {
                    Toast.makeText(AtividadePrincipal.this, "Usuário " + usuarioLogado.getNome() + " excluído com sucesso!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AtividadePrincipal.this, AtividadePrincipalInicial.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish(); // Fecha a AtividadePrincipal
                });
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (layoutGaveta.isDrawerOpen(GravityCompat.START)) {
            layoutGaveta.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Sair do Aplicativo?")
                    .setMessage("Você quer fechar o aplicativo ou voltar para a tela de seleção de usuário?")
                    .setPositiveButton("Fechar", (dialog, which) -> finishAffinity())
                    .setNegativeButton("Voltar para Início", (dialog, which) -> {
                        Intent intent = new Intent(AtividadePrincipal.this, AtividadePrincipalInicial.class);
                        startActivity(intent);
                        finish();
                    })
                    .setNeutralButton("Cancelar", (dialog, which) -> {
                        // O usuário não escolheu nenhuma das opções personalizadas.
                        // Permite que o comportamento padrão do botão "Voltar" aconteça.
                        super.onBackPressed();
                    })
                    .show();
        }
    }

    // MÉTODOS para remoção de alimentos consumidos
    private void showRemoveFoodDialog(List<AlimentoConsumido> alimentos, String tipoRefeicao) {
        if (alimentos.isEmpty()) {
            Toast.makeText(this, "Não há alimentos para remover nesta refeição.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criar um array de Strings para exibir no diálogo
        String[] nomesAlimentos = new String[alimentos.size()];
        for (int i = 0; i < alimentos.size(); i++) {
            AlimentoConsumido ac = alimentos.get(i);
            nomesAlimentos[i] = String.format(Locale.getDefault(), "%s (%.0f kcal)", ac.getNomeAlimento(), ac.getCaloriasConsumidas());
        }

        new AlertDialog.Builder(this)
                .setTitle("Remover alimento do " + tipoRefeicao + "?")
                .setItems(nomesAlimentos, (dialog, which) -> {
                    // 'which' é o índice do item selecionado
                    AlimentoConsumido alimentoParaRemover = alimentos.get(which);
                    confirmRemoval(alimentoParaRemover);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmRemoval(AlimentoConsumido alimento) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Remoção")
                .setMessage("Tem certeza que deseja remover '" + alimento.getNomeAlimento() + "'?")
                .setPositiveButton("Remover", (dialog, which) -> removerAlimentoConsumido(alimento))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void removerAlimentoConsumido(AlimentoConsumido alimento) {
        Executors.newSingleThreadExecutor().execute(() -> {
            daoAlimentoConsumido.deletar(alimento);
            Log.d("AtividadePrincipal", "Alimento consumido removido: " + alimento.getNomeAlimento());
            runOnUiThread(() -> {
                Toast.makeText(AtividadePrincipal.this, alimento.getNomeAlimento() + " removido.", Toast.LENGTH_SHORT).show();
                carregarDadosEAtualizarUI(); // Recarrega a UI
            });
        });
    }


    // Métodos auxiliares para obter o início e fim do dia em milissegundos
    private long getStartOfDayMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getEndOfDayMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }
}