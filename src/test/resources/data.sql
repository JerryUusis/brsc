-- Insert Sample Surveys
INSERT INTO surveys (issue_number, issue_link, task_number, task_title)
VALUES (124, 'www.example.com', 1, 'Click button'),
       (125, 'www.example2.com', 2, 'Find the Artifact!');

-- Insert Sample Instructions for Survey 124 (Click button)
INSERT INTO instructions (instruction_text, survey_id)
VALUES ('Navigate to landing page', 1),
       ('Click button', 1);

-- Insert Sample Instructions for Survey 125 (Find the Artifact!)
INSERT INTO instructions (instruction_text, survey_id)
VALUES ('Enter the ruins', 2),
       ('Look under the altar', 2);
