-- Add surname to clients table
alter table clients add column surname varchar(128) not null;

-- Add email to employees table
alter table employees add column email varchar(128);
