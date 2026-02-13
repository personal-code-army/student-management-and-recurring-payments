CREATE TABLE students(
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    age INT NOT NULL,
    birthday_date DATE NOT NULL,
    active BOOLEAN NOT NULL
);