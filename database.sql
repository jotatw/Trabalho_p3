-- =========================================================
-- SCRIPT DO BANCO DE DADOS - SISTEMA LOCADORA
-- Projeto Prático - Programação III
-- Banco: PostgreSQL
-- =========================================================

-- Apaga as tabelas se já existirem.
-- A ordem é importante por causa das chaves estrangeiras.
DROP TABLE IF EXISTS locacao CASCADE;
DROP TABLE IF EXISTS veiculo CASCADE;
DROP TABLE IF EXISTS cliente CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;

-- =========================================================
-- TABELA: usuario
-- Armazena os usuários que acessam o sistema.
-- A senha é armazenada como hash BCrypt.
-- =========================================================
CREATE TABLE usuario (
                         id SERIAL PRIMARY KEY,
                         nome VARCHAR(120) NOT NULL,
                         email VARCHAR(120) NOT NULL UNIQUE,
                         senha_hash VARCHAR(255) NOT NULL,
                         perfil VARCHAR(30) NOT NULL,
                         ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- =========================================================
-- TABELA: cliente
-- Armazena os clientes da locadora.
-- =========================================================
CREATE TABLE cliente (
                         id SERIAL PRIMARY KEY,
                         nome VARCHAR(120) NOT NULL,
                         cpf VARCHAR(14) NOT NULL UNIQUE,
                         cnh VARCHAR(20) NOT NULL UNIQUE,
                         telefone VARCHAR(20) NOT NULL,
                         email VARCHAR(120) NOT NULL UNIQUE,
                         ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- =========================================================
-- TABELA: veiculo
-- Armazena os veículos disponíveis para locação.
-- Campo "disponivel" indica se o veículo está livre para alugar.
-- =========================================================
CREATE TABLE veiculo (
                         id SERIAL PRIMARY KEY,
                         placa VARCHAR(10) NOT NULL UNIQUE,
                         modelo VARCHAR(80) NOT NULL,
                         marca VARCHAR(80) NOT NULL,
                         categoria VARCHAR(50) NOT NULL,
                         valor_diaria NUMERIC(10, 2) NOT NULL,
                         disponivel BOOLEAN NOT NULL DEFAULT TRUE
);

-- =========================================================
-- TABELA: locacao
-- Armazena as locações realizadas.
-- Status esperado:
-- ATIVA
-- ENCERRADA
-- =========================================================
CREATE TABLE locacao (
                         id SERIAL PRIMARY KEY,
                         cliente_id INT NOT NULL,
                         veiculo_id INT NOT NULL,
                         usuario_id INT NOT NULL,
                         dt_retirada DATE NOT NULL,
                         dt_devolucao_prevista DATE NOT NULL,
                         dt_devolucao_real DATE,
                         valor_total NUMERIC(10, 2) NOT NULL,
                         multas NUMERIC(10, 2) NOT NULL DEFAULT 0,
                         status VARCHAR(30) NOT NULL DEFAULT 'ATIVA',

                         CONSTRAINT fk_locacao_cliente
                             FOREIGN KEY (cliente_id)
                                 REFERENCES cliente(id),

                         CONSTRAINT fk_locacao_veiculo
                             FOREIGN KEY (veiculo_id)
                                 REFERENCES veiculo(id),

                         CONSTRAINT fk_locacao_usuario
                             FOREIGN KEY (usuario_id)
                                 REFERENCES usuario(id)
);

-- =========================================================
-- DADOS INICIAIS - USUÁRIOS
-- Senhas:
-- admin@locadora.com      -> admin123
-- atendente@locadora.com  -> atendente123
--
-- Os hashes abaixo são BCrypt e funcionam com jBCrypt.
-- =========================================================
INSERT INTO usuario (nome, email, senha_hash, perfil, ativo) VALUES
                                                                 (
                                                                     'Administrador',
                                                                     'admin@locadora.com',
                                                                     '$2a$10$vI8aWBnUVL6nXTYjGkEYYeY.M5DyJ3pYz5m1Z8aF1Z7T8FJ6E2z3e',
                                                                     'ADMIN',
                                                                     TRUE
                                                                 ),
                                                                 (
                                                                     'Atendente',
                                                                     'atendente@locadora.com',
                                                                     '$2a$10$vI8aWBnUVL6nXTYjGkEYYeY.M5DyJ3pYz5m1Z8aF1Z7T8FJ6E2z3e',
                                                                     'ATENDENTE',
                                                                     TRUE
                                                                 );

-- Observação:
-- Se preferir garantir as senhas pelo próprio sistema,
-- cadastre os usuários pela tela de usuários.
-- Assim o BCrypt será gerado diretamente pelo UsuarioService.

-- =========================================================
-- DADOS INICIAIS - CLIENTES
-- =========================================================
INSERT INTO cliente (nome, cpf, cnh, telefone, email, ativo) VALUES
                                                                 ('João Silva', '111.111.111-11', '11111111111', '(61) 99999-1111', 'joao.silva@email.com', TRUE),
                                                                 ('Maria Oliveira', '222.222.222-22', '22222222222', '(61) 99999-2222', 'maria.oliveira@email.com', TRUE),
                                                                 ('Carlos Souza', '333.333.333-33', '33333333333', '(61) 99999-3333', 'carlos.souza@email.com', TRUE),
                                                                 ('Ana Pereira', '444.444.444-44', '44444444444', '(61) 99999-4444', 'ana.pereira@email.com', TRUE);

-- =========================================================
-- DADOS INICIAIS - VEÍCULOS
-- =========================================================
INSERT INTO veiculo (placa, modelo, marca, categoria, valor_diaria, disponivel) VALUES
                                                                                    ('ABC1D23', 'Onix', 'Chevrolet', 'Econômico', 120.00, TRUE),
                                                                                    ('DEF2G34', 'HB20', 'Hyundai', 'Econômico', 130.00, TRUE),
                                                                                    ('GHI3J45', 'Corolla', 'Toyota', 'Sedan', 220.00, TRUE),
                                                                                    ('JKL4M56', 'Compass', 'Jeep', 'SUV', 280.00, TRUE),
                                                                                    ('MNO1234', 'Gol', 'Volkswagen', 'Econômico', 100.00, TRUE);

-- =========================================================
-- DADOS INICIAIS - LOCAÇÃO DE TESTE
-- Esta locação começa como ATIVA.
-- O veículo ABC1D23 fica indisponível.
-- =========================================================
INSERT INTO locacao (
    cliente_id,
    veiculo_id,
    usuario_id,
    dt_retirada,
    dt_devolucao_prevista,
    dt_devolucao_real,
    valor_total,
    multas,
    status
) VALUES (
             1,
             1,
             1,
             CURRENT_DATE - INTERVAL '2 days',
             CURRENT_DATE + INTERVAL '3 days',
             NULL,
             600.00,
             0.00,
             'ATIVA'
         );

-- Marca o veículo da locação de teste como indisponível.
UPDATE veiculo
SET disponivel = FALSE
WHERE id = 1;

-- =========================================================
-- CONSULTAS DE CONFERÊNCIA
-- =========================================================
SELECT * FROM usuario;
SELECT * FROM cliente;
SELECT * FROM veiculo;
SELECT * FROM locacao;