package com.spreadsheet.config;

/**
 * Thread-local context holder for shard information
 * This delegates to the service module's ShardContextHolder to maintain consistency
 */
public class ShardContextHolder {
    
    public static void setCurrentShard(int shardNumber) {
        // Delegate to service module's ShardContextHolder
        com.spreadsheet.service.config.ShardContextHolder.setCurrentShard(shardNumber);
    }
    
    public static Integer getCurrentShard() {
        // Delegate to service module's ShardContextHolder
        return com.spreadsheet.service.config.ShardContextHolder.getCurrentShard();
    }
    
    public static void clearShard() {
        // Delegate to service module's ShardContextHolder
        com.spreadsheet.service.config.ShardContextHolder.clearShard();
    }
    
    public static String getCurrentShardDatabaseName() {
        // Delegate to service module's ShardContextHolder
        return com.spreadsheet.service.config.ShardContextHolder.getCurrentShardDatabaseName();
    }
}
