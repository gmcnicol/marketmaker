#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER mrktmkr WITH ENCRYPTED PASSWORD 'secret';
    CREATE DATABASE mrktmkr;
    GRANT ALL PRIVILEGES ON DATABASE mrktmkr TO mrktmkr;
EOSQL
