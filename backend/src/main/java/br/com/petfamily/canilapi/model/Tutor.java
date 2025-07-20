package br.com.petfamily.canilapi.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tutores")
@Getter
@Setter
@ToString(exclude = "cachorros")
@EqualsAndHashCode(of = "id")
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String telefone;

    @OneToMany(mappedBy = "tutor")
    private List<Cachorro> cachorros = new ArrayList<>();

    public Tutor() {
    }

    //CONSTRUTOR PARA OS TESTES
        public Tutor(String nome, String telefone, String email) {
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }
}