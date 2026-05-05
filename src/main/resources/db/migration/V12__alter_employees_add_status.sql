-- Add the status column
alter table employees
    add column status text not null default 'ACTIVE';

