CREATE TABLE surveys
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    issue_number INT          NOT NULL,
    issue_link   VARCHAR(255) NOT NULL,
    task_number  INT          NOT NULL,
    task_title   VARCHAR(255) NOT NULL
);

CREATE TABLE instructions
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    instruction_text TEXT   NOT NULL,
    survey_id        BIGINT NOT NULL,
    FOREIGN KEY (survey_id) REFERENCES surveys (id) ON DELETE CASCADE
);
