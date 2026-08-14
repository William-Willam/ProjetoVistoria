-- ==========================================
-- V1 - Schema inicial AutoVistor
-- ==========================================

CREATE TABLE cliente (
                         id_cliente     BIGINT PRIMARY KEY AUTO_INCREMENT,
                         nome           VARCHAR(100) NOT NULL,
                         cpf            VARCHAR(14) NOT NULL UNIQUE,
                         telefone       VARCHAR(20) NOT NULL,
                         email          VARCHAR(100) NOT NULL UNIQUE,
                         senha_hash     VARCHAR(255) NOT NULL,
                         criado_em      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE funcionario (
                             id_funcionario BIGINT PRIMARY KEY AUTO_INCREMENT,
                             nome           VARCHAR(100) NOT NULL,
                             email          VARCHAR(100) NOT NULL UNIQUE,
                             matricula      VARCHAR(20) NOT NULL UNIQUE,
                             senha_hash     VARCHAR(255) NOT NULL,
                             cargo          ENUM('VISTORIADOR','GERENTE') NOT NULL,
                             ativo          BOOLEAN NOT NULL DEFAULT TRUE,
                             criado_em      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE veiculo (
                         id_veiculo     BIGINT PRIMARY KEY AUTO_INCREMENT,
                         placa          VARCHAR(8) NOT NULL UNIQUE,
                         tipo_veiculo   VARCHAR(20) NOT NULL,
                         nome_veiculo   VARCHAR(100) NOT NULL,
                         modelo         VARCHAR(100) NOT NULL,
                         ano_veiculo    YEAR NOT NULL,
                         chassi         VARCHAR(17) NOT NULL UNIQUE,
                         observacoes    TEXT,
                         id_cliente     BIGINT NOT NULL,
                         CONSTRAINT fk_veiculo_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE agendamento (
                             id_agendamento     BIGINT PRIMARY KEY AUTO_INCREMENT,
                             data_agendamento   DATE NOT NULL,
                             hora               TIME NOT NULL,
                             tipo_vistoria      ENUM('TRANSFERENCIA','CAUTELAR','PREVIA') NOT NULL,
                             status_agendamento ENUM('PENDENTE','CONFIRMADO','CONCLUIDO','CANCELADO','REAGENDADO') NOT NULL DEFAULT 'PENDENTE',
                             id_cliente         BIGINT NOT NULL,
                             id_veiculo         BIGINT NOT NULL,
                             id_funcionario     BIGINT NULL,
                             CONSTRAINT fk_agendamento_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
                             CONSTRAINT fk_agendamento_veiculo FOREIGN KEY (id_veiculo) REFERENCES veiculo(id_veiculo),
                             CONSTRAINT fk_agendamento_funcionario FOREIGN KEY (id_funcionario) REFERENCES funcionario(id_funcionario),
                             CONSTRAINT uq_horario_funcionario UNIQUE (data_agendamento, hora, id_funcionario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE vistoria (
                          id_vistoria     BIGINT PRIMARY KEY AUTO_INCREMENT,
                          data_vistoria   DATE NOT NULL,
                          resultado       ENUM('APROVADO','REPROVADO','APROVADO_COM_RESSALVAS') NOT NULL,
                          observacoes     TEXT,
                          id_agendamento  BIGINT NOT NULL UNIQUE,
                          id_funcionario  BIGINT NOT NULL,
                          CONSTRAINT fk_vistoria_agendamento FOREIGN KEY (id_agendamento) REFERENCES agendamento(id_agendamento),
                          CONSTRAINT fk_vistoria_funcionario FOREIGN KEY (id_funcionario) REFERENCES funcionario(id_funcionario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE pagamento (
                           id_pagamento     BIGINT PRIMARY KEY AUTO_INCREMENT,
                           forma_pagamento  ENUM('DEBITO','CREDITO','PIX','BOLETO','DINHEIRO') NOT NULL,
                           status_pagamento ENUM('PENDENTE','PAGO','RECUSADO') NOT NULL DEFAULT 'PENDENTE',
                           valor            DECIMAL(10,2) NOT NULL,
                           data_pagamento   DATE,
                           id_vistoria      BIGINT NOT NULL UNIQUE,
                           CONSTRAINT fk_pagamento_vistoria FOREIGN KEY (id_vistoria) REFERENCES vistoria(id_vistoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE boleto (
                        id_boleto        BIGINT PRIMARY KEY AUTO_INCREMENT,
                        codigo_barras    VARCHAR(60) NOT NULL,
                        data_vencimento  DATE NOT NULL,
                        status           ENUM('EMITIDO','PAGO','VENCIDO') NOT NULL DEFAULT 'EMITIDO',
                        id_pagamento     BIGINT NOT NULL UNIQUE,
                        CONSTRAINT fk_boleto_pagamento FOREIGN KEY (id_pagamento) REFERENCES pagamento(id_pagamento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE nota_fiscal (
                             id_nota_fiscal  BIGINT PRIMARY KEY AUTO_INCREMENT,
                             numero          VARCHAR(30) NOT NULL UNIQUE,
                             data_emissao    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             id_pagamento    BIGINT NOT NULL UNIQUE,
                             CONSTRAINT fk_nf_pagamento FOREIGN KEY (id_pagamento) REFERENCES pagamento(id_pagamento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE lancamento_caixa (
                                  id_lancamento   BIGINT PRIMARY KEY AUTO_INCREMENT,
                                  tipo            ENUM('ENTRADA','SAIDA') NOT NULL,
                                  valor           DECIMAL(10,2) NOT NULL,
                                  descricao       VARCHAR(255),
                                  data_lancamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  id_pagamento    BIGINT NULL,
                                  CONSTRAINT fk_caixa_pagamento FOREIGN KEY (id_pagamento) REFERENCES pagamento(id_pagamento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE laudo (
                       id_laudo        BIGINT PRIMARY KEY AUTO_INCREMENT,
                       caminho_arquivo VARCHAR(255) NOT NULL,
                       data_geracao    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       id_vistoria     BIGINT NOT NULL UNIQUE,
                       CONSTRAINT fk_laudo_vistoria FOREIGN KEY (id_vistoria) REFERENCES vistoria(id_vistoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE desligamento_funcionario (
                                          id                BIGINT PRIMARY KEY AUTO_INCREMENT,
                                          id_funcionario    BIGINT NULL,
                                          nome_funcionario  VARCHAR(150) NOT NULL,
                                          matricula         VARCHAR(20) NOT NULL,
                                          motivo            VARCHAR(255) NOT NULL,
                                          data_desligamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          CONSTRAINT fk_desligamento_funcionario FOREIGN KEY (id_funcionario)
                                              REFERENCES funcionario(id_funcionario) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;