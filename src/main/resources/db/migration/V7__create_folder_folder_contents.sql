CREATE TABLE folder_folder_contents (
    parent_folder_id INTEGER,
    contained_folder_id INTEGER,

    PRIMARY KEY(parent_folder_id,contained_folder_id),
     FOREIGN KEY(parent_folder_id) REFERENCES folder_metadata(folder_id) ON DELETE CASCADE,
     FOREIGN KEY(contained_folder_id) REFERENCES folder_metadata(folder_id) ON DELETE CASCADE
);
