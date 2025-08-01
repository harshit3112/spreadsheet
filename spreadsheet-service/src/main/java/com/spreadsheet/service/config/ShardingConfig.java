package com.spreadsheet.service.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ShardingConfig {

    /**
     * Estimate Peak Write Throughput:
     * 	•	3B reads/day = ~35k reads/sec
     * 	•	0.3B writes/day = ~3.5k writes/sec
     * 	•	3.3B writes/day = ~38.5k writes/sec
     * 	•	Assume we want each shard to handle max 2k writes/sec
     * 	•	Then we need at least 19 shards
     * Adding 30–40% buffer ⇒ use ~25–31 shards
     */

    private static final int SHARD_COUNT = 31; // Number of shards
    
    /**
     * Determine shard based on sheet ID using consistent hashing
     * @param sheetId The sheet ID to shard
     * @return Shard number (0-based)
     */
    public static int getShardForSheet(Long sheetId) {
        return Math.abs(sheetId.hashCode()) % SHARD_COUNT;
    }
    
    /**
     * Get database name for a specific shard
     * @param shardNumber The shard number (0-based)
     * @return Database name for the shard
     */
    public static String getDatabaseNameForShard(int shardNumber) {
        return "spreadsheet_db_shard_" + shardNumber;
    }

}
