-- Initial data for testing
INSERT INTO sheet (name, description, row_count, column_count, created_at, updated_at) 
VALUES ('Sample Sheet', 'A sample spreadsheet for testing', 10, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO sheet_data (sheet_id, row_number, column_number, cell_value, created_at, updated_at)
VALUES (1, 1, 1, 'Header 1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO sheet_data (sheet_id, row_number, column_number, cell_value, created_at, updated_at)
VALUES (1, 1, 2, 'Header 2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO sheet_data (sheet_id, row_number, column_number, cell_value, created_at, updated_at)
VALUES (1, 2, 1, 'Data 1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO sheet_data (sheet_id, row_number, column_number, cell_value, created_at, updated_at)
VALUES (1, 2, 2, 'Data 2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
