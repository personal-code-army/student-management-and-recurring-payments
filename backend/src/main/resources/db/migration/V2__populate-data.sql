-- 1. Inserir Empresas (Necessário para Users, Plans e Integrations)
INSERT INTO companies (cnpj, name, cep, address, city, cellphone_number)
VALUES
    ('12345678000199', 'Gelo Team Matriz', '01001000', 'Praça da Sé, 1', 'São Paulo', '11999998888'),
    ('98765432000188', 'Academia Extreme', '20040000', 'Av. Rio Branco, 100', 'Rio de Janeiro', '21988887777');

-- 2. Inserir Usuários (Funcionários da Empresa)
INSERT INTO users (company_id, name, email, password, cellphone_number, role)
VALUES
    (1, 'Admin Gelo', 'admin@geloteam.com', '$2b$10$1xNz0oiMOS8T.zek.hRq6Ow4d0N0tUaIopWjRM9MWp0OZKsKLiOya', '11977776666', 'ADMIN'),
    (1, 'Suporte Gelo', 'suporte@geloteam.com', '$2b$10$mXyhCattVsqYIuwLz/WHEuyW97.Nero2Ou.uPhNL5fG75WJLPo9GC', '11966665555', 'ADMIN'),
    (2, 'Carlos Silva', 'carlos@extreme.com', '$2b$10$R5.tg0LJsrW2ew7XE3LUwueO1EBhBjABSoxsynyjAM7Oje79af106', '21955554444', 'USER');

-- 3. Inserir Planos (Necessário para Students e Subscriptions)
INSERT INTO plans (company_id, name, monthly_amount, frequency)
VALUES
    (1, 'Plano Básico', 89.90, 1),
    (1, 'Plano VIP Anual', 850.00, 12),
    (2, 'Plano Musculação', 110.00, 1);

-- 4. Inserir Estudantes/Alunos
INSERT INTO students (plan_id, cpf, active)
VALUES
    (1, '12345678901', true),
    (2, '98765432100', true),
    (3, '11122233344', false);

-- 5. Inserir Assinaturas (Subscriptions)
INSERT INTO subscriptions (student_id, plan_id, start_date, status, payment_method)
VALUES
    (1, 1, '2023-10-01', 'ACTIVE', 'CREDIT_CARD'),
    (2, 2, '2023-11-15', 'ACTIVE', 'PIX');

-- 6. Inserir Pagamentos (Payments)
INSERT INTO payments (subscription_id, description, value, payment_method, due_date, issue_date, status)
VALUES
    (1, 'Mensalidade Outubro', 89.90, 'CREDIT_CARD', '2023-10-10', '2023-10-01', 'PAID'),
    (2, 'Anuidade VIP', 850.00, 'PIX', '2023-11-15', '2023-11-15', 'PAID');