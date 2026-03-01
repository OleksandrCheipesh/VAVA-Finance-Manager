alter table users
drop column login,
add column company_id int;

alter table users
    add constraint fk_users_company
    foreign key (company_id) references companies(id)
    on delete set null;
