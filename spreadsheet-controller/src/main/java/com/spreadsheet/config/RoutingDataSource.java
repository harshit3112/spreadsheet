package com.spreadsheet.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        // First check if there's a shard context set (using controller module's ShardContextHolder)
        Integer currentShard = ShardContextHolder.getCurrentShard();
        if (currentShard != null) {
            // Return shard-specific key
            return "SHARD_" + currentShard;
        }
        
        // Fall back to read/write routing
        DataSourceType dataSourceType = DataSourceContextHolder.getDataSourceType();
        if (dataSourceType != null) {
            return dataSourceType;
        }
        
        // Default to WRITE if no context is set
        return DataSourceType.WRITE;
    }
}
