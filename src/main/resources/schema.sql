CREATE TABLE IF NOT EXISTS surveys
(
    id           BIGSERIAL PRIMARY KEY,
    issue_number INT          NOT NULL,
    issue_link   VARCHAR(255) NOT NULL,
    task_number  INT          NOT NULL,
    task_title   VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS instructions
(
    id               BIGSERIAL PRIMARY KEY,
    instruction_text TEXT   NOT NULL,
    survey_id        BIGINT NOT NULL,
    FOREIGN KEY (survey_id) REFERENCES surveys (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users
(
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    passwordHash TEXT NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS roles
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- Create user roles in the table
INSERT INTO roles(name) VALUES ('USER') ON CONFLICT DO NOTHING;
INSERT INTO roles(name) VALUES ('ADMIN') ON CONFLICT DO NOTHING;
