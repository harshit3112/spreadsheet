# High Load Architecture Implementation

This document describes the implementation of three major features for handling high load in the spreadsheet service:

## 1. Redis-based Distributed Locking

### Overview
Implemented distributed locking using Redis and Redisson to prevent concurrent updates to sheet data across multiple application instances.

### Components
- **DistributedLockService**: Interface for distributed locking operations
- **DistributedLockServiceImpl**: Implementation using Redisson client
- **RedisConfig**: Configuration for Redis connection and Redisson client
- **SheetDataServiceImpl**: Updated to use distributed locks instead of local ReentrantLock

### Key Features
- **Lock Key Pattern**: `sheet:update:{sheetId}` - ensures sheet-level locking
- **Timeout Configuration**: 10 seconds wait time, 30 seconds lease time
- **Automatic Lock Release**: Ensures locks are released even if application crashes
- **Thread Safety**: Uses ThreadLocal for lock context management

### Usage
```java
String lockKey = "sheet:update:" + sheetId;
return distributedLockService.executeWithLock(lockKey, () -> {
    // Critical section - sheet update logic
    return updateSheetData(sheetId, request);
});
```

### Benefits
- Prevents data corruption from concurrent updates
- Scales across multiple application instances
- Automatic failover and lock expiration
- Performance monitoring through Redis metrics

## 2. Read/Write Database Separation

### Overview
Implemented master-slave database architecture to handle 10:1 read/write ratio efficiently by routing read operations to read replicas and write operations to master database.

### Components
- **DatabaseConfig**: Configuration for multiple data sources
- **RoutingDataSource**: Custom data source that routes queries based on operation type
- **DataSourceContextHolder**: ThreadLocal holder for current data source type
- **DataSourceAspect**: AOP aspect for automatic routing based on annotations
- **ReadOnlyRepository**: Annotation to mark read-only operations

### Architecture
```
Application Layer
       |
   AOP Aspect (DataSourceAspect)
       |
   Routing DataSource
      / \
     /   \
Write DB  Read DB(s)
(Master)  (Replicas)
```

### Configuration
```yaml
spring:
  datasource:
    write:  # Master database for writes
      url: jdbc:postgresql://localhost:5433/spreadsheet_db
      hikari:
        maximum-pool-size: 10
    read:   # Replica database for reads
      url: jdbc:postgresql://localhost:5433/spreadsheet_db
      hikari:
        maximum-pool-size: 20  # Higher pool size for read operations
```

### Usage
```java
@ReadOnlyRepository  // Routes to read database
public List<SheetData> findBySheetId(Long sheetId) {
    // Read operation
}

// Write operations automatically route to write database
public SheetData save(SheetData sheetData) {
    // Write operation
}
```

### Benefits
- **Load Distribution**: Separates read and write loads
- **Scalability**: Can add multiple read replicas
- **Performance**: Optimized connection pools for different operation types
- **Automatic Routing**: Transparent to application code

## 3. Database Sharding

### Overview
Implemented horizontal sharding strategy to distribute data across multiple database shards based on sheet ID and user ID for better load distribution and scalability.

### Components
- **ShardingConfig**: Configuration and utility methods for sharding logic
- **Consistent Hashing**: Uses hash-based sharding for even distribution

### Sharding Strategy
- **Shard Count**: 4 shards (configurable)
- **Shard Key**: Sheet ID for sheet-related operations, User ID for user-related operations
- **Hash Function**: `Math.abs(id.hashCode()) % SHARD_COUNT`
- **Database Naming**: `spreadsheet_db_shard_{shardNumber}`

### Implementation
```java
public static int getShardForSheet(Long sheetId) {
    return Math.abs(sheetId.hashCode()) % SHARD_COUNT;
}

public static int getShardForUser(String userId) {
    return Math.abs(userId.hashCode()) % SHARD_COUNT;
}
```

### Benefits
- **Horizontal Scaling**: Distributes data across multiple databases
- **Load Distribution**: Even distribution using consistent hashing
- **Performance**: Reduces load on individual database instances
- **Flexibility**: Easy to add more shards as data grows

## Infrastructure Setup

### Docker Compose Services
```yaml
services:
  postgres:    # Main database
  redis:       # Distributed locking and caching
  pgadmin:     # Database management
```

### Redis Configuration
- **Image**: redis:7-alpine
- **Persistence**: AOF (Append Only File) enabled
- **Health Checks**: Built-in Redis ping
- **Port**: 6379

### Database Configuration
- **Image**: postgres:15-alpine
- **Port**: 5433
- **Health Checks**: pg_isready
- **Initialization**: Automated schema setup

## Performance Considerations

### Connection Pooling
- **Write Pool**: 10 connections (lower for writes)
- **Read Pool**: 20 connections (higher for reads)
- **Redis Pool**: 10 connections with 5 minimum idle

### Caching Strategy
- **Redis**: Used for distributed locking and can be extended for data caching
- **Connection Caching**: HikariCP for optimal database connection management

### Monitoring Points
- **Lock Acquisition Time**: Monitor Redis lock performance
- **Database Connection Usage**: Track pool utilization
- **Shard Distribution**: Monitor data distribution across shards
- **Read/Write Ratio**: Validate 10:1 assumption

## Deployment Considerations

### Environment Profiles
- **Development**: Single database instance
- **Production**: Separate read/write databases with multiple shards
- **Testing**: In-memory H2 database

### Scaling Strategy
1. **Vertical Scaling**: Increase database resources
2. **Read Replicas**: Add more read-only database instances
3. **Shard Expansion**: Increase shard count (requires data migration)
4. **Application Instances**: Scale horizontally with load balancer

### High Availability
- **Redis Cluster**: For distributed lock high availability
- **Database Replication**: Master-slave setup with failover
- **Application Clustering**: Multiple instances behind load balancer

## Future Enhancements

1. **Dynamic Sharding**: Implement resharding without downtime
2. **Cross-Shard Queries**: Implement distributed query capabilities
3. **Caching Layer**: Add Redis-based data caching
4. **Metrics and Monitoring**: Implement comprehensive monitoring
5. **Auto-scaling**: Implement automatic scaling based on load

## Testing Strategy

1. **Load Testing**: Simulate high concurrent user scenarios
2. **Failover Testing**: Test Redis and database failover scenarios
3. **Performance Testing**: Measure response times under load
4. **Data Consistency**: Verify distributed locking prevents race conditions

This architecture provides a solid foundation for handling high load while maintaining data consistency and system reliability.
