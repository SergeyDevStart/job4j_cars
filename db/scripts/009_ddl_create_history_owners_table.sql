create table if not exists history_owners (
    id serial primary key,
    owner_id bigint not null references owners(id),
    car_id bigint not null references car(id),
    unique (owner_id, car_id)
);