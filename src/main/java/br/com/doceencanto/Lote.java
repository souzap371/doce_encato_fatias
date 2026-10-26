package br.com.doceencanto;

import jakarta.persistence.*;

@Entity
public class Lote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Long version;
    private Long eventoId = null;
    private String produto = "";
    private int quantidade = 0;
    @Column(precision = 19, scale = 8)
    private java.math.BigDecimal custoUnitario = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal preco = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal taxa = java.math.BigDecimal.ZERO;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long value) {
        this.eventoId = value;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String value) {
        this.produto = value;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int value) {
        this.quantidade = value;
    }

    public java.math.BigDecimal getCustoUnitario() {
        return custoUnitario;
    }

    public void setCustoUnitario(java.math.BigDecimal value) {
        this.custoUnitario = value;
    }

    public java.math.BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(java.math.BigDecimal value) {
        this.preco = value;
    }

    public java.math.BigDecimal getTaxa() {
        return taxa;
    }

    public void setTaxa(java.math.BigDecimal value) {
        this.taxa = value;
    }
}
