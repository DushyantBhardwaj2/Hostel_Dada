# Hostel Dada – DSA-Based Hostel Resource Management Web App (Kotlin + Firebase)

Hostel Dada is a real-world, DSA-focused hostel management system built using **Kotlin with Jetpack Compose Web**, designed to be scalable into a full-stack website and easily portable to mobile apps. The project integrates **core data structure algorithms** in the backend logic of each module and uses **Firebase** for authentication and database services.

---

## ⚙️ Tech Stack

- **Frontend**: Kotlin + Jetpack Compose Web
- **Backend**: Firebase Realtime Database, Firebase Auth (email/password)
- **Deployment**: Web-first (future-ready for Android)

---

## � User Roles and Login Flow

- The **login page is shared** for both users and admins.
- After login:
  - If the email matches a **predefined admin email list**, user is redirected to the **Admin Dashboard**.
  - If not, redirected to the **User Dashboard**.

### � Authentication Rules

- **Only users** can register and log in freely.
- **Admin credentials (12 total)** are hardcoded and pre-registered.
  - **2 admins per module** × 5 modules = 10
  - **2 global admins** with access to all modules

---

## 🧩 Modules (with backend + user/admin planning)

We are developing **5 modules** one by one, but all are preplanned to avoid breaking future additions.

---

### ✅ Module 1: SnackCart – Smart Food Resale

#### 📌 Real-Life Scenario
- Hostel admins (friends of the user) purchase snacks from wholesale.
- These are listed on the site with a cost price (CP) and selling price (SP).
- Hostel users can **browse and reserve** snacks.
- Snacks are collected from a room, and payment is **Cash on Delivery (COD)**.

#### 👤 User Features
- View all available snacks with quantity and price.
- Search snacks by name (can implement Trie/String Matching).
- Reserve multiple quantities of snacks.
- Only in-stock items are visible.
- Can cancel their reservation (returned to stock).
- See real-time updates of inventory.

#### 🛠️ Admin Features
- Add/edit/delete snack items (name, CP, SP, quantity).
- Track total profit using: `(SP - CP) × quantity sold`
- Mark orders as "Delivered" or "Cancelled".
- Cancelled items return to available stock.
- See top-selling snacks (sorted by popularity or profit).

#### 🔧 Backend Notes
- Firebase structure will include:
  - `/snacks/`: global inventory
  - `/orders/{orderId}`: linked to userID, timestamp, status
- Inventory updates in real-time with Firebase listeners
- Search to be implemented using DSA (string matching or trie optional)

---

### ✅ Module 2: RoomieMatcher – Room Allocation via Admin

#### 📌 Real-Life Scenario
- At the start of each semester, students fill a **one-time roommate survey**.
- Survey includes:
  - Sleep pattern
  - Cleanliness
  - Language
  - Branch
  - Dating Inclination
  - Hometown, etc.
- Admins use these answers + room availability to assign rooms.

#### 🧠 Room Assignment Logic
- Priority is given based on:
  - Years spent in hostel (seniority)
  - Distance from hometown
- Matching will be **admin-controlled**, not automatic.
- Admins can filter users based on multiple attributes for pairing.

#### 👤 User Features
- Submit survey only once per semester.
- See assigned room and roommate (after admin confirmation).

#### 🛠️ Admin Features
- View all user responses.
- Filter users by survey parameters.
- Assign single or double rooms manually.
- Finalize room pairings.
- Cannot be edited mid-semester.

#### 🔧 Backend Notes
- Firebase stores:
  - `/roomieSurvey/{userId}`: user survey data
  - `/roomAssignments/{userId}`: final room info
- Admin dashboard allows querying/filtering survey data

---

### ✅ Module 3: LaundryLoad Balancer – Time Slot Booking

#### 📌 Real-Life Scenario
- Admins set the number of available washing machines and operating hours.
- Users book a slot for a specified time + duration.
- Slots are non-overlapping and on a daily basis.

#### 👤 User Features
- Book a time slot (e.g., 8 AM – 9 AM) for laundry.
- Only free time slots are shown.
- Can cancel or reschedule bookings.

#### 🛠️ Admin Features
- Set washing machine availability and working hours.
- View all booked slots in a calendar or list format.
- Can delete or override any user booking.

#### ⏱️ Booking Rules
- One booking per user per day.
- Prevent overlapping using **Interval Scheduling algorithm** or greedy logic.
- FCFS allocation or admin-defined priority.

#### 🔧 Backend Notes
- Firebase structure:
  - `/laundrySlots/{date}/{machineId}`: stores booking slots
  - `/users/{userId}/laundry`: current user booking info
- Algorithm ensures conflict-free booking

---

## � Remaining Modules to Be Defined Later
- ✅ **MessyMess** – Weekly menu optimization from user feedback
- ✅ **HostelFixer** – Maintenance request handler using priority + path optimization

---

## � Additional Notes

- All **DSA logic is documented in README**, not rendered in UI.
- Each module backend is isolated but coordinated under one app structure.
- Admins are **module-specific** — each sees only the dashboard of their assigned module.

---

## ✅ Summary of DSA Used

- ✅ Hash Maps, Sorting → SnackCart
- ✅ Trie/String Matching (for search) → SnackCart
- ✅ Manual Graph Matching/Filtering → RoomieMatcher
- ✅ Greedy/Interval Scheduling → LaundryLoad Balancer

---
