# parking-backend

> **Resumo**: Este README descreve passo-a-passo como executar a aplicação `parking-backend` (Kotlin + Spring Boot) localmente usando MySQL (diretamente ou via Docker), como iniciar o simulador de garagem e como testar a API (Swagger / Postman).

---

## Requisitos

* Java 21
* Kotlin 2.1.0
* Spring-boot 3.2.2
* IntelliJ IDEA (Community ou Ultimate)
* Maven 3.8+ (o projeto usa Maven)
* Docker (opcional, só se quiser rodar o MySQL em container)
* MySQL local (ou container) — a aplicação usa MySQL por padrão
* Curl ou Postman para testes

---

## Estrutura do projeto

* `src/main/kotlin` — código Kotlin da aplicação
* `src/main/resources/application.yml` — configuração do Spring Boot
* `db/init.sql` — script SQL inicial (criação de tabelas / dados básicos)
* `pom.xml` — build Maven

---

## Passo-a-passo — Opção A: Usando MySQL via Docker (recomendado)

1. Crie e execute um container MySQL:

```bash
docker run -d \
  --name mysql-parking-1 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=parking \
  -p 3307:3306 \
  mysql:8.0
```

> Observação: aqui o porto 3307 (host) é mapeado para 3306 (container). Se preferir, mapeie para 3306 host.

2. Aguarde o container ficar pronto. Em seguida copie o `init.sql` para dentro do container e execute-o (ou execute a partir do host usando cliente `mysql`):

```bash
# copiar para o container
docker cp db/init.sql mysql-parking-1:/init.sql

# executar o script dentro do container (usuário root, senha 'root')
docker exec -i mysql-parking-1 mysql -u root -proot parking < /init.sql
```

3. Verifique os logs do MySQL (opcional):

```bash
docker logs -f --tail 50 mysql-parking-1
```

---

## Passo-a-passo — Opção B: Usando MySQL local (instalado no seu sistema)

1. Instale e configure MySQL.
2. Crie o banco `parking` e execute o script `db/init.sql` (por exemplo pelo MySQL Workbench, DBeaver ou CLI):

```sql
CREATE DATABASE parking;
USE parking;
-- rode o conteúdo de db/init.sql
```

3. Ajuste `src/main/resources/application.yml` caso as credenciais ou host/porta sejam diferentes.

---

## Configurar `application.yml`

O arquivo `application.yml` já contém uma configuração padrão. Verifique e ajuste se necessário:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/parking?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: none # ou validate
    show-sql: true

server:
  port: 3003
```

* Se você usou o container com mapeamento `-p 3307:3306`, mantenha a porta `3307` em `url`.
* `allowPublicKeyRetrieval=true` pode ser necessário para conexões com MySQL 8 dependendo do driver.

---

## Executando a aplicação

1. Abra o IntelliJ IDEA.
2. `Import Project` -> selecione o diretório do projeto -> importe como **Maven project**.
3. Aguarde a indexação e resolução das dependências.
4. Rode a classe `ParkingApplication.kt` (`src/main/kotlin/com/empresa/parking/ParkingApplication.kt`).

Alternativamente, pela linha de comando:

```bash
# build
mvn clean install

# rodar
mvn spring-boot:run
```

A aplicação, por padrão, será executada em `http://localhost:3003` (se `server.port` = 3003).

---

## Testando a API (Swagger + Postman)

* Swagger UI: `http://localhost:3003/swagger-ui/index.html`
* Endpoint de webhook (onde o simulador envia eventos): `POST /webhook`

Teste com `curl` (exemplo simples):

```bash
curl -X POST 'http://localhost:3003/webhook' \
  -H 'Content-Type: application/json' \
  -d '{"license_plate":"XIX08003","event_type":"ENTRY","entry_time":"2025-10-21T13:52:06"}'
```

* Endpoint de revenue (Consulta faturamento): `GET /revenue`

Teste com `curl` (exemplo simples):
```bash
curl -X 'GET' \
  'http://localhost:3003/revenue?date=2025-10-21&sector=A' \
  -H 'accept: */*'
```

---

## Iniciar o simulador de garagem (Docker)

Foi usado o container `cfontes0estapar/garage-sim:1.0.0` como exemplo. Execute:

```bash
docker run -d --network="host" cfontes0estapar/garage-sim:1.0.0
```

* O simulador deve enviar requisições para `localhost` na porta configurada (3000). Se o simulador estiver em outro host/container, ajuste `application.yml` ou as configurações do simulador.
* Caso não queira usar `--network host`, exponha a porta do container do simulador e ajuste endpoints.

Para monitorar os logs do container do simulador:

```bash
docker logs -f --tail 20 <container-id-ou-nome>
```

---

## Dicas de troubleshooting

* **`Public Key Retrieval is not allowed`**: adicione `allowPublicKeyRetrieval=true` na `jdbc url` (como no exemplo acima) ou configure o conector corretamente.
* **Conexão recusada**: verifique se o MySQL está rodando e se porta/host/usuário/senha batem.
* **Erro de versão do driver**: assegure-se de usar uma versão compatível do conector MySQL no `pom.xml`.
* **Tabela não encontrada**: confirme que `db/init.sql` foi executado contra o banco correto.

---

## Build para produção / executar JAR

```bash
mvn clean package
java -jar target/parking-backend-0.0.1-SNAPSHOT.jar --spring.config.location=classpath:/application.yml
```
---

## Testes

Para rodar os testes automatizados no projeto, rode:

```bash
mvn test

```
