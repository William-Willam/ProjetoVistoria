CREATE DATABASE autovistor DEFAULT CHARACTER SET utf8mb4;
use autovistor;
SHOW TABLES;

select * from funcionario;
select * from cliente;
select * from veiculo;
select * from agendamento;
select * from nota_fiscal;
select * from lancamento_caixa;

SELECT * FROM desligamento_funcionario;
SELECT * FROM funcionario WHERE id_funcionario = 2;