-- ==========================================
-- V3 - Checklist de itens e fotos da vistoria
-- ==========================================

CREATE TABLE item_vistoria (
                               id_item_vistoria BIGINT PRIMARY KEY AUTO_INCREMENT,
                               nome_item        VARCHAR(100) NOT NULL,
                               situacao         ENUM('OK', 'AVARIA') NOT NULL,
                               observacao       TEXT,
                               id_vistoria      BIGINT NOT NULL,
                               CONSTRAINT fk_item_vistoria_vistoria FOREIGN KEY (id_vistoria) REFERENCES vistoria(id_vistoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE foto_vistoria (
                               id_foto_vistoria BIGINT PRIMARY KEY AUTO_INCREMENT,
                               caminho_arquivo  VARCHAR(255) NOT NULL,
                               descricao        VARCHAR(255),
                               data_upload      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               id_vistoria      BIGINT NOT NULL,
                               CONSTRAINT fk_foto_vistoria_vistoria FOREIGN KEY (id_vistoria) REFERENCES vistoria(id_vistoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;