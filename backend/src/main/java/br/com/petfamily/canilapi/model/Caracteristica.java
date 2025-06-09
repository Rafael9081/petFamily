package br.com.petfamily.canilapi.model;

import jakarta.persistence.*;

@Entity
public class Caracteristica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    @Enumerated(EnumType.STRING)
    private TipoCaracteristica tipo;


    public Caracteristica() {
        // Construtor padrão
    }

    public Caracteristica(Long id, String descricao, TipoCaracteristica tipo) {
        this.id = id;
        this.descricao = descricao;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoCaracteristica getTipo() {
        return tipo;
    }

    public void setTipo(TipoCaracteristica tipo) {
        this.tipo = tipo;
    }
}
