# Notification System Design

## Stage 1  

### Core Actions the Notification Platform Should Support
1. Fetch all notifications for a logged-in student
2. Mark a notification as read
3. Fetch unread notification count
4. Fetch notifications by type (Placement, Result, Event)
5. Real-time notification delivery

### REST API Endpoints

#### 1. Get All Notifications for a Student
GET /api/notifications
Headers:
Authorization: Bearer <token>

Response 200:
{
"notifications": [
{
"id": "uuid",
"type": "Placement",
"message": "TCS hiring drive on July 10",
"isRead": false,
"createdAt": "2026-04-22T17:51:30"
}
]
}

#### 2. Get Unread Notifications Count
GET /api/notifications/unread/count
Headers:
Authorization: Bearer <token>

Response 200:
{
"unreadCount": 5
}

#### 3. Mark Notification as Read
PATCH /api/notifications/{id}/read
Headers:
Authorization: Bearer <token>

Response 200:
{
"message": "Notification marked as read"
}

#### 4. Get Notifications by Type
GET /api/notifications?type=Placement
Headers:
Authorization: Bearer <token>

Response 200:
{
"notifications": [...]
}

#### 5. Mark All Notifications as Read
PATCH /api/notifications/read-all
Headers:
Authorization: Bearer <token>

Response 200:
{
"message": "All notifications marked as read"
}

### Real-Time Notification Mechanism

Use **WebSockets with STOMP protocol** over SockJS for real-time delivery.

- Server pushes notifications to connected clients instantly
- Each student subscribes to their own topic: `/topic/notifications/{studentId}`
- On new notification, server broadcasts to that topic
- Client receives it and updates the UI without polling

#### WebSocket Endpoint
WS /ws/notifications
Subscribe: /topic/notifications/{studentId}

Publish (server → client):
{
"id": "uuid",
"type": "Placement",
"message": "TCS hiring drive on July 10",
"isRead": false,
"createdAt": "2026-04-22T17:51:30"
}

---

## Stage 2

### Recommended Database: PostgreSQL

**Why PostgreSQL?**
- Strong support for indexing on multiple columns (studentID, isRead, createdAt)
- ENUM type support for notificationType
- Handles relational queries efficiently
- Better suited than NoSQL here since notification data is structured and relational

### DB Schema

CREATE TABLE students (
id           BIGSERIAL PRIMARY KEY,
name         VARCHAR(100) NOT NULL,
email        VARCHAR(150) UNIQUE NOT NULL,
created_at   TIMESTAMP DEFAULT NOW()
);

CREATE TYPE notification_type AS ENUM ('Placement', 'Result', 'Event');

CREATE TABLE notifications (
id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
student_id          BIGINT NOT NULL REFERENCES students(id),
notification_type   notification_type NOT NULL,
message             TEXT NOT NULL,
is_read             BOOLEAN DEFAULT FALSE,
created_at          TIMESTAMP DEFAULT NOW()
);

### Problems as Data Volume Increases
- Full table scans on large notifications table slow down queries
- Fetching unread notifications per student becomes expensive
- Sorting by createdAt without index causes performance degradation

### Solutions
- Add composite indexes on (student_id, is_read) and (student_id, created_at)
- Partition the notifications table by created_at (monthly partitions)
- Archive old notifications to a separate table after 6 months

### Key Queries

-- Fetch all notifications for a student
SELECT * FROM notifications
WHERE student_id = :studentId
ORDER BY created_at DESC;

-- Fetch unread notifications for a student
SELECT * FROM notifications
WHERE student_id = :studentId AND is_read = false
ORDER BY created_at DESC;

-- Mark notification as read
UPDATE notifications
SET is_read = true
WHERE id = :notificationId AND student_id = :studentId;

-- Count unread notifications
SELECT COUNT(*) FROM notifications
WHERE student_id = :studentId AND is_read = false;

-- Fetch notifications by type
SELECT * FROM notifications
WHERE student_id = :studentId AND notification_type = :type
ORDER BY created_at DESC;

---

## Stage 3

### Is the Original Query Accurate?
SELECT * FROM notifications
WHERE studentID = 1042 AND isRead = false
ORDER BY createdAt DESC;

**Yes, the query is logically correct** — it fetches unread notifications for a specific student ordered by most recent first.

### Why is it Slow?
- No index on (studentID, isRead) — causes full table scan on 5,000,000 rows
- SELECT * fetches all columns unnecessarily
- With 50,000 students and 5M notifications, unindexed queries are very expensive

### Likely Computation Cost
- Full table scan: O(n) where n = 5,000,000 rows
- Without index, PostgreSQL scans every row to match studentID and isRead

### Fix: Add Composite Index
CREATE INDEX idx_notifications_student_unread
ON notifications(studentID, isRead, createdAt DESC);

This index directly supports the WHERE clause and ORDER BY, reducing cost to O(log n).

### Is "Index Every Column" Good Advice?
**No.** Indexing every column is bad advice because:
- Each index increases storage overhead
- INSERT/UPDATE/DELETE operations become slower as all indexes must be updated
- Only index columns used in WHERE, ORDER BY, or JOIN clauses

### Query: Students Who Got a Placement Notification in Last 7 Days
SELECT DISTINCT student_id
FROM notifications
WHERE notification_type = 'Placement'
AND created_at >= NOW() - INTERVAL '7 days';

-- Supporting index:
CREATE INDEX idx_notifications_type_created
ON notifications(notification_type, created_at DESC);

---

## Stage 4

### Problem
Notifications are fetched from DB on every page load for every student, overwhelming the database.

### Solution: Caching with Redis

**Strategy: Cache-Aside Pattern**
- On page load, check Redis first for student's notifications
- If cache hit → return cached data (no DB query)
- If cache miss → query DB, store result in Redis with TTL, return data

**Cache Key Structure:**
notifications:studentId:{studentId}
notifications:unread_count:{studentId}

**TTL:** 60 seconds for notification list, 30 seconds for unread count

### Tradeoffs

| Strategy | Benefit | Tradeoff |
|---|---|---|
| Redis Cache-Aside | Reduces DB load significantly | Slight staleness (up to TTL) |
| CDN Caching | Good for static content | Not suitable for per-user data |
| DB Read Replicas | Scales read traffic | Still hits DB, higher infra cost |
| Pagination | Reduces data per query | Doesn't reduce DB hits |

**Recommended:** Redis Cache-Aside + Pagination together
- Cache first page of notifications per student
- Invalidate cache when new notification arrives via WebSocket event

---

## Stage 5

### Shortcomings of Original Implementation

function notify_all(student_ids: array, message: string):
for student_id in student_ids:
send_email(student_id, message)
save_to_db(student_id, message)
push_to_app(student_id, message)

**Problems:**
1. **Sequential processing** — 50,000 students processed one by one, extremely slow
2. **No fault tolerance** — if send_email fails at student 200, remaining 49,800 are skipped
3. **Tight coupling** — email, DB save, and push are all in one synchronous loop
4. **No retry mechanism** — failed emails are lost permanently
5. **Blocking** — entire operation blocks until all 50,000 are processed

### Should DB Save and Email Happen Together?
**No.** They should be decoupled because:
- Email is an external service and can fail independently
- DB save should always succeed regardless of email status
- Mixing them means a failed email rolls back a valid DB record

### Redesigned Solution: Message Queue with Workers

function notify_all(student_ids: array, message: string):
for student_id in student_ids:
save_to_db(student_id, message)          // always save first
enqueue("email_queue", student_id, message)   // async email
enqueue("push_queue", student_id, message)    // async push

// Email Worker (separate process):
function email_worker():
while true:
job = dequeue("email_queue")
try:
send_email(job.student_id, job.message)
catch error:
retry(job, max_attempts=3)             // retry on failure

// Push Worker (separate process):
function push_worker():
while true:
job = dequeue("push_queue")
push_to_app(job.student_id, job.message)

**Benefits:**
- DB save is synchronous and always happens first
- Email and push are async via queues (e.g. RabbitMQ or Kafka)
- Failed emails are retried automatically up to 3 times
- Workers scale horizontally to handle 50,000 students fast
- No single failure blocks the entire batch

---

## Stage 6

### Priority Inbox Approach

Priority is determined by:
1. **Type weight:** Placement = 3, Result = 2, Event = 1
2. **Recency:** More recent notifications rank higher within same weight

### Algorithm: Max-Heap (Priority Queue)

- Assign each notification a score: weight * recency_factor
- Use a max-heap to maintain top N notifications efficiently
- New notifications are inserted into heap; if size > N, pop the minimum
- Time complexity: O(log N) per insertion, O(N log N) overall

### Implementation
See priority_inbox.java in the repository.