package br.com.petfamily.canilapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "caracteristicas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "cachorro")
@EqualsAndHashCode(of = "id")
public class Caracteristica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    @Enumerated(EnumType.STRING)
    private TipoCaracteristica tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cachorro_id")
    private Cachorro cachorro;
}