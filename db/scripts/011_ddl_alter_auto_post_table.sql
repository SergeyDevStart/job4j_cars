alter table auto_post
add column if not exists car_id bigint not null references car(id);