// Setup Admin Accounts in Firebase Database
// Run this script once to initialize admin accounts

const firebase = require('firebase');

// Firebase config (replace with your config)
const firebaseConfig = {
    apiKey: "AIzaSyBlilGYcmSTXzFRnDbOm9HzE-xzJ8X5Xso",
    authDomain: "hostel-dada.firebaseapp.com",
    databaseURL: "https://hostel-dada-default-rtdb.firebaseio.com",
    projectId: "hostel-dada",
    storageBucket: "hostel-dada.appspot.com",
    messagingSenderId: "747399849820",
    appId: "1:747399849820:web:2b7ca14da5f94a6b2d2e50"
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);
const database = firebase.database();

// Admin accounts to create
const adminAccounts = {
    'snackcart_admin1@hosteldada_com': {
        email: 'snackcart.admin1@hosteldada.com',
        password: 'SnackAdmin123!',
        role: 'snackcart',
        modules: ['snacks', 'dashboard'],
        name: 'SnackCart Admin 1',
        createdAt: new Date().toISOString()
    },
    'snackcart_admin2@hosteldada_com': {
        email: 'snackcart.admin2@hosteldada.com',
        password: 'SnackAdmin456!',
        role: 'snackcart',
        modules: ['snacks', 'dashboard'],
        name: 'SnackCart Admin 2',
        createdAt: new Date().toISOString()
    },
    'roomie_admin1@hosteldada_com': {
        email: 'roomie.admin1@hosteldada.com',
        password: 'RoomieAdmin123!',
        role: 'roomiematcher',
        modules: ['rooms', 'dashboard'],
        name: 'Roomie Admin 1',
        createdAt: new Date().toISOString()
    },
    'roomie_admin2@hosteldada_com': {
        email: 'roomie.admin2@hosteldada.com',
        password: 'RoomieAdmin456!',
        role: 'roomiematcher',
        modules: ['rooms', 'dashboard'],
        name: 'Roomie Admin 2',
        createdAt: new Date().toISOString()
    },
    'laundry_admin1@hosteldada_com': {
        email: 'laundry.admin1@hosteldada.com',
        password: 'LaundryAdmin123!',
        role: 'laundrybalancer',
        modules: ['laundry', 'dashboard'],
        name: 'Laundry Admin 1',
        createdAt: new Date().toISOString()
    },
    'laundry_admin2@hosteldada_com': {
        email: 'laundry.admin2@hosteldada.com',
        password: 'LaundryAdmin456!',
        role: 'laundrybalancer',
        modules: ['laundry', 'dashboard'],
        name: 'Laundry Admin 2',
        createdAt: new Date().toISOString()
    },
    'mess_admin1@hosteldada_com': {
        email: 'mess.admin1@hosteldada.com',
        password: 'MessAdmin123!',
        role: 'messymess',
        modules: ['mess', 'dashboard'],
        name: 'Mess Admin 1',
        createdAt: new Date().toISOString()
    },
    'mess_admin2@hosteldada_com': {
        email: 'mess.admin2@hosteldada.com',
        password: 'MessAdmin456!',
        role: 'messymess',
        modules: ['mess', 'dashboard'],
        name: 'Mess Admin 2',
        createdAt: new Date().toISOString()
    },
    'fixer_admin1@hosteldada_com': {
        email: 'fixer.admin1@hosteldada.com',
        password: 'FixerAdmin123!',
        role: 'hostelfixer',
        modules: ['maintenance', 'dashboard'],
        name: 'Fixer Admin 1',
        createdAt: new Date().toISOString()
    },
    'fixer_admin2@hosteldada_com': {
        email: 'fixer.admin2@hosteldada.com',
        password: 'FixerAdmin456!',
        role: 'hostelfixer',
        modules: ['maintenance', 'dashboard'],
        name: 'Fixer Admin 2',
        createdAt: new Date().toISOString()
    },
    'global_admin1@hosteldada_com': {
        email: 'global.admin1@hosteldada.com',
        password: 'GlobalAdmin123!',
        role: 'global',
        modules: ['dashboard', 'snacks', 'rooms', 'mess', 'maintenance', 'students'],
        name: 'Global Admin 1',
        createdAt: new Date().toISOString()
    },
    'global_admin2@hosteldada_com': {
        email: 'global.admin2@hosteldada.com',
        password: 'GlobalAdmin456!',
        role: 'global',
        modules: ['dashboard', 'snacks', 'rooms', 'mess', 'maintenance', 'students'],
        name: 'Global Admin 2',
        createdAt: new Date().toISOString()
    }
};

// Function to setup admin accounts
async function setupAdminAccounts() {
    console.log('🔧 Setting up admin accounts...');
    
    try {
        // Create admin_credentials node
        await database.ref('admin_credentials').set(adminAccounts);
        console.log('✅ Admin accounts created successfully!');
        
        // Log admin credentials for reference
        console.log('\n📋 ADMIN LOGIN CREDENTIALS:');
        console.log('============================');
        Object.values(adminAccounts).forEach(admin => {
            console.log(`📧 ${admin.email}`);
            console.log(`🔑 ${admin.password}`);
            console.log(`👑 Role: ${admin.role}`);
            console.log('----------------------------');
        });
        
    } catch (error) {
        console.error('❌ Error setting up admin accounts:', error);
    }
}

// Run the setup
setupAdminAccounts();
