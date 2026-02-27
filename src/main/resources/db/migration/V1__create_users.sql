create table if not exists users (
    id serial primary key,
    login varchar(64) not null,
    email varchar(64) not null,
    password_hash varchar(255) not null,
    name varchar(64) not null,
    surname varchar(64) not null,
    position varchar(64) not null,
    created_at timestamptz not null default now()
    );