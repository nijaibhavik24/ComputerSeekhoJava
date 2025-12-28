# 🚀 **Practical Microservices Implementation Guide**

## 🎯 **Quick Start: Extract Student Service**

### **Step 1: Create Student Service Structure**
```bash
# Create student service directory
mkdir student-service
cd student-service

# Create Maven project structure
mkdir -p src/main/java/com/example/student
mkdir -p src/main/resources
mkdir -p src/test/java
```

### **Step 2: Copy Existing Code**
```bash
# Copy from your current monolithic app
cp ../RestApiDemo/src/main/java/com/example/demo/model/StudentMaster.java src/main/java/com/example/student/model/
cp ../RestApiDemo/src/main/java/com/example/demo/controller/StudentMasterController.java src/main/java/com/example/student/controller/
cp ../RestApiDemo/src/main/java/com/example/demo/service/StudentMasterService.java src/main/java/com/example/student/service/
cp ../RestApiDemo/src/main/java/com/example/demo/service/impl/StudentMasterServiceImpl.java src/main/java/com/example/student/service/impl/
cp ../RestApiDemo/src/main/java/com/example/demo/repository/StudentMasterRepository.java src/main/java/com/example/student/repository/
```

### **Step 3: Update Package Names**
```java
// Update all package declarations from:
package com.example.demo;
// To:
package com.example.student;
```

### **Step 4: Create Application Configuration**
```yaml
# application.yml
server:
  port: 8081

spring:
  application:
    name: student-service
  datasource:
    url: jdbc:mysql://localhost:3306/student_db
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update
```

## 🔧 **Service Discovery Setup**

### **Step 1: Create Eureka Server**
```xml
<!-- eureka-server/pom.xml -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

```java
// EurekaServerApplication.java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

```yaml
# eureka-server/application.yml
server:
  port: 8761

spring:
  application:
    name: eureka-server

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    wait-time-in-ms-when-sync-empty: 0
```

### **Step 2: Update Student Service for Eureka**
```xml
<!-- student-service/pom.xml -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

```java
// StudentServiceApplication.java
@SpringBootApplication
@EnableEurekaClient
public class StudentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudentServiceApplication.class, args);
    }
}
```

```yaml
# student-service/application.yml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

## 🌐 **API Gateway Setup**

### **Step 1: Create API Gateway**
```xml
<!-- api-gateway/pom.xml -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

```java
// ApiGatewayApplication.java
@SpringBootApplication
@EnableEurekaClient
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
```

```yaml
# api-gateway/application.yml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: student-service
          uri: lb://student-service
          predicates:
            - Path=/api/students/**
        - id: enquiry-service
          uri: lb://enquiry-service
          predicates:
            - Path=/api/enquiries/**
        - id: payment-service
          uri: lb://payment-service
          predicates:
            - Path=/api/payments/**, /api/receipts/**

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

## 🔄 **Cross-Service Communication**

### **Step 1: Using RestTemplate**
```java
// StudentController.java
@RestController
@RequestMapping("/api/students")
public class StudentController {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @GetMapping("/{studentId}/with-enquiries")
    public ResponseEntity<StudentWithEnquiriesDTO> getStudentWithEnquiries(@PathVariable Integer studentId) {
        // Get student data
        StudentMaster student = studentService.getStudentById(studentId);
        
        // Call enquiry service
        String enquiryUrl = "http://enquiry-service/api/enquiries/student/" + studentId;
        ResponseEntity<List<Enquiry>> enquiryResponse = restTemplate.getForEntity(enquiryUrl, List.class);
        List<Enquiry> enquiries = enquiryResponse.getBody();
        
        // Combine data
        StudentWithEnquiriesDTO result = new StudentWithEnquiriesDTO(student, enquiries);
        return ResponseEntity.ok(result);
    }
}
```

### **Step 2: Using Feign Client**
```xml
<!-- Add to pom.xml -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

```java
// EnquiryClient.java
@FeignClient(name = "enquiry-service")
public interface EnquiryClient {
    
    @GetMapping("/api/enquiries/student/{studentId}")
    List<Enquiry> getEnquiriesByStudentId(@PathVariable("studentId") Integer studentId);
}
```

```java
// StudentController.java
@RestController
@RequestMapping("/api/students")
public class StudentController {
    
    @Autowired
    private EnquiryClient enquiryClient;
    
    @GetMapping("/{studentId}/with-enquiries")
    public ResponseEntity<StudentWithEnquiriesDTO> getStudentWithEnquiries(@PathVariable Integer studentId) {
        StudentMaster student = studentService.getStudentById(studentId);
        List<Enquiry> enquiries = enquiryClient.getEnquiriesByStudentId(studentId);
        
        StudentWithEnquiriesDTO result = new StudentWithEnquiriesDTO(student, enquiries);
        return ResponseEntity.ok(result);
    }
}
```

## 🐳 **Docker Containerization**

### **Step 1: Create Dockerfile**
```dockerfile
# student-service/Dockerfile
FROM openjdk:11-jre-slim
VOLUME /tmp
COPY target/student-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### **Step 2: Create Docker Compose**
```yaml
# docker-compose.yml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: student_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  eureka-server:
    build: ./eureka-server
    ports:
      - "8761:8761"
    depends_on:
      - mysql

  student-service:
    build: ./student-service
    ports:
      - "8081:8081"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/student_db
    depends_on:
      - mysql
      - eureka-server

  api-gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    depends_on:
      - eureka-server

volumes:
  mysql_data:
```

## 🚀 **Deployment Commands**

### **Step 1: Build and Run Locally**
```bash
# Start Eureka Server
cd eureka-server
mvn spring-boot:run

# Start Student Service
cd ../student-service
mvn spring-boot:run

# Start API Gateway
cd ../api-gateway
mvn spring-boot:run
```

### **Step 2: Docker Deployment**
```bash
# Build all services
docker-compose build

# Run all services
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs student-service
```

## 📊 **Monitoring & Health Checks**

### **Step 1: Add Actuator**
```xml
<!-- Add to each service pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

### **Step 2: Health Check Endpoints**
```
http://localhost:8081/actuator/health
http://localhost:8082/actuator/health
http://localhost:8083/actuator/health
```

## 🔐 **Security Implementation**

### **Step 1: JWT Authentication**
```xml
<!-- Add to auth-service pom.xml -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
```

```java
// AuthService.java
@Service
public class AuthService {
    
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS512, "secret")
                .compact();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey("secret").parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

### **Step 2: API Gateway Security**
```java
// JwtAuthenticationFilter.java
@Component
public class JwtAuthenticationFilter implements GlobalFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        String token = request.getHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            // Validate token
            if (validateToken(token)) {
                return chain.filter(exchange);
            }
        }
        
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
```

## 📈 **Testing Strategy**

### **Step 1: Unit Tests**
```java
// StudentServiceTest.java
@SpringBootTest
class StudentServiceTest {
    
    @Autowired
    private StudentService studentService;
    
    @Test
    void testGetStudentById() {
        StudentMaster student = studentService.getStudentById(1);
        assertNotNull(student);
        assertEquals("John Doe", student.getStudentName());
    }
}
```

### **Step 2: Integration Tests**
```java
// StudentControllerIntegrationTest.java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testGetStudentById() {
        ResponseEntity<StudentMaster> response = restTemplate.getForEntity(
            "/api/students/1", StudentMaster.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
```

## 🎯 **Next Steps**

1. **Start with Student Service** - Extract and test independently
2. **Add Eureka Server** - For service discovery
3. **Create API Gateway** - For routing and security
4. **Extract Other Services** - One by one
5. **Implement Cross-Service Communication** - Using RestTemplate or Feign
6. **Add Monitoring** - Health checks and metrics
7. **Containerize** - Docker deployment
8. **Production Deployment** - Kubernetes (optional)

## 📞 **Quick Commands**

```bash
# Create new microservice
spring init --dependencies=web,data-jpa,mysql,cloud-eureka-client my-service

# Build and run
mvn clean install
mvn spring-boot:run

# Docker commands
docker build -t my-service .
docker run -p 8081:8081 my-service

# Check service health
curl http://localhost:8081/actuator/health
```

This practical guide will help you migrate from your current monolithic application to a microservices architecture step by step! 🚀
