#!/bin/bash

echo "Testing User Profile Service Integration"
echo "========================================"

# Test 1: Create sheet with valid user ID
echo "Test 1: Creating sheet with valid user ID (1)"
curl -X POST "http://localhost:8080/api/sheets" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Sheet",
    "userId": "1"
  }'
echo -e "\n"

# Test 2: Create sheet with invalid user ID
echo "Test 2: Creating sheet with invalid user ID (999)"
curl -X POST "http://localhost:8080/api/sheets" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Sheet",
    "userId": "999"
  }'
echo -e "\n"

# Test 3: Create sheet with non-numeric user ID
echo "Test 3: Creating sheet with non-numeric user ID (invalid)"
curl -X POST "http://localhost:8080/api/sheets" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Sheet",
    "userId": "invalid"
  }'
echo -e "\n"

echo "User validation integration tests completed!"
