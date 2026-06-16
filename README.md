# Sistema de Locadora de Veículos

Projeto desktop desenvolvido para a disciplina de **Programação III** do **IFG - Luziânia**.

O sistema tem como objetivo gerenciar uma locadora de veículos, permitindo o controle de clientes, veículos, usuários, locações, devoluções, autenticação e registros de auditoria.

---

## Descrição

A aplicação foi desenvolvida em **Java 21** com **JavaFX**, utilizando persistência em banco de dados **PostgreSQL** por meio da API **JDBC**.

O projeto segue uma organização em camadas, separando interface, regras de negócio e acesso ao banco de dados.

Fluxo principal da aplicação:

```text
Controller → Service → DAO → Banco de Dados
```

---

## Tecnologias utilizadas

- Java 21
- JavaFX
- FXML
- CSS
- Maven
- PostgreSQL
- JDBC
- BCrypt
- ControlsFX

---

## Funcionalidades

### Autenticação e usuários

- Login com e-mail e senha
- Senhas armazenadas com hash BCrypt
- Controle de sessão do usuário autenticado
- Controle de perfis de acesso:
    - ADMIN
    - ATENDENTE
- Cadastro, atualização, busca e inativação de usuários

### Clientes

- Cadastro de clientes
- Atualização de dados
- Busca por informações do cliente
- Inativação de clientes
- Validações de CPF, telefone e e-mail

### Veículos

- Cadastro de veículos
- Atualização de veículos
- Busca por placa, modelo, marca, categoria ou status
- Controle de disponibilidade
- Validação de placa
- Controle de valor da diária

### Locação e devolução

- Realização de locações
- Registro de devoluções
- Cálculo automático do valor da locação
- Cálculo de multa por atraso
- Atualização automática da disponibilidade do veículo

### Sistema

- Dashboard com resumo do sistema
- Tabela de locações ativas
- Logs de uso e auditoria
- Logs de erro
- Uso de transações em operações críticas

---

## Arquitetura do projeto

O sistema foi organizado em camadas:

```text
controller  → controla os eventos das telas JavaFX
service     → concentra as regras de negócio
dao         → realiza o acesso ao banco de dados
model       → representa as entidades do sistema
util        → contém classes auxiliares, conexão, sessão, logs e validações
resources   → contém arquivos FXML, CSS e configurações
```

Estrutura principal:

```text
src/main/java/com/br/ifg/luziania/trabalho_p3
├── controller
├── dao
├── model
├── service
└── util

src/main/resources
├── css
├── fxml
└── database.properties
```

---

## Banco de dados

O sistema utiliza **PostgreSQL**.

A conexão com o banco é configurada pelo arquivo:

```text
src/main/resources/database.properties
```

Exemplo de configuração:

```properties
db.driver="seu_banco_de_dados"
db.host="seu_host"
db.port="sua_porta"
db.name="nome_da_sua_tabela"
db.user="seu_usuario"
db.password="sua_senha"
```

Esse arquivo centraliza os dados de conexão com o banco, evitando que URL, usuário e senha fiquem espalhados pelo código Java.

### E necessaria inserir as informações do seu banco de dados!

---

## Criação do banco de dados

Crie o banco no PostgreSQL:

```bash
createdb -U postgres locadora_db
```

Depois execute o script SQL do projeto:

```bash
psql -U postgres -d locadora_db -f database.sql
```

O arquivo `database.sql` cria as tabelas principais e insere dados iniciais para teste.

---

## Tabelas principais

O banco possui as seguintes tabelas:

```text
usuario
cliente
veiculo
locacao
```

### Resumo das tabelas

| Tabela    | Finalidade                                    |
|-----------|-----------------------------------------------|
| `usuario` | Armazena os usuários que acessam o sistema    |
| `cliente` | Armazena os clientes da locadora              |
| `veiculo` | Armazena os veículos disponíveis para locação |
| `locacao` | Armazena as locações realizadas e devoluções  |

---

## Restrições do banco

O script do banco possui restrições de integridade, como:

- E-mail único
- CPF único
- CNH única
- Placa única
- Controle de perfil do usuário
- Controle de status da locação
- Validação de valores positivos
- Validação de datas da locação
- Chaves estrangeiras entre locação, cliente, veículo e usuário

---

## Usuários iniciais

Após executar o script do banco, utilize os seguintes usuários:

### Administrador

```text
E-mail: admin@locadora.com
Senha: admin123
Perfil: ADMIN
```

### Atendente

```text
E-mail: atendente@locadora.com
Senha: atendente123
Perfil: ATENDENTE
```

---

## Como executar o projeto

Clone o repositório:

```bash
git clone https://github.com/jotatw/Trabalho_p3.git
```

Entre na pasta do projeto:

```bash
cd Trabalho_p3
```

Configure o arquivo de conexão:

```text
src/main/resources/database.properties
```

Crie o banco e execute o script:

```bash
createdb -U postgres locadora_db
psql -U postgres -d locadora_db -f database.sql
```

Execute o projeto com Maven Wrapper:

```bash
./mvnw clean javafx:run
```

Ou, se estiver usando Maven instalado no sistema:

```bash
mvn clean javafx:run
```

Também é possível executar pela IDE, rodando a classe principal:

```text
LocadoraApplication.java
```

---

## Classe principal

A classe principal da aplicação é:

```text
com.br.ifg.luziania.trabalho_p3.LocadoraApplication
```

Ela inicia a aplicação JavaFX e carrega a tela de login.

---

## Logs

O sistema gera arquivos de log na pasta:

```text
logs
```

Arquivos principais:

```text
uso.log
erros.log
```

### `uso.log`

Registra ações executadas pelos usuários, como:

- Login
- Cadastro
- Atualização
- Inativação
- Locação
- Devolução

### `erros.log`

Registra exceções e falhas ocorridas durante a execução do sistema.

---

## Boas práticas aplicadas

O projeto aplica boas práticas estudadas na disciplina, como:

- Uso de `PreparedStatement`
- Proteção contra SQL Injection
- Fechamento de recursos com `try-with-resources`
- Separação da lógica de banco em classes DAO
- Tratamento e propagação de `SQLException`
- Configuração centralizada da conexão com o banco
- Uso de BCrypt para senhas
- Uso de transações com `commit()` e `rollback()`
- Separação entre interface, regra de negócio e persistência

---

## Transações

As operações de locação e devolução utilizam transações para manter a consistência dos dados.

### Locação

Na locação, o sistema executa em uma única transação:

```text
1. Inserir a locação.
2. Marcar o veículo como indisponível.
```

Se alguma etapa falhar, a transação é desfeita com `rollback()`.

### Devolução

Na devolução, o sistema executa em uma única transação:

```text
1. Encerrar a locação.
2. Marcar o veículo como disponível.
```

Se alguma etapa falhar, a transação é desfeita com `rollback()`.

---

## Observações

- As senhas dos usuários são armazenadas como hash BCrypt.
- O arquivo `database.properties` contém as configurações locais de conexão.
- Para outro ambiente, basta alterar o host, porta, banco, usuário e senha nesse arquivo.
- O banco PostgreSQL deve estar ativo antes de executar o sistema.
- O projeto foi desenvolvido para fins acadêmicos.

---

## Autor

jotatw