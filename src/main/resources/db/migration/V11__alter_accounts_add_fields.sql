alter table accounts
    add column limit_amount int;

alter table accounts
    add column category text not null default 'OTHER';

alter table accounts
    add column cycle text not null default 'MONTHLY';

