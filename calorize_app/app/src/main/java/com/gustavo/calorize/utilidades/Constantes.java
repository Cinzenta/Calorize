package com.gustavo.calorize.utilidades;

public class Constantes {

    // Chave para passar o objeto Usuario entre Activities
    public static final String EXTRA_USUARIO_OBJETO = "extra_usuario_objeto";

    // Constantes de código de requisição para startActivityForResult
    // ADICIONE ESTA LINHA:
    public static final int CODIGO_REQUISICAO_DEFINIR_METAS = 100;
    public static final int CODIGO_REQUISICAO_ADICIONAR_ALIMENTO = 103;

    // Chaves para Intent relacionadas a alimentos e refeições
    public static final String EXTRA_ALIMENTO_SELECIONADO_OBJETO = "extra_alimento_selecionado_objeto";
    public static final String EXTRA_REFEICAO_TIPO = "extra_refeicao_tipo";
    public static final String RESULTADO_ALIMENTO_CONSUMIDO_OBJETO = "resultado_alimento_consumido_objeto";

    // Chave para passar o ID do usuário
    public static final String EXTRA_ID_USUARIO = "extra_id_usuario";

    // Tipos de refeição
    public static final String REFEICAO_CAFE_DA_MANHA = "Café da Manhã";
    public static final String REFEICAO_ALMOCO = "Almoço";
    public static final String REFEICAO_JANTAR = "Jantar";
    public static final String REFEICAO_LANCHE = "Lanche";
}