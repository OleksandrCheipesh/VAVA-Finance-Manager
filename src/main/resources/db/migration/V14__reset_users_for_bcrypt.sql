-- Reset existing users so everyone must register again with to hash passwords.
DELETE FROM users;
DELETE FROM sqlite_sequence WHERE name = 'users';
