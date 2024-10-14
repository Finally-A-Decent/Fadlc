CREATE TABLE IF NOT EXISTS users
(
    uniqueId          TEXT    NOT NULL PRIMARY KEY,
    username          TEXT    NOT NULL,
    availableChunks   INTEGER NOT NULL,
    showBorders       INTEGER NOT NULL,
    showEnterMessages INTEGER NOT NULL,
    showLeaveMessages INTEGER NOT NULL,
    messageLocation   TEXT    NOT NULL,
    usingProfile      INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS claims
(
    ownerUUID     TEXT NOT NULL PRIMARY KEY,
    ownerUsername TEXT NOT NULL,
    profiles      TEXT NOT NULL,
    chunks        TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS chunks
(
    location    TEXT    NOT NULL PRIMARY KEY,
    timeClaimed INTEGER NOT NULL,
    profile     INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS profiles
(
    uuid   TEXT    NOT NULL PRIMARY KEY,
    id     INTEGER NOT NULL,
    parent TEXT    NOT NULL,
    name   TEXT    NOT NULL,
    groups TEXT    NOT NULL,
    flags  TEXT    NOT NULL,
    border TEXT    NOT NULL
);

CREATE TABLE IF NOT EXISTS groups
(
    uuid     TEXT    NOT NULL PRIMARY KEY,
    id       INTEGER NOT NULL,
    name     TEXT    NOT NULL,
    users    TEXT    NOT NULL,
    settings TEXT    NOT NULL
);