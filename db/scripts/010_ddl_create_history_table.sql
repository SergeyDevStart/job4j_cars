create table if not exists history (
    id serial primary key,
    startAt timestamp not null,
    endAt timestamp not null
);