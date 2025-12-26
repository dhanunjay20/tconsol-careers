# 🚀 Production Deployment Checklist

## ✅ Pre-Deployment Checklist

### 1. Security Configuration

#### Change Default Credentials
- [ ] Change default admin password from `Admin@123`
- [ ] Update JWT secret key (generate new 256-bit key)
- [ ] Use strong MongoDB password

#### Environment Variables
Create `.env` file or use system environment variables:

```properties
# MongoDB
SPRING_DATA_MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/dbname
SPRING_DATA_MONGODB_DATABASE=tconsolcareers

# JWT
JWT_SECRET=your-256-bit-secret-key-here
JWT_EXPIRATION_MS=86400000
JWT_REFRESH_EXPIRATION_MS=604800000

# Email
SPRING_MAIL_HOST=smtp.hostinger.com
SPRING_MAIL_PORT=465
SPRING_MAIL_USERNAME=info@tconsolutions.com
SPRING_MAIL_PASSWORD=your-email-password

# Application
APP_FRONTEND_URL=https://careers.tconsolutions.com
APP_CORS_ALLOWED_ORIGINS=https://careers.tconsolutions.com
APP_ADMIN_DEFAULT_USERNAME=admin@tconsolutions.com
APP_ADMIN_DEFAULT_PASSWORD=your-strong-password-here
```

#### Generate Secure JWT Secret
```bash
# Linux/Mac:
openssl rand -base64 32

# Or use online generator:
# https://generate-random.org/api-token-generator
```

---

### 2. Database Configuration

#### MongoDB Atlas Setup
- [ ] Create MongoDB Atlas account
- [ ] Create cluster
- [ ] Create database user with strong password
- [ ] Add IP whitelist (production server IP or 0.0.0.0/0 for testing)
- [ ] Get connection string
- [ ] Test connection

#### Database Indexes
Auto-created on startup:
- `users.email` (unique)
- `jobs.title`
- `jobs.isActive`
- `applications.email`
- `applications.jobId`
- `applications.status`
- `applications.applicationDate`

---

### 3. Email Configuration

#### Test Email Delivery
- [ ] Verify SMTP settings
- [ ] Send test email
- [ ] Check spam folder
- [ ] Verify email templates render correctly

#### Email Service Providers
Choose one:
- **Hostinger** (current): Port 465, SSL
- **SendGrid**: API-based, high deliverability
- **AWS SES**: Cost-effective, scalable
- **Mailgun**: Developer-friendly

---

### 4. Application Configuration

#### Update `application.properties` for Production

```properties
# Use environment variables for sensitive data
spring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI}
jwt.secret=${JWT_SECRET}
spring.mail.password=${SPRING_MAIL_PASSWORD}

# Production settings
spring.profiles.active=production
logging.level.root=WARN
logging.level.com.tcon.careers=INFO
app.email.enabled=true
app.rate-limit.enabled=true
```

---

### 5. CORS Configuration

#### Update Allowed Origins
In `application.properties`:
```properties
app.cors.allowed-origins=https://careers.tconsolutions.com,https://www.tconsolutions.com
```

Or use environment variable:
```properties
app.cors.allowed-origins=${CORS_ALLOWED_ORIGINS}
```

---

### 6. Rate Limiting

#### Configure Rate Limits
```properties
app.rate-limit.enabled=true
app.rate-limit.applications-per-hour=3
```

#### Add IP-based rate limiting (recommended)
Consider using Spring Security's rate limiting or Redis.

---

### 7. File Storage

#### Current: Local File Storage
Location: `uploads/resumes/`

#### For Production (Recommended):
- **AWS S3**
  - High availability
  - Scalable
  - Cost-effective
  
- **Azure Blob Storage**
  - Good for Microsoft ecosystem
  
- **Google Cloud Storage**
  - Good for Google ecosystem

#### Update FileStorageService for S3:
```java
// Add AWS S3 dependency to pom.xml
// Update FileStorageService to use S3 instead of local storage
```

---

### 8. Logging

#### Configure Logging
Create `logback-spring.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

---

### 9. Health Check Endpoints

#### Add Actuator
Add to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Configure in `application.properties`:
```properties
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
```

---

### 10. SSL/HTTPS

#### Option 1: Use Reverse Proxy (Recommended)
- Nginx or Apache handles SSL
- Simpler certificate management

#### Option 2: Configure in Spring Boot
Add to `application.properties`:
```properties
server.port=8443
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=your-password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
```

---

## 🛠️ Build & Deployment

### Build JAR File
```bash
cd C:\Users\dhanu\GitHub\tconsol-careers\careers
./mvnw clean package -DskipTests
```

Output: `target/careers-0.0.1-SNAPSHOT.jar`

### Run JAR File
```bash
java -jar target/careers-0.0.1-SNAPSHOT.jar
```

### With Environment Variables
```bash
# Linux/Mac:
export SPRING_DATA_MONGODB_URI="mongodb+srv://..."
export JWT_SECRET="your-secret-key"
java -jar target/careers-0.0.1-SNAPSHOT.jar

# Windows PowerShell:
$env:SPRING_DATA_MONGODB_URI="mongodb+srv://..."
$env:JWT_SECRET="your-secret-key"
java -jar target/careers-0.0.1-SNAPSHOT.jar
```

---

## 🐳 Docker Deployment

### Create `Dockerfile`

```dockerfile
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app
COPY target/careers-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Create `docker-compose.yml`

```yaml
version: '3.8'
services:
  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATA_MONGODB_URI=${MONGODB_URI}
      - JWT_SECRET=${JWT_SECRET}
      - SPRING_MAIL_PASSWORD=${EMAIL_PASSWORD}
    restart: unless-stopped
```

### Build & Run
```bash
# Build JAR
./mvnw clean package -DskipTests

# Build Docker image
docker build -t tcon-careers-backend .

# Run with environment variables
docker run -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI="mongodb+srv://..." \
  -e JWT_SECRET="your-secret" \
  tcon-careers-backend
```

---

## ☁️ Cloud Deployment Options

### 1. AWS Elastic Beanstalk
- Upload JAR file
- Configure environment variables
- Auto-scaling supported

### 2. AWS EC2
- Launch Ubuntu instance
- Install Java 25
- Upload JAR and run as systemd service

### 3. Heroku
- Create `Procfile`:
  ```
  web: java -jar target/careers-0.0.1-SNAPSHOT.jar --server.port=$PORT
  ```
- Deploy via Git

### 4. Azure App Service
- Create App Service (Java 25)
- Deploy JAR file
- Configure environment variables

### 5. Google Cloud Run
- Build Docker image
- Push to Google Container Registry
- Deploy to Cloud Run

---

## 📊 Monitoring & Maintenance

### Application Monitoring
- [ ] Set up application monitoring (New Relic, Datadog, etc.)
- [ ] Monitor error rates
- [ ] Track response times
- [ ] Set up alerts

### Database Monitoring
- [ ] Monitor MongoDB Atlas metrics
- [ ] Set up alerts for high usage
- [ ] Regular backups enabled

### Log Monitoring
- [ ] Centralized logging (ELK stack, CloudWatch, etc.)
- [ ] Log rotation configured
- [ ] Error tracking (Sentry, Rollbar, etc.)

---

## 🔒 Security Hardening

### Additional Security Measures

1. **Add Security Headers**
   ```java
   @Configuration
   public class SecurityHeadersConfig {
       @Bean
       public WebSecurityCustomizer webSecurityCustomizer() {
           return (web) -> web.ignoring().requestMatchers("/public/**");
       }
   }
   ```

2. **Enable CSRF for non-API endpoints** (if needed)

3. **Add Request Throttling**
   - Implement using Spring Security
   - Or use Redis-based rate limiting

4. **Input Sanitization**
   - Already using `@Valid` annotations
   - Consider adding HTML sanitization for text fields

5. **SQL Injection Prevention**
   - Using MongoDB (NoSQL)
   - Parameterized queries already in use

---

## 🧪 Testing Checklist

### Before Deployment
- [ ] All unit tests pass
- [ ] Integration tests pass
- [ ] Load testing completed
- [ ] Security scan completed
- [ ] Email delivery tested
- [ ] File upload tested
- [ ] Database connection tested
- [ ] All endpoints tested

### Test Commands
```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

---

## 📝 Post-Deployment Steps

### Immediately After Deployment
1. [ ] Verify application is running
2. [ ] Test health check endpoint
3. [ ] Login as admin
4. [ ] Change admin password
5. [ ] Create test job posting
6. [ ] Submit test application
7. [ ] Verify emails are being sent
8. [ ] Test all critical endpoints

### First Week
1. [ ] Monitor error logs daily
2. [ ] Check email delivery rates
3. [ ] Monitor database usage
4. [ ] Review application performance
5. [ ] Gather user feedback

### Ongoing
1. [ ] Weekly security updates
2. [ ] Monthly dependency updates
3. [ ] Regular database backups
4. [ ] Performance optimization
5. [ ] Feature improvements

---

## 🆘 Troubleshooting

### Common Issues

#### MongoDB Connection Failed
- Check MongoDB Atlas IP whitelist
- Verify connection string
- Check database user permissions

#### Emails Not Sending
- Verify SMTP settings
- Check email credentials
- Check spam folder
- Verify email service status

#### JWT Token Invalid
- Check JWT secret configuration
- Verify token expiration settings
- Check system time synchronization

#### File Upload Fails
- Check upload directory permissions
- Verify file size limits
- Check disk space

---

## 📞 Support & Resources

### Documentation
- Spring Boot: https://spring.io/projects/spring-boot
- MongoDB: https://docs.mongodb.com/
- JWT: https://jwt.io/

### Monitoring Tools
- **Application Monitoring**: New Relic, Datadog, Prometheus
- **Error Tracking**: Sentry, Rollbar
- **Log Management**: ELK Stack, Splunk, CloudWatch

---

## ✅ Final Checklist

Before going live:
- [ ] All environment variables configured
- [ ] Default admin password changed
- [ ] JWT secret generated and configured
- [ ] MongoDB connection tested
- [ ] Email delivery verified
- [ ] CORS configured for production domain
- [ ] HTTPS/SSL enabled
- [ ] Rate limiting enabled
- [ ] Logging configured
- [ ] Health checks working
- [ ] All tests passing
- [ ] Security scan completed
- [ ] Backup strategy in place
- [ ] Monitoring configured
- [ ] Documentation updated
- [ ] Team trained on deployment process

---

**Your application is production-ready! 🎉**

For questions or issues, refer to the documentation or contact the development team.

