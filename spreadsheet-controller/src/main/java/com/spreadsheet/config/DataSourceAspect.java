package com.spreadsheet.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
@Slf4j
public class DataSourceAspect {

    @Around("@annotation(readOnlyRepository)")
    public Object routeToReadDataSource(ProceedingJoinPoint joinPoint, ReadOnlyRepository readOnlyRepository) throws Throwable {
        try {
            DataSourceContextHolder.setDataSourceType(DataSourceType.READ);
            log.debug("Routing to READ datasource for method: {}", joinPoint.getSignature().getName());
            return joinPoint.proceed();
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }
    }

    @Around("@within(org.springframework.stereotype.Repository) && !@annotation(ReadOnlyRepository)")
    public Object routeToWriteDataSource(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            DataSourceContextHolder.setDataSourceType(DataSourceType.WRITE);
            log.debug("Routing to WRITE datasource for method: {}", joinPoint.getSignature().getName());
            return joinPoint.proceed();
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }
    }
}
