-- Insert Sample Surveys
INSERT INTO surveys (issue_number, issue_link, task_number, task_title)
VALUES (124, 'www.example.com', 1, 'Click button'),
       (125, 'www.example2.com', 2, 'Find the Artifact!');

INSERT INTO instructions (instruction_text, survey_id)
SELECT 'Navigate to landing page', id
FROM surveys
WHERE issue_number = 124
  AND task_number = 1
UNION ALL
SELECT 'Click button', id
FROM surveys
WHERE issue_number = 124
  AND task_number = 1
UNION ALL
SELECT 'Enter the ruins', id
FROM surveys
WHERE issue_number = 125
  AND task_number = 2
UNION ALL
SELECT 'Look under the altar', id
FROM surveys
WHERE issue_number = 125
  AND task_number = 2;
