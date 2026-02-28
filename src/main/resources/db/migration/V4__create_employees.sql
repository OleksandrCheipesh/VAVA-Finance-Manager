create table if not exists employees (
    id serial primary key,
    company_id int not null references companies(id) on delete cascade,
    name varchar(64) not null,
    surname varchar(64) not null,
    age int,
    salary numeric(12, 2),
    position varchar(64),
    hired_at timestamptz not null default now()
);

