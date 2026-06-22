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

### Logs e auditoria

- Geração de logs de uso
- Geração de logs de erro
- Registro das principais ações executadas pelos usuários
- Tela administrativa para visualização dos logs de uso
- Filtro de logs por data, ação, usuário ou detalhes
- Acesso à tela de logs restrito ao perfil ADMIN

### Sistema

- Dashboard com resumo do sistema
- Tabela de locações ativas
- Uso de transações em operações críticas
- Organização visual com CSS centralizado

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

O arquivo `database.properties` deve ser criado a partir do modelo `database.properties-exemplo`, localizado na raiz do projeto.

---

## Banco de dados

O sistema utiliza **PostgreSQL** para armazenar os dados da aplicação.

A conexão com o banco é feita por meio do arquivo:

```text
src/main/resources/database.properties
```

Esse arquivo contém as informações necessárias para o Java se conectar ao PostgreSQL, como tipo do banco, endereço do servidor, porta, nome do banco, usuário e senha.

### Arquivo de exemplo

O projeto possui um arquivo de exemplo na raiz:

```text
database.properties-exemplo
```

Esse arquivo serve como modelo para criar o arquivo real de configuração.

Exemplo de conteúdo:

```properties
# Tipo de banco usado pelo sistema
db.driver=postgresql

# Endereço do servidor do banco
db.host=localhost

# Porta do PostgreSQL
db.port=5432

# Nome do banco de dados
db.name=locadora_db

# Usuário do PostgreSQL
db.user=seu_usuario

# Senha do usuário do PostgreSQL
db.password=sua_senha
```

### Como configurar o arquivo

Para configurar a conexão com o banco de dados:

```text
1. Copie o arquivo database.properties-exemplo.
2. Renomeie a cópia para database.properties.
3. Edite o arquivo database.properties com os dados do seu PostgreSQL.
4. Coloque o arquivo final dentro da pasta src/main/resources.
```

O caminho final deve ficar assim:

```text
src/main/resources/database.properties
```

### Explicação dos campos

| Campo         | Explicação                                | Exemplo       |
|---------------|-------------------------------------------|---------------|
| `db.driver`   | Tipo do banco de dados usado pelo sistema | `postgresql`  |
| `db.host`     | Endereço onde o banco está rodando        | `localhost`   |
| `db.port`     | Porta usada pelo PostgreSQL               | `5432`        |
| `db.name`     | Nome do banco de dados da aplicação       | `locadora_db` |
| `db.user`     | Usuário do PostgreSQL                     | `postgres`    |
| `db.password` | Senha do usuário do PostgreSQL            | `sua_senha`   |

### Observações importantes

O arquivo `database.properties` **não deve usar aspas nos valores**.

Correto:

```properties
db.user=postgres
db.password=00000
```

Incorreto:

```properties
db.user="postgres"
db.password="00000"
```

O banco informado em `db.name` precisa existir antes de executar o sistema.

Exemplo de criação do banco:

```bash
createdb -U postgres locadora_db
```

Depois de criar o banco, execute o script SQL do projeto:

```bash
psql -U postgres -d locadora_db -f database.sql
```

Em um projeto real, o ideal é versionar apenas o arquivo `database.properties-exemplo`, pois o arquivo `database.properties` pode conter usuário e senha locais do banco de dados.

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

## Logs e auditoria

O sistema registra logs para auditoria e acompanhamento das ações executadas pelos usuários.

Os arquivos de log são gerados automaticamente na pasta:

```text
logs
```

Arquivos principais:

```text
uso.log
erros.log
```

### `uso.log`

Registra ações executadas no sistema, como:

- Login
- Logout
- Cadastro de clientes
- Atualização de clientes
- Inativação de clientes
- Cadastro e atualização de veículos
- Cadastro, atualização e inativação de usuários
- Realização de locações
- Registro de devoluções
- Tentativas de ações inválidas

Cada registro contém:

```text
data/hora
ação executada
usuário responsável
detalhes da operação
```

Exemplo de linha de log:

```text
[27/05/2026 20:15:33] ACAO='LOCACAO_REALIZADA' USUARIO='admin@locadora.com' DETALHES='CPF=000.000.000-00, PLACA=ABC1D23, DIAS=3, VALOR_TOTAL=450.0'
```

### Tela de logs

Além dos arquivos físicos, o sistema possui uma tela própria para visualização dos logs de uso.

A tela **Logs de Uso do Sistema** permite:

- Visualizar registros do arquivo `uso.log`
- Filtrar logs por data, ação, usuário ou detalhes
- Selecionar um registro e ver a linha completa
- Atualizar a listagem durante a execução do sistema

O acesso à tela de logs é feito pelo menu lateral da Home e fica disponível apenas para usuários com perfil **ADMIN**.

### `erros.log`

Registra exceções e falhas ocorridas durante a execução do sistema.

Esse arquivo auxilia na identificação de problemas internos, como falhas de banco de dados, erros de leitura de arquivos ou exceções inesperadas.

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
- Registro de auditoria das ações executadas pelos usuários
- Tela administrativa para consulta dos logs de uso
- Separação entre geração dos logs e visualização dos logs
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
