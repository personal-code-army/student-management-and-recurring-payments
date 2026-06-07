-- Dev password for all seeded users: SenhaNova@789
UPDATE users
SET password = '$2a$10$ajQ/MHoblHW4AQYw4SE59uPMR8Z.s5si4t6ELsokWwM9q9H2Br1ly'
WHERE email IN ('admin@geloteam.com', 'suporte@geloteam.com', 'carlos@extreme.com');
