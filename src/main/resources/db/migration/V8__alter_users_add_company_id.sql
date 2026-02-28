alter table users
drop column if exists login,
add column if not exists company_id int;

alter table users
    add constraint if not exists fk_users_company
    foreign key (company_id) references companies(id)
    on delete set null;