-- Usuários de teste (senha ainda não hasheada, pois Security entra depois)
INSERT INTO usuarios (nome, login, senha_hash, role, percentual_comissao, ativo) VALUES
('Carlos (Dono)', 'carlos', 'senha123', 'DONO', NULL, true),
('João', 'joao', 'senha123', 'BARBEIRO', 50.00, true),
('Pedro', 'pedro', 'senha123', 'BARBEIRO', 40.00, true);

-- Serviços de teste
INSERT INTO servicos (nome, preco, ativo) VALUES
('Corte', 35.00, true),
('Barba', 25.00, true),
('Corte + Barba', 55.00, true),
('Sobrancelha', 15.00, true);
