package com.gustavo.calorize.api.servico;

import com.gustavo.calorize.api.modelo.AlimentoApiModelo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServicoApi {

    // Endpoint para obter todos os alimentos.
    @GET("alimentos")
    Call<List<AlimentoApiModelo>> obterTodosAlimentos();

    // Endpoint para buscar alimentos por nome.
    @GET("alimentos/search")
    Call<List<AlimentoApiModelo>> buscarAlimentos(@Query("name") String nome);
}