alter table owners
add column if not exists history_id bigint not null references history(id);