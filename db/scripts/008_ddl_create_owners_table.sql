create table if not exists owners (
    id serial primary key,
    name varchar not null,
    user_id bigint references auto_user(id)
);