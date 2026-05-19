# Sistema de Locadora de Veículos

Projeto desenvolvido para a disciplina de Programação III.

## Descrição

O sistema é uma aplicação desktop feita em JavaFX para gerenciar uma locadora de veículos.

Ele permite cadastrar clientes, veículos e usuários, além de realizar locações e devoluções.

## Tecnologias utilizadas

- Java 21
- JavaFX
- Maven
- PostgreSQL
- JDBC
- BCrypt
- FXML
- CSS

## Funcionalidades

- Login com e-mail e senha
- Senhas criptografadas com BCrypt
- Cadastro, atualização, inativação e busca de clientes
- Cadastro, atualização e busca de veículos
- Cadastro, atualização, inativação e busca de usuários
- Realização de locações
- Registro de devoluções
- Cálculo de multa por atraso
- Controle de disponibilidade dos veículos
- Logs de uso
- Logs de erro

## Banco de dados

O sistema utiliza PostgreSQL.

Configuração usada no projeto:

```text
Banco: locadora_db
Usuário: postgres
Senha: 123456
Host: localhost
Porta: 5432
````
Para criar as tabelas e dados iniciais, execute o arquivo:
database.sql

## Como executar

Clone o projeto:

git clone https://github.com/jotatw/Trabalho_p3.git

Entre na pasta do projeto:
````
cd Trabalho_p3
````

Crie o banco no PostgreSQL:

````
CREATE DATABASE locadora_db;
````

Execute o script:
````
database.sql
````

Rode o projeto com Maven:
````
mvn clean javafx:run
````

## Estrutura do projeto

````
src/main/java
├── controller
├── service
├── dao
├── model
└── util
````

## Arquitetura

O projeto segue a divisão em camadas:
````
Controller → Service → DAO → Banco de Dados
````

## Logs

O sistema gera arquivos de log na pasta:
````
logs
````

Arquivos principais:
````
uso.log
erros.log
````

## Usuários de teste

Após executar o script do banco, utilize:
````
E-mail: admin@locadora.com
Senha: admin123
Perfil: ADMIN
````

````
E-mail: atendente@locadora.com
Senha: atendente123
Perfil: ATENDENTE
````
## Autor

jota _tw