package br.com.petfamily.canilapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "vacinas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "cachorro")
@EqualsAndHashCode(of = "id")
public class Vacina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private LocalDate dataAplicacao;
    private LocalDate dataProximaAplicacao;

    // Melhor prática: Usar BigDecimal para valores monetários
    private BigDecimal valor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cachorro_id")
    private Cachorro cachorro;
}