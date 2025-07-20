package br.com.petfamily.canilapi.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ninhadas")
@Getter
@Setter
@ToString(exclude = {"mae", "pai", "filhotes"}) // Evita recursão no toString
@EqualsAndHashCode(of = "id") // Compara objetos apenas pelo ID
public class Ninhada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataNascimento;

    // --- Relacionamentos ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mae_id", nullable = false)
    private Cachorro mae;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pai_id", nullable = false)
    private Cachorro pai;

    // MELHORIA: Adicionando a lista de filhotes
    // Uma ninhada tem muitos filhotes. A entidade Cachorro é a "dona" do relacionamento.
    @OneToMany(
            mappedBy = "ninhada", // Mapeado pelo campo 'ninhada' na classe Cachorro
            cascade = CascadeType.ALL, // Operações na ninhada afetam os filhotes
            orphanRemoval = true // Remove filhotes do DB se forem removidos desta lista
    )
    private List<Cachorro> filhotes = new ArrayList<>();


    // --- Construtor Padrão (necessário para o JPA) ---
    public Ninhada() {
    }

    // --- Construtor com Parâmetros ---
    public Ninhada(LocalDate dataNascimento, Cachorro mae, Cachorro pai) {
        this.dataNascimento = dataNascimento;
        this.mae = mae;
        this.pai = pai;
        // Inicialize a lista de filhotes para evitar NullPointerException
        this.filhotes = new java.util.ArrayList<>();
    }

    // --- Métodos de Negócio ---

    /**
     * Adiciona um filhote à lista da ninhada, garantindo a consistência
     * do relacionamento bidirecional.
     * @param filhote O cachorro a ser adicionado como filhote.
     */
    public void adicionarFilhote(Cachorro filhote) {
        this.filhotes.add(filhote);
        filhote.setNinhada(this);
    }

    /**
     * Retorna a quantidade total de filhotes na ninhada.
     * Este é um valor calculado e não é persistido no banco de dados.
     */
    @Transient // Informa ao JPA para ignorar este método na persistência
    public int getQuantidadeTotalFilhotes() {
        return this.filhotes.size();
    }
}