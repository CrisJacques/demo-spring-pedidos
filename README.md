# API de Pedidos

![Java 25](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot 4.1.1](https://img.shields.io/badge/Spring_Boot-4.1.1-6DB33F?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Wrapper-C71A36?logo=apachemaven&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-supported-4169E1?logo=postgresql&logoColor=white)
![H2](https://img.shields.io/badge/H2-in--memory-blue)
![OpenAPI](https://img.shields.io/badge/OpenAPI-Swagger-85EA2D?logo=swagger&logoColor=black)
![JUnit 5](https://img.shields.io/badge/JUnit-5-25A162?logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/mocks-Mockito-78A641)
![RestAssured](https://img.shields.io/badge/API_tests-RestAssured-4B7BEC)
![JaCoCo](https://img.shields.io/badge/coverage-JaCoCo-orange)
![Checkstyle](https://img.shields.io/badge/code_style-Checkstyle-brightgreen)
![Lombok](https://img.shields.io/badge/boilerplate-Lombok-red)

API REST para gerenciamento de usuários, categorias, produtos e pedidos. O projeto demonstra uma aplicação organizada em camadas, validação de dados, documentação interativa da API e diferentes níveis de testes automatizados.

## Funcionalidades

- Operações de cadastro, consulta, atualização e remoção de usuários, categorias, produtos e pedidos.
- Criação e atualização de pedidos vinculados a usuários e produtos, com itens, quantidades e status.
- Validação de entradas e tratamento padronizado de erros.
- DTOs e mapeadores para estruturar as requisições e respostas de usuários e pedidos.
- Documentação dos endpoints, parâmetros e exemplos de respostas com Swagger UI / OpenAPI.

## Tecnologias

| Área | Tecnologias |
| --- | --- |
| Linguagem e build | Java 25, Maven e Maven Wrapper |
| Aplicação web | Spring Boot 4.1.1, Spring MVC |
| Persistência | Spring Data JPA, Hibernate, PostgreSQL e H2 |
| Validação | Jakarta Bean Validation |
| API e documentação | REST, DTOs, Springdoc OpenAPI e Swagger UI |
| Produtividade | Lombok |
| Testes | JUnit 5, Mockito, Spring Boot Test e RestAssured |
| Qualidade e cobertura | Checkstyle 14.3.0 e JaCoCo 0.8.14 |
| Execução de testes | Maven Surefire (unitários) e Maven Failsafe (integração e API) |

## Organização do projeto

O código da aplicação fica em `src/main/java/com/example/demospringpedidos` e está dividido em:

- `resources`: endpoints REST e tratamento de exceções HTTP.
- `services`: regras de negócio.
- `repositories`: acesso a dados com Spring Data JPA.
- `entities`: entidades persistidas e tipos de domínio.
- `dto`: objetos de transferência e mapeadores.
- `config`: configurações da aplicação e da documentação OpenAPI.

Os testes automatizados estão em `src/test/java` e incluem testes unitários, de integração e de API.

## Pré-requisitos

- JDK 25 instalado e disponível no `PATH`.
- Git, para clonar o repositório.
- Para a execução padrão: nenhum servidor de banco adicional; a aplicação usa H2 em memória.
- Para usar o perfil PostgreSQL: PostgreSQL instalado e em execução.

O Maven Wrapper incluído no repositório (`mvnw` / `mvnw.cmd`) baixa e utiliza a versão de Maven necessária, sem exigir uma instalação global.

## Como executar

Clone o repositório e acesse a pasta do projeto:

```bash
git clone https://github.com/CrisJacques/demo-spring-pedidos.git
cd demo-spring-pedidos
```

### Windows (PowerShell)

```powershell
.\mvnw.cmd spring-boot:run
```

### Linux ou macOS

```bash
./mvnw spring-boot:run
```

Por padrão, a aplicação seleciona o perfil `test`, que configura um banco H2 em memória. A API estará disponível em `http://localhost:8080`. Como o banco é temporário, os dados são descartados ao encerrar a aplicação.

### Usar PostgreSQL

O perfil `dev` está configurado para conectar a `jdbc:postgresql://localhost:5432/demo-spring-pedidos`. Crie o banco e configure usuário e senha locais em `src/main/resources/application-dev.properties` antes de iniciar; não versione credenciais pessoais.

Ative o perfil `dev` definindo `SPRING_PROFILES_ACTIVE=dev` no ambiente e execute o comando `spring-boot:run` acima. No PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev"
.\mvnw.cmd spring-boot:run
```

## API e documentação

Com a aplicação em execução:

- Swagger UI: [`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html)
- Especificação OpenAPI: [`http://localhost:8080/v3/api-docs`](http://localhost:8080/v3/api-docs)
- Console H2 (perfil padrão): [`http://localhost:8080/h2-console`](http://localhost:8080/h2-console)

Principais recursos:

| Recurso | Caminho | Operações |
| --- | --- | --- |
| Usuários | `/users` | `GET`, `POST`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}` |
| Categorias | `/categories` | `GET`, `POST`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}` |
| Produtos | `/products` | `GET`, `POST`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}` |
| Pedidos | `/orders` | `GET`, `POST`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}` |

Exemplo de consulta:

```bash
curl http://localhost:8080/products
```

Consulte o Swagger UI para ver os campos, exemplos de payload e respostas de cada operação.

## Testes e qualidade

Execute a suíte completa com:

```bash
./mvnw clean verify
```

No Windows, use `.\mvnw.cmd clean verify`. Esse ciclo executa o Checkstyle, os testes unitários, os testes de integração e de API, e verifica a cobertura de linhas dos testes unitários — com mínimo configurado de 80%.

Para executar somente os testes unitários:

```bash
./mvnw test
```

Após `verify`, o relatório HTML de cobertura do JaCoCo fica em `target/site/jacoco/index.html`.
