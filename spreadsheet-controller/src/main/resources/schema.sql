-- Create sheet table
CREATE TABLE IF NOT EXISTS sheet (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    row_count INTEGER,
    column_count INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create sheet_data table
CREATE TABLE IF NOT EXISTS sheet_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sheet_id BIGINT NOT NULL,
    row_number INTEGER NOT NULL,
    column_number INTEGER NOT NULL,
    cell_value TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (sheet_id) REFERENCES sheet(id) ON DELETE CASCADE,
    UNIQUE KEY unique_cell (sheet_id, row_number, column_number)
);
