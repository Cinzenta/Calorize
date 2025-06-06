package com.gustavo.calorize.api.modelo;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AlimentoApiModelo implements Serializable {

    @SerializedName("id")
    private Long id;
    @SerializedName("nome")
    private String nome;
    @SerializedName("porcao")
    private String porcao;
    @SerializedName("calorias")
    private double calorias;
    @SerializedName("carboidratos")
    private double carboidratos;
    @SerializedName("proteinas")
    private double proteinas;
    @SerializedName("gorduras")
    private double gorduras;

    // Construtor completo para quando a API retorna todos os dados
    public AlimentoApiModelo(Long id, String nome, String porcao, double calorias, double carboidratos, double proteinas, double gorduras) {
        this.id = id;
        this.nome = nome;
        this.porcao = porcao;
        this.calorias = calorias;
        this.carboidratos = carboidratos;
        this.proteinas = proteinas;
        this.gorduras = gorduras;
    }


    // --- Getters ---
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getPorcao() { return porcao; }
    public double getCalorias() { return calorias; }
    public double getCarboidratos() { return carboidratos; }
    public double getProteinas() { return proteinas; }
    public double getGorduras() { return gorduras; }

    // --- Setters ---
    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setCalorias(double calorias) { this.calorias = calorias; }


    @Override
    public String toString() {
        return "AlimentoApiModelo{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", porcao='" + porcao + '\'' +
                ", calorias=" + calorias +
                ", carboidratos=" + carboidratos +
                ", proteinas=" + proteinas +
                ", gorduras=" + gorduras +
                '}';
    }
}