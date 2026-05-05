create table if not exists users (
    id integer primary key autoincrement,
    email varchar(64) not null,
    password_hash varchar(255) not null,
    name varchar(64) not null,
    surname varchar(64) not null,
    position varchar(64) not null check (position in ('Director', 'Analyst', 'Accountant')),
    created_at text not null default (datetime('now'))
);
