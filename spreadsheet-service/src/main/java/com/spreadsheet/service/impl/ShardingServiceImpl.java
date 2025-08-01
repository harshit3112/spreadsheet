package com.spreadsheet.service.impl;

import com.spreadsheet.service.config.ShardingConfig;
import com.spreadsheet.service.ShardingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// Import ShardContextHolder from service config
import com.spreadsheet.service.config.ShardContextHolder;

@Service
@Slf4j
public class ShardingServiceImpl implements ShardingService {

    @Override
    public void setShardForSheet(Long sheetId) {
        int shard = ShardingConfig.getShardForSheet(sheetId);
        
        // Set the shard context for database routing
        ShardContextHolder.setCurrentShard(shard);
        
        logShardAssignment(sheetId, "Sheet", shard);
    }

    @Override
    public String getDatabaseNameForShard(int shardNumber) {
        return ShardingConfig.getDatabaseNameForShard(shardNumber);
    }

    @Override
    public void logShardAssignment(Object entityId, String entityType, int shardNumber) {
        String databaseName = getDatabaseNameForShard(shardNumber);
        log.info("SHARDING: {} ID {} assigned to Shard {} (Database: {})", 
                entityType, entityId, shardNumber, databaseName);
    }
}
