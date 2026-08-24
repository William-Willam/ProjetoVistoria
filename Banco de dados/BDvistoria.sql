CREATE DATABASE autovistor DEFAULT CHARACTER SET utf8mb4;
use autovistor;
SHOW TABLES;

select * from funcionario;
select * from cliente;
select * from veiculo;
select * from agendamento;
select * from nota_fiscal;
select * from lancamento_caixa;

SELECT id_vistoria FROM vistoria WHERE id_agendamento = 6;
DELETE FROM laudo WHERE id_vistoria = 4;