create table if not exists clients (
    id integer primary key autoincrement,
    company_id int not null references companies(id) on delete cascade,
    name varchar(128) not null,
    email varchar(128),
    phone varchar(32),
    monthly_income numeric(15, 2),
    created_at text not null default (datetime('now'))
);

