DO
$$
BEGIN
    -- Create employee_status enum
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'employee_status') THEN
        CREATE TYPE employee_status AS ENUM (
            'ACTIVE',
            'INACTIVE',
            'CONTRACTOR'
        );
    END IF;
END
$$;

-- Add the status column
alter table employees
    add column if not exists status employee_status not null default 'ACTIVE';

