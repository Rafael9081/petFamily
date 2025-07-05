# --- Estágio 1: O Construtor (Builder) ---
# Usamos uma imagem oficial do Maven que já vem com o Java 21 (especificado no seu pom.xml).
# Esta imagem é grande, mas só será usada para compilar o projeto.
FROM maven:3.9-eclipse-temurin-21 AS builder

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia apenas o pom.xml primeiro. Isso é um truque de cache do Docker.
# Se as dependências não mudarem, o Docker reutiliza a camada já baixada,
# tornando as construções futuras muito mais rápidas.
COPY pom.xml .
RUN mvn dependency:go-offline

# Agora copia o resto do cdigo-fonte do seu projeto
COPY src ./src

# Executa o build do Maven para gerar o arquivo .jar.
# -DskipTests pula a execução dos testes, o que é uma prática comum para builds de deploy.
RUN mvn package -DskipTests

# --- Estágio 2: A Execução (Runner) ---
# Agora, usamos uma imagem muito menor, que contém apenas o Java 21 para EXECUTAR a aplicação.
# Não precisamos mais do Maven nem do código-fonte, apenas do .jar compilado.
FROM eclipse-temurin:21-jre-jammy

# Define o diretório de trabalho
WORKDIR /app

# Copia APENAS o arquivo .jar que foi gerado no estágio "builder" para dentro da nossa imagem final.
# Note que o nome do .jar é baseado no <artifactId> e <version> do seu pom.xml.
COPY --from=builder /app/target/canil-api-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta 8080, que é a porta padrão do Spring Boot.
# O Render vai detectar isso e direcionar o tráfego para ela.
EXPOSE 8080

# Define o comando que será executado quando o container iniciar.
# Ele simplesmente executa a sua aplicação Spring Boot.
ENTRYPOINT ["java", "-jar", "app.jar"]