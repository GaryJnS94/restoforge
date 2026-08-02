# Image de l'API RestoForge — build multi-stage.
# Contexte de build attendu : le dossier api/
#   docker build -t restoforge-api .

# ---------------------------------------------------------------------------
# Stage 1 — compilation
# ---------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Le wrapper et le POM d'abord, sans le code source.
# Docker met en cache le résultat de chaque instruction et le réutilise tant
# que les fichiers copiés n'ont pas changé. Isoler cette copie permet à la
# couche de dépendances ci-dessous de survivre à toute modification d'une
# classe Java : le rebuild passe de quelques minutes à quelques secondes.
COPY mvnw ./
COPY .mvn/ .mvn/
COPY pom.xml ./

# Télécharge l'intégralité des dépendances dans l'image.
# Cette couche n'est invalidée que lorsque pom.xml change.
RUN ./mvnw dependency:go-offline

# Le code source vient après : c'est ce qui change à chaque commit.
COPY src/ src/

# -DskipTests est délibéré : les tests reposent sur Testcontainers, qui exige
# un démon Docker. On ne lance pas Docker dans Docker. Les tests s'exécutent
# dans la CI, en amont de la construction de cette image.
RUN ./mvnw clean package -DskipTests

# ---------------------------------------------------------------------------
# Stage 2 — exécution
# ---------------------------------------------------------------------------
# Seul le JRE est nécessaire ici : ni compilateur, ni Maven, ni code source
# ne sont embarqués dans l'image finale.
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Un conteneur qui tourne en root est un risque évitable : une évasion du
# conteneur hériterait de privilèges élevés. -S crée un compte système,
# sans mot de passe ni répertoire personnel.
RUN addgroup -S spring && adduser -S spring -G spring

# Nom fixe garanti par <finalName>api</finalName> dans le POM : la copie ne
# dépend ni du numéro de version, ni d'un glob susceptible de capturer un
# second jar présent dans target/.
COPY --from=builder /build/target/api.jar app.jar

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
