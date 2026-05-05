create table if not exists accounts (
    id integer primary key autoincrement,
    company_id int not null references companies(id) on delete cascade,
    account_name varchar(128) not null,
    current_balance numeric(15, 2) not null default 0,
    currency varchar(8) not null default 'USD',
    created_at text not null default (datetime('now'))
);

