package br.com.doceencanto;

import jakarta.persistence.*;

@Entity
public class Ingrediente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long produtoId = null;
    private String nome = "";
    private String unidade = "g";
    private java.math.BigDecimal precoPacote = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal quantidadePacote = java.math.BigDecimal.ONE;
    private java.math.BigDecimal quantidadeUsada = java.math.BigDecimal.ONE;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long value) {
        this.produtoId = value;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String value) {
        this.nome = value;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String value) {
        this.unidade = value;
    }

    public java.math.BigDecimal getPrecoPacote() {
        return precoPacote;
    }

    public void setPrecoPacote(java.math.BigDecimal value) {
        this.precoPacote = value;
    }

    public java.math.BigDecimal getQuantidadePacote() {
        return quantidadePacote;
    }

    public void setQuantidadePacote(java.math.BigDecimal value) {
        this.quantidadePacote = value;
    }

    public java.math.BigDecimal getQuantidadeUsada() {
        return quantidadeUsada;
    }

    public void setQuantidadeUsada(java.math.BigDecimal value) {
        this.quantidadeUsada = value;
    }
}
