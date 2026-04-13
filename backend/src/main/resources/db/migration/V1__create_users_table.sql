-- V1: Users, invitations, password reset tokens
-- Owner: Josh (Person 1)
-- UCs: UC-25, UC-26, UC-30 (account setup/edit), UC-11, UC-18 (invitations)

CREATE TABLE peer_evaluation_user (
    id         INT          NOT NULL AUTO_INCREMENT,
    username   VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    roles      VARCHAR(255) NOT NULL,
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);

CREATE TABLE user_invitation (
    id               INT          NOT NULL AUTO_INCREMENT,
    email            VARCHAR(255) NOT NULL,
    invitation_token VARCHAR(255) NOT NULL UNIQUE,
    role             VARCHAR(50)  NOT NULL,
    section_id       INT,
    used             BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE password_reset_token (
    id      INT          NOT NULL AUTO_INCREMENT,
    user_id INT          NOT NULL,
    token   VARCHAR(255) NOT NULL UNIQUE,
    expiry  DATETIME     NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES peer_evaluation_user(id) ON DELETE CASCADE
);
