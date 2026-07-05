CREATE TABLE "wallet_transactions" (
    "id" uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    "wallet_id" uuid NOT NULL,
    "amount" decimal(12, 2) NOT NULL,
    "balance_before_transaction" decimal(12, 2) NOT NULL,
    "balance_after_transaction" decimal(12, 2) NOT NULL,
    "created_at" timestamptz DEFAULT NOW(),
    "updated_at" timestamptz DEFAULT NOW()
);

ALTER TABLE "wallet_transactions" ADD FOREIGN KEY ("wallet_id") REFERENCES "wallets" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

CREATE TABLE user_addresses (
    "id" uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    "user_id" uuid NOT NULL,
    "address_name" varchar(100) NOT NULL,
    "is_default" boolean NOT NULL DEFAULT false,
    "receiver_name" varchar(100) NOT NULL,
    "receiver_phone" varchar(20) NOT NULL,
    "street_address" text NOT NULL,
    "district" varchar(100) NOT NULL,
    "city" varchar(100) NOT NULL,
    "province" varchar(100) NOT NULL,
    "postal_code" varchar(20) NOT NULL,
    "created_at" timestamptz DEFAULT NOW(),
    "updated_at" timestamptz DEFAULT NOW()
);

ALTER TABLE user_addresses ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;