CREATE TABLE folder_metadata (
    folder_id INTEGER PRIMARY KEY,
    parent_folder_id INTEGER,
    name TEXT,
    path_lower TEXT,
    path_display TEXT,
    server_created_at TEXT,
    owner_id INTEGER,

    FOREIGN KEY(parent_folder_id) REFERENCES folder_metadata(folder_id) ON DELETE CASCADE,
    FOREIGN KEY(owner_id) REFERENCES users(id)

);
