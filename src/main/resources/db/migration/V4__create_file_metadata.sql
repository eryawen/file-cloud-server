
CREATE TABLE file_metadata (
    file_id INTEGER PRIMARY KEY,
    name TEXT,
    path_lower TEXT,
    path_display TEXT,
    enclosing_folder_id INTEGER,
    size INTEGER,
    server_created_at TEXT,
    server_changed_at TEXT,
    owner_id INTEGER,

    FOREIGN KEY(enclosing_folder_id) REFERENCES folder_metadata(folder_id) ON DELETE CASCADE,
    FOREIGN KEY(owner_id) REFERENCES users(id)

);
