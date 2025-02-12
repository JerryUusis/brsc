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