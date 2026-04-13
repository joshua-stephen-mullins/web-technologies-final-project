-- V6: Peer evaluations and per-criterion ratings
-- Owner: Whitey (Person 3)
-- UCs: UC-28 (submit eval), UC-29 (student view), UC-31 (section report), UC-33 (student report)

CREATE TABLE peer_evaluation (
    id           INT      NOT NULL AUTO_INCREMENT,
    evaluator_id INT      NOT NULL,
    evaluatee_id INT      NOT NULL,
    week_id      INT      NOT NULL,
    public_comments  TEXT,
    private_comments TEXT,
    submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_eval (evaluator_id, evaluatee_id, week_id),
    FOREIGN KEY (evaluator_id) REFERENCES peer_evaluation_user(id) ON DELETE CASCADE,
    FOREIGN KEY (evaluatee_id) REFERENCES peer_evaluation_user(id) ON DELETE CASCADE,
    FOREIGN KEY (week_id)      REFERENCES week(id) ON DELETE CASCADE
);

CREATE TABLE rating (
    id                 INT           NOT NULL AUTO_INCREMENT,
    peer_evaluation_id INT           NOT NULL,
    criterion_id       INT           NOT NULL,
    score              DECIMAL(5,2)  NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (peer_evaluation_id) REFERENCES peer_evaluation(id) ON DELETE CASCADE,
    FOREIGN KEY (criterion_id)       REFERENCES criterion(id) ON DELETE CASCADE
);
