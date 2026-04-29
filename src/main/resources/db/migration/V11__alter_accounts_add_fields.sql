DO
$$
BEGIN
    -- Create account_category enum if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'account_category') THEN
        CREATE TYPE account_category AS ENUM (
            'BANK_ACCOUNT',
            'CASH',
            'CREDIT_LINE',
            'SAVINGS',
            'INVESTMENT',
            'OTHER'
        );
    END IF;

    -- Create account_cycle enum if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'account_cycle') THEN
        CREATE TYPE account_cycle AS ENUM (
            'MONTHLY',
            'WEEKLY'
        );
    END IF;
END
$$;

alter table accounts
    add column if not exists limit_amount int,
    add column if not exists category account_category not null default 'OTHER',
    add column if not exists cycle account_cycle not null default 'MONTHLY';

