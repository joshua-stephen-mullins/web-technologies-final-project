-- V4: Teams, student/instructor memberships
-- Owner: Oscar (Person 2)
-- UCs: UC-7,8,9,10,13,14 (team CRUD), UC-12,19 (assign members)

CREATE TABLE team (
    id          INT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    website_url VARCHAR(500),
    section_id  INT          NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (section_id) REFERENCES section(id) ON DELETE CASCADE
);

-- Students belong to a team (many-to-one)
ALTER TABLE peer_evaluation_user
    ADD COLUMN team_id INT,
    ADD FOREIGN KEY (team_id) REFERENCES team(id) ON DELETE SET NULL;

-- Instructors can supervise multiple teams (many-to-many)
CREATE TABLE team_instructor (
    team_id       INT NOT NULL,
    instructor_id INT NOT NULL,
    PRIMARY KEY (team_id, instructor_id),
    FOREIGN KEY (team_id)       REFERENCES team(id) ON DELETE CASCADE,
    FOREIGN KEY (instructor_id) REFERENCES peer_evaluation_user(id) ON DELETE CASCADE
);
