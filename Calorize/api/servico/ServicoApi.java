package com.gustavo.calorize.api.servico;

import com.gustavo.calorize.api.modelo.AlimentoApiModelo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServicoApi {

    // Endpoints de Alimentos
    // Busca alimentos por nome
    @GET("api/v1/foods/search")
    Call<List<AlimentoApiModelo>> buscarAlimentos(@Query("name") String nomeAlimento);

    // Obtém alimentos frequentemente consumidos
    @GET("api/v1/foods/frequent")
    Call<List<AlimentoApiModelo>> obterAlimentosFrequentes();

    // Obtém todos os alimentos
    @GET("api/v1/foods")
    Call<List<AlimentoApiModelo>> obterTodosAlimentos();

    // Cria um novo alimento
    @POST("api/v1/foods")
    Call<AlimentoApiModelo> criarAlimento(@Body AlimentoApiModelo novoAlimento);

    // Obtém um alimento por ID
    @GET("api/v1/foods/{id}")
    Call<AlimentoApiModelo> obterAlimentoPorId(@Path("id") int idAlimento);

    // Atualiza um alimento existente
    @PUT("api/v1/foods/{id}")
    Call<AlimentoApiModelo> atualizarAlimento(@Path("id") int idAlimento, @Body AlimentoApiModelo atualizacoesAlimento);

}