create table if not exists companies (
    id integer primary key autoincrement,
    name varchar(128) not null,
    industry varchar(128),
    country varchar(64),
    currency varchar(8) not null default 'USD',
    created_at text not null default (datetime('now'))
);


