-- Insert surveys (without instructions)
INSERT INTO surveys (issue_number, issue_link, task_title)
VALUES (101, 'http://example.com/101', 'Fix UI'),
       (102, 'http://example.com/102', 'Improve layout');

-- Insert instructions into `survey_instructions` table
INSERT INTO survey_instructions (survey_id, instruction)
VALUES (1, 'Open the app'),
       (1, 'Click login'),
       (2, 'Open settings'),
       (2, 'Change theme');
