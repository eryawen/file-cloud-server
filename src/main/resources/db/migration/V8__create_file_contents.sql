CREATE TABLE file_contents (
    file_id INTEGER PRIMARY KEY,
    contents BLOB,

     FOREIGN KEY(file_id) REFERENCES file_metadata(file_id) ON DELETE CASCADE
);
