CREATE TABLE "carts" (
    "id" uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    "user_id" uuid NOT NULL,
    "store_id" uuid NOT NULL,
    "created_at" timestamptz DEFAULT NOW(),
    "updated_at" timestamptz DEFAULT NOW()
);

CREATE TABLE "cart_items" (
    "id" uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    "cart_id" uuid NOT NULL,
    "product_id" uuid NOT NULL,
    "quantity" integer NOT NULL,
    "created_at" timestamptz DEFAULT NOW(),
    "updated_at" timestamptz DEFAULT NOW()
);

ALTER TABLE "carts" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "carts" ADD FOREIGN KEY ("store_id") REFERENCES "stores" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "cart_items" ADD FOREIGN KEY ("cart_id") REFERENCES "carts" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "cart_items" ADD FOREIGN KEY ("product_id") REFERENCES "products" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;
