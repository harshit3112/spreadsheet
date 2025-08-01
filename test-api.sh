#!/bin/bash

echo "Testing Spreadsheet API..."
echo "=========================="

# Test 1: Create a new sheet
echo "1. Creating a new sheet..."
CREATE_RESPONSE=$(curl -s -X POST http://localhost:8080/v1/sheet \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Spreadsheet",
    "userId": "testUser"
  }')
echo "$CREATE_RESPONSE" | jq '.'
SHEET_ID=$(echo "$CREATE_RESPONSE" | jq -r '.data')

echo -e "\n"

# Test 2: Get the created sheet
echo "2. Getting sheet with ID $SHEET_ID..."
curl -s -X GET http://localhost:8080/v1/sheet/$SHEET_ID | jq '.'

echo -e "\n"

# Test 3: Update cell data
echo "3. Updating cell data..."
curl -s -X PUT http://localhost:8080/v1/sheet-data/$SHEET_ID \
  -H "Content-Type: application/json" \
  -d '{
    "cells": [
      {
        "rowNumber": 1,
        "columnNumber": 1,
        "cellType": "VALUE",
        "value": 1
      }
    ]
  }' | jq '.'

echo -e "\n"

# Test 4: Update another cell
echo "4. Updating another cell..."
curl -s -X PUT http://localhost:8080/v1/sheet-data/$SHEET_ID \
  -H "Content-Type: application/json" \
  -d '{
    "cells": [
      {
        "rowNumber": 1,
        "columnNumber": 2,
        "cellType": "VALUE",
        "value": 2
      }
    ]
  }' | jq '.'

echo -e "\n"
echo "API testing completed!"
