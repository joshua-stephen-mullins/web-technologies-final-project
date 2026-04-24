-- V7: Add optional middle initial to peer_evaluation_user
-- Owner: Whitney (Person 3) — UC-30
ALTER TABLE peer_evaluation_user
    ADD COLUMN middle_initial VARCHAR(1) NULL;
