package br.com.petfamily.canilapi.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
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

    @OneToMany(mappedBy = "cachorro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Despesa> historicoDespesas = new ArrayList<>();

    @OneToMany(mappedBy = "mae", cascade = CascadeType.ALL)
    private List<Ninhada> historicoNinhadas = new ArrayList<>();

    // --- CORREÇÃO AQUI ---
    // O valor de 'mappedBy' deve ser o nome do campo na entidade Venda, que é "cachorro".
    @OneToOne(mappedBy = "cachorro", cascade = CascadeType.ALL)
    private Venda registroVenda;

    @OneToMany(mappedBy = "cachorro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Caracteristica> caracteristicas = new ArrayList<>();

    @OneToMany(mappedBy = "cachorro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vacina> carteiraVacinacao = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "plano_alimentar_id", referencedColumnName = "id")
    private PlanoAlimentar planoAlimentar;

    // --- CONSTRUTORES ---

    public Cachorro() {
        // Construtor padrão necessrio para JPA
    }

    public Cachorro(String nome, Sexo sexo, LocalDate dataNascimento, String raca, Tutor tutorInicial) {
        this.nome = nome;
        this.sexo = sexo;
        this.dataNascimento = dataNascimento;
        this.raca = raca;
        this.tutor = tutorInicial;
    }

    // --- GETTERS E SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Sexo getSexo() { return sexo; }
    public void setSexo(Sexo sexo) { this.sexo = sexo; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }
    public boolean isFoiVendido() { return foiVendido; }
    public void setFoiVendido(boolean foiVendido) { this.foiVendido = foiVendido; }
    public Tutor getTutor() { return tutor; }
    public void setTutor(Tutor tutor) { this.tutor = tutor; }
    public Cachorro getPai() { return pai; }
    public void setPai(Cachorro pai) { this.pai = pai; }
    public Cachorro getMae() { return mae; }
    public void setMae(Cachorro mae) { this.mae = mae; }
    public List<Despesa> getHistoricoDespesas() { return historicoDespesas; }
    public void setHistoricoDespesas(List<Despesa> historicoDespesas) { this.historicoDespesas = historicoDespesas; }
    public List<Ninhada> getHistoricoNinhadas() { return historicoNinhadas; }
    public void setHistoricoNinhadas(List<Ninhada> historicoNinhadas) { this.historicoNinhadas = historicoNinhadas; }
    public Venda getRegistroVenda() { return registroVenda; }
    public void setRegistroVenda(Venda registroVenda) { this.registroVenda = registroVenda; }
    public List<Caracteristica> getCaracteristicas() { return caracteristicas; }
    public void setCaracteristicas(List<Caracteristica> caracteristicas) { this.caracteristicas = caracteristicas; }
    public List<Vacina> getCarteiraVacinacao() { return carteiraVacinacao; }
    public void setCarteiraVacinacao(List<Vacina> carteiraVacinacao) { this.carteiraVacinacao = carteiraVacinacao; }
    public PlanoAlimentar getPlanoAlimentar() { return planoAlimentar; }
    public void setPlanoAlimentar(PlanoAlimentar planoAlimentar) { this.planoAlimentar = planoAlimentar; }

    // --- MÉTODOS DE NEGÓCIO ---

    public void adicionarVacina(Vacina vacina) {
        this.carteiraVacinacao.add(vacina);
        vacina.setCachorro(this);
    }

    public void adicionarDespesa(Despesa despesa) {
        this.historicoDespesas.add(despesa); // Adiciona a despesa à lista
        despesa.setCachorro(this);
    }


    public void adicionarCaracteristica(Caracteristica caracteristica) {

        this.caracteristicas.add(caracteristica);
        caracteristica.setCachorro(this);
    }

    public void adicionarNinhada(Ninhada ninhada) {
        if (this.sexo != Sexo.FEMEA) {
            throw new IllegalArgumentException("Apenas fêmeas podem ter ninhadas.");
        }
        this.historicoNinhadas.add(ninhada);
        ninhada.setMae(this);
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

    // --- MÉTODOS DE CÁLCULO E EXIBIÇÃO ---

    public double calcularCustoTotal() {
        return this.historicoDespesas.stream()
                .mapToDouble(Despesa::getValor)
                .sum();
    }

    public void gerarRelatorioFinanceiro() {
        System.out.println("\n===== RELATÓRIO FINANCEIRO: " + this.nome.toUpperCase() + " =====");
        System.out.println("\n--- HISTÓRICO DE DESPESAS ---");

        if (this.historicoDespesas.isEmpty()) {
            System.out.println("Nenhuma despesa registrada.");
        } else {
            for (Despesa d : this.historicoDespesas) {
                System.out.println(d.toString());
            }
        }

        double custoTotal = calcularCustoTotal();
        System.out.printf("\nCUSTO TOTAL ACUMULADO: R$ %.2f\n", custoTotal);
        System.out.println("\n--- DADOS DA VENDA ---");

        if (!foiVendido) {
            System.out.println("Este cachorro ainda não foi vendido.");
        } else {
            System.out.println(this.registroVenda.toString());
            double valorVenda = this.registroVenda.getValor();
            double lucro = valorVenda - custoTotal;

            System.out.printf("\nLUCRO DA VENDA: R$ %.2f (Venda) - R$ %.2f (Custo) = R$ %.2f\n",
                    valorVenda, custoTotal, lucro);
        }
        System.out.println("=========================================\n");
    }

    public void setNinhadasComoMae(List<Ninhada> ninhadas) {
        this.historicoNinhadas = ninhadas;
    }
}