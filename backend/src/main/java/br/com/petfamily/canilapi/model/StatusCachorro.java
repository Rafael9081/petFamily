package br.com.petfamily.canilapi.model;

public enum StatusCachorro {
    DISPONIVEL("Disponível"),
    RESERVADO("Reservado"),
    VENDIDO("Vendido"),
    MATRIZ_PADREADOR("Matriz/Padreador"), // Para cães que ficam no canil
    INDISPONIVEL("Indisponível"); // Para outros casos, como tratamento médico

    private final String descricao;

    StatusCachorro(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}