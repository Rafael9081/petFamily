# Canil Manager API

![Java](https://img.shields.io/badge/Java-21-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.1-brightgreen.svg)
![Maven](https://img.shields.io/badge/Maven-4.0.0-red.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

API REST para o gerenciamento de um canil de cães, desenvolvida com Spring Boot. O projeto permite o controle completo sobre os animais, incluindo cadastro, despesas, vendas e relatórios financeiros.

## Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Principais Funcionalidades](#principais-funcionalidades)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Como Executar](#como-executar)
- [Documentação da API (Swagger)](#documentação-da-api-swagger)
- [Endpoints da API](#endpoints-da-api)
- [Testes](#testes)

## Sobre o Projeto

O **Canil Manager API** foi criado para ser um sistema de back-end robusto e escalável para a gestão de um canil. A arquitetura foi desenhada seguindo as melhores práticas de desenvolvimento, como a separação de responsabilidades em camadas (Controller, Service, Repository) e o uso do padrão DTO (Data Transfer Object) para garantir um contrato de API estável e seguro.

## Principais Funcionalidades

- ✅ **CRUD completo** para a entidade `Cachorro`.
- ✅ Gerenciamento de **sub-recursos**, como a adição de despesas a um cachorro específico.
- ✅ Geração de **relatórios financeiros** detalhados por animal, com cálculo de custos e lucro.
- ✅ **Atualização parcial** de recursos com o método `PATCH`, permitindo modificar apenas campos específicos.
- ✅ **Paginação** nas listagens para otimizar a performance.
- ✅ **Validação de dados** de entrada na camada de Controller (`@Valid`).
- ✅ **Tratamento de exceções** centralizado para respostas de erro padronizadas.
- ✅ **Documentação automática** da API com Springdoc (Swagger UI).
- ✅ Cobertura de **testes de unidade e integração** para garantir a confiabilidade do código.

## Tecnologias Utilizadas

- **Java 21**: Versão mais recente da linguagem Java.
- **Spring Boot 3.3.1**: Framework principal para a construção da aplicação.
- **Spring Web**: Para a criação de endpoints REST.
- **Spring Data JPA**: Para a persistência de dados de forma simplificada.
- **Hibernate**: Implementação JPA para o mapeamento objeto-relacional.
- **H2 Database**: Banco de dados em memória para desenvolvimento e testes.
- **Maven**: Gerenciador de dependências e build do projeto.
- **Springdoc OpenAPI (Swagger)**: Para documentação interativa da API.
- **JUnit 5 & Mockito**: Para a escrita de testes de unidade e integração.

## Como Executar

### Pré-requisitos

- JDK 21 ou superior.
- Apache Maven 3.8 ou superior.

### Passos

1.  **Clone o repositório:**
   bash git clone <url-do-seu-repositorio>
2.  **Navegue até o diretório do backend:**
   bash cd br.PetFamily.CanilManager/ backend
3. **Execute a aplicação com o Maven:**
   bash mvn spring-boot:run
4.  A API estará disponível em `http://localhost:8080`.

## Documentação da API (Swagger)

Após iniciar a aplicação, a documentação completa e interativa da API, gerada pelo Springdoc, pode ser acessada no seu navegador:

➡️ **http://localhost:8080/swagger-ui.html**

Lá você pode visualizar todos os endpoints, seus parâmetros, corpos de requisição e respostas, além de poder testá-los diretamente.

## Endpoints da API

A base da URL para todos os endpoints é `http://localhost:8080`.

### Recurso: Cachorro (`/cachorros`)

| Método | Endpoint                               | Descrição                                                              |
| :----- | :------------------------------------- | :--------------------------------------------------------------------- |
| `GET`  | `/cachorros`                           | Lista todos os cachorros de forma paginada.                            |
| `GET`  | `/cachorros/todos`                     | Lista todos os cachorros (sem paginação).                              |
| `GET`  | `/cachorros/{id}`                      | Busca um cachorro específico pelo seu ID.                              |
| `POST` | `/cachorros`                           | Cria um novo cachorro.                                                 |
| `PUT`  | `/cachorros/{id}`                      | Atualiza todos os dados de um cachorro.                                |
| `PATCH`| `/cachorros/{id}`                      | Atualiza parcialmente os dados de um cachorro.                         |
| `DELETE`| `/cachorros/{id}`                     | Deleta um cachorro.                                                    |
| `POST` | `/cachorros/{id}/despesas`             | Adiciona uma nova despesa a um cachorro.                               |
| `GET`  | `/cachorros/{id}/relatorio-financeiro` | Gera um relatório financeiro detalhado para um cachorro.               |

#### Exemplo: Corpo da Requisição para `POST /cachorros`

   json { "nome": "Rex", "sexo": "MACHO", "raca": "Pastor Alemão", "dataNascimento": "2023-01-15", "tutorId": 1 }

#### Exemplo: Corpo da Requisição para `PATCH /cachorros/{id}`

  json { "nome": "Rex Atualizado", "tutorId": 2 }

## Testes

O projeto possui uma suíte de testes para garantir a qualidade e o correto funcionamento da lógica de negócio e dos endpoints.

- **Testes de Unidade**: Localizados em `src/test/java`, testam as classes de serviço de forma isolada usando Mockito.
- **Testes de Integração**: Testam os controllers e o fluxo da API usando MockMvc e o contexto do Spring Boot.

Para executar todos os testes, utilize o seguinte comando Maven:
   bash mvn test
