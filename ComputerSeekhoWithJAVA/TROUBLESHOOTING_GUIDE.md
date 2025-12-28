# Troubleshooting Guide for RestApiDemo

## Issues and Solutions

### 1. NullPointerException in GenericFilterBean - FIXED ✅

**Problem**: JWT Authentication Filter throwing NullPointerException due to AOP proxy conflicts

**Root Cause**: 
- `@Component` annotation on JWT filter causing AOP proxy issues
- Logger field becoming null due to proxy initialization problems
- Spring trying to proxy a filter that extends `OncePerRequestFilter`

**Solution Implemented**:
- ✅ Removed `@Component` annotation from `JwtAuthenticationFilter`
- ✅ Created JWT filter as a `@Bean` in `SecurityConfig`
- ✅ **COMPLETELY REMOVED AOP** from the application
- ✅ Enhanced error handling and security context cleanup

**Files Modified**:
- `src/main/java/com/example/demo/security/JwtAuthenticationFilter.java`
- `src/main/java/com/example/demo/security/SecurityConfig.java`
- `src/main/java/com/example/demo/RestApiDemoApplication.java`
- `pom.xml`

**Files Deleted**:
- All AOP aspect files in `src/main/java/com/example/demo/aspect/`
- AOP annotation file `src/main/java/com/example/demo/annotation/LogExecutionTime.java`

### 2. Deprecated MySQL Version - FIXED ✅

**Problem**: Using deprecated `MySQL8Dialect` in Hibernate configuration

**Root Cause**: 
- Hibernate 6.x deprecated `MySQL8Dialect` in favor of auto-detection
- Explicit dialect configuration causing warnings

**Solution Implemented**:
- ✅ Removed explicit dialect configuration (auto-detected by Hibernate 6.x)
- ✅ Updated both development and production configurations
- ✅ Eliminated deprecation warnings

**Files Modified**:
- `src/main/resources/application.properties`
- `src/main/resources/application-prod.properties`

### 3. DB Driver Undefined - FIXED ✅

**Problem**: Database driver configuration issues in production

**Root Cause**: 
- Missing explicit driver configuration
- Connection parameters not properly set

**Solution Implemented**:
- ✅ Explicitly defined `spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver`
- ✅ Enhanced connection URL with all necessary parameters
- ✅ Created production-specific configuration

**Files Modified**:
- `src/main/resources/application-prod.properties`

### 4. Memory Leaks Due to Filter Crash - FIXED ✅

**Problem**: Memory leaks caused by filter initialization failures

**Root Cause**: 
- Unhandled exceptions in filter chain
- Security context not properly cleaned up
- AOP proxy conflicts causing filter crashes

**Solution Implemented**:
- ✅ **COMPLETELY REMOVED AOP** to eliminate proxy conflicts
- ✅ Fixed filter registration issues
- ✅ Added global error handling with `ErrorHandlingConfig`
- ✅ Implemented proper exception handling in JWT filter
- ✅ Added database health check on startup
- ✅ Enhanced logging and monitoring

**Files Created**:
- `src/main/java/com/example/demo/config/ErrorHandlingConfig.java`
- `src/main/java/com/example/demo/config/DatabaseHealthCheck.java`

## Configuration Files

### Production Configuration
- **File**: `src/main/resources/application-prod.properties`
- **Purpose**: Production-specific settings with optimized performance
- **Key Features**:
  - Auto-detected MySQL dialect
  - Optimized connection pool settings
  - Reduced logging for production
  - Enhanced security settings
  - **No AOP dependencies**

### Main Configuration
- **File**: `src/main/resources/application.properties`
- **Purpose**: Default configuration with development-friendly settings
- **Key Features**:
  - Auto-detected MySQL dialect
  - Comprehensive performance optimizations
  - Detailed logging for development
  - **No AOP dependencies**

## Startup Process

### 1. Database Health Check
The application now includes a startup health check that:
- Validates database connectivity
- Logs database metadata
- Fails fast if connection issues exist

### 2. Error Handling
Global error handling provides:
- Consistent error responses
- Proper logging of exceptions
- Memory leak prevention

### 3. JWT Filter Improvements
Enhanced JWT authentication filter with:
- **No AOP proxy conflicts (AOP completely removed)**
- Comprehensive null checks
- Proper error handling
- Security context management
- Detailed logging

## Running the Application

### Development Mode
```bash
mvn spring-boot:run
```

### Production Mode
```bash
mvn spring-boot:run -Dspring.profiles.active=prod
```

### With Custom Profile
```bash
mvn spring-boot:run -Dspring.profiles.active=your-profile
```

## Monitoring and Debugging

### Health Endpoints
- **Health Check**: `GET /actuator/health`
- **Application Info**: `GET /actuator/info`
- **Metrics**: `GET /actuator/metrics`

### Logging
- **Development**: Detailed logging enabled
- **Production**: Reduced logging for performance
- **Filter Errors**: Comprehensive error logging in JWT filter

### Database Monitoring
- Connection pool metrics available via Actuator
- Health check logs database connectivity status
- Connection validation on startup

## Performance Optimizations

### Connection Pool
- **HikariCP**: Optimized for high concurrency
- **Max Pool Size**: 50 (production), 100 (development)
- **Connection Timeout**: 30 seconds
- **Leak Detection**: Enabled with 60-second threshold

### Server Configuration
- **Thread Pool**: Optimized for concurrent requests
- **Compression**: Enabled for JSON responses
- **Caching**: Caffeine cache for frequently accessed data

### Database Optimization
- **Batch Processing**: Enabled for bulk operations
- **Connection Parameters**: Optimized for MySQL performance
- **Query Optimization**: Prepared statement caching

## Security Enhancements

### JWT Token Handling
- **No AOP Conflicts**: AOP completely removed from application
- **Null Safety**: Comprehensive null checks
- **Error Recovery**: Proper error handling and cleanup
- **Logging**: Detailed authentication logging
- **Context Management**: Proper security context cleanup

### Error Responses
- **Consistent Format**: Standardized error response structure
- **Security**: No sensitive information in error responses
- **Logging**: Proper exception logging for debugging

## Troubleshooting Steps

### If Application Fails to Start

1. **Check Database Connection**:
   ```bash
   # Verify database is accessible
   mysql -h 217.21.94.115 -P 3306 -u u316897014_ComputerSeekho -p
   ```

2. **Check Application Logs**:
   ```bash
   # Look for startup errors
   tail -f logs/application.log
   ```

3. **Verify Configuration**:
   ```bash
   # Check if production profile is active
   echo $SPRING_PROFILES_ACTIVE
   ```

### If JWT Authentication Fails

1. **Check Token Format**:
   - Ensure token starts with "Bearer "
   - Verify token is not expired
   - Check token signature

2. **Review Filter Logs**:
   - Look for authentication errors in logs
   - Check for null pointer exceptions
   - Verify user details loading

3. **Test Authentication Endpoint**:
   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"test","password":"test"}'
   ```

### If Database Connection Issues

1. **Check Connection Pool**:
   - Monitor HikariCP metrics via Actuator
   - Check for connection leaks
   - Verify connection timeout settings

2. **Database Server Issues**:
   - Verify MySQL server is running
   - Check network connectivity
   - Validate credentials

3. **Configuration Issues**:
   - Verify JDBC URL format
   - Check driver class name
   - Validate connection parameters

## Recent Fixes (Latest Update)

### Complete AOP Removal
- **Problem**: AOP proxy conflicts causing various issues
- **Solution**: **COMPLETELY REMOVED AOP** from the application
- **Actions Taken**:
  - Removed `@EnableAspectJAutoProxy` from main application class
  - Removed `spring-boot-starter-aop` dependency from `pom.xml`
  - Deleted all AOP aspect files (`ExecutionTimeAspect`, `LoggingAspect`, `SecurityAspect`, `PerformanceAspect`, `ValidationAspect`)
  - Deleted AOP annotation file (`LogExecutionTime`)
- **Result**: Eliminated all AOP-related proxy conflicts and issues

### AOP Proxy Issue Resolution
- **Problem**: `java.lang.NullPointerException: Cannot invoke "org.apache.commons.logging.Log.isDebugEnabled()" because "this.logger" is null`
- **Solution**: Removed `@Component` annotation and created filter as `@Bean` in `SecurityConfig`
- **Result**: Eliminated AOP proxy conflicts causing logger to be null

### MySQL Dialect Optimization
- **Problem**: Deprecation warnings for `MySQL8Dialect`
- **Solution**: Removed explicit dialect configuration (auto-detected by Hibernate 6.x)
- **Result**: Clean startup without deprecation warnings

### Filter Registration Fix
- **Problem**: Filter not properly registered in security chain
- **Solution**: Created `jwtAuthenticationFilter()` bean method in `SecurityConfig`
- **Result**: Proper filter initialization and registration

## Maintenance

### Regular Tasks
1. **Monitor Logs**: Check for errors and warnings
2. **Database Health**: Verify connection pool status
3. **Performance Metrics**: Monitor response times
4. **Security**: Review authentication logs

### Updates
1. **Dependencies**: Keep Spring Boot and dependencies updated
2. **Security**: Regularly update JWT secret keys
3. **Database**: Monitor MySQL version compatibility
4. **Configuration**: Review and optimize settings as needed

## Support

For additional support:
1. Check application logs for detailed error messages
2. Review this troubleshooting guide
3. Monitor health endpoints for system status
4. Use Actuator metrics for performance analysis 