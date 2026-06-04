# MongoDB Learning Resources (For Step 5)

## Core Concepts to Learn
- Documents vs Tables, Collections, `_id` (ObjectId)
- Embedded documents vs References (`@DBRef`)
- Schema-less design patterns

## Spring Data MongoDB (Priority)
| JPA | MongoDB |
|-----|---------|
| `@Entity` | `@Document` |
| `JpaRepository` | `MongoRepository` |
| `@Id` + `@GeneratedValue` | `@Id` (String) |

## Recommended Resources

### Free
1. **MongoDB University** - [learn.mongodb.com](https://learn.mongodb.com)
   - M001: MongoDB Basics
   - M220J: MongoDB for Java Developers ⭐

2. **YouTube**
   - "MongoDB Crash Course" - Traversy Media
   - "Spring Boot MongoDB Tutorial" - Amigoscode

### Paid
- **Udemy**: "MongoDB - The Complete Developer's Guide" by Maximilian Schwarzmüller

## Quick Start
```bash
# Run MongoDB locally
docker run -d -p 27017:27017 --name mongodb mongo:7

# GUI Tool
# Download MongoDB Compass from mongodb.com
```
