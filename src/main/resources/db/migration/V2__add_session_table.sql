CREATE TABLE "sessions" (
    "id" uuid PRIMARY KEY,
    "user_id" uuid NOT NULL,
    "device_info" text,
    "ip_address" varchar(50),
    "created_at" timestamptz DEFAULT NOW(),
    "updated_at" timestamptz DEFAULT NOW(),
    "expires_at" timestamptz NOT NULL
);

CREATE INDEX idx_sessions_user_id ON sessions(user_id);