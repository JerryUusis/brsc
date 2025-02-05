CREATE TABLE surveys (
    id BIGSERIAL PRIMARY KEY,
    issue_number INT NOT NULL,
    issue_link VARCHAR(255) NOT NULL,
    task_title VARCHAR(255) NOT NULL

);

CREATE TABLE survey_instructions (
    survey_id BIGINT NOT NULL,
    instruction VARCHAR(255) NOT NULL,
    FOREIGN KEY (survey_id) REFERENCES surveys(id) ON DELETE CASCADE
)