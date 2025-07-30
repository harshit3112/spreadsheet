#!/bin/bash

echo "Testing Spreadsheet API..."
echo "=========================="

# Test 1: Create a new sheet
echo "1. Creating a new sheet..."
curl -X POST http://localhost:8080/api/sheet/create \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Spreadsheet",
    "description": "A test spreadsheet for API testing",
    "rowCount": 10,
    "columnCount": 5
  }' | jq '.'

echo -e "\n"

# Test 2: Get the created sheet
echo "2. Getting sheet with ID 1..."
curl -X GET http://localhost:8080/api/sheet/1 | jq '.'

echo -e "\n"

# Test 3: Update cell data
echo "3. Updating cell data..."
curl -X PUT http://localhost:8080/api/sheet-data/update \
  -H "Content-Type: application/json" \
  -d '{
    "sheetId": 1,
    "rowNumber": 1,
    "columnNumber": 1,
    "cellValue": "Header A1"
  }' | jq '.'

echo -e "\n"

# Test 4: Update another cell
echo "4. Updating another cell..."
curl -X PUT http://localhost:8080/api/sheet-data/update \
  -H "Content-Type: application/json" \
  -d '{
    "sheetId": 1,
    "rowNumber": 1,
    "columnNumber": 2,
    "cellValue": "Header B1"
  }' | jq '.'

echo -e "\n"
echo "API testing completed!"
