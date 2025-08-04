// Direct Firebase Admin Account Creation Script
// Run this with: node create-admins.js

const { createUserWithEmailAndPassword } = require('firebase/auth');
const { doc, setDoc } = require('firebase/firestore');

// Import your Firebase config (you'll need to adjust this)
// This is a Node.js script to create all admin accounts

const ADMIN_ACCOUNTS = [
  // Global Admins (2)
  { email: 'admin1@hosteldada.com', password: 'admin123', role: 'admin', type: 'global', module: null },
  { email: 'admin2@hosteldada.com', password: 'admin456', role: 'admin', type: 'global', module: null },
  
  // SnackCart Admins (2)
  { email: 'snackcart.admin1@hosteldada.com', password: 'snack123', role: 'admin', type: 'module', module: 'snackCart' },
  { email: 'snackcart.admin2@hosteldada.com', password: 'snack456', role: 'admin', type: 'module', module: 'snackCart' },
  
  // RoomieMatcher Admins (2)
  { email: 'roomie.admin1@hosteldada.com', password: 'roomie123', role: 'admin', type: 'module', module: 'roomieMatcher' },
  { email: 'roomie.admin2@hosteldada.com', password: 'roomie456', role: 'admin', type: 'module', module: 'roomieMatcher' },
  
  // LaundryBalancer Admins (2)
  { email: 'laundry.admin1@hosteldada.com', password: 'laundry123', role: 'admin', type: 'module', module: 'laundryBalancer' },
  { email: 'laundry.admin2@hosteldada.com', password: 'laundry456', role: 'admin', type: 'module', module: 'laundryBalancer' },
  
  // MessyMess Admins (2)
  { email: 'mess.admin1@hosteldada.com', password: 'mess123', role: 'admin', type: 'module', module: 'messyMess' },
  { email: 'mess.admin2@hosteldada.com', password: 'mess456', role: 'admin', type: 'module', module: 'messyMess' },
  
  // HostelFixer Admins (2)
  { email: 'maintenance.admin1@hosteldada.com', password: 'maintenance123', role: 'admin', type: 'module', module: 'hostelFixer' },
  { email: 'maintenance.admin2@hosteldada.com', password: 'maintenance456', role: 'admin', type: 'module', module: 'hostelFixer' },

  // Test Users (2)
  { email: 'student@university.edu', password: 'student123', role: 'user', type: 'student', module: null },
  { email: 'demo@hosteldada.com', password: 'demo123', role: 'user', type: 'demo', module: null }
];

console.log('📋 Admin Accounts to Create:');
console.log('=============================');
ADMIN_ACCOUNTS.forEach((account, index) => {
  console.log(`${index + 1}. ${account.email} (${account.type} ${account.role})`);
});

console.log(`\n🔢 Total Accounts: ${ADMIN_ACCOUNTS.length}`);
console.log('💡 To create these accounts, use the Admin Setup page at:');
console.log('   http://localhost:3004/admin-setup');
console.log('\n⚠️  IMPORTANT: Make sure your Firebase project is configured correctly!');
