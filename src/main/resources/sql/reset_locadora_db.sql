DROP TABLE IF EXISTS locacao CASCADE;
DROP TABLE IF EXISTS veiculo CASCADE;
DROP TABLE IF EXISTS cliente CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE usuario (
                         id SERIAL PRIMARY KEY,
                         nome VARCHAR(120) NOT NULL,
                         email VARCHAR(120) NOT NULL UNIQUE,
                         senha_hash VARCHAR(255) NOT NULL,
                         perfil VARCHAR(30) NOT NULL,
                         ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE cliente (
                         id SERIAL PRIMARY KEY,
                         nome VARCHAR(120) NOT NULL,
                         cpf VARCHAR(14) NOT NULL UNIQUE,
                         cnh VARCHAR(20) NOT NULL UNIQUE,
                         telefone VARCHAR(20),
                         email VARCHAR(120),
                         ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE veiculo (
                         id SERIAL PRIMARY KEY,
                         placa VARCHAR(10) NOT NULL UNIQUE,
                         modelo VARCHAR(80) NOT NULL,
                         marca VARCHAR(80) NOT NULL,
                         categoria VARCHAR(50) NOT NULL,
                         valor_diaria NUMERIC(10, 2) NOT NULL,
                         disponivel BOOLEAN NOT NULL DEFAULT TRUE
);

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

INSERT INTO usuario
(nome, email, senha_hash, perfil, ativo)
VALUES
    (
        'Administrador do Sistema',
        'admin@locadora.com',
        crypt('admin123', gen_salt('bf', 10)),
        'ADMIN',
        TRUE
    ),
    (
        'Atendente Principal',
        'atendente@locadora.com',
        crypt('atendente123', gen_salt('bf', 10)),
        'ATENDENTE',
        TRUE
    );

INSERT INTO cliente
(nome, cpf, cnh, telefone, email, ativo)
VALUES
    (
        'João Pedro Silva',
        '111.111.111-11',
        '11111111111',
        '(61) 99999-1111',
        'joao@email.com',
        TRUE
    ),
    (
        'Maria Oliveira Souza',
        '222.222.222-22',
        '22222222222',
        '(61) 99999-2222',
        'maria@email.com',
        TRUE
    ),
    (
        'Carlos Henrique Lima',
        '333.333.333-33',
        '33333333333',
        '(61) 99999-3333',
        'carlos@email.com',
        TRUE
    );

INSERT INTO veiculo
(placa, modelo, marca, categoria, valor_diaria, disponivel)
VALUES
    ('ABC1D23', 'Onix', 'Chevrolet', 'HATCH', 120.00, TRUE),
    ('DEF4G56', 'HB20', 'Hyundai', 'HATCH', 130.00, TRUE),
    ('GHI7J89', 'Corolla', 'Toyota', 'SEDAN', 220.00, TRUE),
    ('JKL0M12', 'Compass', 'Jeep', 'SUV', 280.00, FALSE),
    ('MNO3P45', 'Strada', 'Fiat', 'PICKUP', 190.00, TRUE);

INSERT INTO locacao
(cliente_id, veiculo_id, usuario_id, dt_retirada, dt_devolucao_prevista, dt_devolucao_real, valor_total, multas, status)
VALUES
    (
        1,
        4,
        2,
        CURRENT_DATE,
        CURRENT_DATE + INTERVAL '3 days',
        NULL,
        840.00,
        0.00,
        'ATIVA'
    ),
    (
        2,
        1,
        2,
        CURRENT_DATE - INTERVAL '10 days',
        CURRENT_DATE - INTERVAL '7 days',
        CURRENT_DATE - INTERVAL '7 days',
        360.00,
        0.00,
        'ENCERRADA'
    ),
    (
        3,
        3,
        2,
        CURRENT_DATE - INTERVAL '15 days',
        CURRENT_DATE - INTERVAL '12 days',
        CURRENT_DATE - INTERVAL '10 days',
        660.00,
        100.00,
        'ENCERRADA'
    );