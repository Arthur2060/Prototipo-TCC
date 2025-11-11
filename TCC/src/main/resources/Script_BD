CREATE DATABASE IF NOT EXISTS prototipo_estacionamento;
USE prototipo_estacionamento;

-- =============================
-- TABELA USUÁRIO
-- =============================
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    data_nascimento DATE NOT NULL,
    tipo_usuario VARCHAR(50),
    status BOOLEAN DEFAULT TRUE,
    role VARCHAR(50)
);

-- =============================
-- TABELAS DERIVADAS DE USUÁRIO
-- =============================
CREATE TABLE cliente (
    id BIGINT PRIMARY KEY
);

CREATE TABLE dono_estacionamento (
    id BIGINT PRIMARY KEY
);

CREATE TABLE gerente (
    id BIGINT PRIMARY KEY,
    cpf_ou_cnpj VARCHAR(18) UNIQUE NOT NULL,
    estacionamento_id BIGINT
);

-- =============================
-- TABELA ESTACIONAMENTO
-- =============================
CREATE TABLE estacionamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    cep VARCHAR(20),
    numero VARCHAR(20),
    foto VARCHAR(255),
    numero_alvara_funcionamento VARCHAR(100) NOT NULL,
    funcionamento BOOLEAN DEFAULT TRUE,
    dono_id BIGINT NOT NULL,
    latitude DOUBLE,
    longitude DOUBLE,
    max_vagas INT NOT NULL,
    vagas_disponiveis INT NOT NULL,
    vagas_preferenciais INT,
    hora_abertura TIME NOT NULL,
    hora_fechamento TIME NOT NULL,
    numero_conta_dono VARCHAR(100),
    dia_atual DATE,
    numero_escritura_imovel VARCHAR(100),
    valor_arrecadado_dia DECIMAL(10,2) DEFAULT 0,
    nota_media DECIMAL(3,2) DEFAULT 0,
    quantidade_avaliacoes INT DEFAULT 0,
    status BOOLEAN DEFAULT TRUE
);

-- =============================
-- TABELAS AUXILIARES (ENUMS)
-- =============================
CREATE TABLE tipo_cobranca (
    id TINYINT PRIMARY KEY AUTO_INCREMENT,
    tipo_cobranca VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO tipo_cobranca (tipo_cobranca) VALUES
('POR_VAGA'), ('POR_HORA');

CREATE TABLE tipo_pagamento (
    id TINYINT PRIMARY KEY AUTO_INCREMENT,
    tipo_pagamento VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO tipo_pagamento (tipo_pagamento) VALUES
('PIX'), ('CARTAO');

CREATE TABLE periodo (
    id TINYINT PRIMARY KEY AUTO_INCREMENT,
    periodo VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO periodo (periodo) VALUES
('UTIL'), ('FIM_DE_SEMANA');

CREATE TABLE status_reserva (
    id TINYINT PRIMARY KEY AUTO_INCREMENT,
    status_reserva VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO status_reserva (status_reserva) VALUES
('ACEITA'), ('RECUSADA'), ('PENDENTE'), ('ENCERRADA');

-- =============================
-- TABELAS RELACIONADAS
-- =============================
CREATE TABLE valor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    preco DECIMAL(10,2) NOT NULL,
    tipo_cobranca_id TINYINT NOT NULL,
    tipo_pagamento_id TINYINT NOT NULL,
    periodo_id TINYINT NOT NULL,
    status BOOLEAN DEFAULT TRUE,
    estacionamento_id BIGINT NOT NULL
);

CREATE TABLE carro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    placa VARCHAR(7) UNIQUE NOT NULL,
    modelo VARCHAR(255) NOT NULL,
    cor VARCHAR(255) NOT NULL,
    status BOOLEAN DEFAULT TRUE
);

CREATE TABLE acesso (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estacionamento_id BIGINT NOT NULL,
    carro_id BIGINT NOT NULL,
    hora_entrada TIME,
    hora_saida TIME,
    total_horas INT,
    valor_a_pagar DECIMAL(10,2),
    status BOOLEAN DEFAULT TRUE
);

CREATE TABLE reserva (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    estacionamento_id BIGINT NOT NULL,
    data_reserva DATE,
    hora_reserva TIME,
    status_reserva_id TINYINT,
    status BOOLEAN DEFAULT TRUE
);

CREATE TABLE avaliacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    estacionamento_id BIGINT NOT NULL,
    nota DECIMAL(3,2) NOT NULL,
    comentario VARCHAR(255),
    data_avaliacao DATETIME NOT NULL,
    status BOOLEAN DEFAULT TRUE
);

CREATE TABLE metodos_pagamento_estacionamento (
    estacionamento_id BIGINT NOT NULL,
    tipo_pagamento_id TINYINT NOT NULL,
    PRIMARY KEY (estacionamento_id, tipo_pagamento_id)
);

-- =============================
-- FOREIGN KEYS
-- =============================
ALTER TABLE cliente 
    ADD CONSTRAINT fk_cliente_usuario FOREIGN KEY (id) REFERENCES usuario(id);

ALTER TABLE dono_estacionamento 
    ADD CONSTRAINT fk_dono_usuario FOREIGN KEY (id) REFERENCES usuario(id);

ALTER TABLE gerente 
    ADD CONSTRAINT fk_gerente_usuario FOREIGN KEY (id) REFERENCES usuario(id);

ALTER TABLE gerente 
    ADD CONSTRAINT fk_gerente_estacionamento FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id);

ALTER TABLE estacionamento 
    ADD CONSTRAINT fk_estacionamento_dono FOREIGN KEY (dono_id) REFERENCES dono_estacionamento(id);

ALTER TABLE valor 
    ADD CONSTRAINT fk_valor_estacionamento FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id),
    ADD CONSTRAINT fk_valor_tipo_cobranca FOREIGN KEY (tipo_cobranca_id) REFERENCES tipo_cobranca(id),
    ADD CONSTRAINT fk_valor_tipo_pagamento FOREIGN KEY (tipo_pagamento_id) REFERENCES tipo_pagamento(id),
    ADD CONSTRAINT fk_valor_periodo FOREIGN KEY (periodo_id) REFERENCES periodo(id);

ALTER TABLE carro 
    ADD CONSTRAINT fk_carro_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id);

ALTER TABLE acesso 
    ADD CONSTRAINT fk_acesso_estacionamento FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id),
    ADD CONSTRAINT fk_acesso_carro FOREIGN KEY (carro_id) REFERENCES carro(id);

ALTER TABLE reserva 
    ADD CONSTRAINT fk_reserva_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    ADD CONSTRAINT fk_reserva_estacionamento FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id),
    ADD CONSTRAINT fk_reserva_status FOREIGN KEY (status_reserva_id) REFERENCES status_reserva(id);

ALTER TABLE avaliacao 
    ADD CONSTRAINT fk_avaliacao_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    ADD CONSTRAINT fk_avaliacao_estacionamento FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id);

ALTER TABLE metodos_pagamento_estacionamento 
    ADD CONSTRAINT fk_metodo_estacionamento FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id),
    ADD CONSTRAINT fk_metodo_pagamento FOREIGN KEY (tipo_pagamento_id) REFERENCES tipo_pagamento(id);