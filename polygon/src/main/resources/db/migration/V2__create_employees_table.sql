CREATE TABLE employees (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    position TEXT NOT NULL,
    salary INT NOT NULL,
    department_id BIGINT REFERENCES departments(id) ON DELETE SET NULL
);