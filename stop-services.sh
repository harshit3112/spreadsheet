#!/bin/bash

echo "🛑 Stopping Spreadsheet Application Services"
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

# Stop Spring Boot application
if check_port 8080; then
    echo "🌱 Stopping Spring Boot application on port 8080..."
    PID=$(lsof -ti:8080)
    if [ ! -z "$PID" ]; then
        kill -TERM $PID
        echo "✅ Spring Boot application stopped"
    fi
else
    echo "ℹ️  Spring Boot application is not running on port 8080"
fi

# Stop Docker containers
echo "📦 Stopping Docker containers..."
docker compose down

echo ""
echo "✅ All services stopped successfully!"
echo ""
echo "🚀 To start services again, run:"
echo "  ./start-services.sh"
