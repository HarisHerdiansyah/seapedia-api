-- =============================================================================
-- Seapedia API — Reset & Seed Script
-- =============================================================================
-- Tujuan   : Menghapus semua data dan mengisi data baru untuk testing semua flow
-- Password : Password123! (BCrypt hash, cost factor 10)
-- =============================================================================

\echo '============================================================'
\echo '  Seapedia API — Reset & Seed'
\echo '============================================================'

-- ============================================================
-- BAGIAN 1: RESET — Hapus semua data (tanpa drop table/schema)
-- Flyway schema history TIDAK dihapus agar migrasi tidak perlu diulang.
-- ============================================================

\echo ''
\echo '[RESET] Menghapus semua data...'

-- TRUNCATE CASCADE menangani foreign key secara otomatis
-- tanpa perlu mengubah session_replication_role (tidak butuh privilege superuser)
TRUNCATE TABLE
    cart_items,
    carts,
    wallet_transactions,
    wallets,
    user_addresses,
    delivery,
    app_reviews,
    products,
    categories,
    drivers,
    sessions,
    stores,
    users
RESTART IDENTITY CASCADE;

\echo '[RESET] Selesai. Semua data berhasil dihapus.'

-- ============================================================
-- BAGIAN 2: SEED — Insert data baru untuk testing
-- UUID di-hardcode agar referensi antar tabel konsisten.
-- ============================================================

\echo ''
\echo '[SEED] Mulai insert data...'

-- ------------------------------------------------------------
-- 2.1 USERS
-- Password: Password123!
-- BCrypt hash (cost=10): $2y$10$EYw.iNnxfYo.XrGfRzJ4zeIZjvbDHWd6S2E6TTOLraHffYF6eXqAi
-- Note: Spring Security mendukung hash $2a dan $2y (keduanya kompatibel)
-- ------------------------------------------------------------

\echo '  > Inserting users...'

INSERT INTO users (id, username, email, password_hash, role) VALUES
    -- Admin
    ('00000000-0000-0000-0000-000000000001',
     'admin',
     'admin@seapedia.com',
     '$2y$10$EYw.iNnxfYo.XrGfRzJ4zeIZjvbDHWd6S2E6TTOLraHffYF6eXqAi',
     'ADMIN'),

    -- Seller (NON_ADMIN, akan mendaftarkan toko)
    ('00000000-0000-0000-0000-000000000002',
     'seller_budi',
     'budi@seapedia.com',
     '$2y$10$EYw.iNnxfYo.XrGfRzJ4zeIZjvbDHWd6S2E6TTOLraHffYF6eXqAi',
     'NON_ADMIN'),

    -- Buyer (NON_ADMIN, akan berbelanja)
    ('00000000-0000-0000-0000-000000000003',
     'buyer_andi',
     'andi@seapedia.com',
     '$2y$10$EYw.iNnxfYo.XrGfRzJ4zeIZjvbDHWd6S2E6TTOLraHffYF6eXqAi',
     'NON_ADMIN'),

    -- Driver (NON_ADMIN, sebagai pengemudi)
    ('00000000-0000-0000-0000-000000000004',
     'driver_reza',
     'reza@seapedia.com',
     '$2y$10$EYw.iNnxfYo.XrGfRzJ4zeIZjvbDHWd6S2E6TTOLraHffYF6eXqAi',
     'NON_ADMIN');

\echo '  > Users inserted: admin, seller_budi, buyer_andi, driver_reza'

-- ------------------------------------------------------------
-- 2.2 STORE — Toko milik seller_budi
-- ------------------------------------------------------------

\echo '  > Inserting store...'

INSERT INTO stores (id, user_id, store_name, location) VALUES
    ('10000000-0000-0000-0000-000000000001',
     '00000000-0000-0000-0000-000000000002',
     'Toko Budi Seafood',
     'Jakarta Utara');

\echo '  > Store inserted: Toko Budi Seafood'

-- ------------------------------------------------------------
-- 2.3 DRIVER — driver_reza terdaftar sebagai driver
-- ------------------------------------------------------------

\echo '  > Inserting driver...'

INSERT INTO drivers (id, user_id, status) VALUES
    ('20000000-0000-0000-0000-000000000001',
     '00000000-0000-0000-0000-000000000004',
     'ACTIVE');

\echo '  > Driver inserted: driver_reza (ACTIVE)'

-- ------------------------------------------------------------
-- 2.4 CATEGORIES — 6 kategori produk laut
-- ------------------------------------------------------------

\echo '  > Inserting categories...'

INSERT INTO categories (id, name, description) VALUES
    ('30000000-0000-0000-0000-000000000001',
     'Ikan Segar',
     'Berbagai jenis ikan laut dan air tawar yang masih segar, langsung dari nelayan.'),

    ('30000000-0000-0000-0000-000000000002',
     'Udang & Kerang',
     'Udang segar berbagai ukuran, kerang hijau, kerang darah, dan kerang-kerangan lainnya.'),

    ('30000000-0000-0000-0000-000000000003',
     'Cumi & Gurita',
     'Cumi-cumi segar, sotong, dan gurita berkualitas tinggi dari hasil tangkapan laut.'),

    ('30000000-0000-0000-0000-000000000004',
     'Ikan Olahan',
     'Produk ikan yang telah diolah seperti ikan asin, ikan asap, bakso ikan, dan nugget ikan.'),

    ('30000000-0000-0000-0000-000000000005',
     'Produk Laut Lainnya',
     'Aneka produk laut lainnya seperti kepiting, lobster, teripang, dan rumput laut.'),

    ('30000000-0000-0000-0000-000000000006',
     'Ikan Air Tawar',
     'Ikan dari perairan tawar seperti lele, nila, mas, gurame, dan patin segar.');

\echo '  > Categories inserted: 6 kategori'

-- ------------------------------------------------------------
-- 2.5 PRODUCTS — Produk dalam Toko Budi Seafood
-- ------------------------------------------------------------

\echo '  > Inserting products...'

INSERT INTO products (id, store_id, name, price, stock, image_url, description, category_id, rating) VALUES
    -- Kategori: Ikan Segar
    ('40000000-0000-0000-0000-000000000001',
     '10000000-0000-0000-0000-000000000001',
     'Ikan Salmon Fillet Segar',
     185000.00, 50,
     'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?w=400',
     'Salmon Atlantik premium grade A, fillet tanpa tulang, berat per 500gr. Kaya omega-3, cocok untuk sashimi atau dipanggang.',
     '30000000-0000-0000-0000-000000000001',
     4.8),

    ('40000000-0000-0000-0000-000000000002',
     '10000000-0000-0000-0000-000000000001',
     'Ikan Tuna Segar',
     95000.00, 40,
     'https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=400',
     'Ikan tuna sirip biru segar per kg. Daging tebal dan lezat, cocok untuk steak tuna, sushi, atau digoreng.',
     '30000000-0000-0000-0000-000000000001',
     4.5),

    ('40000000-0000-0000-0000-000000000003',
     '10000000-0000-0000-0000-000000000001',
     'Ikan Kembung Segar',
     35000.00, 120,
     'https://images.unsplash.com/photo-1583623025817-d180a2221d0a?w=400',
     'Ikan kembung segar hasil tangkapan harian per kg. Cocok untuk digoreng, dibakar, atau dibuat pindang.',
     '30000000-0000-0000-0000-000000000001',
     4.3),

    -- Kategori: Udang & Kerang
    ('40000000-0000-0000-0000-000000000004',
     '10000000-0000-0000-0000-000000000001',
     'Udang Vaname Super',
     98000.00, 80,
     'https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?w=400',
     'Udang vaname tambak pilihan size 50/kg. Segar, bersih, dan sudah dikupas kepala. Siap masak untuk berbagai hidangan.',
     '30000000-0000-0000-0000-000000000002',
     4.7),

    ('40000000-0000-0000-0000-000000000005',
     '10000000-0000-0000-0000-000000000001',
     'Kerang Hijau Segar',
     45000.00, 60,
     'https://images.unsplash.com/photo-1534620808146-d33bb39128b2?w=400',
     'Kerang hijau segar per 500gr. Cocok untuk tumis kerang, sop kerang, atau dibakar bumbu. Langsung dari petambak.',
     '30000000-0000-0000-0000-000000000002',
     4.2),

    -- Kategori: Cumi & Gurita
    ('40000000-0000-0000-0000-000000000006',
     '10000000-0000-0000-0000-000000000001',
     'Cumi-Cumi Segar',
     78000.00, 75,
     'https://images.unsplash.com/photo-1559153-4e7bef0ab1e6?w=400',
     'Cumi-cumi segar tangkapan nelayan per 500gr. Daging kenyal dan segar, cocok untuk cumi goreng tepung, cumi bakar, atau calamari.',
     '30000000-0000-0000-0000-000000000003',
     4.6),

    -- Kategori: Ikan Olahan
    ('40000000-0000-0000-0000-000000000007',
     '10000000-0000-0000-0000-000000000001',
     'Ikan Asin Jambal Roti',
     55000.00, 90,
     'https://images.unsplash.com/photo-1574484284002-952d92456975?w=400',
     'Ikan asin jambal roti premium per 250gr. Diproses secara tradisional dengan kadar garam yang pas. Cocok sebagai lauk nasi.',
     '30000000-0000-0000-0000-000000000004',
     4.4),

    -- Kategori: Produk Laut Lainnya
    ('40000000-0000-0000-0000-000000000008',
     '10000000-0000-0000-0000-000000000001',
     'Kepiting Bakau Segar',
     145000.00, 25,
     'https://images.unsplash.com/photo-1559339352-11d035aa65de?w=400',
     'Kepiting bakau hidup per ekor berat 500-700gr. Daging padat dan manis, cocok untuk kepiting saus tiram atau kepiting rebus.',
     '30000000-0000-0000-0000-000000000005',
     4.9);

\echo '  > Products inserted: 8 produk'

-- ------------------------------------------------------------
-- 2.6 WALLETS — Dompet untuk semua user
-- ------------------------------------------------------------

\echo '  > Inserting wallets...'

INSERT INTO wallets (id, user_id, balance) VALUES
    -- Admin wallet (saldo awal 0)
    ('50000000-0000-0000-0000-000000000001',
     '00000000-0000-0000-0000-000000000001',
     0.00),

    -- Seller wallet (saldo awal 0, akan bertambah saat ada penjualan)
    ('50000000-0000-0000-0000-000000000002',
     '00000000-0000-0000-0000-000000000002',
     0.00),

    -- Buyer wallet (saldo awal Rp 500.000 untuk langsung bisa belanja)
    ('50000000-0000-0000-0000-000000000003',
     '00000000-0000-0000-0000-000000000003',
     500000.00),

    -- Driver wallet (saldo awal 0)
    ('50000000-0000-0000-0000-000000000004',
     '00000000-0000-0000-0000-000000000004',
     0.00);

\echo '  > Wallets inserted: 4 wallet (buyer sudah punya saldo Rp 500.000)'

-- ------------------------------------------------------------
-- 2.7 WALLET TRANSACTIONS — Riwayat top-up awal untuk buyer
-- ------------------------------------------------------------

\echo '  > Inserting wallet transactions...'

INSERT INTO wallet_transactions (id, wallet_id, amount, balance_before_transaction, balance_after_transaction, transaction_type) VALUES
    ('60000000-0000-0000-0000-000000000001',
     '50000000-0000-0000-0000-000000000003',
     500000.00,
     0.00,
     500000.00,
     'TOP_UP');

\echo '  > Wallet transactions inserted: 1 riwayat top-up untuk buyer'

-- ------------------------------------------------------------
-- 2.8 DELIVERY METHODS — 3 metode pengiriman
-- ------------------------------------------------------------

\echo '  > Inserting delivery methods...'

INSERT INTO delivery (id, delivery_method, price) VALUES
    ('70000000-0000-0000-0000-000000000001', 'INSTANT',  25000.00),
    ('70000000-0000-0000-0000-000000000002', 'NEXT_DAY', 15000.00),
    ('70000000-0000-0000-0000-000000000003', 'REGULAR',  10000.00);

\echo '  > Delivery methods inserted: INSTANT (25k), NEXT_DAY (15k), REGULAR (10k)'

-- ------------------------------------------------------------
-- 2.9 USER ADDRESSES — Alamat pengiriman untuk buyer_andi
-- ------------------------------------------------------------

\echo '  > Inserting user addresses...'

INSERT INTO user_addresses (id, user_id, address_name, is_default, receiver_name, receiver_phone, street_address, district, city, province, postal_code) VALUES
    -- Alamat utama (default)
    ('80000000-0000-0000-0000-000000000001',
     '00000000-0000-0000-0000-000000000003',
     'Rumah',
     true,
     'Andi Pratama',
     '081234567890',
     'Jl. Kebon Jeruk No. 45, RT 03/RW 07',
     'Kebon Jeruk',
     'Jakarta Barat',
     'DKI Jakarta',
     '11530'),

    -- Alamat kantor (secondary)
    ('80000000-0000-0000-0000-000000000002',
     '00000000-0000-0000-0000-000000000003',
     'Kantor',
     false,
     'Andi Pratama',
     '081234567890',
     'Jl. Sudirman Kav. 52-53, Gedung Seapedia Tower Lt. 8',
     'Senayan',
     'Jakarta Pusat',
     'DKI Jakarta',
     '10270');

\echo '  > User addresses inserted: 2 alamat untuk buyer_andi'

-- ------------------------------------------------------------
-- 2.10 APP REVIEWS — Beberapa review aplikasi
-- ------------------------------------------------------------

\echo '  > Inserting app reviews...'

INSERT INTO app_reviews (id, reviewer, rating, content) VALUES
    ('90000000-0000-0000-0000-000000000001',
     'Andi Pratama',
     5.0,
     'Aplikasi yang sangat membantu! Saya bisa membeli ikan segar langsung dari nelayan tanpa harus ke pasar. Pengirimannya cepat dan ikan selalu dalam kondisi segar.'),

    ('90000000-0000-0000-0000-000000000002',
     'Budi Santoso',
     4.5,
     'Platform yang bagus untuk jualan seafood. Fiturnya lengkap dan mudah digunakan. Harapannya bisa ada fitur promo voucher di masa depan.'),

    ('90000000-0000-0000-0000-000000000003',
     'Siti Rahayu',
     4.0,
     'Senang bisa belanja seafood segar di sini. Harganya terjangkau dan kualitasnya tidak mengecewakan. Perlu peningkatan di bagian search produk.');

\echo '  > App reviews inserted: 3 review'

-- ============================================================
-- SELESAI — Ringkasan Data
-- ============================================================

\echo ''
\echo '============================================================'
\echo '  SEED SELESAI! Ringkasan Data:'
\echo '============================================================'
\echo ''
\echo '  USERS (password: Password123!)'
\echo '  ┌─────────────────┬──────────────────────────┬───────────┐'
\echo '  │ Username        │ Email                    │ Role      │'
\echo '  ├─────────────────┼──────────────────────────┼───────────┤'
\echo '  │ admin           │ admin@seapedia.com        │ ADMIN     │'
\echo '  │ seller_budi     │ budi@seapedia.com         │ NON_ADMIN │'
\echo '  │ buyer_andi      │ andi@seapedia.com         │ NON_ADMIN │'
\echo '  │ driver_reza     │ reza@seapedia.com         │ NON_ADMIN │'
\echo '  └─────────────────┴──────────────────────────┴───────────┘'
\echo ''
\echo '  STORE   : Toko Budi Seafood (milik seller_budi)'
\echo '  PRODUK  : 8 produk seafood tersedia'
\echo '  KATEGORI: 6 kategori (Ikan Segar, Udang, Cumi, Olahan, dll)'
\echo '  DELIVERY: INSTANT (25k) | NEXT_DAY (15k) | REGULAR (10k)'
\echo '  WALLET  : buyer_andi sudah punya saldo Rp 500.000'
\echo '  ALAMAT  : 2 alamat untuk buyer_andi (Rumah & Kantor)'
\echo ''
\echo '  ALUR TESTING:'
\echo '  1. Login sebagai seller_budi → select role SELLER → register/lihat store'
\echo '  2. Login sebagai buyer_andi  → select role BUYER  → browse produk → cart → checkout'
\echo '  3. Login sebagai admin       → akses admin features'
\echo '  4. Login sebagai driver_reza → (driver flow jika sudah ada)'
\echo '============================================================'

-- Verifikasi data
\echo ''
\echo '  Verifikasi count data:'

SELECT 'users'              AS table_name, COUNT(*) AS total FROM users
UNION ALL
SELECT 'stores',            COUNT(*) FROM stores
UNION ALL
SELECT 'drivers',           COUNT(*) FROM drivers
UNION ALL
SELECT 'categories',        COUNT(*) FROM categories
UNION ALL
SELECT 'products',          COUNT(*) FROM products
UNION ALL
SELECT 'wallets',           COUNT(*) FROM wallets
UNION ALL
SELECT 'wallet_transactions', COUNT(*) FROM wallet_transactions
UNION ALL
SELECT 'delivery',          COUNT(*) FROM delivery
UNION ALL
SELECT 'user_addresses',    COUNT(*) FROM user_addresses
UNION ALL
SELECT 'app_reviews',       COUNT(*) FROM app_reviews;
