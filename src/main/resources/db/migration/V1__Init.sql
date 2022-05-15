CREATE TABLE todo_list
(
    id    INTEGER PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL
);

CREATE TABLE todo_task
(
    id           INTEGER PRIMARY KEY AUTO_INCREMENT,
    todo_list_id INTEGER      NOT NULL,
    description  VARCHAR(255) NOT NULL,
    FOREIGN KEY (todo_list_id) REFERENCES todo_list (id)
);
