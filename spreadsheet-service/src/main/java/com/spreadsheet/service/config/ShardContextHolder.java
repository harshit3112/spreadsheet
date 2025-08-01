package com.spreadsheet.service.config;

/**
 * Thread-local context holder for shard information
 */
public class ShardContextHolder {
    
    private static final ThreadLocal<Integer> shardContext = new ThreadLocal<>();
    
    public static void setCurrentShard(int shardNumber) {
        shardContext.set(shardNumber);
    }
    
    public static Integer getCurrentShard() {
        return shardContext.get();
    }
    
    public static void clearShard() {
        shardContext.remove();
    }
    
    public static String getCurrentShardDatabaseName() {
        Integer shard = getCurrentShard();
        if (shard == null) {
            return "spreadsheet_db"; // Default database
        }
        return "spreadsheet_db_shard_" + shard;
    }
}
