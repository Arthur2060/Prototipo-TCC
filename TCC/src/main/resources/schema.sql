CREATE DATABASE IF NOT EXISTS inpark_db;
USE inpark_db;

CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    data_nascimento DATE NOT NULL,
    tipo_usuario VARCHAR(50),
    status BOOLEAN DEFAULT TRUE,
    role VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS cliente (
    id BIGINT PRIMARY KEY,
    CONSTRAINT FOREIGN KEY (id) REFERENCES usuario(id)
);

CREATE TABLE IF NOT EXISTS dono_estacionamento (
    id BIGINT PRIMARY KEY,
    CONSTRAINT FOREIGN KEY (id) REFERENCES usuario(id)
);

CREATE TABLE IF NOT EXISTS estacionamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    cep VARCHAR(20),
    numero VARCHAR(20),
    foto VARCHAR(255),
    numero_alvara_de_funcionamento VARCHAR(100) NOT NULL,
    funcionamento BOOLEAN DEFAULT TRUE,
    dono_id BIGINT NOT NULL,
    latitude DOUBLE,
    longitude DOUBLE,
    max_vagas INT NOT NULL,
    vagas_disponiveis INT ,
    vagas_preferenciais INT,
    hora_abertura TIME NOT NULL,
    hora_fechamento TIME NOT NULL,
    metodo_de_pagamento ENUM('PIX', 'DEBITO', 'DINHEIRO') default 'DINHEIRO',
    numero_conta_dono VARCHAR(100),
    dia_atual DATE,
    numero_de_escritura_imovel VARCHAR(100),
    valor_arrecadado_do_dia DECIMAL(10,2) DEFAULT 0,
    nota_media DECIMAL(3,2) DEFAULT 0,
    quantidade_de_avaliacoes INT DEFAULT 0,
    status BOOLEAN DEFAULT TRUE,
    CONSTRAINT FOREIGN KEY (dono_id) REFERENCES dono_estacionamento(id)
);

CREATE TABLE IF NOT EXISTS gerente (
    id BIGINT PRIMARY KEY,
    cpf_ou_cnpj VARCHAR(18) UNIQUE NOT NULL,
    estacionamento_id BIGINT,
    CONSTRAINT FOREIGN KEY (id) REFERENCES usuario(id),
    CONSTRAINT FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id)
);

CREATE TABLE IF NOT EXISTS valor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    preco DECIMAL(10,2) NOT NULL,
    tipo_cobranca ENUM('POR_VAGA', 'POR_HORA') NOT NULL,
    tipo_pagamento ENUM('PIX', 'DEBITO', 'DINHEIRO') NOT NULL,
    periodo ENUM('UTIL', 'FIM_DE_SEMANA') NOT NULL,
    status BOOLEAN DEFAULT TRUE,
    estacionamento_id BIGINT NOT NULL,
    CONSTRAINT FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id)
);

CREATE TABLE IF NOT EXISTS carro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    placa VARCHAR(7) UNIQUE NOT NULL,
    modelo VARCHAR(255) NOT NULL,
    cor VARCHAR(255) NOT NULL,
    status BOOLEAN DEFAULT TRUE,
    CONSTRAINT FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

CREATE TABLE IF NOT EXISTS acesso (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estacionamento_id BIGINT NOT NULL,
    carro_id BIGINT NOT NULL,
    hora_entrada TIME,
    hora_saida TIME,
    total_horas INT,
    valor_a_pagar DECIMAL(10,2),
    status BOOLEAN DEFAULT TRUE,
    CONSTRAINT FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id),
    CONSTRAINT FOREIGN KEY (carro_id) REFERENCES carro(id)
);

CREATE TABLE IF NOT EXISTS reserva (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    estacionamento_id BIGINT NOT NULL,
    data_reserva DATE,
    hora_reserva TIME,
    status_reserva ENUM('ACEITA', 'RECUSADA', 'PENDENTE', 'ENCERRADA') NOT NULL,
    status BOOLEAN DEFAULT TRUE,
    CONSTRAINT FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id)
);

CREATE TABLE IF NOT EXISTS avaliacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    estacionamento_id BIGINT NOT NULL,
    nota DECIMAL(3,2) NOT NULL,
    comentario VARCHAR(255),
    data_avaliacao DATETIME NOT NULL,
    status BOOLEAN DEFAULT TRUE,
    CONSTRAINT FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id)
);
