CREATE TYPE "active_role" AS ENUM ('ADMIN', 'NON_ADMIN', 'BUYER', 'SELLER', 'DRIVER');

ALTER TABLE sessions ADD COLUMN active_role active_role NOT NULL;
