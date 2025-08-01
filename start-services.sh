#!/bin/bash

# Start all services for the spreadsheet application
echo "Starting Spreadsheet Application Services..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker first."
    exit 1
fi

# Start all services using docker-compose
echo "Starting PostgreSQL, Redis, and pgAdmin..."
docker-compose up -d

# Wait for services to be healthy
echo "Waiting for services to be ready..."
sleep 10

# Check PostgreSQL health
echo "Checking PostgreSQL connection..."
until docker exec spreadsheet-postgres pg_isready -U postgres > /dev/null 2>&1; do
    echo "Waiting for PostgreSQL to be ready..."
    sleep 2
done
echo "✓ PostgreSQL is ready"

# Check Redis health
echo "Checking Redis connection..."
until docker exec spreadsheet-redis redis-cli ping > /dev/null 2>&1; do
    echo "Waiting for Redis to be ready..."
    sleep 2
done
echo "✓ Redis is ready"

echo ""
echo "All services are running!"
echo ""
echo "Service URLs:"
echo "- PostgreSQL: localhost:5433"
echo "- Redis: localhost:6379"
echo "- pgAdmin: http://localhost:8081"
echo "  - Email: admin@spreadsheet.com"
echo "  - Password: admin123"
echo ""
echo "To stop all services, run: docker-compose down"
echo "To view logs, run: docker-compose logs -f"
echo ""
echo "You can now start your Spring Boot application!"
