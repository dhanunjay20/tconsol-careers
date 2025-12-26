# ⚠️ DEVELOPMENT MODE - IMPORTANT SECURITY NOTES

## 🔓 Security Features DISABLED for Development

The following security features have been **temporarily disabled** for easier development. 

**⚠️ CRITICAL: RE-ENABLE THESE BEFORE PRODUCTION DEPLOYMENT!**

---

## Changes Made for Development

### 1. Admin Registration Without Authentication
**File:** `AuthController.java`

**Current (Development):**
```java
@PostMapping("/admin/register")
// @PreAuthorize("hasRole('ADMIN')")  // TODO: Enable for production
public ResponseEntity<ApiResponse<UserResponse>> registerAdmin(...)
```

**Change for Production:**
```java
@PostMapping("/admin/register")
@PreAuthorize("hasRole('ADMIN')")  // ✅ ENABLED
public ResponseEntity<ApiResponse<UserResponse>> registerAdmin(...)
```

**Why:** Allows you to create admin accounts directly during development without being logged in.

---

### 2. All Auth Endpoints Publicly Accessible
**File:** `SecurityConfig.java`

**Current (Development):**
```java
.requestMatchers(
    "/api/auth/**",  // All auth endpoints allowed (including admin registration)
    ...
).permitAll()
```

**Change for Production (Optional):**
```java
.requestMatchers(
    "/api/auth/login",
    "/api/auth/register",
    "/api/auth/reset-password",
    "/api/auth/check-email/**",
    ...
).permitAll()
.requestMatchers("/api/auth/admin/**").hasRole("ADMIN")  // ✅ Protect admin endpoints
```

**Why:** Currently all auth endpoints are public. For production, you may want to protect admin-specific endpoints.

---

## 📋 Pre-Production Checklist

Before deploying to production, you MUST:

### Security
- [ ] **Re-enable @PreAuthorize on admin registration**
  - Uncomment `@PreAuthorize("hasRole('ADMIN')")` in `AuthController.java`
  
- [ ] **Update SecurityConfig to protect admin endpoints**
  - Restrict `/api/auth/admin/**` to authenticated admins only

- [ ] **Change default admin password**
  - Login as `admin@tconsolutions.com`
  - Use `/api/auth/change-password` to update

- [ ] **Generate new JWT secret**
  ```bash
  openssl rand -base64 32
  ```
  - Update in `application.properties` or environment variable

### Configuration
- [ ] **Use environment variables for sensitive data**
  - MongoDB URI
  - JWT secret
  - Email password
  - Admin credentials

- [ ] **Update CORS settings**
  - Change from `http://localhost:5173` to your production domain

- [ ] **Enable HTTPS**
  - Configure SSL certificate
  - Update all URLs to use `https://`

### Testing
- [ ] **Test all endpoints with authentication**
  - Verify admin endpoints require ROLE_ADMIN
  - Verify user endpoints require authentication
  - Test with invalid/expired tokens

---

## 🚀 How to Use in Development

### Register First Admin (No Auth Required)
```bash
curl -X POST http://localhost:8080/api/auth/admin/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "youradmin@tconsolutions.com",
    "password": "YourPassword123!",
    "firstName": "Your",
    "lastName": "Name"
  }'
```

### Register Regular Users (No Auth Required)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "UserPass123!",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "youradmin@tconsolutions.com",
    "password": "YourPassword123!"
  }'
```

---

## 🔒 Production Security Configuration

### Step 1: Update AuthController

**Find this line:**
```java
// @PreAuthorize("hasRole('ADMIN')")  // TODO: Enable for production
```

**Change to:**
```java
@PreAuthorize("hasRole('ADMIN')")  // ✅ Production ready
```

### Step 2: Update SecurityConfig (Optional - Recommended)

**Replace:**
```java
.requestMatchers("/api/auth/**").permitAll()
```

**With:**
```java
.requestMatchers(
    "/api/auth/login",
    "/api/auth/register",
    "/api/auth/reset-password",
    "/api/auth/check-email/**"
).permitAll()
.requestMatchers("/api/auth/admin/**").hasRole("ADMIN")
.requestMatchers("/api/auth/change-password", "/api/auth/me").authenticated()
```

### Step 3: Update Application Properties

**Create environment-specific properties:**

`application-dev.properties`:
```properties
# Development settings
app.admin.default-password=Admin@123
logging.level.com.tcon.careers=DEBUG
```

`application-prod.properties`:
```properties
# Production settings (use environment variables)
app.admin.default-password=${ADMIN_PASSWORD}
logging.level.com.tcon.careers=INFO
spring.profiles.active=prod
```

---

## 🧪 Testing After Re-enabling Security

### Test Admin Registration (Should Fail Without Auth)
```bash
curl -X POST http://localhost:8080/api/auth/admin/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!",
    "firstName": "Test",
    "lastName": "User"
  }'

# Expected: 403 Forbidden or 401 Unauthorized
```

### Test Admin Registration (Should Work With Admin Token)
```bash
# First, login as admin
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@tconsolutions.com","password":"Admin@123"}' \
  | jq -r '.data.token')

# Then register new admin
curl -X POST http://localhost:8080/api/auth/admin/register \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newadmin@example.com",
    "password": "NewAdmin123!",
    "firstName": "New",
    "lastName": "Admin"
  }'

# Expected: 201 Created
```

---

## 📝 Quick Reference

### Current Development Mode Features
✅ Admin registration **WITHOUT** authentication  
✅ All `/api/auth/**` endpoints are public  
✅ Default admin auto-created on startup  
✅ Simple testing and development  

### Required for Production
❌ Admin registration **REQUIRES** admin authentication  
❌ Admin endpoints protected by role-based access  
❌ Strong passwords enforced  
❌ HTTPS enabled  
❌ Environment variables for secrets  

---

## ⚡ Quick Commands

### Restart with Production Profile
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### Build for Production
```bash
./mvnw clean package -DskipTests -Pprod
```

### Run Production JAR
```bash
java -jar target/careers-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

---

## 🆘 Troubleshooting

### "Access Denied" after re-enabling security
1. Make sure you're logged in as admin
2. Check that JWT token is included in Authorization header
3. Verify token hasn't expired (24 hours default)
4. Check role is exactly "ROLE_ADMIN" (case-sensitive)

### Can't login after changes
1. Verify database connection
2. Check if default admin was created
3. Try password reset if needed
4. Check logs for authentication errors

---

## 📞 Support

For questions about security configuration:
1. Check `PRODUCTION_DEPLOYMENT.md`
2. Review Spring Security documentation
3. Test all endpoints before deployment
4. Conduct security audit

---

**Remember: Security is not optional in production!**

Always test security configurations thoroughly before deploying to production.

---

Last Updated: December 26, 2025  
Mode: **DEVELOPMENT** ⚠️  
Status: Security features temporarily disabled for easier development

