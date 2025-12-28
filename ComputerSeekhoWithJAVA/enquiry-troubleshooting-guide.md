# 🔍 Enquiry 400 Bad Request Troubleshooting Guide

## 🚨 **Current Issue:**
Your POST request to `http://localhost:8080/api/enquiries` is returning a **400 Bad Request** error.

## 🔧 **Debugging Steps:**

### **Step 1: Check Server Logs**
Look at your Spring Boot application console/logs for these debug messages:
```
Received enquiry data:
enquirerName: [value]
enquirerMobile: [value]
enquirerEmailId: [value]
```

### **Step 2: Test with Minimal JSON**
Try this minimal request first:
```json
{
  "enquirerName": "Test User",
  "enquirerMobile": 9876543210,
  "enquirerEmailId": "test@example.com"
}
```

### **Step 3: Test Raw JSON Endpoint**
Send your JSON to the debug endpoint:
```
POST http://localhost:8080/api/enquiries/test
Content-Type: application/json

[Your JSON here]
```

## 🚫 **Common Validation Errors:**

### **1. Missing Required Fields:**
- ❌ `"enquirerName": null` or missing
- ❌ `"enquirerMobile": null` or missing  
- ❌ `"enquirerEmailId": null` or missing

### **2. Invalid Email Format:**
- ❌ `"enquirerEmailId": "invalid-email"`
- ✅ `"enquirerEmailId": "user@example.com"`

### **3. Invalid Mobile Number:**
- ❌ `"enquirerMobile": 123` (less than 10 digits)
- ✅ `"enquirerMobile": 9876543210` (10+ digits)

### **4. Empty String Values:**
- ❌ `"enquirerName": ""`
- ❌ `"enquirerEmailId": ""`
- ✅ `"enquirerName": "John Doe"`

## 📋 **Correct JSON Structure:**

### **Minimal (Required Fields Only):**
```json
{
  "enquirerName": "John Doe",
  "enquirerMobile": 9876543210,
  "enquirerEmailId": "john@example.com"
}
```

### **Complete (All Fields):**
```json
{
  "enquirerName": "John Doe",
  "enquirerAddress": "123 Main St",
  "enquirerMobile": 9876543210,
  "enquirerAlternateMobile": 9876543211,
  "enquirerEmailId": "john@example.com",
  "enquiryDate": "2024-01-15",
  "enquirerQuery": "Interested in Java course",
  "courseId": 1,
  "assignedStaffId": 1,
  "studentName": "John Doe"
}
```

## 🔍 **Validation Rules:**

| Field | Required | Type | Validation |
|-------|----------|------|------------|
| `enquirerName` | ✅ | String | Non-empty string |
| `enquirerMobile` | ✅ | Number | At least 10 digits |
| `enquirerEmailId` | ✅ | String | Must contain "@" |
| `enquiryDate` | ❌ | String | "YYYY-MM-DD" format |
| `courseId` | ❌ | Number | Any integer |
| `assignedStaffId` | ❌ | Number | Any integer |

## 🛠️ **Quick Fixes:**

### **If you get "Enquirer name is required":**
```json
{
  "enquirerName": "Your Name Here",
  "enquirerMobile": 9876543210,
  "enquirerEmailId": "your@email.com"
}
```

### **If you get "Invalid email format":**
```json
{
  "enquirerName": "John Doe",
  "enquirerMobile": 9876543210,
  "enquirerEmailId": "john.doe@example.com"
}
```

### **If you get "Mobile number must be at least 10 digits":**
```json
{
  "enquirerName": "John Doe",
  "enquirerMobile": 9876543210,
  "enquirerEmailId": "john@example.com"
}
```

## 🧪 **Testing Commands:**

### **Using curl:**
```bash
curl -X POST http://localhost:8080/api/enquiries \
  -H "Content-Type: application/json" \
  -d '{
    "enquirerName": "Test User",
    "enquirerMobile": 9876543210,
    "enquirerEmailId": "test@example.com"
  }'
```

### **Using Postman:**
1. Method: `POST`
2. URL: `http://localhost:8080/api/enquiries`
3. Headers: `Content-Type: application/json`
4. Body: Raw JSON (use the minimal example above)

## 📞 **Next Steps:**

1. **Check your frontend code** - Make sure it's sending the required fields
2. **Test with the minimal JSON** - Start with just the 3 required fields
3. **Check server logs** - Look for the debug output I added
4. **Use the test endpoint** - Send your JSON to `/api/enquiries/test` first

## 🎯 **Expected Response:**

### **Success (201 Created):**
```json
{
  "enquiryId": 1,
  "enquirerName": "John Doe",
  "enquirerMobile": 9876543210,
  "enquirerEmailId": "john@example.com",
  "enquiryDate": "2024-01-15",
  "enquiryCounter": 0,
  "enquiryProcessedFlag": false,
  "createdDate": "2024-01-15T10:30:00",
  "updatedDate": "2024-01-15T10:30:00"
}
```

### **Error (400 Bad Request):**
```json
{
  "error": "Enquirer name is required"
}
```

Try the minimal JSON first and let me know what error message you get! 🚀
