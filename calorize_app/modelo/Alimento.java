package com.gustavo.calorize.modelo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.io.Serializable;

// Define esta classe como uma entidade do Room, mapeada para a tabela "alimento"
// Adicionada a foreign key para a classe Usuario E um índice para idUsuario
@Entity(tableName = "alimento",
        foreignKeys = @ForeignKey(entity = Usuario.class,
                parentColumns = "id", // Coluna na tabela "usuario"
                childColumns = "idUsuario", // Coluna na tabela "alimento"
                onDelete = ForeignKey.CASCADE), // Se o usuário for deletado, os alimentos associados também são
        indices = {@Index(value = {"idUsuario"})}) // Adicionado índice para a coluna idUsuario (para performance de FK)
public class Alimento implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "calorias")
    private int calorias;

    @ColumnInfo(name = "proteinas")
    private double proteinas;

    @ColumnInfo(name = "gorduras")
    private double gorduras;

    @ColumnInfo(name = "carboidratos")
    private double carboidratos;

    @ColumnInfo(name = "ehPersonalizado")
    private boolean ehPersonalizado;

    @ColumnInfo(name = "idUsuario")
    private Long idUsuario; // Long com L maiúsculo permite valores nulos para alimentos não personalizados

    // Construtor COMPLETO para Room
    public Alimento(long id, String nome, int calorias, double proteinas,
                    double gorduras, double carboidratos, boolean ehPersonalizado, Long idUsuario) {
        this.id = id;
        this.nome = nome;
        this.calorias = calorias;
        this.proteinas = proteinas;
        this.gorduras = gorduras;
        this.carboidratos = carboidratos;
        this.ehPersonalizado = ehPersonalizado;
        this.idUsuario = idUsuario;
    }

    // Construtor para alimentos criados pelo usuário sem ID inicial, com ehPersonalizado=true e idUsuario
    // @Ignore: fala para o Room para NÃO usar este construtor para ler dados do banco de dados.
    @Ignore
    public Alimento(String nome, int calorias, double proteinas,
                    double gorduras, double carboidratos, Long idUsuario) {
        this(0L, nome, calorias, proteinas, gorduras, carboidratos, true, idUsuario);
    }

    // Construtor para alimentos da API sem ID inicial, com ehPersonalizado=false e idUsuario=null
    // @Ignore: fala para o Room para NÃO usar este construtor para ler dados do banco de dados.
    @Ignore
    public Alimento(String nome, int calorias, double proteinas,
                    double gorduras, double carboidratos) {
        this(0L, nome, calorias, proteinas, gorduras, carboidratos, false, null);
    }

    // --- Getters ---
    public long getId() { return id; }
    public String getNome() { return nome; }
    public int getCalorias() { return calorias; }
    public double getProteinas() { return proteinas; }
    public double getGorduras() { return gorduras; }
    public double getCarboidratos() { return carboidratos; }
    public boolean isEhPersonalizado() { return ehPersonalizado; }
    public Long getIdUsuario() { return idUsuario; }


    // --- Setters ---
    public void setId(long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setCalorias(int calorias) { this.calorias = calorias; }
    public void setProteinas(double proteinas) { this.proteinas = proteinas; }
    public void setGorduras(double gorduras) { this.gorduras = gorduras; }
    public void setCarboidratos(double carboidratos) { this.carboidratos = carboidratos; }
    public void setEhPersonalizado(boolean ehPersonalizado) { this.ehPersonalizado = ehPersonalizado; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }


    public String getInfoNutricional() {
        return nome + ": " + calorias + " kcal, P: " + proteinas + "g, G: " + gorduras + "g, C: " + carboidratos + "g";
    }

    @Override
    public String toString() {
        return "Alimento{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", calorias=" + calorias +
                ", proteinas=" + proteinas +
                ", gorduras=" + gorduras +
                ", carboidratos=" + carboidratos +
                ", ehPersonalizado=" + ehPersonalizado +
                ", idUsuario=" + idUsuario +
                '}';
    }
}