-- PostgreSQL Database Setup Script for Spreadsheet Application
-- Run this script as a PostgreSQL superuser (e.g., postgres)

-- Create database user
CREATE USER spreadsheet_user WITH PASSWORD 'spreadsheet_password';

-- Create databases for different environments
CREATE DATABASE spreadsheet_db OWNER spreadsheet_user;
CREATE DATABASE spreadsheet_dev OWNER spreadsheet_user;
CREATE DATABASE spreadsheet_prod OWNER spreadsheet_user;

-- Create shard databases
CREATE DATABASE spreadsheet_db_shard_0 OWNER spreadsheet_user;
CREATE DATABASE spreadsheet_db_shard_1 OWNER spreadsheet_user;
CREATE DATABASE spreadsheet_db_shard_2 OWNER spreadsheet_user;
CREATE DATABASE spreadsheet_db_shard_3 OWNER spreadsheet_user;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE spreadsheet_db TO spreadsheet_user;
GRANT ALL PRIVILEGES ON DATABASE spreadsheet_dev TO spreadsheet_user;
GRANT ALL PRIVILEGES ON DATABASE spreadsheet_prod TO spreadsheet_user;
GRANT ALL PRIVILEGES ON DATABASE spreadsheet_db_shard_0 TO spreadsheet_user;
GRANT ALL PRIVILEGES ON DATABASE spreadsheet_db_shard_1 TO spreadsheet_user;
GRANT ALL PRIVILEGES ON DATABASE spreadsheet_db_shard_2 TO spreadsheet_user;
GRANT ALL PRIVILEGES ON DATABASE spreadsheet_db_shard_3 TO spreadsheet_user;

-- Connect to each database and grant schema privileges
\c spreadsheet_db;
GRANT ALL ON SCHEMA public TO spreadsheet_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO spreadsheet_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO spreadsheet_user;

\c spreadsheet_dev;
GRANT ALL ON SCHEMA public TO spreadsheet_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO spreadsheet_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO spreadsheet_user;

\c spreadsheet_prod;
GRANT ALL ON SCHEMA public TO spreadsheet_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO spreadsheet_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO spreadsheet_user;

-- Create extensions (if needed)
\c spreadsheet_db;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

\c spreadsheet_dev;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

\c spreadsheet_prod;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Display created databases
\l
