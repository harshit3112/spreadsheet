package com.spreadsheet.service;

public interface ShardingService {
    
    /**
     * Get the shard number for a given sheet ID and set shard context
     *
     * @param sheetId The sheet ID
     */
    void setShardForSheet(Long sheetId);
    
    /**
     * Get the database name for a specific shard
     * @param shardNumber The shard number
     * @return The database name for the shard
     */
    String getDatabaseNameForShard(int shardNumber);

    
    /**
     * Log sharding information for debugging
     * @param entityId The entity ID (sheet or user)
     * @param entityType The type of entity
     * @param shardNumber The assigned shard
     */
    void logShardAssignment(Object entityId, String entityType, int shardNumber);
}
