create table if not exists projects (
    id integer primary key autoincrement,
    company_id int not null references companies(id) on delete cascade,
    name varchar(128) not null,
    description text,
    budget_limit numeric(15, 2),
    start_date date,
    end_date date,
    is_active boolean not null default true,
    created_at text not null default (datetime('now'))
);

