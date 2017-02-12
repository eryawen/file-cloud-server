CREATE TABLE folder_file_contents (
    parent_folder_id INTEGER,
    contained_file_id INTEGER,

    PRIMARY KEY(parent_folder_id,contained_file_id),
    FOREIGN KEY(parent_folder_id) REFERENCES folder_metadata(folder_id) ON DELETE CASCADE,
    FOREIGN KEY(contained_file_id) REFERENCES file_metadata(file_id) ON DELETE CASCADE
);
