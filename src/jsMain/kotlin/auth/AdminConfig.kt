package auth

/**
 * 🔐 Admin Configuration for Hostel Dada
 * 
 * Contains predefined admin emails and role assignments:
 * - 2 admins per module (5 modules) = 10 module-specific admins
 * - 2 global admins with access to all modules = 2 global admins
 * Total: 12 admin accounts
 */
object AdminConfig {
    
    /**
     * Module-specific admin emails
     * Each module has exactly 2 dedicated admins
     */
    val SNACKCART_ADMINS = setOf(
        "snackcart.admin1@hosteldada.com",
        "snackcart.admin2@hosteldada.com"
    )
    
    val ROOMIEMATCHER_ADMINS = setOf(
        "roomie.admin1@hosteldada.com", 
        "roomie.admin2@hosteldada.com"
    )
    
    val LAUNDRYBALANCER_ADMINS = setOf(
        "laundry.admin1@hosteldada.com",
        "laundry.admin2@hosteldada.com"
    )
    
    val MESSYMESS_ADMINS = setOf(
        "mess.admin1@hosteldada.com",
        "mess.admin2@hosteldada.com"
    )
    
    val HOSTELFIXER_ADMINS = setOf(
        "fixer.admin1@hosteldada.com",
        "fixer.admin2@hosteldada.com"
    )
    
    /**
     * Global admins with access to all modules
     */
    val GLOBAL_ADMINS = setOf(
        "global.admin1@hosteldada.com",
        "global.admin2@hosteldada.com"
    )
    
    /**
     * All admin emails combined
     */
    val ALL_ADMIN_EMAILS = SNACKCART_ADMINS + 
                          ROOMIEMATCHER_ADMINS + 
                          LAUNDRYBALANCER_ADMINS + 
                          MESSYMESS_ADMINS + 
                          HOSTELFIXER_ADMINS + 
                          GLOBAL_ADMINS
    
    /**
     * Check if an email belongs to an admin
     */
    fun isAdmin(email: String): Boolean {
        return email.lowercase() in ALL_ADMIN_EMAILS.map { it.lowercase() }
    }
    
    /**
     * Get admin role based on email
     */
    fun getAdminRole(email: String): AdminRole? {
        val emailLower = email.lowercase()
        
        return when {
            emailLower in GLOBAL_ADMINS.map { it.lowercase() } -> AdminRole.GLOBAL_ADMIN
            emailLower in SNACKCART_ADMINS.map { it.lowercase() } -> AdminRole.SNACKCART_ADMIN
            emailLower in ROOMIEMATCHER_ADMINS.map { it.lowercase() } -> AdminRole.ROOMIEMATCHER_ADMIN
            emailLower in LAUNDRYBALANCER_ADMINS.map { it.lowercase() } -> AdminRole.LAUNDRYBALANCER_ADMIN
            emailLower in MESSYMESS_ADMINS.map { it.lowercase() } -> AdminRole.MESSYMESS_ADMIN
            emailLower in HOSTELFIXER_ADMINS.map { it.lowercase() } -> AdminRole.HOSTELFIXER_ADMIN
            else -> null
        }
    }
    
    /**
     * Get accessible modules for an admin
     */
    fun getAccessibleModules(email: String): Set<String> {
        val adminRole = getAdminRole(email) ?: return emptySet()
        
        return when (adminRole) {
            AdminRole.GLOBAL_ADMIN -> setOf("snackcart", "roomiematcher", "laundrybalancer", "messymess", "hostelfixer")
            AdminRole.SNACKCART_ADMIN -> setOf("snackcart")
            AdminRole.ROOMIEMATCHER_ADMIN -> setOf("roomiematcher") 
            AdminRole.LAUNDRYBALANCER_ADMIN -> setOf("laundrybalancer")
            AdminRole.MESSYMESS_ADMIN -> setOf("messymess")
            AdminRole.HOSTELFIXER_ADMIN -> setOf("hostelfixer")
        }
    }
}

/**
 * Admin role enumeration
 */
enum class AdminRole {
    GLOBAL_ADMIN,
    SNACKCART_ADMIN,
    ROOMIEMATCHER_ADMIN,
    LAUNDRYBALANCER_ADMIN,
    MESSYMESS_ADMIN,
    HOSTELFIXER_ADMIN
}

/**
 * User role enumeration
 */
enum class UserRole {
    USER,
    ADMIN
}
