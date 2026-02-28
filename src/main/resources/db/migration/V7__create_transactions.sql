create table if not exists transactions (
    id serial primary key,
    company_id int not null references companies(id) on delete cascade,
    account_id int not null references accounts(id) on delete cascade,
    project_id int references projects(id) on delete set null,
    client_id int references clients(id) on delete set null,
    type varchar(32) not null,
    amount numeric(15, 2) not null,
    description text,
    date date not null default current_date,
    created_at timestamptz not null default now()
);

