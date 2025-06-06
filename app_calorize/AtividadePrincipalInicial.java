package com.gustavo.calorize;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gustavo.calorize.api.cliente.ClienteRetrofit; // Importe o cliente Retrofit atualizado
import com.gustavo.calorize.api.servico.ServicoApi; // Importe o serviço de API atualizado
import com.gustavo.calorize.banco.BancoDeDadosPrincipal; // Importe o banco de dados Room
import com.gustavo.calorize.banco.dao.DaoUsuario; // Importe o DAO
import com.gustavo.calorize.modelo.Usuario;
import com.gustavo.calorize.utilidades.Constantes; // Importe as constantes

import java.util.List;
import java.util.concurrent.Executors; // Para operações de banco de dados

public class AtividadePrincipalInicial extends AppCompatActivity {

    private LinearLayout layoutListaUsuarios;
    private Button btnAdicionarNovoUsuario;
    private DaoUsuario daoUsuario;
    private ServicoApi servicoApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_principal_inicial);

        layoutListaUsuarios = findViewById(R.id.layout_users);
        btnAdicionarNovoUsuario = findViewById(R.id.btn_add_new_user);

        // Inicializa o DAO do Room
        daoUsuario = BancoDeDadosPrincipal.obterBancoDeDados(this).daoUsuario();
        // Inicializa o serviço da API
        servicoApi = ClienteRetrofit.obterCliente().create(ServicoApi.class);

        // Carrega e exibe os usuários existentes ao iniciar
        carregarEExibirUsuarios();

        btnAdicionarNovoUsuario.setOnClickListener(v -> exibirDialogoCriarUsuario());
    }

    // Método para carregar usuários do DB e exibir
    private void carregarEExibirUsuarios() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Usuario> usuarios = daoUsuario.obterTodosUsuarios();
            runOnUiThread(() -> { // Atualiza a  principal
                layoutListaUsuarios.removeAllViews(); // Limpa visualizações antigas
                if (usuarios.isEmpty()) {
                    TextView textoNenhumUsuario = new TextView(AtividadePrincipalInicial.this);
                    textoNenhumUsuario.setText("Nenhum usuário cadastrado. Crie um novo!");
                    textoNenhumUsuario.setTextSize(18);
                    textoNenhumUsuario.setPadding(16, 16, 16, 16);
                    layoutListaUsuarios.addView(textoNenhumUsuario);
                } else {
                    for (Usuario usuario : usuarios) {
                        adicionarUsuarioAView(usuario);
                    }
                }
            });
        });
    }

    // Adiciona um card para cada usuário na lista
    private void adicionarUsuarioAView(Usuario usuario) {

        LinearLayout itemUsuario = new LinearLayout(this);
        itemUsuario.setOrientation(LinearLayout.VERTICAL);
        itemUsuario.setPadding(16, 16, 16, 16);
        itemUsuario.setBackgroundResource(R.drawable.fundo_item_usuario);

        TextView nomeUsuario = new TextView(this);
        nomeUsuario.setText(usuario.getNome());
        nomeUsuario.setTextSize(20);
        nomeUsuario.setTextColor(getResources().getColor(android.R.color.black));

        TextView metaUsuario = new TextView(this);
        metaUsuario.setText("Meta: " + usuario.getMetaDeCalorias() + " kcal");
        metaUsuario.setTextSize(16);
        metaUsuario.setTextColor(getResources().getColor(android.R.color.darker_gray));

        itemUsuario.addView(nomeUsuario);
        itemUsuario.addView(metaUsuario);

        itemUsuario.setOnClickListener(v -> navegarParaAtividadePrincipal(usuario));
        itemUsuario.setOnLongClickListener(v -> { // Pressionar e segurar para deletar
            exibirDialogoExcluirUsuario(usuario);
            return true;
        });

        layoutListaUsuarios.addView(itemUsuario);
        View separador = new View(this);
        separador.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) getResources().getDisplayMetrics().density * 1 // 1dp separacao
        ));
        separador.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        layoutListaUsuarios.addView(separador);
    }

    // Exibe uma caixa para criar um novo usuário
    private void exibirDialogoCriarUsuario() {
        AlertDialog.Builder construtor = new AlertDialog.Builder(this);
        construtor.setTitle("Criar Novo Usuário");

        View viewDialogo = LayoutInflater.from(this).inflate(R.layout.dialogo_criar_usuario, null);
        EditText etNomeNovoUsuario = viewDialogo.findViewById(R.id.et_new_user_name);
        EditText etMetaCaloriasNovoUsuario = viewDialogo.findViewById(R.id.et_new_user_calorie_goal);

        construtor.setView(viewDialogo);

        construtor.setPositiveButton("Salvar", (dialogo, which) -> {
            String nome = etNomeNovoUsuario.getText().toString().trim();
            String caloriasStr = etMetaCaloriasNovoUsuario.getText().toString().trim();

            if (nome.isEmpty() || caloriasStr.isEmpty()) {
                Toast.makeText(AtividadePrincipalInicial.this, "Nome e meta calórica são obrigatórios.", Toast.LENGTH_SHORT).show();
                return;
            }

            int metaCalorias;
            try {
                metaCalorias = Integer.parseInt(caloriasStr);
                if (metaCalorias <= 0) {
                    Toast.makeText(AtividadePrincipalInicial.this, "A meta de calorias deve ser um número positivo.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(AtividadePrincipalInicial.this, "Meta calórica inválida.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cria o usuário no banco de dados local (Room
            Executors.newSingleThreadExecutor().execute(() -> {
                Usuario novoUsuario = new Usuario(nome, metaCalorias); // Usa o construtor do modelo 'Usuario'
                long idGerado = daoUsuario.inserir(novoUsuario); // Insere e obtém o ID gerado (long)

                // Atualiza o objeto novoUsuario com o ID real do banco de dados
                novoUsuario.setId(idGerado); // Define o ID retornado pelo Room

                runOnUiThread(() -> {
                    Toast.makeText(AtividadePrincipalInicial.this, "Usuário '" + nome + "' criado!", Toast.LENGTH_SHORT).show();
                    carregarEExibirUsuarios(); // Recarrega a lista para mostrar o novo usuário
                    navegarParaAtividadeAjustarMacros(novoUsuario); // Vai para definir macros do novo usuário
                });
            });
        });

        construtor.setNegativeButton("Cancelar", (dialogo, which) -> dialogo.dismiss());
        construtor.create().show();
    }

    // Exibe uma caixa para confirmar a exclusão de um usuário
    private void exibirDialogoExcluirUsuario(Usuario usuario) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Usuário")
                .setMessage("Tem certeza que deseja excluir o usuário '" + usuario.getNome() + "'?")
                .setPositiveButton("Excluir", (dialogo, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        daoUsuario.deletarUsuarioPorId(usuario.getId()); // Usa o método e DAO
                        runOnUiThread(() -> {
                            Toast.makeText(AtividadePrincipalInicial.this, "Usuário '" + usuario.getNome() + "' excluído.", Toast.LENGTH_SHORT).show();
                            carregarEExibirUsuarios(); // Recarrega a lista
                        });
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Navega para AtividadeAjustarMacros, passando o objeto Usuario
    private void navegarParaAtividadeAjustarMacros(Usuario usuario) {
        Intent intent = new Intent(AtividadePrincipalInicial.this, AtividadeAjustarMacros.class);
        intent.putExtra(Constantes.EXTRA_USUARIO_OBJETO, usuario);
        startActivityForResult(intent, Constantes.CODIGO_REQUISICAO_DEFINIR_METAS);
    }

    // Navega para AtividadePrincipal, passando o objeto Usuario
    private void navegarParaAtividadePrincipal(Usuario usuario) {
        Intent intent = new Intent(AtividadePrincipalInicial.this, AtividadePrincipal.class); // **ATENÇÃO: Nome da sua próxima tela principal!**
        intent.putExtra(Constantes.EXTRA_USUARIO_OBJETO, usuario);
        startActivity(intent);
    }

    // resultado retornado de AtividadeAjustarMacros quando as macros são salvas
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constantes.CODIGO_REQUISICAO_DEFINIR_METAS && resultCode == RESULT_OK && data != null) {
            Usuario usuarioAtualizado = (Usuario) data.getSerializableExtra(Constantes.EXTRA_USUARIO_OBJETO);
            if (usuarioAtualizado != null) {
                // As macros foram definidas/atualizadas. Atualize o usuário no DB local.
                Executors.newSingleThreadExecutor().execute(() -> {
                    daoUsuario.atualizar(usuarioAtualizado);
                    runOnUiThread(() -> {
                        Toast.makeText(AtividadePrincipalInicial.this, "Metas definidas e salvas para " + usuarioAtualizado.getNome() + "!", Toast.LENGTH_SHORT).show();
                        carregarEExibirUsuarios(); // Recarrega para mostrar qualquer atualização de nome e meta
                        navegarParaAtividadePrincipal(usuarioAtualizado); // Vai para a tela principal
                    });
                });
            } else {
                Toast.makeText(AtividadePrincipalInicial.this, "Erro ao obter usuário atualizado de macros.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}