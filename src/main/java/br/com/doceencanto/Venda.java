package br.com.doceencanto;

import jakarta.persistence.*;

@Entity
public class Venda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long loteId = null;
    private int quantidade = 1;
    private java.math.BigDecimal preco = java.math.BigDecimal.ZERO;
    private java.time.LocalDateTime data = java.time.LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Long getLoteId() {
        return loteId;
    }

    public void setLoteId(Long value) {
        this.loteId = value;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int value) {
        this.quantidade = value;
    }

    public java.math.BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(java.math.BigDecimal value) {
        this.preco = value;
    }

    public java.time.LocalDateTime getData() {
        return data;
    }

    public void setData(java.time.LocalDateTime value) {
        this.data = value;
    }
}
