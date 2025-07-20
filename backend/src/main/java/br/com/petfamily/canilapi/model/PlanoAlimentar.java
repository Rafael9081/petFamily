package br.com.petfamily.canilapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "planos_alimentares")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class PlanoAlimentar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoRacao;

    // BigDecimal é mais preciso para medidas do que double
    private BigDecimal quantidadeGramasPorDia;

    private int frequenciaDiaria;
    private String instrucoesEspeciais;
}