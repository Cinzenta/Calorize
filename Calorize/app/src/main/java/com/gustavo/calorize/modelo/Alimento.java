package com.gustavo.calorize.modelo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "alimento",
        foreignKeys = @ForeignKey(entity = Usuario.class,
                parentColumns = "id",
                childColumns = "idUsuario",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = {"idUsuario"})})
public class Alimento implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "porcao")
    private String porcao;

    @ColumnInfo(name = "calorias")
    private double calorias;

    @ColumnInfo(name = "proteinas")
    private double proteinas;

    @ColumnInfo(name = "gorduras")
    private double gorduras;

    @ColumnInfo(name = "carboidratos")
    private double carboidratos;

    @ColumnInfo(name = "ehPersonalizado")
    private boolean ehPersonalizado;

    @ColumnInfo(name = "idUsuario")
    private Long idUsuario;

    // Construtor COMPLETO para Room
    public Alimento(long id, String nome, String porcao, double calorias, double proteinas,
                    double gorduras, double carboidratos, boolean ehPersonalizado, Long idUsuario) {
        this.id = id;
        this.nome = nome;
        this.porcao = porcao;
        this.calorias = calorias;
        this.proteinas = proteinas;
        this.gorduras = gorduras;
        this.carboidratos = carboidratos;
        this.ehPersonalizado = ehPersonalizado;
        this.idUsuario = idUsuario;
    }

    // Construtor para alimentos criados pelo usuário
    @Ignore
    public Alimento(String nome, String porcao, double calorias, double proteinas,
                    double gorduras, double carboidratos, Long idUsuario) {
        this(0L, nome, porcao, calorias, proteinas, gorduras, carboidratos, true, idUsuario);
    }

    // Construtor para alimentos da API
    @Ignore
    public Alimento(String nome, String porcao, double calorias, double proteinas,
                    double gorduras, double carboidratos) {
        this(0L, nome, porcao, calorias, proteinas, gorduras, carboidratos, false, null);
    }

    // --- Getters ---
    public long getId() { return id; }
    public String getNome() { return nome; }
    public String getPorcao() { return porcao; }
    public double getCalorias() { return calorias; }
    public double getProteinas() { return proteinas; }
    public double getGorduras() { return gorduras; }
    public double getCarboidratos() { return carboidratos; }
    public boolean isEhPersonalizado() { return ehPersonalizado; }
    public Long getIdUsuario() { return idUsuario; }

    @Override
    public String toString() {
        return "Alimento{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", porcao='" + porcao + '\'' +
                ", calorias=" + calorias +
                ", proteinas=" + proteinas +
                ", gorduras=" + gorduras +
                ", carboidratos=" + carboidratos +
                ", ehPersonalizado=" + ehPersonalizado +
                ", idUsuario=" + idUsuario +
                '}';
    }
}