package com.gustavo.calorize.banco.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.gustavo.calorize.modelo.Usuario; // Importa sua classe Usuario

import java.util.List;

@Dao //  esta interface é um Data Access Object
public interface DaoUsuario {

    @Insert // Anotação para inserir um novo objeto
    long inserir(Usuario usuario);

    @Update // atualização de um objeto existente
    int atualizar(Usuario usuario);

    @Query("SELECT * FROM usuario WHERE id = :idUsuario") // Consulta SQL para buscar por ID, usa 'idUsuario'
    Usuario obterUsuarioPorId(long idUsuario);


    @Query("SELECT * FROM usuario ORDER BY nome ASC") // Consulta para obter todos os usuários
    List<Usuario> obterTodosUsuarios();

    @Delete
    int deletar(Usuario usuario);

    @Query("DELETE FROM usuario WHERE id = :idUsuario") // Consulta para deletar por ID, usa 'idUsuario'
    int deletarUsuarioPorId(long idUsuario);

}