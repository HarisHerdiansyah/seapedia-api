CREATE TYPE "transaction_type" AS ENUM ('TOP_UP', 'PAYMENT', 'REFUND', 'INCOME');

ALTER TABLE "wallet_transactions" ADD COLUMN "transaction_type" transaction_type NOT NULL;