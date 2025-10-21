# Parking Backend (Kotlin + Spring Boot)

Estrutura inicial do projeto para o desafio do simulador de garagem.

## Como usar

1. Abra o IntelliJ -> Import Project -> selecione este diretório (`parking-backend`) -> Import Gradle project.
2. Configure um MySQL local (ou altere `application.yml`) e execute o script SQL em `db/init.sql`.
3. Rode a aplicação `ParkingApplication.kt` (src/main/kotlin/com/empresa/parking/ParkingApplication.kt).
4. Inicie o simulador (ou use Postman) para enviar eventos para `/webhook`.



docker run -d \
--name mysql-parking-1 \
-e MYSQL_ROOT_PASSWORD=root \
-e MYSQL_DATABASE=parking \
-p 3307:3306 \
mysql:8.0


http://localhost:3003/swagger-ui/index.html


