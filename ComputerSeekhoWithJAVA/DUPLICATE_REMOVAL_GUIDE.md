# Course Duplicate Removal Guide

This guide explains how to use the duplicate detection and removal functionality for courses in the REST API.

## Overview

The system provides multiple levels of duplicate detection and removal for courses:

1. **By Name Only** - Removes courses with identical names
2. **By Name and Description** - Removes courses with identical names and descriptions
3. **By Name, Description, and Duration** - Removes courses with identical names, descriptions, and durations
4. **Exact Match** - Removes courses with identical content (all fields except ID and timestamps)

## API Endpoints

### 1. Duplicate Detection (GET endpoints)

#### Find Duplicates by Name Only
```http
GET /api/courses/duplicates/name
```
Returns all courses that have duplicate names.

#### Find Duplicates by Name and Description
```http
GET /api/courses/duplicates/name-description
```
Returns all courses that have duplicate names and descriptions.

#### Find Duplicates by Name, Description, and Duration
```http
GET /api/courses/duplicates/name-description-duration
```
Returns all courses that have duplicate names, descriptions, and durations.

#### Find Exact Duplicates
```http
GET /api/courses/duplicates/exact
```
Returns all courses that have identical content (all fields except ID and timestamps).

#### Count Duplicates by Name
```http
GET /api/courses/duplicates/count/name
```
Returns the count of courses with duplicate names.

#### Count Duplicates by Name and Description
```http
GET /api/courses/duplicates/count/name-description
```
Returns the count of courses with duplicate names and descriptions.

### 2. Duplicate Removal (DELETE endpoints)

#### Remove Duplicates by Name Only
```http
DELETE /api/courses/duplicates/name
```
Removes duplicate courses based on name only. Keeps the course with the lowest ID.

#### Remove Duplicates by Name and Description
```http
DELETE /api/courses/duplicates/name-description
```
Removes duplicate courses based on name and description. Keeps the course with the lowest ID.

#### Remove Duplicates by Name, Description, and Duration
```http
DELETE /api/courses/duplicates/name-description-duration
```
Removes duplicate courses based on name, description, and duration. Keeps the course with the lowest ID.

#### Remove All Duplicates
```http
DELETE /api/courses/duplicates/all
```
Removes all types of duplicates in order of strictness (most strict first).

### 3. Find and Remove in One Operation (DELETE endpoints)

#### Find and Remove Duplicates by Name
```http
DELETE /api/courses/duplicates/find-and-remove/name
```
Returns the list of duplicate courses found and then removes them.

#### Find and Remove Duplicates by Name and Description
```http
DELETE /api/courses/duplicates/find-and-remove/name-description
```
Returns the list of duplicate courses found and then removes them.

#### Find and Remove Duplicates by Name, Description, and Duration
```http
DELETE /api/courses/duplicates/find-and-remove/name-description-duration
```
Returns the list of duplicate courses found and then removes them.

#### Find and Remove All Duplicates
```http
DELETE /api/courses/duplicates/find-and-remove/all
```
Returns the list of all duplicate courses found and then removes them.

## Usage Examples

### Example 1: Check for Duplicates Before Removing

```bash
# First, check how many duplicates exist
curl -X GET "http://localhost:8080/api/courses/duplicates/count/name"

# View the duplicate courses
curl -X GET "http://localhost:8080/api/courses/duplicates/name"

# Remove the duplicates
curl -X DELETE "http://localhost:8080/api/courses/duplicates/name"
```

### Example 2: Find and Remove in One Operation

```bash
# Find and remove duplicates by name, returning the list of removed courses
curl -X DELETE "http://localhost:8080/api/courses/duplicates/find-and-remove/name"
```

### Example 3: Remove All Types of Duplicates

```bash
# Remove all duplicates (most strict criteria first)
curl -X DELETE "http://localhost:8080/api/courses/duplicates/all"
```

### Example 4: Using JavaScript/Fetch

```javascript
// Check for duplicates
async function checkDuplicates() {
    const response = await fetch('/api/courses/duplicates/name');
    const duplicates = await response.json();
    console.log('Duplicate courses:', duplicates);
}

// Remove duplicates
async function removeDuplicates() {
    const response = await fetch('/api/courses/duplicates/name', {
        method: 'DELETE'
    });
    if (response.ok) {
        console.log('Duplicates removed successfully');
    }
}

// Find and remove duplicates
async function findAndRemoveDuplicates() {
    const response = await fetch('/api/courses/duplicates/find-and-remove/name', {
        method: 'DELETE'
    });
    const removedDuplicates = await response.json();
    console.log('Removed duplicates:', removedDuplicates);
}
```

## Duplicate Removal Strategy

The system uses the following strategy for removing duplicates:

1. **Keep the course with the lowest ID** - This preserves the oldest/earliest created course
2. **Remove in order of strictness** - When using "remove all duplicates", it processes:
   - First: Name + Description + Duration duplicates
   - Second: Name + Description duplicates  
   - Third: Name-only duplicates

This ensures that the most specific duplicates are handled first, preventing conflicts.

## Response Codes

- **200 OK** - Successfully retrieved duplicates or found and removed duplicates
- **204 No Content** - Successfully removed duplicates (no content returned)
- **500 Internal Server Error** - Error occurred during processing

## Important Notes

1. **Backup your data** before running duplicate removal operations
2. **Test on a copy** of your data first
3. **Review the results** of find operations before removing
4. **The operations are irreversible** - once duplicates are removed, they cannot be recovered
5. **Use the most appropriate criteria** for your use case:
   - Use "name only" if you want to keep only one course per name
   - Use "name + description" if courses with same name but different descriptions should be kept
   - Use "exact match" only if you want to remove truly identical courses

## Database Impact

The duplicate removal operations:
- Use SQL DELETE statements with subqueries
- Are wrapped in transactions for data consistency
- Keep the course with the lowest ID (oldest record)
- Update the database immediately

## Troubleshooting

### Common Issues

1. **No duplicates found** - This is normal if your data is clean
2. **Permission errors** - Ensure your database user has DELETE permissions
3. **Transaction errors** - Check for foreign key constraints or locks

### Best Practices

1. Always check for duplicates before removing them
2. Use the most specific criteria that fits your business logic
3. Consider the impact on related data (students, batches, etc.)
4. Monitor the results after removal operations
