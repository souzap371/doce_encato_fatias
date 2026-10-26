package br.com.doceencanto;

import jakarta.persistence.*;

@Entity
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Long version;
    private String nome = "";
    private String local = "";
    private java.time.LocalDate data = java.time.LocalDate.now();
    private String observacoes = "";
    private boolean encerrado = false;

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

    public String getLocal() {
        return local;
    }

    public void setLocal(String value) {
        this.local = value;
    }

    public java.time.LocalDate getData() {
        return data;
    }

    public void setData(java.time.LocalDate value) {
        this.data = value;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String value) {
        this.observacoes = value;
    }

    public boolean getEncerrado() {
        return encerrado;
    }

    public void setEncerrado(boolean value) {
        this.encerrado = value;
    }
}
