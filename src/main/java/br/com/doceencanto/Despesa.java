package br.com.doceencanto;

import jakarta.persistence.*;

@Entity
public class Despesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long eventoId = null;
    private String descricao = "";
    private java.math.BigDecimal valor = java.math.BigDecimal.ZERO;

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String value) {
        this.descricao = value;
    }

    public java.math.BigDecimal getValor() {
        return valor;
    }

    public void setValor(java.math.BigDecimal value) {
        this.valor = value;
    }
}
