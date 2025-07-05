# Use uma imagem base que já tenha Java e Maven instalados
FROM openjdk:17-jdk-slim as builder

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o arquivo pom.xml para que o Maven possa baixar as dependências
# Isso otimiza o cache do Docker, pois as dependências não serão baixadas novamente
# se apenas o código-fonte mudar
COPY pom.xml .

# Baixa as dependências do Maven para otimizar o cache
# Isso garante que as dependências estejam disponíveis antes de copiar o código-fonte completo
RUN mvn dependency:go-offline

# Copia todo o código-fonte do projeto para o contêiner
# Esta linha estava comentada no seu Dockerfile anterior e é essencial!
COPY src ./src

# Compila o projeto Spring Boot e empacota em um JAR
# O -DskipTests é opcional, mas útil para builds de deploy
RUN mvn clean install -DskipTests

# --- Segunda Stage: Imagem de Runtime Menor ---
# Usa uma imagem base menor, ideal para rodar a aplicação em produção
FROM openjdk:17-jre-slim

# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR compilado da stage "builder" para esta nova imagem
# Lembre-se de verificar o nome exato do seu JAR na pasta 'target/'
# Ex: se o JAR for 'backend-0.0.1-SNAPSHOT.jar', use 'backend-0.0.1-SNAPSHOT.jar'
# Ou use 'app.jar' e seu ENTRYPOINT precisa ser 'app.jar'
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta que sua aplicação Spring Boot usa (padrão é 8080)
EXPOSE 8080

# Comando para rodar a aplicação Spring Boot quando o contêiner iniciar
ENTRYPOINT ["java", "-jar", "app.jar"]