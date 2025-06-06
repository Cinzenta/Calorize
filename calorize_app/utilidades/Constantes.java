package com.gustavo.calorize.utilidades;

public class Constantes {

    // Chave para passar o objeto Usuario entre Activities
    public static final String EXTRA_USUARIO_OBJETO = "extra_usuario_objeto";


    public static final int CODIGO_REQUISICAO_DEFINIR_METAS = 100;
    public static final int CODIGO_REQUISICAO_CRIAR_ALIMENTO = 101;
    public static final int CODIGO_REQUISICAO_EDITAR_ALIMENTO = 102; // não usado ainda

    public static final int CODIGO_REQUISICAO_ADICIONAR_ALIMENTO = 103;
    public static final int CODIGO_REQUISICAO_DEFINIR_GRAMAS = 104;

    // Chaves para Intent relacionadas a alimentos e refeições
    public static final String EXTRA_ALIMENTO_SELECIONADO_OBJETO = "extra_alimento_selecionado_objeto";
    public static final String EXTRA_REFEICAO_TIPO = "extra_refeicao_tipo";
    public static final String RESULTADO_ALIMENTO_CONSUMIDO_OBJETO = "resultado_alimento_consumido_objeto";

    // Chave para passar o ID do usuário
    public static final String EXTRA_ID_USUARIO = "extra_id_usuario";

    // Chave para passar o objeto Alimento entre Activities
    public static final String EXTRA_ALIMENTO_OBJETO = "extra_alimento_objeto";

    public static final String RESULTADO_NOME_ALIMENTO = "resultado_nome_alimento";
    public static final String RESULTADO_CALORIAS_ALIMENTO = "resultado_calorias_alimento";
    public static final String RESULTADO_PROTEINA_ALIMENTO = "resultado_proteina_alimento";
    public static final String RESULTADO_CARBOIDRATOS_ALIMENTO = "resultado_carboidratos_alimento";
    public static final String RESULTADO_GORDURA_ALIMENTO = "resultado_gordura_alimento";


    // Tipos de refeição
    public static final String REFEICAO_CAFE_DA_MANHA = "Café da Manhã";
    public static final String REFEICAO_ALMOCO = "Almoço";
    public static final String REFEICAO_JANTAR = "Jantar";
    public static final String REFEICAO_LANCHE = "Lanche";


}