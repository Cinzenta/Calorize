package com.gustavo.calorize.modelo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "alimentos_consumidos",
        foreignKeys = @ForeignKey(entity = Usuario.class,
                parentColumns = "id",
                childColumns = "usuarioId",
                onDelete = CASCADE),
        indices = {@androidx.room.Index(value = {"usuarioId"})})
public class AlimentoConsumido implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "usuarioId")
    private long usuarioId;

    @ColumnInfo(name = "alimentoOriginalId")
    private Long alimentoOriginalId; // Pode ser nulo se o alimento foi digitado manualmente

    @ColumnInfo(name = "nomeAlimento")
    private String nomeAlimento;

    @ColumnInfo(name = "caloriasConsumidas")
    private double caloriasConsumidas;

    @ColumnInfo(name = "proteinasConsumidas")
    private double proteinasConsumidas;

    @ColumnInfo(name = "carboidratosConsumidas")
    private double carboidratosConsumidas;

    @ColumnInfo(name = "gordurasConsumidas")
    private double gordurasConsumidas;

    @ColumnInfo(name = "gramasConsumidas")
    private double gramasConsumidas; // Quantidade em gramas

    @ColumnInfo(name = "tipoRefeicao")
    private String tipoRefeicao;

    @ColumnInfo(name = "timestamp")
    private long timestamp; // Data e hora em milissegundos (System.currentTimeMillis())

    // Construtor completo para o Room usado pelo Room para recriar objetos do banco
    // O Room usa este construtor para criar objetos a partir das linhas do banco.
    public AlimentoConsumido(long id, long usuarioId, Long alimentoOriginalId, String nomeAlimento,
                             double caloriasConsumidas, double proteinasConsumidas,
                             double carboidratosConsumidas, double gordurasConsumidas,
                             double gramasConsumidas, String tipoRefeicao, long timestamp) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.alimentoOriginalId = alimentoOriginalId;
        this.nomeAlimento = nomeAlimento;
        this.caloriasConsumidas = caloriasConsumidas;
        this.proteinasConsumidas = proteinasConsumidas;
        this.carboidratosConsumidas = carboidratosConsumidas;
        this.gordurasConsumidas = gordurasConsumidas;
        this.gramasConsumidas = gramasConsumidas;
        this.tipoRefeicao = tipoRefeicao;
        this.timestamp = timestamp;
    }

    // Construtor para criar um novo AlimentoConsumido sem ID inicial
    @androidx.room.Ignore // O Room usará o construtor completo para operações de leitura
    public AlimentoConsumido(long usuarioId, Long alimentoOriginalId, String nomeAlimento,
                             double caloriasConsumidas, double proteinasConsumidas,
                             double carboidratosConsumidas, double gordurasConsumidas,
                             double gramasConsumidas, String tipoRefeicao, long timestamp) {
        // Chamando o construtor completo com id = 0L para que o Room autogenere
        this(0L, usuarioId, alimentoOriginalId, nomeAlimento, caloriasConsumidas, proteinasConsumidas,
                carboidratosConsumidas, gordurasConsumidas, gramasConsumidas, tipoRefeicao, timestamp);
    }

    // --- GETTERS ---
    public long getId() { return id; }
    public long getUsuarioId() { return usuarioId; }
    public Long getAlimentoOriginalId() { return alimentoOriginalId; }
    public String getNomeAlimento() { return nomeAlimento; }
    public double getCaloriasConsumidas() { return caloriasConsumidas; }
    public double getProteinasConsumidas() { return proteinasConsumidas; }
    public double getCarboidratosConsumidas() { return carboidratosConsumidas; }
    public double getGordurasConsumidas() { return gordurasConsumidas; }
    public double getGramasConsumidas() { return gramasConsumidas; }
    public String getTipoRefeicao() { return tipoRefeicao; }
    public long getTimestamp() { return timestamp; }


    // --- SETTERS ---
    public void setId(long id) { this.id = id; }
    public void setUsuarioId(long usuarioId) { this.usuarioId = usuarioId; }

    public void setTipoRefeicao(String tipoRefeicao) { this.tipoRefeicao = tipoRefeicao; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "AlimentoConsumido{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", nomeAlimento='" + nomeAlimento + '\'' +
                ", caloriasConsumidas=" + caloriasConsumidas +
                ", gramasConsumidas=" + gramasConsumidas +
                ", tipoRefeicao='" + tipoRefeicao + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}