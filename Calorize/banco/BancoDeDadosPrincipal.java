package com.gustavo.calorize.banco;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.gustavo.calorize.banco.dao.DaoAlimento;
import com.gustavo.calorize.banco.dao.DaoAlimentoConsumido; // Importe o novo DAO
import com.gustavo.calorize.banco.dao.DaoUsuario;
import com.gustavo.calorize.modelo.Alimento;
import com.gustavo.calorize.modelo.AlimentoConsumido; // Importe a nova entidade
import com.gustavo.calorize.modelo.Usuario;

@Database(entities = {Usuario.class, Alimento.class, AlimentoConsumido.class }, version = 2 , exportSchema = false)
public abstract class BancoDeDadosPrincipal extends RoomDatabase {

    private static final String NOME_BANCO_DE_DADOS = "calorize_db";

    public abstract DaoUsuario daoUsuario();
    public abstract DaoAlimento daoAlimento();
    public abstract DaoAlimentoConsumido daoAlimentoConsumido();

    private static volatile BancoDeDadosPrincipal INSTANCE;

    public static BancoDeDadosPrincipal obterBancoDeDados(final Context context) {
        if (INSTANCE == null) {
            synchronized (BancoDeDadosPrincipal.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    BancoDeDadosPrincipal.class, NOME_BANCO_DE_DADOS)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}