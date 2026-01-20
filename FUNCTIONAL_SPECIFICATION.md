# Hostel Dada - Functional Specification

## Document Information
- **Version**: 2.0 (KMP Migration)
- **Project Type**: Kotlin Multiplatform Application
- **Architecture**: Clean Architecture + MVVM
- **Original Platform**: Next.js 14 + TypeScript

---

## 1. System Overview

### 1.1 Purpose
Hostel Dada is a comprehensive hostel management application for NSUT (Netaji Subhas University of Technology) students. It provides:
- **SnackCart** - Food ordering system with inventory management
- **Roomie Matcher** - Roommate compatibility matching using graph algorithms
- **Profile Management** - Student profile with hostel information
- **Admin Dashboard** - Administrative controls for each module

### 1.2 Target Users
| User Type | Description | Access Level |
|-----------|-------------|--------------|
| Student | NSUT hostel resident | Basic features |
| Module Admin | Manages specific module | Module-level admin |
| Super Admin | Full system access | All features |

### 1.3 Domain Validation
- Only `@nsut.ac.in` email addresses are allowed
- Domain validation occurs at signup and Google Sign-In

---

## 2. Feature Specifications

### 2.1 Authentication Module

#### 2.1.1 Login
```
Flow:
1. User enters email and password
2. System validates NSUT domain
3. Firebase Authentication verifies credentials
4. On success: Redirect to Dashboard
5. On failure: Show error message
```

**Fields:**
- Email (required, must be @nsut.ac.in)
- Password (required, min 6 characters)

**Actions:**
- Sign In with Email
- Sign In with Google
- Forgot Password
- Switch to Sign Up

#### 2.1.2 Sign Up
```
Flow:
1. User enters name, email, password
2. System validates all fields
3. System validates NSUT domain
4. Firebase creates account
5. Verification email sent
6. Redirect to Profile Setup
```

**Fields:**
- Display Name (required, min 2 characters)
- Email (required, @nsut.ac.in only)
- Password (required, min 6 characters)
- Confirm Password (must match)

#### 2.1.3 Password Reset
```
Flow:
1. User clicks "Forgot Password"
2. Enter email address
3. Firebase sends reset link
4. User clicks link and resets password
```

---

### 2.2 Profile Module

#### 2.2.1 Profile Setup (Multi-step Wizard)

**Step 1: Basic Information**
- Display Name
- Phone Number
- Emergency Contact

**Step 2: Academic Details**
- Roll Number (e.g., 2023UCS1234)
- Branch (dropdown)
- Year (1-4)
- Section

**Step 3: Hostel Information**
- Hostel Block (A, B, C, D)
- Room Number
- Floor Number

#### 2.2.2 Profile Guard
```
Logic:
IF user.isAuthenticated AND NOT profile.isComplete:
    REDIRECT to Profile Setup
ELSE:
    ALLOW access to requested page
```

---

### 2.3 SnackCart Module

#### 2.3.1 User Features

##### Browse Snacks
```
Display:
- Grid of snack cards
- Each card shows: Image, Name, Price, Availability
- Category filter tabs: All, Beverages, Snacks, Quick Bites, Meals, Desserts
```

##### Search (Trie-Based)
```
Algorithm: Trie (Prefix Tree)
Time Complexity: O(k) where k is query length

Implementation:
1. User types in search box
2. Each keystroke triggers trie search
3. Results update in real-time
4. Matching snacks displayed instantly
```

##### Cart Management
```
Actions:
- Add to Cart (with quantity)
- Update Quantity (+/-)
- Remove Item
- Clear Cart

Cart Display:
- Item list with quantities
- Subtotal per item
- Total amount
- Checkout button
```

##### Checkout Flow
```
Flow:
1. Click "Checkout" from cart
2. Enter delivery location (room number)
3. Select payment method (Cash/UPI/Card)
4. Add order notes (optional)
5. Confirm order
6. Order created with PENDING status
```

##### Order Tracking
```
Status Flow:
PENDING → CONFIRMED → PREPARING → READY → DELIVERED

Display:
- Order ID
- Items list
- Status badge (color-coded)
- Estimated delivery time
- Cancel button (only for PENDING)
```

#### 2.3.2 Admin Features

##### Order Management
```
Display:
- List of all orders (sorted by timestamp)
- Filter by status
- Order details expandable

Actions:
- Update status (dropdown)
- View order details
- Bulk status update
```

##### Inventory Management
```
Display:
- Snack list with stock levels
- Low stock alerts (< 10 units)

Actions:
- Add new snack
- Edit snack details
- Update stock quantity
- Toggle availability
- Delete snack
```

##### Statistics Dashboard
```
Metrics:
- Total orders (all time)
- Today's orders
- Total revenue
- Today's revenue
- Pending orders count
- Top selling items (top 5)
```

---

### 2.4 Roomie Matcher Module

#### 2.4.1 Survey System

##### Survey Categories (with weights)
```
1. Lifestyle (20%)
   - Sleep/wake time
   - Food preference (Veg/Non-veg/Eggetarian)
   - Smoking habit
   - Drinking habit
   - AC temperature preference
   - Music preference

2. Study Habits (20%)
   - Study style (Focused/Relaxed/Intense)
   - Preferred study time
   - Need for quiet environment
   - Group study preference
   - Music while studying

3. Cleanliness (20%)
   - Cleaning frequency
   - Organization level (1-5)
   - Shared items comfort (1-5)
   - Bathroom habits

4. Social Preferences (15%)
   - Visitor frequency
   - Party attitude
   - Conversation style
   - Privacy needs (1-5)

5. Sleep Schedule (15%)
   - Typical bedtime
   - Typical wake time
   - Sleep sensitivity
   - Nap habits
   - Weekend schedule differs

6. Personality (10%)
   - Introvert/Extrovert scale (1-5)
   - Conflict resolution style
   - Communication style
   - Adaptability (1-5)
```

##### Survey Questions (Indian Context)
```
Example Questions:
Q: "आपकी खाने की पसंद क्या है?" (Food preference)
   - Pure Vegetarian 🥗
   - Non-Vegetarian 🍗
   - Eggetarian 🥚

Q: "Do you smoke?"
   - Never ✨
   - Occasionally 🚬
   - Regularly

Q: "When do you prefer to study?"
   - Early Morning (5-8 AM) 🌅
   - Daytime 🌞
   - Evening (6-10 PM) 🌆
   - Late Night (After 10 PM) 🌙
```

#### 2.4.2 Compatibility Algorithm

##### Graph-Based Matching
```
Algorithm: Weighted Adjacency List Graph
Time Complexity: 
  - Add student: O(1)
  - Calculate edge: O(1)
  - Get top matches: O(n log n)
  - Generate all: O(n²)

Scoring Formula:
overallScore = 
  lifestyle * 0.20 +
  study * 0.20 +
  cleanliness * 0.20 +
  social * 0.15 +
  sleep * 0.15 +
  personality * 0.10
```

##### Individual Category Scoring
```kotlin
fun calculateLifestyleScore(l1, l2): Int {
    score = 0
    
    // Sleep time similarity (0-25 points)
    sleepDiff = |parseTime(l1.sleepTime) - parseTime(l2.sleepTime)|
    score += max(0, 25 - sleepDiff/30)  // Lose 1pt per 30min
    
    // Wake time (0-25 points) - similar logic
    // Food preference (0-20 points)
    // Smoking/Drinking (0-30 points)
    
    return score  // Max 100
}
```

#### 2.4.3 Matching Display

##### Match Card
```
Components:
- Student photo/avatar
- Name and branch
- Compatibility percentage (colored)
- Match reasons (icons + text)
- Warnings (if any)
- "View Details" button
```

##### Match Reasons
```
Generated automatically based on scores:
- 🏠 Similar lifestyle habits (lifestyle >= 80%)
- 📚 Compatible study preferences (study >= 80%)
- 🧹 Similar cleanliness standards (cleanliness >= 80%)
- 👥 Matching social preferences (social >= 80%)
- 😴 Compatible sleep schedules (sleep >= 80%)
```

##### Warnings
```
Generated for potential conflicts:
- ⚠️ Different smoking habits
- ⚠️ Different food preferences
- ⚠️ Significantly different sleep schedules (>3 hours)
```

#### 2.4.4 Admin Features

##### Survey Management
```
Display:
- Total surveys submitted
- Semester filter
- Survey list with student details

Actions:
- View survey responses
- Delete survey
- Export data (CSV)
```

##### Room Management
```
Display:
- Room list with occupancy
- Available rooms count
- Room details (block, floor, capacity)

Actions:
- Add new room
- Edit room details
- View occupants
- Change room status
```

##### Assignment Management
```
Display:
- Assignment list by semester
- Compatibility scores
- Status badges

Actions:
- Create assignment (select students + room)
- Auto-assign (greedy algorithm)
- Approve/Reject assignments
- View assignment details
```

##### Auto-Assignment Algorithm
```
Greedy Algorithm:
1. Get all surveys for semester
2. Generate all compatibility scores
3. Sort pairs by compatibility (descending)
4. For each pair:
   a. If both students unassigned AND room available:
      - Create assignment
      - Mark students as assigned
      - Update room occupancy
5. Return list of assignments
```

---

## 3. Data Models

### 3.1 User
```kotlin
data class User(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
    val isEmailVerified: Boolean,
    val createdAt: Long,
    val lastLoginAt: Long
)
```

### 3.2 Snack
```kotlin
data class Snack(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: SnackCategory,
    val imageUrl: String,
    val isAvailable: Boolean,
    val stockQuantity: Int,
    val preparationTime: Int,
    val isVegetarian: Boolean,
    val tags: List<String>
)

enum class SnackCategory {
    BEVERAGES, SNACKS, QUICK_BITES, 
    MEALS, DESSERTS, OTHER
}
```

### 3.3 Order
```kotlin
data class SnackOrder(
    val id: String,
    val userId: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val status: OrderStatus,
    val deliveryLocation: String,
    val paymentMethod: PaymentMethod,
    val notes: String,
    val estimatedDelivery: Long,
    val createdAt: Long
)

enum class OrderStatus {
    PENDING, CONFIRMED, PREPARING, 
    READY, DELIVERED, CANCELLED
}
```

### 3.4 Survey
```kotlin
data class RoommateSurvey(
    val id: String,
    val studentId: String,
    val semester: String,
    val lifestyle: LifestylePreferences,
    val studyHabits: StudyHabits,
    val cleanliness: CleanlinessPreferences,
    val socialPreferences: SocialPreferences,
    val sleepSchedule: SleepSchedule,
    val personalityTraits: PersonalityTraits,
    val isComplete: Boolean,
    val submittedAt: Long
)
```

### 3.5 Compatibility Score
```kotlin
data class CompatibilityScore(
    val id: String,
    val studentId1: String,
    val studentId2: String,
    val overallScore: Int,      // 0-100
    val lifestyleScore: Int,    // 0-100
    val studyScore: Int,        // 0-100
    val cleanlinessScore: Int,  // 0-100
    val socialScore: Int,       // 0-100
    val sleepScore: Int,        // 0-100
    val personalityScore: Int,  // 0-100
    val matchReasons: List<String>,
    val warnings: List<String>,
    val calculatedAt: Long
)
```

---

## 4. API Contracts

### 4.1 Firebase Realtime Database Structure
```
/users/{userId}/
    - profile: UserProfile
    
/snacks/{snackId}/
    - name, price, category, etc.

/carts/{userId}/
    - items: Map<snackId, CartItem>
    - totalAmount

/orders/{orderId}/
    - Order data

/userOrders/{userId}/{orderId}/
    - Reference to order

/surveys/{surveyId}/
    - Survey data

/studentSurveys/{studentId}/{semester}/
    - Reference to survey

/rooms/{roomId}/
    - Room data

/assignments/{assignmentId}/
    - Assignment data

/compatibility/{studentId1}_{studentId2}/
    - CompatibilityScore
```

### 4.2 Admin Configuration
```
/admin_config/
    - modules: {
        snackcart: [...admin emails],
        roomie: [...admin emails],
        laundry: [...admin emails]
      }
    - global_admins: [...super admin emails]
```

---

## 5. Non-Functional Requirements

### 5.1 Performance
| Metric | Requirement |
|--------|-------------|
| Search latency | < 50ms (Trie-based) |
| Compatibility calc | < 100ms per pair |
| App startup | < 2 seconds |
| Screen transitions | < 300ms |

### 5.2 Security
- Firebase Authentication required
- Domain-restricted access (@nsut.ac.in)
- Role-based access control
- Secure data transmission (HTTPS)

### 5.3 Scalability
- Support 1000+ concurrent users
- Handle 10,000+ snack items
- Process 500+ surveys per semester

---

## 6. UI/UX Guidelines

### 6.1 Color Scheme
```
Primary: #6366F1 (Indigo)
Secondary: #8B5CF6 (Violet)
Success: #10B981 (Green)
Warning: #F59E0B (Amber)
Error: #EF4444 (Red)
```

### 6.2 Typography
```
Headings: Inter Semi-Bold
Body: Inter Regular
Monospace: JetBrains Mono (for codes)
```

### 6.3 Component Guidelines
- Card radius: 12px
- Button radius: 8px
- Shadow: 0 4px 6px rgba(0,0,0,0.1)
- Spacing: 8px base unit

---

## 7. Future Enhancements

### 7.1 Planned Modules
1. **Laundry** - Slot booking for washing machines
2. **Maintenance** - Request tracking with priority queue
3. **Mess Menu** - Daily menu display with feedback
4. **Events** - Hostel event management
5. **Lost & Found** - Item reporting system

### 7.2 Technical Improvements
- Offline-first with SQLDelight
- Push notifications
- Image caching
- Analytics dashboard
- Export/Import functionality

---

## Document History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2024-01 | Initial Next.js implementation |
| 2.0 | 2024-12 | KMP migration with Clean Architecture |
