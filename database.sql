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
                         ativo BOOLEAN NOT NULL DEFAULT TRUE,

                         CONSTRAINT chk_usuario_perfil
                             CHECK (perfil IN ('ADMIN', 'ATENDENTE')),

                         CONSTRAINT chk_usuario_email_minusculo
                             CHECK (email = LOWER(email))
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
                         ativo BOOLEAN NOT NULL DEFAULT TRUE,

                         CONSTRAINT chk_cliente_email_minusculo
                             CHECK (email = LOWER(email))
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
                         disponivel BOOLEAN NOT NULL DEFAULT TRUE,

                         CONSTRAINT chk_veiculo_valor_diaria
                             CHECK (valor_diaria > 0),

                         CONSTRAINT chk_veiculo_categoria
                             CHECK (categoria IN (
                                                  'Econômico',
                                                  'Hatch',
                                                  'Sedan',
                                                  'SUV',
                                                  'Picape',
                                                  'Luxo',
                                                  'Van'
                                 ))
);

-- =========================================================
-- TABELA: locacao
-- Armazena as locações realizadas.
-- Uma locação ATIVA indica veículo em uso.
-- Uma locação ENCERRADA indica veículo devolvido.
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
                                 REFERENCES usuario(id),

                         CONSTRAINT chk_locacao_status
                             CHECK (status IN ('ATIVA', 'ENCERRADA')),

                         CONSTRAINT chk_locacao_datas
                             CHECK (dt_devolucao_prevista > dt_retirada),

                         CONSTRAINT chk_locacao_valor_total
                             CHECK (valor_total > 0),

                         CONSTRAINT chk_locacao_multas
                             CHECK (multas >= 0),

                         CONSTRAINT chk_locacao_devolucao_real
                             CHECK (
                                 dt_devolucao_real IS NULL
                                     OR dt_devolucao_real >= dt_retirada
                                 )
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
                                                                     LOWER('admin@locadora.com'),
                                                                     '$2a$10$JCFaGvHbCWfcNePhC6ufi.pVOZM9KxzMO9dSJOfmAwrNWA63vz4Fu',
                                                                     'ADMIN',
                                                                     TRUE
                                                                 ),
                                                                 (
                                                                     'Atendente',
                                                                     LOWER('atendente@locadora.com'),
                                                                     '$2a$10$1Dc3339GTRipp6hGBpZMtOd.WBs327oI0c8Yfw7Eicz.Zkny7GSl6',
                                                                     'ATENDENTE',
                                                                     TRUE
                                                                 );

-- =========================================================
-- DADOS INICIAIS - CLIENTES
-- =========================================================
INSERT INTO cliente (nome, cpf, cnh, telefone, email, ativo) VALUES
                                                                 ('João Silva', '111.111.111-11', '11111111111', '(61) 99999-1111', LOWER('joao.silva@email.com'), TRUE),
                                                                 ('Maria Oliveira', '222.222.222-22', '22222222222', '(61) 99999-2222', LOWER('maria.oliveira@email.com'), TRUE),
                                                                 ('Carlos Souza', '333.333.333-33', '33333333333', '(61) 99999-3333', LOWER('carlos.souza@email.com'), TRUE),
                                                                 ('Ana Pereira', '444.444.444-44', '44444444444', '(61) 99999-4444', LOWER('ana.pereira@email.com'), TRUE);

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
-- Execute manualmente no psql se quiser verificar os dados:
--
-- SELECT * FROM usuario;
-- SELECT * FROM cliente;
-- SELECT * FROM veiculo;
-- SELECT * FROM locacao;
-- =========================================================SELECT * FROM locacao;