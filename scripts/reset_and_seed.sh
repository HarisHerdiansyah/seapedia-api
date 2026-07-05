#!/usr/bin/env bash
# =============================================================================
# Seapedia API — Reset & Seed Database
# =============================================================================
# Script ini membaca konfigurasi dari file .env, lalu menjalankan
# reset_and_seed.sql ke database PostgreSQL yang ditentukan.
#
# Usage:
#   ./scripts/reset_and_seed.sh                        (dev: baca .env)
#   ./scripts/reset_and_seed.sh --env-file .env.prod   (prod: baca .env.prod)
#   ./scripts/reset_and_seed.sh --env-file /path/to/custom.env
#
# Flags:
#   --env-file <path>   Path ke file .env yang digunakan (default: .env)
#   --help              Tampilkan bantuan
#
# Override via environment variable juga didukung:
#   DB_HOST=myhost DB_PORT=5432 ./scripts/reset_and_seed.sh
# =============================================================================

set -euo pipefail

# ---- Warna terminal ----
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m' # No Color

# ---- Cari root directory project ----
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
SQL_FILE="${SCRIPT_DIR}/reset_and_seed.sql"

# ---- Parse Arguments ----
ENV_FILE="${PROJECT_ROOT}/.env"   # default

usage() {
    echo ""
    echo -e "${BOLD}Usage:${NC}"
    echo "  ./scripts/reset_and_seed.sh [OPTIONS]"
    echo ""
    echo -e "${BOLD}Options:${NC}"
    echo "  --env-file <path>   Path ke file .env (default: .env di root project)"
    echo "  --help              Tampilkan bantuan ini"
    echo ""
    echo -e "${BOLD}Contoh:${NC}"
    echo "  ./scripts/reset_and_seed.sh                         # Development (baca .env)"
    echo "  ./scripts/reset_and_seed.sh --env-file .env.prod    # Production (baca .env.prod)"
    echo "  ./scripts/reset_and_seed.sh --env-file .env.staging # Staging"
    echo ""
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        --env-file)
            if [[ -z "${2:-}" ]]; then
                echo -e "${RED}[ERROR] --env-file membutuhkan path file sebagai argumen.${NC}"
                usage
                exit 1
            fi
            # Jika path relatif, resolve dari project root
            if [[ "${2}" == /* ]]; then
                ENV_FILE="${2}"
            else
                ENV_FILE="${PROJECT_ROOT}/${2}"
            fi
            shift 2
            ;;
        --help | -h)
            usage
            exit 0
            ;;
        *)
            echo -e "${RED}[ERROR] Argumen tidak dikenali: $1${NC}"
            usage
            exit 1
            ;;
    esac
done

# ---- Deteksi environment berdasarkan nama file .env ----
ENV_LABEL="development"
IS_PRODUCTION=false

ENV_BASENAME="$(basename "${ENV_FILE}")"
if [[ "${ENV_BASENAME}" == *"prod"* ]]; then
    ENV_LABEL="PRODUCTION"
    IS_PRODUCTION=true
elif [[ "${ENV_BASENAME}" == *"staging"* ]]; then
    ENV_LABEL="staging"
fi

# ---- Banner ----
echo -e "${BOLD}${CYAN}"
echo "  ╔══════════════════════════════════════════════════════════╗"
echo "  ║         Seapedia API — Reset & Seed Database             ║"
echo "  ╚══════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# ---- Validasi SQL file ada ----
if [[ ! -f "${SQL_FILE}" ]]; then
    echo -e "${RED}[ERROR] File SQL tidak ditemukan: ${SQL_FILE}${NC}"
    exit 1
fi

# ---- Load variabel dari .env ----
if [[ -f "${ENV_FILE}" ]]; then
    echo -e "${BLUE}[INFO]${NC}  Membaca konfigurasi dari: ${BOLD}${ENV_FILE}${NC}"
    set -a
    source <(grep -v '^\s*#' "${ENV_FILE}" | grep -v '^\s*$')
    set +a
else
    echo -e "${YELLOW}[WARN]${NC}  File .env tidak ditemukan di: ${ENV_FILE}"
    echo -e "${YELLOW}[WARN]${NC}  Menggunakan variabel environment yang sudah ada."
fi

# ---- Ambil nilai konfigurasi (dengan fallback) ----
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-seapedia_db}"
DB_USERNAME="${DB_USERNAME:-postgres}"
DB_PASSWORD="${DB_PASSWORD:-}"

# ---- Tampilkan info environment ----
echo ""
if [[ "${IS_PRODUCTION}" == true ]]; then
    echo -e "${RED}${BOLD}  ⚠️  TARGET ENVIRONMENT: ${ENV_LABEL}  ⚠️${NC}"
else
    echo -e "${GREEN}${BOLD}  Target environment: ${ENV_LABEL}${NC}"
fi

echo ""
echo -e "${BLUE}[INFO]${NC}  Konfigurasi database:"
echo -e "         Host     : ${BOLD}${DB_HOST}${NC}"
echo -e "         Port     : ${BOLD}${DB_PORT}${NC}"
echo -e "         Database : ${BOLD}${DB_NAME}${NC}"
echo -e "         Username : ${BOLD}${DB_USERNAME}${NC}"
echo ""

# ---- Konfirmasi: standard warning ----
echo -e "${YELLOW}[WARN]${NC}  ${BOLD}PERHATIAN: Script ini akan menghapus SEMUA DATA dari database '${DB_NAME}'!${NC}"
echo -e "${YELLOW}[WARN]${NC}  Flyway schema history TIDAK akan dihapus."
echo ""

# ---- Konfirmasi berlapis untuk PRODUCTION ----
if [[ "${IS_PRODUCTION}" == true ]]; then
    echo -e "${RED}${BOLD}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${RED}${BOLD}║  🚨  PERINGATAN KERAS: INI ADALAH DATABASE PRODUCTION!     ║${NC}"
    echo -e "${RED}${BOLD}║                                                             ║${NC}"
    echo -e "${RED}${BOLD}║  Semua data production AKAN DIHAPUS secara permanen!       ║${NC}"
    echo -e "${RED}${BOLD}║  Pastikan Anda sudah melakukan backup sebelum melanjutkan. ║${NC}"
    echo -e "${RED}${BOLD}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""

    # Konfirmasi 1: ketik nama database
    read -r -p "$(echo -e "${BOLD}[PROD LOCK 1/2] Ketik nama database '${DB_NAME}' untuk konfirmasi: ${NC}")" CONFIRM_DBNAME
    if [[ "${CONFIRM_DBNAME}" != "${DB_NAME}" ]]; then
        echo ""
        echo -e "${RED}[ABORT]${NC} Nama database tidak cocok. Operasi dibatalkan."
        exit 1
    fi

    echo ""

    # Konfirmasi 2: ketik kalimat konfirmasi
    CONFIRM_PHRASE="SAYA MENGERTI RISIKO INI"
    read -r -p "$(echo -e "${BOLD}[PROD LOCK 2/2] Ketik '${CONFIRM_PHRASE}' untuk melanjutkan: ${NC}")" CONFIRM_PHRASE_INPUT
    if [[ "${CONFIRM_PHRASE_INPUT}" != "${CONFIRM_PHRASE}" ]]; then
        echo ""
        echo -e "${RED}[ABORT]${NC} Kalimat konfirmasi tidak tepat. Operasi dibatalkan."
        exit 1
    fi

    echo ""
    echo -e "${YELLOW}[WARN]${NC}  Menunggu 5 detik sebelum eksekusi... (Ctrl+C untuk batalkan)"
    for i in 5 4 3 2 1; do
        echo -ne "\r         ${RED}${BOLD}${i}...${NC}  "
        sleep 1
    done
    echo ""

else
    # Konfirmasi biasa untuk non-production
    read -r -p "$(echo -e "${BOLD}Lanjutkan? (ketik 'ya' untuk konfirmasi): ${NC}")" CONFIRM
    if [[ "${CONFIRM}" != "ya" ]]; then
        echo ""
        echo -e "${YELLOW}[ABORT]${NC} Operasi dibatalkan."
        exit 0
    fi
fi

echo ""

# ---- Cek apakah psql tersedia ----
if ! command -v psql &> /dev/null; then
    echo -e "${RED}[ERROR] 'psql' tidak ditemukan. Pastikan PostgreSQL client sudah terinstall.${NC}"
    echo -e "        Install dengan: sudo apt-get install postgresql-client"
    exit 1
fi

# ---- Jalankan SQL script ----
echo -e "${BLUE}[INFO]${NC}  Menjalankan script: ${SQL_FILE}"
echo ""

PGPASSWORD="${DB_PASSWORD}" psql \
    --host="${DB_HOST}" \
    --port="${DB_PORT}" \
    --dbname="${DB_NAME}" \
    --username="${DB_USERNAME}" \
    --file="${SQL_FILE}" \
    --no-psqlrc \
    --set ON_ERROR_STOP=1

# ---- Status akhir ----
EXIT_CODE=$?
echo ""
if [[ ${EXIT_CODE} -eq 0 ]]; then
    echo -e "${GREEN}${BOLD}"
    echo "  ╔══════════════════════════════════════════════════════════╗"
    echo "  ║   ✓  Reset & Seed berhasil!                              ║"
    echo "  ╠══════════════════════════════════════════════════════════╣"
    echo "  ║   Akun yang tersedia (password: Password123!):           ║"
    echo "  ║   • admin@seapedia.com       → ADMIN                    ║"
    echo "  ║   • budi@seapedia.com        → NON_ADMIN (Seller)       ║"
    echo "  ║   • andi@seapedia.com        → NON_ADMIN (Buyer)        ║"
    echo "  ║   • reza@seapedia.com        → NON_ADMIN (Driver)       ║"
    echo "  ╠══════════════════════════════════════════════════════════╣"
    echo "  ║   Wallet buyer_andi sudah terisi: Rp 500.000            ║"
    echo "  ╚══════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
else
    echo -e "${RED}[ERROR]${NC} Script gagal dijalankan (exit code: ${EXIT_CODE})."
    echo -e "         Periksa koneksi database dan log di atas."
    exit ${EXIT_CODE}
fi
