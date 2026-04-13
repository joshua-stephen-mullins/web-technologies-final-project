-- V3: Sections and Weeks
-- Owner: Josh (Person 1)
-- UCs: UC-2,3,4,5 (section CRUD), UC-6 (active weeks setup)

CREATE TABLE section (
    id         INT          NOT NULL AUTO_INCREMENT,
    name       VARCHAR(255) NOT NULL UNIQUE,
    start_date DATE         NOT NULL,
    end_date   DATE         NOT NULL,
    rubric_id  INT,
    PRIMARY KEY (id),
    FOREIGN KEY (rubric_id) REFERENCES rubric(id) ON DELETE SET NULL
);

CREATE TABLE week (
    id         INT     NOT NULL AUTO_INCREMENT,
    section_id INT     NOT NULL,
    week_number INT    NOT NULL,
    start_date DATE    NOT NULL,
    end_date   DATE    NOT NULL,
    is_active  BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    FOREIGN KEY (section_id) REFERENCES section(id) ON DELETE CASCADE
);
