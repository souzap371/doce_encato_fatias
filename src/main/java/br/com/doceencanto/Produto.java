package br.com.doceencanto;

import jakarta.persistence.*;

@Entity
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome = "";
    private int rendimento = 12;
    private java.math.BigDecimal maoObra = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal energia = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal embalagem = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal perda = new java.math.BigDecimal("5");
    private java.math.BigDecimal taxa = new java.math.BigDecimal("5");
    private java.math.BigDecimal margem = new java.math.BigDecimal("30");

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String value) {
        this.nome = value;
    }

    public int getRendimento() {
        return rendimento;
    }

    public void setRendimento(int value) {
        this.rendimento = value;
    }

    public java.math.BigDecimal getMaoObra() {
        return maoObra;
    }

    public void setMaoObra(java.math.BigDecimal value) {
        this.maoObra = value;
    }

    public java.math.BigDecimal getEnergia() {
        return energia;
    }

    public void setEnergia(java.math.BigDecimal value) {
        this.energia = value;
    }

    public java.math.BigDecimal getEmbalagem() {
        return embalagem;
    }

    public void setEmbalagem(java.math.BigDecimal value) {
        this.embalagem = value;
    }

    public java.math.BigDecimal getPerda() {
        return perda;
    }

    public void setPerda(java.math.BigDecimal value) {
        this.perda = value;
    }

    public java.math.BigDecimal getTaxa() {
        return taxa;
    }

    public void setTaxa(java.math.BigDecimal value) {
        this.taxa = value;
    }

    public java.math.BigDecimal getMargem() {
        return margem;
    }

    public void setMargem(java.math.BigDecimal value) {
        this.margem = value;
    }
}
