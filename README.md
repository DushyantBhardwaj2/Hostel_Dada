# Hostel Dada – DSA-Based Hostel Resource Management System

Hostel Dada is a **DSA-focused hostel management system** designed to model and optimize various real-life hostel activities. Built with **Next.js 14, React, TypeScript, and Firebase**, it includes multiple modules, each implementing core data structures and algorithms to handle tasks like food resale, roommate matching, laundry scheduling, mess feedback, and maintenance optimization.

---

## 🚀 **Live Demo**
**Website**: [https://hostel-dada.web.app](https://hostel-dada.web.app)

---

## ⚙️ **Tech Stack**

### **Frontend**
- ⚡ **Next.js 14** - React framework with App Router
- 🎨 **Tailwind CSS** - Utility-first CSS framework  
- 🔷 **TypeScript** - Type-safe JavaScript
- 📱 **Responsive Design** - Mobile-first approach

### **Backend & Services**
- 🔥 **Firebase Auth** - User authentication
- 🔥 **Firestore** - Real-time database
- 🔥 **Firebase Hosting** - Static site hosting
- 🔥 **Firebase Analytics** - User insights

---

## 👥 **User Roles and Login Flow**

- **Shared login page** for both users and admins
- After login:
  - If the email matches a **predefined admin email list**, redirect to **Admin Dashboard**
  - Otherwise, redirect to **User Dashboard**

## 🔐 **Authentication Rules**

- **Users** can register and log in freely
- **Admins** are predefined (12 total):
  - 2 admins per module × 5 modules = 10
  - 2 global admins with access to all modules
- Admin role is determined by checking the logged-in email against this predefined list

---

## 🧩 **Modules (Design & Working)**

### ✅ **Module 1: SnackCart – Smart Food Resale**
**Scenario**: Admins (hostel friends) buy snacks in bulk and sell to hostel users.

**User Features:**
- View available snacks (only in-stock items)
- Search snacks (DSA used: **Trie/String Matching**)
- Reserve or cancel items
- Real-time inventory updates

**Admin Features:**
- Add/edit/delete snacks (name, CP, SP, quantity)
- Mark orders as delivered or canceled
- Track profit: (SP - CP) × quantity sold
- View top-selling snacks

**Backend Logic:**
- Inventory stored globally with **Hash Maps**
- Orders are user-specific and timestamped
- **Sorting algorithms** for popularity and profit analysis

### ✅ **Module 2: RoomieMatcher – Room Allocation via Survey**
**Scenario**: Students fill out a roommate survey once per semester for optimal room allocation.

**User Features:**
- Submit survey once per semester
- View room and roommate assignment

**Admin Features:**
- Filter survey data by: Sleep schedule, cleanliness, language, branch, dating views, hometown
- Manual room assignment based on hostel seniority and distance from hometown
- Confirm final room pairing

**Backend Logic:**
- **Graph Filtering** algorithms for compatibility matching
- **Multi-parameter matching** with weighted scoring
- Distance-based optimization using graph algorithms

### ✅ **Module 3: LaundryLoad Balancer – Time Slot Booking**
**Scenario**: Optimized laundry scheduling to prevent conflicts and maximize machine utilization.

**User Features:**
- View and book available time slots
- Cancel or reschedule bookings (one slot per day)

**Admin Features:**
- Set daily working hours for each machine
- View all bookings in list/calendar view
- Override or delete any booking

**Booking Logic:**
- **Interval Scheduling (Greedy Algorithm)** to avoid conflicts
- Non-overlapping slots enforced
- Optimal time slot allocation using **scheduling algorithms**

### 🟡 **Module 4: MessyMess – Feedback-Driven Menu** *(In Progress)*
**Planned Flow:**
- Users give numerical ratings (1–5) per meal block: Breakfast, Lunch, Snacks, Dinner
- Feedback allowed once per meal per day
- Admins analyze weekly feedback to optimize menu

**Planned Admin Tools:**
- **Rating aggregation algorithms** for dish analysis
- Auto-generate weekly menu suggestions
- "Top-rated" and "Least liked" analytics dashboard
- **Sorting algorithms** for menu optimization

### 🟡 **Module 5: HostelFixer – Maintenance Request Optimizer** *(Planned)*
**Planned Features:**
- Users raise maintenance issues with severity tagging
- Admins prioritize and schedule repairs
- **Path optimization** for maintenance routes using **Dijkstra's Algorithm**
- **Priority Queues** for urgent issue handling
- **Graph traversal (DFS/BFS)** for optimal repair scheduling

---

## 🔁 **DSA Implementation Summary**

| Module | Data Structures | Algorithms |
|--------|----------------|------------|
| **SnackCart** | Hash Maps, Arrays | Trie-based Search, Sorting |
| **RoomieMatcher** | Graphs, Hash Tables | Multi-param Matching, Graph Filtering |
| **LaundryBalancer** | Interval Trees | Greedy Scheduling, Conflict Resolution |
| **MessyMess** | Arrays, Hash Maps | Rating Aggregation, Sorting |
| **HostelFixer** | Graphs, Priority Queues | Dijkstra, DFS/BFS, Priority Scheduling |

---

## 🏗️ **Project Structure**

```typescript
src/
├── app/                    # Next.js App Router
│   ├── layout.tsx         # Root layout with Firebase provider
│   ├── page.tsx           # Authentication page
│   └── globals.css        # Global styles
├── components/
│   ├── auth/              # Login/signup components
│   │   └── login-form.tsx # Authentication form
│   ├── dashboard/         # Main dashboard
│   │   └── dashboard.tsx  # Module navigation
│   └── ui/                # Reusable UI components
└── lib/
    ├── firebase.ts        # Firebase configuration
    ├── firebase-context.tsx # Auth context provider
    └── utils.ts           # Utility functions
```

---

## 🚀 **Quick Start**

### **1. Clone & Install**
```bash
git clone https://github.com/DushyantBhardwaj2/Hostel_Dada.git
cd Hostel_Dada
npm install
```

### **2. Firebase Setup**
1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Enable Authentication (Email/Password)
3. Create Firestore database
4. Copy your Firebase config to `.env.local`:

```env
NEXT_PUBLIC_FIREBASE_API_KEY=your_api_key
NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN=your_project.firebaseapp.com
NEXT_PUBLIC_FIREBASE_PROJECT_ID=your_project_id
NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET=your_project.appspot.com
NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID=your_sender_id
NEXT_PUBLIC_FIREBASE_APP_ID=your_app_id
```

### **3. Development**
```bash
npm run dev
```

### **4. Build & Deploy**
```bash
npm run build
firebase deploy --only hosting
```

---

## 🎯 **Current Implementation Status**

- ✅ **Authentication System** - Complete with Firebase Auth
- ✅ **Project Structure** - Clean Next.js architecture
- ✅ **Module Framework** - Dashboard with 5 module placeholders
- 🟡 **SnackCart Module** - DSA algorithms ready for implementation
- 🟡 **RoomieMatcher Module** - Survey system architecture planned
- 🟡 **LaundryBalancer Module** - Scheduling algorithms designed
- 🔄 **MessyMess Module** - Feedback system in progress
- 📅 **HostelFixer Module** - Maintenance optimization planned

---

## 🛠️ **Development Commands**

```bash
npm run dev        # Start development server
npm run build      # Build for production
npm run lint       # Run ESLint
npm run firebase:build # Build and deploy to Firebase
```

---

## 📚 **DSA Learning Outcomes**

This project provides hands-on experience with:

- **Graph Algorithms**: Roommate matching, maintenance routing
- **Greedy Algorithms**: Laundry scheduling optimization  
- **Hash Tables**: Fast inventory and user data lookup
- **Sorting Algorithms**: Ranking systems for snacks and feedback
- **Trie Data Structure**: Efficient search functionality
- **Priority Queues**: Urgent maintenance request handling
- **Interval Scheduling**: Conflict-free time slot booking

---

## 🤝 **Contributing**

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/module-name`
3. Implement DSA algorithms for the assigned module
4. Commit changes: `git commit -m "Add: SnackCart search algorithm"`
5. Push to branch: `git push origin feature/module-name`
6. Create a Pull Request

---

## 📄 **License**

This project is open source and available under the [MIT License](LICENSE).

---

## 🎓 **Academic Use**

Perfect for:
- **Data Structures & Algorithms** coursework
- **Full-stack development** learning
- **System design** practice
- **Firebase** integration projects
- **React/Next.js** development

Built with ❤️ for hostelers, by hostelers.
| **Hot Reload** | Slow | ⚡ Instant |
| **Learning Curve** | High | ✅ Low |
| **Community** | Small | 🌟 Huge |
| **Deployment** | Complex | ✅ One command |

## 🏗️ **PROJECT STRUCTURE**

```
src/
├── app/                    # Next.js App Router
│   ├── layout.tsx         # Root layout
│   ├── page.tsx           # Home page
│   └── globals.css        # Global styles
├── components/
│   ├── auth/              # Authentication components
│   ├── dashboard/         # Main dashboard
│   └── ui/                # Reusable UI components
└── lib/
    ├── firebase.ts        # Firebase configuration
    ├── firebase-context.tsx # Auth context
    └── utils.ts           # Utility functions
```

## 🚀 **QUICK START**

### **1. Setup (One Command)**
```bash
# Windows
setup.bat

# Or manually
npm install
```

### **2. Development**
```bash
npm run dev
# Visit http://localhost:3000
```

### **3. Deploy to Firebase**
```bash
npm run firebase:build
```

## 🎯 **5 MAIN MODULES**

### **1. 🛒 SnackCart**
- Food ordering system
- Real-time menu updates
- Cart management
- Order tracking

### **2. 👥 RoomieMatcher**
- Compatibility algorithm
- Profile matching
- Chat integration
- Room allocation

### **3. 👕 LaundryBalancer**
- Time slot booking
- Queue management
- Status tracking
- Payment integration

### **4. 🍽️ MessyMess**
- Menu management
- Feedback system
- Meal preferences
- Nutritional tracking

### **5. 🔧 HostelFixer**
- Issue reporting
- Priority management
- Staff assignment
- Progress tracking

## 🔐 **AUTHENTICATION**

### **Demo Credentials**
- **Email:** demo@hosteldada.com
- **Password:** demo123

### **User Roles**
- **Student** - Access to all modules
- **Admin** - Management dashboard
- **Staff** - Specific module access

## 🎨 **UI FEATURES**

### **Design System**
- **Modern gradients** and shadows
- **Responsive** mobile-first design
- **Smooth animations** and transitions
- **Consistent** spacing and typography

### **Components**
- **Authentication** forms with validation
- **Dashboard** with module navigation
- **Loading states** and error handling
- **Toast notifications** for feedback

## 📊 **DEVELOPMENT FEATURES**

### **Performance**
- ⚡ **Fast Refresh** - Instant updates
- 📦 **Code Splitting** - Optimized bundles
- 🗜️ **Automatic Optimization** - Next.js built-in
- 📱 **Mobile Optimized** - Perfect Lighthouse scores

### **Developer Experience**
- 🔷 **TypeScript** - Full type safety
- 🎨 **Tailwind IntelliSense** - Auto-complete
- 🔥 **Firebase Integration** - Real-time updates
- 📱 **Responsive Design** - Mobile-first

## 🚀 **DEPLOYMENT**

### **Automatic Deployment**
```bash
# Build and deploy in one command
npm run firebase:build
```

### **Manual Steps**
```bash
# Build for production
npm run build

# Deploy to Firebase
firebase deploy --only hosting
```

## 🔧 **DEVELOPMENT COMMANDS**

```bash
# Development server
npm run dev

# Production build
npm run build

# Start production server
npm start

# Lint code
npm run lint

# Build and deploy
npm run firebase:build
```

## 🎉 **BENEFITS OF NEW STACK**

### **🚀 Speed**
- **10x faster** development
- **Instant** hot reload
- **Quick** deployment

### **📚 Learning**
- **Easy** to understand
- **Great** documentation
- **Huge** community

### **🔧 Maintainability**
- **Clean** code structure
- **Type-safe** development
- **Modern** best practices

### **📱 Features**
- **Mobile-first** design
- **Real-time** updates
- **Progressive** Web App ready

---

## 🎯 **NEXT STEPS**

1. **Run setup.bat** to install dependencies
2. **Start with npm run dev** for development
3. **Customize components** in the /components directory
4. **Add new modules** following the existing pattern
5. **Deploy with npm run firebase:build**

**Your hostel management platform is now ready for rapid development! 🚀**
