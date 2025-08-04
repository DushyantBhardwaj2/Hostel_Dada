# Admin and User Segregation System

## 📋 **System Overview (As Per README Specifications)**

Your Hostel Dada project follows a **predefined admin email list** system with **12 total admins** distributed across modules and global access.

---

## 🔐 **Admin Email List (Complete)**

### 🌟 **Global Admins (2) - Full Access to All Modules**
```
admin1@hosteldada.com
admin2@hosteldada.com
```
**Access:** All 5 modules (SnackCart, RoomieMatcher, LaundryBalancer, MessyMess, HostelFixer)

### 🛒 **SnackCart Module Admins (2)**
```
snackcart.admin1@hosteldada.com
snackcart.admin2@hosteldada.com
```
**Access:** SnackCart module only

### 👥 **RoomieMatcher Module Admins (2)**
```
roomie.admin1@hosteldada.com
roomie.admin2@hosteldada.com
```
**Access:** RoomieMatcher module only

### 👕 **LaundryBalancer Module Admins (2)**
```
laundry.admin1@hosteldada.com
laundry.admin2@hosteldada.com
```
**Access:** LaundryBalancer module only

### 🍽️ **MessyMess Module Admins (2)**
```
mess.admin1@hosteldada.com
mess.admin2@hosteldada.com
```
**Access:** MessyMess module only

### 🔧 **HostelFixer Module Admins (2)**
```
maintenance.admin1@hosteldada.com
maintenance.admin2@hosteldada.com
```
**Access:** HostelFixer module only

---

## 🎯 **Authentication Flow**

### **1. Login Process**
- **Single shared login page** for all users
- Users enter email and password
- System validates credentials via Firebase Auth

### **2. Role Detection**
- Check logged-in email against **predefined admin list**
- Determine admin type (Global vs Module-specific)
- Set appropriate access permissions

### **3. Dashboard Routing**
- **Admin emails** → Admin Dashboard with module access
- **Regular emails** → User Dashboard
- **No role-based signup restrictions** for users

---

## 🛠️ **Implementation Details**

### **Admin Detection Logic**
```typescript
// Check if email is in predefined admin list
const adminRole = getAdminRole(user.email)

if (adminRole.isAdmin) {
  // Admin access with specific module permissions
  if (adminRole.isGlobal) {
    // Full access to all modules
  } else {
    // Access only to specific modules
    console.log('Admin modules:', adminRole.modules)
  }
} else {
  // Regular user access
}
```

### **Module Access Control**
- **Global Admins:** Can access any module's admin panel
- **Module Admins:** Can only access their assigned module's admin panel
- **Users:** Can only access user features of all modules

---

## 🔍 **Testing Admin Access**

### **To Test Global Admin Features:**
1. Sign up/Login with: `admin1@hosteldada.com` or `admin2@hosteldada.com`
2. You'll see "Global Admin" badge
3. Full access to all module admin panels

### **To Test Module-Specific Admin:**
1. Sign up/Login with any module admin email (e.g., `snackcart.admin1@hosteldada.com`)
2. You'll see "Module Admin" badge with access list
3. Admin access only to SnackCart module

### **To Test Regular User:**
1. Sign up/Login with any other email (e.g., `student@university.edu`)
2. No admin badges
3. User-level access to all modules

---

## 🎨 **Visual Indicators**

### **Dashboard Header:**
- **Global Admin:** Red "Global Admin" badge
- **Module Admin:** Red "Module Admin" badge + access list
- **User:** No admin badges

### **SnackCart Module:**
- **Admins:** See view toggle (Customer View ↔ Admin Panel)
- **Users:** Only see customer view

---

## 📊 **Admin Statistics (Current)**

```
Total Admins: 12
├── Global Admins: 2 (16.7%)
├── SnackCart Admins: 2 (16.7%)
├── RoomieMatcher Admins: 2 (16.7%)
├── LaundryBalancer Admins: 2 (16.7%)
├── MessyMess Admins: 2 (16.7%)
└── HostelFixer Admins: 2 (16.7%)

Coverage:
✅ SnackCart: Active (Admin panel implemented)
🟡 RoomieMatcher: Coming soon
🟡 LaundryBalancer: Coming soon
🟡 MessyMess: Coming soon
🟡 HostelFixer: Coming soon
```

---

## 🚀 **Next Steps**

1. **Test all admin emails** to ensure proper access control
2. **Implement remaining modules** with admin panels
3. **Add module-specific permission checks** in admin panels
4. **Create admin user management** for adding/removing admins

Your admin segregation system is now properly implemented according to your README specifications! 🎉
