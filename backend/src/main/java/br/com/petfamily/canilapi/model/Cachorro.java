package br.com.petfamily.canilapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cachorros")
@Getter
@Setter
@NoArgsConstructor // Adiciona o construtor vazio (public Cachorro() {}) que o JPA precisa.
@ToString(exclude = {"tutor", "pai", "mae", "ninhadasComoMae", "ninhadasComoPai", "registroVenda", "caracteristicas", "carteiraVacinacao", "planoAlimentar", "historicoDespesas", "ninhada"})
@EqualsAndHashCode(of = "id")
public class Cachorro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    private LocalDate dataNascimento;
    private String raca;
    private boolean foiVendido = false;

    @Enumerated(EnumType.STRING)
    private StatusCachorro status;

    // --- RELACIONAMENTOS MAPEADOS ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pai_id")
    private Cachorro pai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mae_id")
    private Cachorro mae;

    @OneToMany(mappedBy = "mae", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Ninhada> ninhadasComoMae = new ArrayList<>();

    @OneToMany(mappedBy = "pai", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Ninhada> ninhadasComoPai = new ArrayList<>();

    @OneToOne(mappedBy = "cachorro", cascade = CascadeType.ALL)
    private Venda registroVenda;

    @OneToMany(mappedBy = "cachorro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Caracteristica> caracteristicas = new ArrayList<>();

    @OneToMany(mappedBy = "cachorro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vacina> carteiraVacinacao = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "plano_alimentar_id", referencedColumnName = "id")
    private PlanoAlimentar planoAlimentar;

    @OneToMany(mappedBy = "cachorro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Despesa> historicoDespesas = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ninhada_id")
    private Ninhada ninhada;

    // --- CONSTRUTOR PERSONALIZADO ---
    // Manter construtores com lógica específica é uma ótima prática.
    public Cachorro(String nome, Sexo sexo, LocalDate dataNascimento, String raca, Tutor tutorInicial) {
        this.nome = nome;
        this.sexo = sexo;
        this.dataNascimento = dataNascimento;
        this.raca = raca;
        this.tutor = tutorInicial;
    }

    // --- MÉTODOS DE NEGÓCIO ---

    public void adicionarVacina(Vacina vacina) {
        this.carteiraVacinacao.add(vacina);
        vacina.setCachorro(this);
    }

    public void adicionarDespesa(Despesa despesa) {
        this.historicoDespesas.add(despesa);
        despesa.setCachorro(this);
    }

    public void adicionarCaracteristica(Caracteristica caracteristica) {
        this.caracteristicas.add(caracteristica);
        caracteristica.setCachorro(this);
    }

    public void adicionarNinhada(Ninhada ninhada) {
        if (this.equals(ninhada.getPai())) {
            this.ninhadasComoPai.add(ninhada);
        } else if (this.equals(ninhada.getMae())) {
            if (this.sexo != Sexo.FEMEA) {
                throw new IllegalArgumentException("Apenas fêmeas podem ser mães de ninhadas.");
            }
            this.ninhadasComoMae.add(ninhada);
        } else {
            throw new IllegalArgumentException("O cachorro não é pai nem mãe da ninhada fornecida.");
        }
    }

    public void realizarVenda(Venda venda) {
        if (this.foiVendido) {
            throw new IllegalStateException("Este cachorro já foi vendido.");
        }
        this.setFoiVendido(true);
        this.setTutor(venda.getNovoTutor());
        this.setRegistroVenda(venda);
        venda.setCachorro(this);
    }

    public double calcularCustoTotal() {
        return this.historicoDespesas.stream()
                .map(Despesa::getValor)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
    }
}