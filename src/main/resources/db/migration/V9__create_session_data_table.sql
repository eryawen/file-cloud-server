CREATE TABLE session_data (
    session_id TEXT PRIMARY KEY,
    user_id INTEGER,
    last_accessed TEXT,

    FOREIGN KEY (user_id) REFERENCES users(id)
);