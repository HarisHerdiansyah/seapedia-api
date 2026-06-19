CREATE TYPE "user_role" AS ENUM ('ADMIN', 'NON_ADMIN');
CREATE TYPE "driver_status" AS ENUM ('ACTIVE', 'INACTIVE');

CREATE TABLE "users" (
                         "id" uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                         "username" varchar(100) UNIQUE NOT NULL,
                         "email" varchar(100) NOT NULL,
                         "password_hash" varchar(255) NOT NULL,
                         "role" user_role NOT NULL,
                         "created_at" timestamptz DEFAULT NOW(),
                         "updated_at" timestamptz DEFAULT NOW()
);

CREATE TABLE "stores" (
                          "id" uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                          "user_id" uuid UNIQUE NOT NULL,
                          "store_name" varchar(100) UNIQUE NOT NULL,
                          "location" varchar(100) NOT NULL,
                          "created_at" timestamptz DEFAULT NOW(),
                          "updated_at" timestamptz DEFAULT NOW()
);

CREATE TABLE "drivers" (
                           "id" uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                           "user_id" uuid UNIQUE NOT NULL,
                           "status" driver_status NOT NULL,
                           "created_at" timestamptz DEFAULT NOW(),
                           "updated_at" timestamptz DEFAULT NOW()
);

CREATE TABLE "products" (
                            "id" uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                            "store_id" uuid NOT NULL,
                            "name" varchar(100) NOT NULL,
                            "price" decimal(12, 2) NOT NULL DEFAULT 0.0,
                            "stock" integer NOT NULL DEFAULT 0,
                            "image_url" text,
                            "description" text NOT NULL,
                            "created_at" timestamptz DEFAULT NOW(),
                            "updated_at" timestamptz DEFAULT NOW()
);

CREATE TABLE "app_reviews" (
                              "id" uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                              "reviewer" varchar(100) NOT NULL,
                              "rating" decimal(2, 1) NOT NULL DEFAULT 0.0,
                              "content" text NOT NULL,
                              "created_at" timestamptz DEFAULT NOW(),
                              "updated_at" timestamptz DEFAULT NOW()
);

ALTER TABLE "stores" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "drivers" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "products" ADD FOREIGN KEY ("store_id") REFERENCES "stores" ("id") DEFERRABLE INITIALLY IMMEDIATE;