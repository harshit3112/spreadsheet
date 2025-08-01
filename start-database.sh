#!/bin/bash

echo "🚀 Starting PostgreSQL Database for Spreadsheet Application"
echo "=========================================================="

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Check if docker compose is available
if ! docker compose version &> /dev/null; then
    echo "❌ docker compose is not available. Please install Docker Desktop or Docker Compose plugin."
    exit 1
fi

echo "📦 Starting PostgreSQL and pgAdmin containers..."
docker compose up -d

echo "⏳ Waiting for PostgreSQL to be ready..."
sleep 10

# Check if PostgreSQL is ready
until docker exec spreadsheet-postgres pg_isready -U postgres > /dev/null 2>&1; do
    echo "⏳ Waiting for PostgreSQL to start..."
    sleep 2
done

echo "✅ PostgreSQL is ready!"

# Verify database setup
echo "🔍 Verifying database setup..."
docker exec spreadsheet-postgres psql -U postgres -c "\l" | grep spreadsheet

echo ""
echo "🎉 Database setup complete!"
echo ""
echo "📊 Database Information:"
echo "  - PostgreSQL: localhost:5432"
echo "  - Username: spreadsheet_user"
echo "  - Password: spreadsheet_password"
echo "  - Databases: spreadsheet_db, spreadsheet_dev, spreadsheet_prod"
echo ""
echo "🌐 pgAdmin (Optional):"
echo "  - URL: http://localhost:8081"
echo "  - Email: admin@spreadsheet.com"
echo "  - Password: admin123"
echo ""
echo "🚀 You can now start your Spring Boot application:"
echo "  mvn spring-boot:run"
echo ""
echo "📋 To stop the database:"
echo "  docker compose down"
