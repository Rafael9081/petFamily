package br.com.petfamily.canilapi.model;

import java.time.LocalDate;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Cachorro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    private LocalDate dataNascimento;
    private String raca;
    private boolean foiVendido;

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

    @OneToOne(mappedBy = "cachorroVendido", cascade = CascadeType.ALL)
    private Venda registroVenda;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cachorro_id")
    private List<Caracteristica> caracteristicas = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cachorro_id")
    private List<Vacina> carteiraVacinacao = new ArrayList<>();

    public Cachorro() {
        // Construtor padrão necessário para JPA
    }

    public Cachorro(String nome, Sexo sexo, LocalDate dataNascimento, String raca, Tutor tutorInicial) {
        this.nome = nome;
        this.sexo = sexo;
        this.dataNascimento = dataNascimento;
        this.raca = raca;
        this.tutor = tutorInicial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public boolean isFoiVendido() {
        return foiVendido;
    }

    public void setFoiVendido(boolean foiVendido) {
        this.foiVendido = foiVendido;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public Cachorro getPai() {
        return pai;
    }

    public void setPai(Cachorro pai) {
        this.pai = pai;
    }

    public Cachorro getMae() {
        return mae;
    }

    public void setMae(Cachorro mae) {
        this.mae = mae;
    }

    public List<Despesa> getHistoricoDespesas() {
        return historicoDespesas;
    }

    public void setHistoricoDespesas(List<Despesa> historicoDespesas) {
        this.historicoDespesas = historicoDespesas;
    }

    public List<Ninhada> getHistoricoNinhadas() {
        return historicoNinhadas;
    }

    public void setHistoricoNinhadas(List<Ninhada> historicoNinhadas) {
        this.historicoNinhadas = historicoNinhadas;
    }

    public Venda getRegistroVenda() {
        return registroVenda;
    }

    public void setRegistroVenda(Venda registroVenda) {
        this.registroVenda = registroVenda;
    }

    public List<Vacina> getCarteiraVacinacao() {
        return carteiraVacinacao;
    }

    public void setCarteiraVacinacao(List<Vacina> carteiraVacinacao) {
        this.carteiraVacinacao = carteiraVacinacao;
    }

    public void adicionarDespesa(Despesa despesa) {
        this.historicoDespesas.add(despesa);
        despesa.setCachorro(this);
    }

    public void adicionarVacina(Vacina vacina) {
        this.carteiraVacinacao.add(vacina);
    }

    public void adicionarCaracteristica(Caracteristica caracteristica) {
        this.caracteristicas.add(caracteristica);
    }

    public void adicionarNinhada(Ninhada ninhada) {
        if (this.sexo == Sexo.FEMEA) {
            this.historicoNinhadas.add(ninhada);
            ninhada.setMae(this);
        } else {
            throw new IllegalArgumentException("Apenas fêmeas podem ter ninhadas.");
        }
    }

    public void realizarVenda(Venda venda) {
        if (this.foiVendido) {
            throw new IllegalStateException("Este cachorro já foi vendido.");
        }
        this.setFoiVendido(true);
        this.setTutor(venda.getNovoTutor());
        this.setRegistroVenda(venda);
        venda.setCachorroVendido(this);
    }

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
        System.out.println("\nCUSTO TOTAL ACUMULADO R$ " + String.format("%.2f", custoTotal));

        System.out.println("\n--- DADOS DE VENDA ---");
        if (!foiVendido) {
            System.out.println("Este cachorro ainda não foi vendido.");
        } else {
            System.out.println(this.registroVenda.toString());
            double valorVenda = this.registroVenda.getValorVenda();
            double lucro = valorVenda - custoTotal;

            System.out.println("\nLUCRO DA VENDA: R$ %.2f (Venda): R$ %.2f - R$ %.2f (Custo) = R$ %.2f"
                    .formatted(valorVenda, custoTotal, lucro));
        }
        System.out.println("=========================================\n");
    }
}