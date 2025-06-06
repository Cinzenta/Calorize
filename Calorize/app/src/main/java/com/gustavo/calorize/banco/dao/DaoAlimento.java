package com.gustavo.calorize.banco.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.gustavo.calorize.modelo.Alimento; // Ausando Alimento

import java.util.List;

@Dao
public interface DaoAlimento {

    @Insert
    long insert(Alimento alimento); // retornar long ID inserido

    @Update
    void update(Alimento alimento);

    @Delete
    void deletar(Alimento alimento); // metodo deletar

    // Consulta para obter TODOS os alimentos que podem ser exibidos na tela de busca
    // (alimentos comuns e alimentos personalizados do usuário logado)
    @Query("SELECT * FROM alimento WHERE idUsuario  = 0 OR idUsuario  = :idUsuario ORDER BY nome ASC")
    List<Alimento> obterTodosAlimentosComunsEPersonalizados(long idUsuario);

    // A query para buscar por nome
    @Query("SELECT * FROM alimento WHERE nome LIKE '%' || :termoBusca || '%' AND (idUsuario  = 0 OR idUsuario  = :idUsuario) ORDER BY nome ASC")
    List<Alimento> buscarAlimentosPorNome(String termoBusca, long idUsuario);
}