// Test Admin Config Functionality
import { getAdminRole, isAdminEmail, ALL_ADMIN_EMAILS } from './src/lib/admin-config'

console.log('🧪 Testing Admin Config...')
console.log('========================')

// Test emails from ADMIN_CREDENTIALS.txt
const testEmails = [
  'admin1@hosteldada.com',
  'admin2@hosteldada.com', 
  'snackcart.admin1@hosteldada.com',
  'snackcart.admin2@hosteldada.com',
  'roomie.admin1@hosteldada.com',
  'laundry.admin1@hosteldada.com',
  'mess.admin1@hosteldada.com',
  'maintenance.admin1@hosteldada.com',
  'student@university.edu', // Not admin
  'demo@hosteldada.com'     // Not admin
]

console.log('📋 All Admin Emails:', ALL_ADMIN_EMAILS)
console.log('📊 Total Admin Emails:', ALL_ADMIN_EMAILS.length)

console.log('\n🔍 Testing Each Email:')
console.log('=====================')

testEmails.forEach(email => {
  const adminRole = getAdminRole(email)
  const isAdmin = isAdminEmail(email)
  
  console.log(`\n📧 ${email}`)
  console.log(`   ✅ isAdmin: ${isAdmin}`)
  console.log(`   🌟 isGlobal: ${adminRole.isGlobal}`)
  console.log(`   📦 modules: [${adminRole.modules.join(', ')}]`)
  console.log(`   🔧 adminRole object:`, adminRole)
})

// Test case sensitivity
console.log('\n🔤 Testing Case Sensitivity:')
console.log('============================')
const testCases = [
  'ADMIN1@HOSTELDADA.COM',
  'Admin1@HostelDada.com',
  'admin1@HOSTELDADA.COM'
]

testCases.forEach(email => {
  const result = isAdminEmail(email)
  console.log(`📧 ${email} -> isAdmin: ${result}`)
})

export { }
