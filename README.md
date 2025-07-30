# Spreadsheet Application

A multi-module Spring Boot application for managing spreadsheets and their data.

## Project Structure

The project is organized into the following modules:

### 1. spreadsheet-model
Contains DTOs and model classes for API communication:
- `ApiResponse<T>` - Generic API response wrapper
- `SheetDto` - Data transfer object for sheet operations
- `SheetDataDto` - Data transfer object for cell data operations

### 2. spreadsheet-repository
Contains JPA entities and repository interfaces:
- `Sheet` entity - Represents a spreadsheet
- `SheetData` entity - Represents cell data in a spreadsheet
- `SheetRepository` - Repository for sheet operations
- `SheetDataRepository` - Repository for sheet data operations

### 3. spreadsheet-service
Contains business logic interfaces and implementations:
- `SheetService` - Interface for sheet operations (createSheet, getSheet)
- `SheetDataService` - Interface for sheet data operations (updateData)
- Service implementations in the `impl` package

### 4. spreadsheet-controller
Contains REST controllers and the main application:
- `SheetController` - REST endpoints for sheet management
- `SheetDataController` - REST endpoints for cell data management
- `SpreadsheetApplication` - Main Spring Boot application class
- `GlobalExceptionHandler` - Global exception handling

## Database Schema

The application uses H2 in-memory database with two tables:

### sheet
- `id` (BIGINT, Primary Key, Auto-generated)
- `name` (VARCHAR(255), Not Null)
- `description` (VARCHAR(500))
- `row_count` (INTEGER)
- `column_count` (INTEGER)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

### sheet_data
- `id` (BIGINT, Primary Key, Auto-generated)
- `sheet_id` (BIGINT, Foreign Key to sheet.id)
- `row_number` (INTEGER, Not Null)
- `column_number` (INTEGER, Not Null)
- `cell_value` (TEXT)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)
- Unique constraint on (sheet_id, row_number, column_number)

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## How to Run

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd spreadsheet
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   cd spreadsheet-controller
   mvn spring-boot:run
   ```

   Or run the main class directly:
   ```bash
   java -jar spreadsheet-controller/target/spreadsheet-controller-1.0.0.jar
   ```

4. **Access the application:**
   - Application: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - H2 Console: http://localhost:8080/h2-console
     - JDBC URL: `jdbc:h2:mem:spreadsheet`
     - Username: `sa`
     - Password: `password`

## API Endpoints

### Sheet Management
- **POST** `/api/sheet/create` - Create a new sheet
- **GET** `/api/sheet/{id}` - Get sheet by ID

### Sheet Data Management
- **PUT** `/api/sheet-data/update` - Update cell data

## Sample API Requests

### Create a Sheet
```bash
curl -X POST http://localhost:8080/api/sheet/create \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My Spreadsheet",
    "description": "A test spreadsheet",
    "rowCount": 10,
    "columnCount": 5
  }'
```

### Get a Sheet
```bash
curl -X GET http://localhost:8080/api/sheet/1
```

### Update Cell Data
```bash
curl -X PUT http://localhost:8080/api/sheet-data/update \
  -H "Content-Type: application/json" \
  -d '{
    "sheetId": 1,
    "rowNumber": 1,
    "columnNumber": 1,
    "cellValue": "Updated Value"
  }'
```

## Architecture Principles

The application follows SOLID principles and best practices:

- **Single Responsibility**: Each class has a single, well-defined purpose
- **Open/Closed**: Code is open for extension but closed for modification
- **Liskov Substitution**: Implementations can be substituted without breaking functionality
- **Interface Segregation**: Interfaces are focused and specific
- **Dependency Inversion**: High-level modules don't depend on low-level modules

### Layer Architecture
- **Controller Layer**: Handles HTTP requests and responses
- **Service Layer**: Contains business logic
- **Repository Layer**: Handles data access
- **Model Layer**: Contains DTOs and data structures

## Features

- ✅ Multi-module Maven project structure
- ✅ Spring Boot 3 with Java 17
- ✅ H2 in-memory database
- ✅ JPA/Hibernate for data persistence
- ✅ RESTful API design
- ✅ Swagger/OpenAPI documentation
- ✅ Global exception handling
- ✅ Input validation
- ✅ Transaction management
- ✅ Lombok for reducing boilerplate code

## Development

To add new features:

1. Add DTOs in the `spreadsheet-model` module
2. Add entities and repositories in the `spreadsheet-repository` module
3. Add service interfaces and implementations in the `spreadsheet-service` module
4. Add controllers in the `spreadsheet-controller` module

## Testing

The application includes sample data that is automatically loaded on startup for testing purposes.
