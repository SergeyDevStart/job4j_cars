create table if not exists owners (
    id serial primary key,
    name varchar not null,
    user_id bigint not null references auto_user(id)
);