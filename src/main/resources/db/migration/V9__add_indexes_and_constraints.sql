-- users
-- unique email
alter table users
    add constraint uq_users_email unique (email);

-- index on email
create index if not exists idx_users_email
    on users (company_id);

-- index on company_id
create index if not exists idx_users_company_id
    on users (company_id);


-- change position type to enum
create type position_enum as enum ('Director', 'Analyst', 'Accountant');

update users
set position = 'Director'
where position is null
   or position not in ('Director', 'Analyst', 'Accountant');

alter table users
    alter column position type position_enum
    using position::position_enum;


-- companies
-- unique company name
alter table companies
    add constraint uq_companies_name unique (name);


-- accounts
-- unique account name within a company
alter table accounts
    add constraint uq_accounts_company_name unique (company_id, account_name);

-- index on company_id
create index if not exists idx_accounts_company_id
    on accounts (company_id);


-- employees
-- index on company_id
create index if not exists idx_employees_company_id
    on employees (company_id);


-- clients
-- index on company_id
create index if not exists idx_clients_company_id
    on clients (company_id);


-- projects
-- index on company_id
create index if not exists idx_projects_company_id
    on projects (company_id);

-- index on is_active for filtering active projects
create index if not exists idx_projects_is_active
    on projects (is_active)
    where is_active = true;


-- transactions
-- index on company_id
create index if not exists idx_transactions_company_id
    on transactions (company_id);

-- index on account_id
create index if not exists idx_transactions_account_id
    on transactions (account_id);

-- index on project_id
create index if not exists idx_transactions_project_id
    on transactions (project_id)
    where project_id is not null;

-- index on client_id
create index if not exists idx_transactions_client_id
    on transactions (client_id)
    where client_id is not null;

-- index on date
create index if not exists idx_transactions_date
    on transactions (date);

-- index on type
create index if not exists idx_transactions_type
    on transactions (type);
