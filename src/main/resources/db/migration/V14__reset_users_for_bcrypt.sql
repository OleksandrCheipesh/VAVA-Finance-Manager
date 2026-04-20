-- Reset existing users so everyone must register again with to hash passwords.
TRUNCATE TABLE users RESTART IDENTITY;
