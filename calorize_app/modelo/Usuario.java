package com.gustavo.calorize.modelo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "usuario")
public class Usuario implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "meta_de_calorias")
    private int metaDeCalorias;

    @ColumnInfo(name = "meta_proteinas")
    private double metaProteinas;

    @ColumnInfo(name = "meta_gorduras")
    private double metaGorduras;

    @ColumnInfo(name = "meta_carboidratos")
    private double metaCarboidratos;

    // ATRIBUTOS PARA RASTREAR O CONSUMO ATUAL
    @ColumnInfo(name = "calorias_consumidas")
    private int caloriasConsumidas;

    @ColumnInfo(name = "proteinas_consumidas")
    private double proteinasConsumidas;

    @ColumnInfo(name = "carboidratos_consumidos")
    private double carboidratosConsumidos;

    @ColumnInfo(name = "gorduras_consumidas")
    private double gordurasConsumidas;

    // Constructors

    // Construtor principal que o Room de usar para reconstruir objetos do DB
    public Usuario(long id, String nome, int metaDeCalorias, double metaProteinas, double metaGorduras, double metaCarboidratos,
                   int caloriasConsumidas, double proteinasConsumidas, double carboidratosConsumidos, double gordurasConsumidas) {
        this.id = id;
        this.nome = nome;
        this.metaDeCalorias = metaDeCalorias;
        this.metaProteinas = metaProteinas;
        this.metaGorduras = metaGorduras;
        this.metaCarboidratos = metaCarboidratos;
        this.caloriasConsumidas = caloriasConsumidas;
        this.proteinasConsumidas = proteinasConsumidas;
        this.carboidratosConsumidos = carboidratosConsumidos;
        this.gordurasConsumidas = gordurasConsumidas;
    }

    // Construtor para CRIAR um NOVO usuário ID será gerado pelo Room
    // Inicializa as metas de macros como 0.0 por padrão, e consumo como 0/0.0
    @Ignore
    public Usuario(String nome, int metaDeCalorias) {
        this(0L, nome, metaDeCalorias, 0.0, 0.0, 0.0, 0, 0.0, 0.0, 0.0);
    }

    // Construtor para quando você só tem o nome
    // Inicializa todas as metas e consumos como 0/0.0, sem macros
    @Ignore
    public Usuario(String nome) {
        this(0L, nome, 0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, 0.0);
    }

    //Getters e Setters

    public long getId() { return id; } // Tipo de retorno long
    public void setId(long id) { this.id = id; } // Parâmetro long

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getMetaDeCalorias() { return metaDeCalorias; }
    public void setMetaDeCalorias(int metaDeCalorias) { this.metaDeCalorias = metaDeCalorias; }

    public double getMetaProteinas() { return metaProteinas; }
    public void setMetaProteinas(double metaProteinas) { this.metaProteinas = metaProteinas; }

    public double getMetaGorduras() { return metaGorduras; }
    public void setMetaGorduras(double metaGorduras) { this.metaGorduras = metaGorduras; }

    public double getMetaCarboidratos() { return metaCarboidratos; }
    public void setMetaCarboidratos(double metaCarboidratos) { this.metaCarboidratos = metaCarboidratos; }

    // GETTERS E SETTERS PARA OS ATRIBUTOS DE CONSUMO
    public int getCaloriasConsumidas() { return caloriasConsumidas; }
    public void setCaloriasConsumidas(int caloriasConsumidas) { this.caloriasConsumidas = caloriasConsumidas; }

    public double getProteinasConsumidas() { return proteinasConsumidas; }
    public void setProteinasConsumidas(double proteinasConsumidas) { this.proteinasConsumidas = proteinasConsumidas; }

    public double getCarboidratosConsumidos() { return carboidratosConsumidos; }
    public void setCarboidratosConsumidos(double carboidratosConsumidos) { this.carboidratosConsumidos = carboidratosConsumidos; }

    public double getGordurasConsumidas() { return gordurasConsumidas; }
    public void setGordurasConsumidas(double gordurasConsumidas) { this.gordurasConsumidas = gordurasConsumidas; }

    // Métodos Adicionais
    public void editarPerfil(String nome) {
        this.nome = nome;
    }

    public void redefinirMeta(int novaMeta) {
        this.metaDeCalorias = novaMeta;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", metaDeCalorias=" + metaDeCalorias +
                ", metaProteinas=" + metaProteinas +
                ", metaGorduras=" + metaGorduras +
                ", metaCarboidratos=" + metaCarboidratos +
                ", caloriasConsumidas=" + caloriasConsumidas +
                ", proteinasConsumidas=" + proteinasConsumidas +
                ", carboidratosConsumidos=" + carboidratosConsumidos +
                ", gordurasConsumidas=" + gordurasConsumidas +
                '}';
    }
}