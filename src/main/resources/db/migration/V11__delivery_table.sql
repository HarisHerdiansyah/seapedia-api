CREATE TYPE "delivery_method" AS ENUM ('INSTANT', 'NEXT_DAY', 'REGULAR');

CREATE TABLE "delivery" (
    "id" uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    "delivery_method" delivery_method NOT NULL,
    "price" decimal(12, 2) NOT NULL,
    "created_at" timestamptz DEFAULT NOW(),
    "updated_at" timestamptz DEFAULT NOW()
);