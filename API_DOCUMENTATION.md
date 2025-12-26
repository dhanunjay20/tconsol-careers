# 🎯 TCON Careers API Documentation

Complete API reference with JSON request/response examples for all controllers.

**Base URL:** `http://localhost:8080`

---

## 📑 Table of Contents

1. [Authentication API](#authentication-api)
2. [Jobs API](#jobs-api)
3. [Job Applications API](#job-applications-api)
4. [Admin Dashboard API](#admin-dashboard-api)

---

## 🔐 Authentication API

### POST `/api/auth/login`
Admin login to get JWT token.

**Request:**
```json
{
  "email": "admin@tconsolutions.com",
  "password": "Admin@123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "admin@tconsolutions.com",
    "role": "ROLE_ADMIN"
  },
  "timestamp": "2025-12-26T14:30:00"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Invalid email or password",
  "data": null,
  "timestamp": "2025-12-26T14:30:00"
}
```

---

## 💼 Jobs API

### 1. GET `/api/jobs`
Get all active job listings with optional filters and pagination.

**Query Parameters:**
- `department` (optional): Engineering, Design, Management
- `location` (optional): Remote, Hybrid, On-site
- `type` (optional): Full-time, Part-time, Contract
- `experience` (optional): Junior, Mid-Level, Senior
- `search` (optional): Search term for title/description
- `page` (optional, default: 0): Page number
- `limit` (optional, default: 10): Items per page

**Request:**
```
GET /api/jobs?department=Engineering&location=Remote&page=0&limit=10
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "content": [
      {
        "id": "675d4f2a8e9b1c3d4e5f6a7b",
        "title": "Senior Full Stack Developer",
        "department": "Engineering",
        "location": "Remote",
        "type": "Full-time",
        "salary": "$120,000 - $150,000",
        "experience": "Senior",
        "description": "We are looking for an experienced Full Stack Developer...",
        "requirements": [
          "5+ years of experience in web development",
          "Strong proficiency in React, Node.js, and MongoDB",
          "Experience with RESTful APIs and microservices"
        ],
        "responsibilities": [
          "Design and develop scalable web applications",
          "Collaborate with cross-functional teams",
          "Mentor junior developers"
        ],
        "color": "from-blue-500 to-purple-600",
        "isActive": true,
        "postedDate": "2025-12-20T10:00:00",
        "closingDate": "2026-01-20T23:59:59",
        "createdAt": "2025-12-20T10:00:00",
        "updatedAt": "2025-12-20T10:00:00"
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 15,
    "totalPages": 2,
    "first": true,
    "last": false
  },
  "timestamp": "2025-12-26T14:30:00"
}
```

### 2. GET `/api/jobs/{id}`
Get a single job by ID.

**Request:**
```
GET /api/jobs/675d4f2a8e9b1c3d4e5f6a7b
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": "675d4f2a8e9b1c3d4e5f6a7b",
    "title": "Senior Full Stack Developer",
    "department": "Engineering",
    "location": "Remote",
    "type": "Full-time",
    "salary": "$120,000 - $150,000",
    "experience": "Senior",
    "description": "We are looking for an experienced Full Stack Developer...",
    "requirements": [
      "5+ years of experience in web development",
      "Strong proficiency in React, Node.js, and MongoDB",
      "Experience with RESTful APIs and microservices"
    ],
    "responsibilities": [
      "Design and develop scalable web applications",
      "Collaborate with cross-functional teams",
      "Mentor junior developers"
    ],
    "color": "from-blue-500 to-purple-600",
    "isActive": true,
    "postedDate": "2025-12-20T10:00:00",
    "closingDate": "2026-01-20T23:59:59",
    "createdAt": "2025-12-20T10:00:00",
    "updatedAt": "2025-12-20T10:00:00"
  },
  "timestamp": "2025-12-26T14:30:00"
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Job not found with id: 675d4f2a8e9b1c3d4e5f6a7b",
  "data": null,
  "timestamp": "2025-12-26T14:30:00"
}
```

### 3. GET `/api/jobs/stats`
Get job statistics.

**Request:**
```
GET /api/jobs/stats
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "totalJobs": 15,
    "activeJobs": 12,
    "byDepartment": {
      "Engineering": 8,
      "Design": 3,
      "Management": 4
    },
    "byLocation": {
      "Remote": 7,
      "Hybrid": 4,
      "On-site": 4
    },
    "byType": {
      "Full-time": 10,
      "Part-time": 2,
      "Contract": 3
    },
    "byExperience": {
      "Junior": 3,
      "Mid-Level": 7,
      "Senior": 5
    }
  },
  "timestamp": "2025-12-26T14:30:00"
}
```

### 4. POST `/api/admin/jobs` 🔒 Admin Only
Create a new job listing.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request:**
```json
{
  "title": "Senior Full Stack Developer",
  "department": "Engineering",
  "location": "Remote",
  "type": "Full-time",
  "salary": "$120,000 - $150,000",
  "experience": "Senior",
  "description": "We are looking for an experienced Full Stack Developer to join our team...",
  "requirements": [
    "5+ years of experience in web development",
    "Strong proficiency in React, Node.js, and MongoDB",
    "Experience with RESTful APIs and microservices",
    "Excellent problem-solving skills"
  ],
  "responsibilities": [
    "Design and develop scalable web applications",
    "Collaborate with cross-functional teams",
    "Mentor junior developers",
    "Participate in code reviews"
  ],
  "color": "from-blue-500 to-purple-600",
  "closingDate": "2026-01-20T23:59:59"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Job created successfully",
  "data": {
    "id": "675d4f2a8e9b1c3d4e5f6a7b",
    "title": "Senior Full Stack Developer",
    "department": "Engineering",
    "location": "Remote",
    "type": "Full-time",
    "salary": "$120,000 - $150,000",
    "experience": "Senior",
    "description": "We are looking for an experienced Full Stack Developer...",
    "requirements": [...],
    "responsibilities": [...],
    "color": "from-blue-500 to-purple-600",
    "isActive": true,
    "postedDate": "2025-12-26T14:30:00",
    "closingDate": "2026-01-20T23:59:59",
    "createdAt": "2025-12-26T14:30:00",
    "updatedAt": "2025-12-26T14:30:00"
  },
  "timestamp": "2025-12-26T14:30:00"
}
```

### 5. PUT `/api/admin/jobs/{id}` 🔒 Admin Only
Update an existing job.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request:**
```json
{
  "title": "Senior Full Stack Developer (Updated)",
  "department": "Engineering",
  "location": "Hybrid",
  "type": "Full-time",
  "salary": "$130,000 - $160,000",
  "experience": "Senior",
  "description": "Updated description...",
  "requirements": [
    "5+ years of experience in web development",
    "Strong proficiency in React, Node.js, and MongoDB"
  ],
  "responsibilities": [
    "Design and develop scalable web applications",
    "Collaborate with cross-functional teams"
  ],
  "color": "from-blue-500 to-purple-600",
  "isActive": true
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Job updated successfully",
  "data": {
    "id": "675d4f2a8e9b1c3d4e5f6a7b",
    "title": "Senior Full Stack Developer (Updated)",
    "department": "Engineering",
    "location": "Hybrid",
    "type": "Full-time",
    "salary": "$130,000 - $160,000",
    "experience": "Senior",
    "description": "Updated description...",
    "requirements": [...],
    "responsibilities": [...],
    "color": "from-blue-500 to-purple-600",
    "isActive": true,
    "postedDate": "2025-12-20T10:00:00",
    "closingDate": "2026-01-20T23:59:59",
    "createdAt": "2025-12-20T10:00:00",
    "updatedAt": "2025-12-26T14:30:00"
  },
  "timestamp": "2025-12-26T14:30:00"
}
```

### 6. DELETE `/api/admin/jobs/{id}` 🔒 Admin Only
Soft delete a job (sets isActive to false).

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Request:**
```
DELETE /api/admin/jobs/675d4f2a8e9b1c3d4e5f6a7b
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Job deleted successfully",
  "data": null,
  "timestamp": "2025-12-26T14:30:00"
}
```

---

## 📝 Job Applications API

### 1. POST `/api/applications`
Submit a new job application with resume upload.

**Headers:**
```
Content-Type: multipart/form-data
```

**Request (Multipart Form Data):**
```
application (JSON part):
{
  "jobId": "675d4f2a8e9b1c3d4e5f6a7b",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "+1234567890",
  "linkedinUrl": "https://linkedin.com/in/johndoe",
  "portfolioUrl": "https://johndoe.com",
  "githubUrl": "https://github.com/johndoe",
  "currentLocation": "New York, NY",
  "willingToRelocate": true,
  "yearsOfExperience": 6,
  "currentRole": "Senior Developer",
  "currentCompany": "Tech Corp",
  "noticePeriod": "30 days",
  "expectedSalary": "$140,000",
  "coverLetter": "I am very interested in this position...",
  "referralSource": "LinkedIn",
  "skills": ["React", "Node.js", "MongoDB", "AWS"],
  "certifications": ["AWS Certified Developer"],
  "education": "Bachelor's"
}

resume (File part): resume.pdf
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Application submitted successfully",
  "data": {
    "applicationId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "jobId": "675d4f2a8e9b1c3d4e5f6a7b",
    "jobTitle": "Senior Full Stack Developer",
    "department": "Engineering",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "+1234567890",
    "linkedinUrl": "https://linkedin.com/in/johndoe",
    "portfolioUrl": "https://johndoe.com",
    "githubUrl": "https://github.com/johndoe",
    "currentLocation": "New York, NY",
    "willingToRelocate": true,
    "yearsOfExperience": 6,
    "currentRole": "Senior Developer",
    "currentCompany": "Tech Corp",
    "noticePeriod": "30 days",
    "expectedSalary": "$140,000",
    "resumeUrl": "http://localhost:8080/api/files/resume_675d4f2a8e9b1c3d4e5f6a7b_20251226_143000_a1b2c3d4.pdf",
    "resumeFileName": "resume.pdf",
    "resumeFileSize": 245678,
    "coverLetter": "I am very interested in this position...",
    "referralSource": "LinkedIn",
    "skills": ["React", "Node.js", "MongoDB", "AWS"],
    "certifications": ["AWS Certified Developer"],
    "education": "Bachelor's",
    "status": "submitted",
    "statusHistory": [
      {
        "status": "submitted",
        "changedBy": "System",
        "changedAt": "2025-12-26T14:30:00",
        "notes": "Application submitted"
      }
    ],
    "adminNotes": [],
    "interviewSchedule": [],
    "applicationDate": "2025-12-26T14:30:00",
    "lastUpdated": "2025-12-26T14:30:00",
    "ipAddress": "192.168.1.100",
    "userAgent": "Mozilla/5.0..."
  },
  "timestamp": "2025-12-26T14:30:00"
}
```

**Error Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Failed to submit application: File size exceeds maximum limit of 5MB",
  "data": null,
  "timestamp": "2025-12-26T14:30:00"
}
```

### 2. GET `/api/applications/track/{applicationId}`
Track application status (public endpoint - no auth required).

**Request:**
```
GET /api/applications/track/a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "applicationId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "jobTitle": "Senior Full Stack Developer",
    "department": "Engineering",
    "status": "interview-scheduled",
    "applicationDate": "2025-12-26T14:30:00",
    "lastUpdated": "2025-12-27T10:15:00"
  },
  "timestamp": "2025-12-27T10:15:00"
}
```

### 3. GET `/api/admin/applications` 🔒 Admin Only
Get all applications with filters and pagination.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Query Parameters:**
- `jobId` (optional): Filter by job ID
- `status` (optional): submitted, screening, interview-scheduled, interview-completed, rejected, offer-extended, hired
- `department` (optional): Engineering, Design, Management
- `dateFrom` (optional): ISO date-time (e.g., 2025-12-01T00:00:00)
- `dateTo` (optional): ISO date-time
- `search` (optional): Search in name, email
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Items per page

**Request:**
```
GET /api/admin/applications?status=submitted&page=0&size=10
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "content": [
      {
        "applicationId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
        "jobId": "675d4f2a8e9b1c3d4e5f6a7b",
        "jobTitle": "Senior Full Stack Developer",
        "department": "Engineering",
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@example.com",
        "phone": "+1234567890",
        "currentLocation": "New York, NY",
        "yearsOfExperience": 6,
        "currentRole": "Senior Developer",
        "status": "submitted",
        "applicationDate": "2025-12-26T14:30:00",
        "resumeUrl": "http://localhost:8080/api/files/resume_...",
        "statusHistory": [...],
        "adminNotes": [],
        "interviewSchedule": []
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 25,
    "totalPages": 3,
    "first": true,
    "last": false
  },
  "timestamp": "2025-12-27T10:15:00"
}
```

### 4. GET `/api/admin/applications/{id}` 🔒 Admin Only
Get a single application by ID.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Request:**
```
GET /api/admin/applications/a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "applicationId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "jobId": "675d4f2a8e9b1c3d4e5f6a7b",
    "jobTitle": "Senior Full Stack Developer",
    "department": "Engineering",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "+1234567890",
    "linkedinUrl": "https://linkedin.com/in/johndoe",
    "portfolioUrl": "https://johndoe.com",
    "githubUrl": "https://github.com/johndoe",
    "currentLocation": "New York, NY",
    "willingToRelocate": true,
    "yearsOfExperience": 6,
    "currentRole": "Senior Developer",
    "currentCompany": "Tech Corp",
    "noticePeriod": "30 days",
    "expectedSalary": "$140,000",
    "resumeUrl": "http://localhost:8080/api/files/resume_...",
    "resumeFileName": "resume.pdf",
    "resumeFileSize": 245678,
    "coverLetter": "I am very interested in this position...",
    "referralSource": "LinkedIn",
    "skills": ["React", "Node.js", "MongoDB", "AWS"],
    "certifications": ["AWS Certified Developer"],
    "education": "Bachelor's",
    "status": "submitted",
    "statusHistory": [
      {
        "status": "submitted",
        "changedBy": "System",
        "changedAt": "2025-12-26T14:30:00",
        "notes": "Application submitted"
      }
    ],
    "adminNotes": [],
    "interviewSchedule": [],
    "applicationDate": "2025-12-26T14:30:00",
    "lastUpdated": "2025-12-26T14:30:00",
    "ipAddress": "192.168.1.100",
    "userAgent": "Mozilla/5.0..."
  },
  "timestamp": "2025-12-27T10:15:00"
}
```

### 5. PUT `/api/admin/applications/{id}/status` 🔒 Admin Only
Update application status.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request:**
```json
{
  "status": "screening",
  "notes": "Resume reviewed, moving to screening phase"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Status updated successfully",
  "data": {
    "applicationId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "status": "screening",
    "statusHistory": [
      {
        "status": "submitted",
        "changedBy": "System",
        "changedAt": "2025-12-26T14:30:00",
        "notes": "Application submitted"
      },
      {
        "status": "screening",
        "changedBy": "admin@tconsolutions.com",
        "changedAt": "2025-12-27T10:15:00",
        "notes": "Resume reviewed, moving to screening phase"
      }
    ],
    "lastUpdated": "2025-12-27T10:15:00"
  },
  "timestamp": "2025-12-27T10:15:00"
}
```

**Valid Status Values:**
- `submitted`
- `screening`
- `interview-scheduled`
- `interview-completed`
- `rejected`
- `offer-extended`
- `hired`

### 6. POST `/api/admin/applications/{id}/notes` 🔒 Admin Only
Add an admin note to an application.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request:**
```json
{
  "note": "Candidate has excellent communication skills. Strong technical background."
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Note added successfully",
  "data": {
    "applicationId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "adminNotes": [
      {
        "note": "Candidate has excellent communication skills. Strong technical background.",
        "addedBy": "admin@tconsolutions.com",
        "addedAt": "2025-12-27T10:20:00"
      }
    ],
    "lastUpdated": "2025-12-27T10:20:00"
  },
  "timestamp": "2025-12-27T10:20:00"
}
```

### 7. POST `/api/admin/applications/{id}/interview` 🔒 Admin Only
Schedule an interview for an application.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request:**
```json
{
  "round": 1,
  "scheduledDate": "2025-12-30T14:00:00",
  "interviewer": "Sarah Johnson",
  "type": "video",
  "meetingLink": "https://zoom.us/j/123456789",
  "location": null
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Interview scheduled successfully",
  "data": {
    "applicationId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "status": "interview-scheduled",
    "interviewSchedule": [
      {
        "round": 1,
        "scheduledDate": "2025-12-30T14:00:00",
        "interviewer": "Sarah Johnson",
        "type": "video",
        "feedback": null,
        "status": "scheduled",
        "meetingLink": "https://zoom.us/j/123456789",
        "location": null
      }
    ],
    "statusHistory": [
      {
        "status": "submitted",
        "changedBy": "System",
        "changedAt": "2025-12-26T14:30:00",
        "notes": "Application submitted"
      },
      {
        "status": "interview-scheduled",
        "changedBy": "admin@tconsolutions.com",
        "changedAt": "2025-12-27T10:25:00",
        "notes": "Interview scheduled for round 1"
      }
    ],
    "lastUpdated": "2025-12-27T10:25:00"
  },
  "timestamp": "2025-12-27T10:25:00"
}
```

**Interview Types:**
- `phone`
- `video`
- `in-person`
- `technical`

### 8. DELETE `/api/admin/applications/{id}` 🔒 Admin Only
Delete an application and its resume file.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Request:**
```
DELETE /api/admin/applications/a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Application deleted successfully",
  "data": null,
  "timestamp": "2025-12-27T10:30:00"
}
```

---

## 📊 Admin Dashboard API

### 1. GET `/api/admin/dashboard/stats` 🔒 Admin Only
Get overall application statistics.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Request:**
```
GET /api/admin/dashboard/stats
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "totalApplications": 125,
    "byStatus": {
      "submitted": 35,
      "screening": 28,
      "interview-scheduled": 15,
      "interview-completed": 20,
      "rejected": 18,
      "offer-extended": 5,
      "hired": 4
    },
    "byDepartment": {
      "Engineering": 70,
      "Design": 30,
      "Management": 25
    }
  },
  "timestamp": "2025-12-27T10:35:00"
}
```

### 2. GET `/api/admin/dashboard/recent-applications` 🔒 Admin Only
Get the 10 most recent applications.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Request:**
```
GET /api/admin/dashboard/recent-applications
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "applicationId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "jobTitle": "Senior Full Stack Developer",
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "status": "submitted",
      "applicationDate": "2025-12-27T10:00:00"
    },
    {
      "applicationId": "b2c3d4e5-f6a7-8901-bcde-f23456789012",
      "jobTitle": "UI/UX Designer",
      "firstName": "Jane",
      "lastName": "Smith",
      "email": "jane.smith@example.com",
      "status": "screening",
      "applicationDate": "2025-12-27T09:30:00"
    }
  ],
  "timestamp": "2025-12-27T10:35:00"
}
```

### 3. GET `/api/admin/dashboard/pending-reviews` 🔒 Admin Only
Get applications pending review (submitted status).

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Request:**
```
GET /api/admin/dashboard/pending-reviews
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "applicationId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "jobTitle": "Senior Full Stack Developer",
      "department": "Engineering",
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "yearsOfExperience": 6,
      "status": "submitted",
      "applicationDate": "2025-12-27T10:00:00"
    }
  ],
  "timestamp": "2025-12-27T10:35:00"
}
```

---

## 🔑 Authentication & Authorization

### Public Endpoints (No Auth Required):
- `POST /api/auth/login`
- `GET /api/jobs`
- `GET /api/jobs/{id}`
- `GET /api/jobs/stats`
- `POST /api/applications`
- `GET /api/applications/track/{applicationId}`

### Protected Endpoints (Admin Only - Requires JWT Token):
All endpoints under `/api/admin/**` require:
1. Valid JWT token in Authorization header
2. ROLE_ADMIN authority

**How to use JWT token:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Getting a JWT Token:
1. Login with admin credentials: `POST /api/auth/login`
2. Extract the `token` from response
3. Use the token in Authorization header for all admin endpoints

---

## 📋 Common Response Codes

- **200 OK** - Request successful
- **201 Created** - Resource created successfully
- **400 Bad Request** - Invalid request data
- **401 Unauthorized** - Invalid or missing credentials
- **403 Forbidden** - Insufficient permissions
- **404 Not Found** - Resource not found
- **500 Internal Server Error** - Server error

---

## 🎨 Standard API Response Format

All endpoints return responses in this format:

```json
{
  "success": true,           // boolean: true for success, false for error
  "message": "Success",       // string: descriptive message
  "data": {...},             // object/array/null: actual response data
  "timestamp": "2025-12-27T10:35:00"  // ISO date-time
}
```

---

## 📝 Notes

1. **File Upload**: The `/api/applications` endpoint uses `multipart/form-data`. Send application data as JSON in the "application" part and the resume file in the "resume" part.

2. **Date Format**: All dates use ISO 8601 format: `YYYY-MM-DDTHH:mm:ss`

3. **Pagination**: Page numbers are 0-indexed (first page = 0)

4. **Resume Files**: 
   - Supported formats: PDF, DOC, DOCX
   - Maximum size: 5MB
   - Files stored in: `uploads/resumes/`

5. **Rate Limiting**: Application submissions are rate-limited (configurable, default: 3 per hour per email)

6. **Email Notifications**: Automatic emails are sent for:
   - Application confirmation (to applicant)
   - New application notification (to HR)
   - Status updates (to applicant)
   - Interview invitations (to applicant)
   - Rejection emails (to applicant)
   - Offer letters (to applicant)

---

## 🧪 Testing with cURL Examples

### Login:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@tconsolutions.com","password":"Admin@123"}'
```

### Get Jobs:
```bash
curl -X GET "http://localhost:8080/api/jobs?department=Engineering"
```

### Create Job (Admin):
```bash
curl -X POST http://localhost:8080/api/admin/jobs \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Developer","department":"Engineering","location":"Remote","type":"Full-time","salary":"$100k","experience":"Mid-Level","description":"Great job","requirements":["Skill 1"],"responsibilities":["Task 1"]}'
```

### Submit Application:
```bash
curl -X POST http://localhost:8080/api/applications \
  -F 'application={"jobId":"JOB_ID","firstName":"John","lastName":"Doe","email":"john@example.com","phone":"1234567890","currentLocation":"NY","yearsOfExperience":5,"currentRole":"Developer","noticePeriod":"30 days","education":"Bachelor'"'"'s"};type=application/json' \
  -F 'resume=@/path/to/resume.pdf'
```

---

## 🌐 Swagger UI

Access interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

---

**Last Updated:** December 26, 2025  
**API Version:** 1.0.0  
**Base URL:** http://localhost:8080

