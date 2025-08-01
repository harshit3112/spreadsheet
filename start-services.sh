#!/bin/bash

echo "🚀 Starting Spreadsheet Application Services"
echo "============================================="

# Function to check if a port is in use
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
        return 0
    else
        return 1
    fi
}

# Start database if not running
if ! check_port 5433; then
    echo "📦 Starting PostgreSQL database..."
    ./start-database.sh
else
    echo "✅ PostgreSQL database is already running on port 5433"
fi

# Check if Redis is needed (based on the logs, it's trying to connect to Redis)
if ! check_port 6379; then
    echo "⚠️  Redis is not running on port 6379. Starting Redis container..."
    docker compose up -d redis 2>/dev/null || echo "⚠️  Redis service not found in docker-compose.yml"
else
    echo "✅ Redis is already running on port 6379"
fi

# Start Spring Boot application if not running
if ! check_port 8080; then
    echo "🌱 Starting Spring Boot application..."
    cd spreadsheet-controller
    mvn spring-boot:run
else
    echo "✅ Spring Boot application is already running on port 8080"
    echo "🌐 Application is accessible at: http://localhost:8080"
    echo "📚 API Documentation: http://localhost:8080/swagger-ui.html"
fi
