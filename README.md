# Seapedia API

[English](#english) | [Bahasa Indonesia](#bahasa-indonesia)

---

## English

Seapedia API backend application built using Spring Boot 3, Java 21, and PostgreSQL.

### Prerequisites

Before starting, ensure you have installed:
- [Java 21](https://adoptium.net/temurin/releases/?version=21) or newer.
- [Maven](https://maven.apache.org/download.cgi) (optional, if using `./mvnw`).
- [Docker](https://www.docker.com/get-started) and [Docker Compose](https://docs.docker.com/compose/install/) (optional, for running via container).
- [PostgreSQL](https://www.postgresql.org/download/) (if running the application locally without Docker).

### Installation

#### 1. Clone Repository
```bash
git clone https://github.com/harisherdiansyah/seapedia-api.git
cd seapedia-api
```

#### 2. Environment Configuration
Copy the `.env.example` file (if available) or create a new `.env` file in the root directory and adjust the configuration:
```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=seapedia_db
DB_USERNAME=postgres
DB_PASSWORD=your_password
TOKEN_SECRET=your_jwt_secret_key
DOCKER_USERNAME=your_docker_username
```

### How to Run the Application

#### Option 1: Run Locally (Development)

1. **Prepare Database**: Ensure PostgreSQL is running and a database with the name specified in `.env` has been created.
2. **Build and Run**:
   ```bash
   ./mvnw spring-boot:run
   ```

#### Option 2: Run Using Docker Compose

This method will run the application along with the PostgreSQL database in containers:
```bash
docker-compose up -d
```
The application will be available at `http://localhost:8080`.

### API Documentation

Once the application is running, you can access the API documentation (Swagger UI) via:
`http://localhost:8080/swagger-ui/index.html`

### Testing

To run unit and integration tests:
```bash
./mvnw test
```

---

## Bahasa Indonesia

Aplikasi backend Seapedia API dibangun menggunakan Spring Boot 3, Java 21, dan PostgreSQL.

### Prasyarat

Sebelum memulai, pastikan Anda telah menginstal:
- [Java 21](https://adoptium.net/temurin/releases/?version=21) atau versi terbaru.
- [Maven](https://maven.apache.org/download.cgi) (opsional, jika menggunakan `./mvnw`).
- [Docker](https://www.docker.com/get-started) dan [Docker Compose](https://docs.docker.com/compose/install/) (opsional, untuk menjalankan via container).
- [PostgreSQL](https://www.postgresql.org/download/) (jika menjalankan aplikasi secara lokal tanpa Docker).

### Instalasi

#### 1. Clone Repositori
```bash
git clone https://github.com/harisherdiansyah/seapedia-api.git
cd seapedia-api
```

#### 2. Konfigurasi Lingkungan
Salin file `.env.example` (jika ada) atau buat file `.env` baru di root direktori dan sesuaikan konfigurasinya:
```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=seapedia_db
DB_USERNAME=postgres
DB_PASSWORD=your_password
TOKEN_SECRET=your_jwt_secret_key
DOCKER_USERNAME=your_docker_username
```

### Cara Menjalankan Aplikasi

#### Opsi 1: Menjalankan Secara Lokal (Development)

1. **Persiapkan Database**: Pastikan PostgreSQL berjalan dan database dengan nama yang sesuai di `.env` sudah dibuat.
2. **Build dan Jalankan**:
   ```bash
   ./mvnw spring-boot:run
   ```

#### Opsi 2: Menjalankan Menggunakan Docker Compose

Cara ini akan menjalankan aplikasi beserta database PostgreSQL dalam container:
```bash
docker-compose up -d
```
Aplikasi akan tersedia di `http://localhost:8080`.

### Dokumentasi API

Setelah aplikasi berjalan, Anda dapat mengakses dokumentasi API (Swagger UI) melalui:
`http://localhost:8080/swagger-ui/index.html`

### Pengujian

Untuk menjalankan unit test dan integration test:
```bash
./mvnw test
```
