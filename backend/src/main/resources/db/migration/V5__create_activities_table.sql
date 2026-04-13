-- V5: Weekly Activity Report (WAR) activities
-- Owner: Oscar (Person 2)
-- UCs: UC-27 (manage WAR), UC-32 (team WAR report), UC-34 (student WAR report)

CREATE TABLE activity (
    id          INT          NOT NULL AUTO_INCREMENT,
    student_id  INT          NOT NULL,
    week_id     INT          NOT NULL,
    category    VARCHAR(50)  NOT NULL,  -- DEVELOPMENT, TESTING, BUGFIX, etc.
    description TEXT         NOT NULL,
    planned_hours DECIMAL(5,2) NOT NULL,
    actual_hours  DECIMAL(5,2),
    status      VARCHAR(50)  NOT NULL,  -- IN_PROGRESS, UNDER_TESTING, DONE
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (student_id) REFERENCES peer_evaluation_user(id) ON DELETE CASCADE,
    FOREIGN KEY (week_id)    REFERENCES week(id) ON DELETE CASCADE
);
