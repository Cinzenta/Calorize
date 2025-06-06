package com.gustavo.calorize.banco.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.gustavo.calorize.modelo.AlimentoConsumido;

import java.util.List;

@Dao
public interface DaoAlimentoConsumido {

    // Retorna o ID da linha inserida.
    @Insert
    long inserir(AlimentoConsumido alimentoConsumido);

    // Consulta para obter alimentos consumidos por usuário
    // Ordena por tempo
    @Query("SELECT * FROM alimentos_consumidos WHERE usuarioId = :idUsuario AND timestamp BETWEEN :inicioDoDiaMillis AND :fimDoDiaMillis ORDER BY timestamp ASC")
    List<AlimentoConsumido> obterAlimentosConsumidosDoDia(long idUsuario, long inicioDoDiaMillis, long fimDoDiaMillis);

    // Deleta um alimento consumido específico usando o objeto
    @Delete
    int deletar(AlimentoConsumido alimentoConsumido);
}