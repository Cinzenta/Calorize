package com.gustavo.calorize.api.cliente;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClienteRetrofit {

    // Esta é a BASE_URL
    // Se você estiver usando um emulador Android, "10.0.2.2" é o IP especial para o seu computador local.
    private static final String URL_BASE = "http://10.0.2.2:8080/";

    private static Retrofit retrofit = null;

    public static Retrofit obterCliente() {
        if (retrofit == null) {
            // Interceptor para logar requisições e respostas HTTP (
            HttpLoggingInterceptor logarHttp = new HttpLoggingInterceptor();
            logarHttp.setLevel(HttpLoggingInterceptor.Level.BODY); // Loga  requisição e resposta

            // Cria um OkHttpClient com o interceptor de log
            OkHttpClient.Builder construtorHttpClient = new OkHttpClient.Builder();
            construtorHttpClient.addInterceptor(logarHttp);

            // Constrói a instância do Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create()) // Conversor JSON
                    .client(construtorHttpClient.build()) // Adiciona o cliente HTTP customizado
                    .build();
        }
        return retrofit;
    }
}