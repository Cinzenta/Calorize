package com.calorize.calorize_api;
import jakarta.persistence.*; //Importa as anotações JPA usadas para transformar a classe em uma tabela no banco de dados (mapeamento objeto-relacional).

@Entity //Define que essa classe será uma entidade, será convertida em tabela
public class Alimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String porcao;
    private double calorias;
    private double carboidratos;
    private double proteinas;
    private double gorduras;

    // Getters e setters


    public Long getId() { return id; }
    public void setId(Long id) {this.id = id; }

    public String getNome() {return nome; }
    public void setNome(String nome) {this.nome = nome; }

    public String getPorcao() {return porcao; }
    public void setPorcao(String porcao) {this.porcao = porcao; }

    public double getCalorias() {return calorias; }
    public void setCalorias(double calorias) {this.calorias = calorias; }

    public double getCarboidratos() {return carboidratos; }
    public void setCarboidratos(double carboidratos) {this.carboidratos = carboidratos; }

    public double getProteinas() {return proteinas; }
    public void setProteinas(double proteinas) {this.proteinas = proteinas; }

    public  double getGorduras() {return gorduras; }
    public void setGorduras(double gorduras) {this.gorduras = gorduras; }
}
