// Predefined admin email list as specified in README
// 12 total admins: 2 per module (10) + 2 global admins

export const ADMIN_EMAILS = {
  // Global Admins (Access to all modules)
  global: [
    'admin1@hosteldada.com',
    'admin2@hosteldada.com'
  ],
  
  // Module-Specific Admins (2 per module)
  snackCart: [
    'snackcart.admin1@hosteldada.com',
    'snackcart.admin2@hosteldada.com'
  ],
  
  roomieMatcher: [
    'roomie.admin1@hosteldada.com', 
    'roomie.admin2@hosteldada.com'
  ],
  
  laundryBalancer: [
    'laundry.admin1@hosteldada.com',
    'laundry.admin2@hosteldada.com'
  ],
  
  messyMess: [
    'mess.admin1@hosteldada.com',
    'mess.admin2@hosteldada.com'
  ],
  
  hostelFixer: [
    'maintenance.admin1@hosteldada.com',
    'maintenance.admin2@hosteldada.com'
  ]
}

// Flatten all admin emails for quick lookup
export const ALL_ADMIN_EMAILS: string[] = [
  ...ADMIN_EMAILS.global,
  ...ADMIN_EMAILS.snackCart,
  ...ADMIN_EMAILS.roomieMatcher,
  ...ADMIN_EMAILS.laundryBalancer,
  ...ADMIN_EMAILS.messyMess,
  ...ADMIN_EMAILS.hostelFixer
]

// Admin role detection
export const getAdminRole = (email: string): {
  isAdmin: boolean
  isGlobal: boolean
  modules: string[]
} => {
  if (!email) {
    return {
      isAdmin: false,
      isGlobal: false,
      modules: []
    }
  }
  
  const normalizedEmail = email.toLowerCase().trim()
  
  // Check if global admin
  if (ADMIN_EMAILS.global.some(adminEmail => adminEmail.toLowerCase() === normalizedEmail)) {
    return {
      isAdmin: true,
      isGlobal: true,
      modules: ['snackCart', 'roomieMatcher', 'laundryBalancer', 'messyMess', 'hostelFixer']
    }
  }
  
  // Check module-specific admin access
  const modules: string[] = []
  
  if (ADMIN_EMAILS.snackCart.some(adminEmail => adminEmail.toLowerCase() === normalizedEmail)) {
    modules.push('snackCart')
  }
  if (ADMIN_EMAILS.roomieMatcher.some(adminEmail => adminEmail.toLowerCase() === normalizedEmail)) {
    modules.push('roomieMatcher')
  }
  if (ADMIN_EMAILS.laundryBalancer.some(adminEmail => adminEmail.toLowerCase() === normalizedEmail)) {
    modules.push('laundryBalancer')
  }
  if (ADMIN_EMAILS.messyMess.some(adminEmail => adminEmail.toLowerCase() === normalizedEmail)) {
    modules.push('messyMess')
  }
  if (ADMIN_EMAILS.hostelFixer.some(adminEmail => adminEmail.toLowerCase() === normalizedEmail)) {
    modules.push('hostelFixer')
  }
  
  return {
    isAdmin: modules.length > 0,
    isGlobal: false,
    modules
  }
}

// Quick admin check
export const isAdminEmail = (email: string): boolean => {
  if (!email) return false
  const normalizedEmail = email.toLowerCase().trim()
  return ALL_ADMIN_EMAILS.some(adminEmail => adminEmail.toLowerCase() === normalizedEmail)
}
