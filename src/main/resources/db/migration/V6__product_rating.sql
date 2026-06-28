ALTER TABLE "products" ADD COLUMN "rating" DECIMAL(2, 1) NOT NULL DEFAULT 0.0 CHECK ("rating" >= 0.0 AND "rating" <= 5.0);
