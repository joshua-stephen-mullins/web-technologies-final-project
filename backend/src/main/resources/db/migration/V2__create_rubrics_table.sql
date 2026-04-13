-- V2: Rubrics and Criteria
-- Owner: Josh (Person 1)
-- UCs: UC-1 (create rubric), UC-4 (section linked to rubric)

CREATE TABLE rubric (
    id   INT          NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE criterion (
    id          INT           NOT NULL AUTO_INCREMENT,
    rubric_id   INT           NOT NULL,
    name        VARCHAR(255)  NOT NULL,
    description TEXT          NOT NULL,
    max_score   DECIMAL(5,2)  NOT NULL,
    sort_order  INT           NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    FOREIGN KEY (rubric_id) REFERENCES rubric(id) ON DELETE CASCADE
);
