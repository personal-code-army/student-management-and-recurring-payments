INSERT INTO companies (cnpj, name, cep, address, city, cellphone_number)
VALUES
    ('12345678000199', 'Gelo Team Matriz', '01001000', 'Praça da Sé, 1', 'São Paulo', '11999998888'),
    ('98765432000188', 'Academia Extreme', '20040000', 'Av. Rio Branco, 100', 'Rio de Janeiro', '21988887777');

INSERT INTO users (company_id, name, email, password, cellphone_number, role)
VALUES
    (1, 'Admin Gelo', 'admin@geloteam.com', '$2a$10$QyEotZNsjkarJa5FHqmV9uHorH6AvTKuxcrWGIXTlwnFRq2/lhKzW', '11977776666', 'ADMIN'),
    (1, 'Suporte Gelo', 'suporte@geloteam.com', '$2a$10$QyEotZNsjkarJa5FHqmV9uHorH6AvTKuxcrWGIXTlwnFRq2/lhKzW', '11966665555', 'ADMIN'),
    (2, 'Carlos Silva', 'carlos@extreme.com', '$2a$10$QyEotZNsjkarJa5FHqmV9uHorH6AvTKuxcrWGIXTlwnFRq2/lhKzW', '21955554444', 'USER');

INSERT INTO plans (company_id, name, monthly_amount, frequency)
VALUES
    (1, 'Plano Básico', 89.90, 1),
    (1, 'Plano VIP Anual', 850.00, 12),
    (2, 'Plano Musculação', 110.00, 1);

INSERT INTO students (plan_id, name, cpf, birth_date, phone, email, address, active)
VALUES
    (1, 'João Silva', '12345678901', '1995-05-20', '11999998888', 'joao@geloteam.com', 'Rua das Flores, 100', true),
    (2, 'Maria Souza', '98765432100', '2000-03-10', '21988887777', 'maria@extreme.com', 'Av. Brasil, 200', true),
    (3, 'Carlos Inativo', '11122233344', '1988-07-15', null, null, null, false);

INSERT INTO subscriptions (student_id, plan_id, start_date, status, payment_method)
VALUES
    (1, 1, '2023-10-01', 'ACTIVE', 'CREDIT_CARD'),
    (2, 2, '2023-11-15', 'ACTIVE', 'PIX');

INSERT INTO payments (subscription_id, description, value, payment_method, due_date, issue_date, status)
VALUES
    (1, 'Mensalidade Outubro', 89.90, 'CREDIT_CARD', '2023-10-10', '2023-10-01', 'PAID'),
    (2, 'Anuidade VIP', 850.00, 'PIX', '2023-11-15', '2023-11-15', 'PAID');
