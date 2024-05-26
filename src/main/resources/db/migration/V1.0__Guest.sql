CREATE TABLE if not exists guest (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    document TEXT NOT NULL,
    phone TEXT NOT NULL
);