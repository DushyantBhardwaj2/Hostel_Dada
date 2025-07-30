package modules.hostelfixer

import kotlinx.serialization.Serializable
import kotlin.math.*

/**
 * 🔧 HostelFixer Module - Maintenance & Issue Resolution Data Models
 * 
 * Advanced issue management with DSA optimization:
 * - Priority classification using decision trees
 * - Assignment algorithms for optimal resource allocation
 * - SLA tracking with automated escalation
 * - Predictive maintenance using pattern analysis
 */

/**
 * 🎫 Issue/Complaint
 */
@Serializable
data class Issue(
    val issueId: String,
    val reportedBy: String, // userId
    val hostelId: String,
    val roomNumber: String? = null,
    val location: String, // specific location details
    val floor: Int? = null,
    val category: IssueCategory,
    val subCategory: String,
    val title: String,
    val description: String,
    val priority: IssuePriority,
    val severity: IssueSeverity,
    val urgency: IssueUrgency,
    val status: IssueStatus,
    val assignedTo: String? = null, // maintenance staff ID
    val createdAt: Long,
    val updatedAt: Long,
    val acknowledgedAt: Long? = null,
    val resolvedAt: Long? = null,
    val estimatedResolutionTime: Long? = null,
    val actualResolutionTime: Long? = null,
    val photoUrls: List<String> = emptyList(),
    val videoUrls: List<String> = emptyList(),
    val attachments: List<String> = emptyList(),
    val impact: ImpactLevel,
    val affectedUsers: List<String> = emptyList(),
    val relatedIssues: List<String> = emptyList(),
    val requiredSkills: List<String> = emptyList(),
    val estimatedCost: Double? = null,
    val actualCost: Double? = null,
    val satisfaction: Int? = null, // 1-5 rating from reporter
    val feedback: String? = null,
    val escalationLevel: EscalationLevel = EscalationLevel.L1,
    val escalationHistory: List<EscalationRecord> = emptyList(),
    val slaDeadline: Long? = null,
    val isRecurring: Boolean = false,
    val tags: List<String> = emptyList(),
    val workOrderId: String? = null
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Issue {
            return Issue(
                issueId = map["issueId"] as String,
                reportedBy = map["reportedBy"] as String,
                hostelId = map["hostelId"] as String,
                roomNumber = map["roomNumber"] as? String,
                location = map["location"] as String,
                floor = (map["floor"] as? Number)?.toInt(),
                category = IssueCategory.valueOf(map["category"] as String),
                subCategory = map["subCategory"] as String,
                title = map["title"] as String,
                description = map["description"] as String,
                priority = IssuePriority.valueOf(map["priority"] as String),
                severity = IssueSeverity.valueOf(map["severity"] as String),
                urgency = IssueUrgency.valueOf(map["urgency"] as String),
                status = IssueStatus.valueOf(map["status"] as String),
                assignedTo = map["assignedTo"] as? String,
                createdAt = (map["createdAt"] as Number).toLong(),
                updatedAt = (map["updatedAt"] as Number).toLong(),
                acknowledgedAt = (map["acknowledgedAt"] as? Number)?.toLong(),
                resolvedAt = (map["resolvedAt"] as? Number)?.toLong(),
                estimatedResolutionTime = (map["estimatedResolutionTime"] as? Number)?.toLong(),
                actualResolutionTime = (map["actualResolutionTime"] as? Number)?.toLong(),
                photoUrls = (map["photoUrls"] as? List<String>) ?: emptyList(),
                videoUrls = (map["videoUrls"] as? List<String>) ?: emptyList(),
                attachments = (map["attachments"] as? List<String>) ?: emptyList(),
                impact = ImpactLevel.valueOf(map["impact"] as String),
                affectedUsers = (map["affectedUsers"] as? List<String>) ?: emptyList(),
                relatedIssues = (map["relatedIssues"] as? List<String>) ?: emptyList(),
                requiredSkills = (map["requiredSkills"] as? List<String>) ?: emptyList(),
                estimatedCost = (map["estimatedCost"] as? Number)?.toDouble(),
                actualCost = (map["actualCost"] as? Number)?.toDouble(),
                satisfaction = (map["satisfaction"] as? Number)?.toInt(),
                feedback = map["feedback"] as? String,
                escalationLevel = EscalationLevel.valueOf(map["escalationLevel"] as? String ?: "L1"),
                escalationHistory = (map["escalationHistory"] as? List<Map<String, Any>>)?.map { 
                    EscalationRecord.fromMap(it) 
                } ?: emptyList(),
                slaDeadline = (map["slaDeadline"] as? Number)?.toLong(),
                isRecurring = map["isRecurring"] as? Boolean ?: false,
                tags = (map["tags"] as? List<String>) ?: emptyList(),
                workOrderId = map["workOrderId"] as? String
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("issueId", issueId)
            put("reportedBy", reportedBy)
            put("hostelId", hostelId)
            roomNumber?.let { put("roomNumber", it) }
            put("location", location)
            floor?.let { put("floor", it) }
            put("category", category.name)
            put("subCategory", subCategory)
            put("title", title)
            put("description", description)
            put("priority", priority.name)
            put("severity", severity.name)
            put("urgency", urgency.name)
            put("status", status.name)
            assignedTo?.let { put("assignedTo", it) }
            put("createdAt", createdAt)
            put("updatedAt", updatedAt)
            acknowledgedAt?.let { put("acknowledgedAt", it) }
            resolvedAt?.let { put("resolvedAt", it) }
            estimatedResolutionTime?.let { put("estimatedResolutionTime", it) }
            actualResolutionTime?.let { put("actualResolutionTime", it) }
            put("photoUrls", photoUrls)
            put("videoUrls", videoUrls)
            put("attachments", attachments)
            put("impact", impact.name)
            put("affectedUsers", affectedUsers)
            put("relatedIssues", relatedIssues)
            put("requiredSkills", requiredSkills)
            estimatedCost?.let { put("estimatedCost", it) }
            actualCost?.let { put("actualCost", it) }
            satisfaction?.let { put("satisfaction", it) }
            feedback?.let { put("feedback", it) }
            put("escalationLevel", escalationLevel.name)
            put("escalationHistory", escalationHistory.map { it.toMap() })
            slaDeadline?.let { put("slaDeadline", it) }
            put("isRecurring", isRecurring)
            put("tags", tags)
            workOrderId?.let { put("workOrderId", it) }
        }
    }
    
    fun isOverdue(): Boolean {
        return slaDeadline?.let { it < System.currentTimeMillis() } ?: false
    }
    
    fun getTimeToDeadline(): Long? {
        return slaDeadline?.let { it - System.currentTimeMillis() }
    }
    
    fun getResolutionTime(): Long? {
        return if (resolvedAt != null && createdAt != null) {
            resolvedAt - createdAt
        } else null
    }
    
    fun getAcknowledgmentTime(): Long? {
        return if (acknowledgedAt != null && createdAt != null) {
            acknowledgedAt - createdAt
        } else null
    }
    
    fun requiresEscalation(): Boolean {
        val timeElapsed = System.currentTimeMillis() - createdAt
        val thresholdHours = when (priority) {
            IssuePriority.CRITICAL -> 1
            IssuePriority.HIGH -> 4
            IssuePriority.MEDIUM -> 24
            IssuePriority.LOW -> 72
        }
        return timeElapsed > (thresholdHours * 60 * 60 * 1000) && status != IssueStatus.RESOLVED
    }
    
    fun calculatePriorityScore(): Double {
        val severityScore = when (severity) {
            IssueSeverity.CRITICAL -> 10.0
            IssueSeverity.HIGH -> 7.0
            IssueSeverity.MEDIUM -> 5.0
            IssueSeverity.LOW -> 2.0
        }
        
        val urgencyScore = when (urgency) {
            IssueUrgency.IMMEDIATE -> 10.0
            IssueUrgency.HIGH -> 7.0
            IssueUrgency.MEDIUM -> 5.0
            IssueUrgency.LOW -> 2.0
        }
        
        val impactScore = when (impact) {
            ImpactLevel.BUILDING -> 10.0
            ImpactLevel.FLOOR -> 7.0
            ImpactLevel.MULTIPLE_ROOMS -> 5.0
            ImpactLevel.SINGLE_ROOM -> 2.0
            ImpactLevel.INDIVIDUAL -> 1.0
        }
        
        return (severityScore * 0.4) + (urgencyScore * 0.4) + (impactScore * 0.2)
    }
}

/**
 * 🔧 Maintenance Staff
 */
@Serializable
data class MaintenanceStaff(
    val staffId: String,
    val name: String,
    val contactNumber: String,
    val email: String,
    val specializations: List<String>, // plumbing, electrical, carpentry, etc.
    val skillLevel: SkillLevel,
    val experience: Int, // years
    val availability: StaffAvailability,
    val currentLocation: String? = null,
    val assignedIssues: List<String> = emptyList(),
    val maxConcurrentIssues: Int = 3,
    val workingHours: WorkingSchedule,
    val performanceRating: Double = 0.0,
    val completedIssues: Int = 0,
    val averageResolutionTime: Double = 0.0, // hours
    val customerSatisfactionRating: Double = 0.0,
    val certifications: List<String> = emptyList(),
    val emergencyContact: String? = null,
    val joinedDate: Long,
    val lastActiveAt: Long,
    val isActive: Boolean = true,
    val preferredShift: WorkShift,
    val costPerHour: Double = 0.0,
    val tools: List<String> = emptyList()
) {
    companion object {
        fun fromMap(map: Map<String, Any>): MaintenanceStaff {
            return MaintenanceStaff(
                staffId = map["staffId"] as String,
                name = map["name"] as String,
                contactNumber = map["contactNumber"] as String,
                email = map["email"] as String,
                specializations = map["specializations"] as List<String>,
                skillLevel = SkillLevel.valueOf(map["skillLevel"] as String),
                experience = (map["experience"] as Number).toInt(),
                availability = StaffAvailability.valueOf(map["availability"] as String),
                currentLocation = map["currentLocation"] as? String,
                assignedIssues = (map["assignedIssues"] as? List<String>) ?: emptyList(),
                maxConcurrentIssues = (map["maxConcurrentIssues"] as? Number)?.toInt() ?: 3,
                workingHours = WorkingSchedule.fromMap(map["workingHours"] as Map<String, Any>),
                performanceRating = (map["performanceRating"] as? Number)?.toDouble() ?: 0.0,
                completedIssues = (map["completedIssues"] as? Number)?.toInt() ?: 0,
                averageResolutionTime = (map["averageResolutionTime"] as? Number)?.toDouble() ?: 0.0,
                customerSatisfactionRating = (map["customerSatisfactionRating"] as? Number)?.toDouble() ?: 0.0,
                certifications = (map["certifications"] as? List<String>) ?: emptyList(),
                emergencyContact = map["emergencyContact"] as? String,
                joinedDate = (map["joinedDate"] as Number).toLong(),
                lastActiveAt = (map["lastActiveAt"] as Number).toLong(),
                isActive = map["isActive"] as? Boolean ?: true,
                preferredShift = WorkShift.valueOf(map["preferredShift"] as? String ?: "DAY"),
                costPerHour = (map["costPerHour"] as? Number)?.toDouble() ?: 0.0,
                tools = (map["tools"] as? List<String>) ?: emptyList()
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("staffId", staffId)
            put("name", name)
            put("contactNumber", contactNumber)
            put("email", email)
            put("specializations", specializations)
            put("skillLevel", skillLevel.name)
            put("experience", experience)
            put("availability", availability.name)
            currentLocation?.let { put("currentLocation", it) }
            put("assignedIssues", assignedIssues)
            put("maxConcurrentIssues", maxConcurrentIssues)
            put("workingHours", workingHours.toMap())
            put("performanceRating", performanceRating)
            put("completedIssues", completedIssues)
            put("averageResolutionTime", averageResolutionTime)
            put("customerSatisfactionRating", customerSatisfactionRating)
            put("certifications", certifications)
            emergencyContact?.let { put("emergencyContact", it) }
            put("joinedDate", joinedDate)
            put("lastActiveAt", lastActiveAt)
            put("isActive", isActive)
            put("preferredShift", preferredShift.name)
            put("costPerHour", costPerHour)
            put("tools", tools)
        }
    }
    
    fun isAvailable(): Boolean = availability == StaffAvailability.AVAILABLE && isActive
    fun canTakeMoreIssues(): Boolean = assignedIssues.size < maxConcurrentIssues
    fun hasSkill(skill: String): Boolean = specializations.contains(skill)
    
    fun getOverallScore(): Double {
        return (performanceRating * 0.3) + 
               (customerSatisfactionRating * 0.3) + 
               ((100.0 - averageResolutionTime) / 100.0 * 0.2) + 
               (experience / 20.0 * 0.2)
    }
    
    fun getWorkload(): Double = assignedIssues.size.toDouble() / maxConcurrentIssues
}

/**
 * 📋 Work Order
 */
@Serializable
data class WorkOrder(
    val workOrderId: String,
    val issueId: String,
    val assignedStaffId: String,
    val title: String,
    val description: String,
    val priority: IssuePriority,
    val estimatedDuration: Int, // minutes
    val actualDuration: Int? = null,
    val scheduledStartTime: Long,
    val actualStartTime: Long? = null,
    val completedTime: Long? = null,
    val status: WorkOrderStatus,
    val materials: List<Material> = emptyList(),
    val estimatedCost: Double,
    val actualCost: Double? = null,
    val notes: String? = null,
    val beforePhotos: List<String> = emptyList(),
    val afterPhotos: List<String> = emptyList(),
    val customerSignature: String? = null,
    val qualityCheck: QualityCheck? = null,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        fun fromMap(map: Map<String, Any>): WorkOrder {
            return WorkOrder(
                workOrderId = map["workOrderId"] as String,
                issueId = map["issueId"] as String,
                assignedStaffId = map["assignedStaffId"] as String,
                title = map["title"] as String,
                description = map["description"] as String,
                priority = IssuePriority.valueOf(map["priority"] as String),
                estimatedDuration = (map["estimatedDuration"] as Number).toInt(),
                actualDuration = (map["actualDuration"] as? Number)?.toInt(),
                scheduledStartTime = (map["scheduledStartTime"] as Number).toLong(),
                actualStartTime = (map["actualStartTime"] as? Number)?.toLong(),
                completedTime = (map["completedTime"] as? Number)?.toLong(),
                status = WorkOrderStatus.valueOf(map["status"] as String),
                materials = (map["materials"] as? List<Map<String, Any>>)?.map { 
                    Material.fromMap(it) 
                } ?: emptyList(),
                estimatedCost = (map["estimatedCost"] as Number).toDouble(),
                actualCost = (map["actualCost"] as? Number)?.toDouble(),
                notes = map["notes"] as? String,
                beforePhotos = (map["beforePhotos"] as? List<String>) ?: emptyList(),
                afterPhotos = (map["afterPhotos"] as? List<String>) ?: emptyList(),
                customerSignature = map["customerSignature"] as? String,
                qualityCheck = (map["qualityCheck"] as? Map<String, Any>)?.let { 
                    QualityCheck.fromMap(it) 
                },
                createdAt = (map["createdAt"] as Number).toLong(),
                updatedAt = (map["updatedAt"] as Number).toLong()
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("workOrderId", workOrderId)
            put("issueId", issueId)
            put("assignedStaffId", assignedStaffId)
            put("title", title)
            put("description", description)
            put("priority", priority.name)
            put("estimatedDuration", estimatedDuration)
            actualDuration?.let { put("actualDuration", it) }
            put("scheduledStartTime", scheduledStartTime)
            actualStartTime?.let { put("actualStartTime", it) }
            completedTime?.let { put("completedTime", it) }
            put("status", status.name)
            put("materials", materials.map { it.toMap() })
            put("estimatedCost", estimatedCost)
            actualCost?.let { put("actualCost", it) }
            notes?.let { put("notes", it) }
            put("beforePhotos", beforePhotos)
            put("afterPhotos", afterPhotos)
            customerSignature?.let { put("customerSignature", it) }
            qualityCheck?.let { put("qualityCheck", it.toMap()) }
            put("createdAt", createdAt)
            put("updatedAt", updatedAt)
        }
    }
    
    fun isOverdue(): Boolean {
        val currentTime = System.currentTimeMillis()
        return currentTime > scheduledStartTime + (estimatedDuration * 60 * 1000)
    }
    
    fun getActualDurationHours(): Double? {
        return if (actualStartTime != null && completedTime != null) {
            (completedTime - actualStartTime).toDouble() / (1000 * 60 * 60)
        } else null
    }
}

/**
 * 📦 Material/Part
 */
@Serializable
data class Material(
    val materialId: String,
    val name: String,
    val description: String,
    val category: MaterialCategory,
    val quantityRequired: Double,
    val unit: String, // pieces, meters, kg, etc.
    val unitCost: Double,
    val totalCost: Double,
    val supplier: String? = null,
    val partNumber: String? = null,
    val isAvailable: Boolean = true,
    val leadTime: Int = 0, // days
    val minimumStock: Double = 0.0,
    val currentStock: Double = 0.0
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Material {
            return Material(
                materialId = map["materialId"] as String,
                name = map["name"] as String,
                description = map["description"] as String,
                category = MaterialCategory.valueOf(map["category"] as String),
                quantityRequired = (map["quantityRequired"] as Number).toDouble(),
                unit = map["unit"] as String,
                unitCost = (map["unitCost"] as Number).toDouble(),
                totalCost = (map["totalCost"] as Number).toDouble(),
                supplier = map["supplier"] as? String,
                partNumber = map["partNumber"] as? String,
                isAvailable = map["isAvailable"] as? Boolean ?: true,
                leadTime = (map["leadTime"] as? Number)?.toInt() ?: 0,
                minimumStock = (map["minimumStock"] as? Number)?.toDouble() ?: 0.0,
                currentStock = (map["currentStock"] as? Number)?.toDouble() ?: 0.0
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("materialId", materialId)
            put("name", name)
            put("description", description)
            put("category", category.name)
            put("quantityRequired", quantityRequired)
            put("unit", unit)
            put("unitCost", unitCost)
            put("totalCost", totalCost)
            supplier?.let { put("supplier", it) }
            partNumber?.let { put("partNumber", it) }
            put("isAvailable", isAvailable)
            put("leadTime", leadTime)
            put("minimumStock", minimumStock)
            put("currentStock", currentStock)
        }
    }
    
    fun needsReorder(): Boolean = currentStock <= minimumStock
    fun isInStock(): Boolean = currentStock >= quantityRequired
}

/**
 * ✅ Quality Check
 */
@Serializable
data class QualityCheck(
    val checkId: String,
    val workOrderId: String,
    val inspectorId: String,
    val checklistItems: List<ChecklistItem>,
    val overallScore: Int, // 1-10
    val passed: Boolean,
    val notes: String? = null,
    val followUpRequired: Boolean = false,
    val followUpDate: Long? = null,
    val completedAt: Long
) {
    companion object {
        fun fromMap(map: Map<String, Any>): QualityCheck {
            return QualityCheck(
                checkId = map["checkId"] as String,
                workOrderId = map["workOrderId"] as String,
                inspectorId = map["inspectorId"] as String,
                checklistItems = (map["checklistItems"] as List<Map<String, Any>>).map { 
                    ChecklistItem.fromMap(it) 
                },
                overallScore = (map["overallScore"] as Number).toInt(),
                passed = map["passed"] as Boolean,
                notes = map["notes"] as? String,
                followUpRequired = map["followUpRequired"] as? Boolean ?: false,
                followUpDate = (map["followUpDate"] as? Number)?.toLong(),
                completedAt = (map["completedAt"] as Number).toLong()
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("checkId", checkId)
            put("workOrderId", workOrderId)
            put("inspectorId", inspectorId)
            put("checklistItems", checklistItems.map { it.toMap() })
            put("overallScore", overallScore)
            put("passed", passed)
            notes?.let { put("notes", it) }
            put("followUpRequired", followUpRequired)
            followUpDate?.let { put("followUpDate", it) }
            put("completedAt", completedAt)
        }
    }
}

/**
 * ☑️ Checklist Item
 */
@Serializable
data class ChecklistItem(
    val itemId: String,
    val description: String,
    val category: String,
    val isPassed: Boolean,
    val score: Int? = null, // 1-10 for graded items
    val notes: String? = null,
    val photoUrl: String? = null
) {
    companion object {
        fun fromMap(map: Map<String, Any>): ChecklistItem {
            return ChecklistItem(
                itemId = map["itemId"] as String,
                description = map["description"] as String,
                category = map["category"] as String,
                isPassed = map["isPassed"] as Boolean,
                score = (map["score"] as? Number)?.toInt(),
                notes = map["notes"] as? String,
                photoUrl = map["photoUrl"] as? String
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("itemId", itemId)
            put("description", description)
            put("category", category)
            put("isPassed", isPassed)
            score?.let { put("score", it) }
            notes?.let { put("notes", it) }
            photoUrl?.let { put("photoUrl", it) }
        }
    }
}

/**
 * 📈 Escalation Record
 */
@Serializable
data class EscalationRecord(
    val escalationId: String,
    val fromLevel: EscalationLevel,
    val toLevel: EscalationLevel,
    val reason: String,
    val escalatedBy: String,
    val escalatedTo: String,
    val escalatedAt: Long,
    val notes: String? = null
) {
    companion object {
        fun fromMap(map: Map<String, Any>): EscalationRecord {
            return EscalationRecord(
                escalationId = map["escalationId"] as String,
                fromLevel = EscalationLevel.valueOf(map["fromLevel"] as String),
                toLevel = EscalationLevel.valueOf(map["toLevel"] as String),
                reason = map["reason"] as String,
                escalatedBy = map["escalatedBy"] as String,
                escalatedTo = map["escalatedTo"] as String,
                escalatedAt = (map["escalatedAt"] as Number).toLong(),
                notes = map["notes"] as? String
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("escalationId", escalationId)
            put("fromLevel", fromLevel.name)
            put("toLevel", toLevel.name)
            put("reason", reason)
            put("escalatedBy", escalatedBy)
            put("escalatedTo", escalatedTo)
            put("escalatedAt", escalatedAt)
            notes?.let { put("notes", it) }
        }
    }
}

/**
 * 📅 Working Schedule
 */
@Serializable
data class WorkingSchedule(
    val monday: ShiftHours,
    val tuesday: ShiftHours,
    val wednesday: ShiftHours,
    val thursday: ShiftHours,
    val friday: ShiftHours,
    val saturday: ShiftHours,
    val sunday: ShiftHours,
    val isFlexible: Boolean = false,
    val overtimeAllowed: Boolean = false,
    val maxOvertimeHours: Int = 0
) {
    companion object {
        fun fromMap(map: Map<String, Any>): WorkingSchedule {
            return WorkingSchedule(
                monday = ShiftHours.fromMap(map["monday"] as Map<String, Any>),
                tuesday = ShiftHours.fromMap(map["tuesday"] as Map<String, Any>),
                wednesday = ShiftHours.fromMap(map["wednesday"] as Map<String, Any>),
                thursday = ShiftHours.fromMap(map["thursday"] as Map<String, Any>),
                friday = ShiftHours.fromMap(map["friday"] as Map<String, Any>),
                saturday = ShiftHours.fromMap(map["saturday"] as Map<String, Any>),
                sunday = ShiftHours.fromMap(map["sunday"] as Map<String, Any>),
                isFlexible = map["isFlexible"] as? Boolean ?: false,
                overtimeAllowed = map["overtimeAllowed"] as? Boolean ?: false,
                maxOvertimeHours = (map["maxOvertimeHours"] as? Number)?.toInt() ?: 0
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "monday" to monday.toMap(),
            "tuesday" to tuesday.toMap(),
            "wednesday" to wednesday.toMap(),
            "thursday" to thursday.toMap(),
            "friday" to friday.toMap(),
            "saturday" to saturday.toMap(),
            "sunday" to sunday.toMap(),
            "isFlexible" to isFlexible,
            "overtimeAllowed" to overtimeAllowed,
            "maxOvertimeHours" to maxOvertimeHours
        )
    }
}

/**
 * 🕐 Shift Hours
 */
@Serializable
data class ShiftHours(
    val isWorking: Boolean,
    val startTime: String, // HH:mm format
    val endTime: String, // HH:mm format
    val breakStart: String? = null,
    val breakEnd: String? = null
) {
    companion object {
        fun fromMap(map: Map<String, Any>): ShiftHours {
            return ShiftHours(
                isWorking = map["isWorking"] as Boolean,
                startTime = map["startTime"] as String,
                endTime = map["endTime"] as String,
                breakStart = map["breakStart"] as? String,
                breakEnd = map["breakEnd"] as? String
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("isWorking", isWorking)
            put("startTime", startTime)
            put("endTime", endTime)
            breakStart?.let { put("breakStart", it) }
            breakEnd?.let { put("breakEnd", it) }
        }
    }
}

/**
 * 📊 SLA Configuration
 */
@Serializable
data class SLAConfig(
    val priority: IssuePriority,
    val acknowledgmentTime: Int, // minutes
    val resolutionTime: Int, // hours
    val escalationThresholds: List<EscalationThreshold>
) {
    companion object {
        fun fromMap(map: Map<String, Any>): SLAConfig {
            return SLAConfig(
                priority = IssuePriority.valueOf(map["priority"] as String),
                acknowledgmentTime = (map["acknowledgmentTime"] as Number).toInt(),
                resolutionTime = (map["resolutionTime"] as Number).toInt(),
                escalationThresholds = (map["escalationThresholds"] as List<Map<String, Any>>).map { 
                    EscalationThreshold.fromMap(it) 
                }
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "priority" to priority.name,
            "acknowledgmentTime" to acknowledgmentTime,
            "resolutionTime" to resolutionTime,
            "escalationThresholds" to escalationThresholds.map { it.toMap() }
        )
    }
}

/**
 * ⏰ Escalation Threshold
 */
@Serializable
data class EscalationThreshold(
    val level: EscalationLevel,
    val timeMinutes: Int,
    val notifyRoles: List<String>
) {
    companion object {
        fun fromMap(map: Map<String, Any>): EscalationThreshold {
            return EscalationThreshold(
                level = EscalationLevel.valueOf(map["level"] as String),
                timeMinutes = (map["timeMinutes"] as Number).toInt(),
                notifyRoles = map["notifyRoles"] as List<String>
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "level" to level.name,
            "timeMinutes" to timeMinutes,
            "notifyRoles" to notifyRoles
        )
    }
}

/**
 * 🏷️ Enums for Issue Management System
 */
enum class IssueCategory { 
    ELECTRICAL, PLUMBING, CARPENTRY, PAINTING, CLEANING, HVAC, 
    SECURITY, TECHNOLOGY, FURNITURE, APPLIANCE, STRUCTURAL, OTHER 
}

enum class IssuePriority { CRITICAL, HIGH, MEDIUM, LOW }
enum class IssueSeverity { CRITICAL, HIGH, MEDIUM, LOW }
enum class IssueUrgency { IMMEDIATE, HIGH, MEDIUM, LOW }

enum class IssueStatus { 
    REPORTED, ACKNOWLEDGED, ASSIGNED, IN_PROGRESS, ON_HOLD, 
    PENDING_PARTS, RESOLVED, CLOSED, CANCELLED 
}

enum class ImpactLevel { 
    INDIVIDUAL, SINGLE_ROOM, MULTIPLE_ROOMS, FLOOR, BUILDING 
}

enum class SkillLevel { JUNIOR, INTERMEDIATE, SENIOR, EXPERT }

enum class StaffAvailability { 
    AVAILABLE, BUSY, ON_BREAK, OFF_DUTY, SICK_LEAVE, VACATION 
}

enum class WorkShift { DAY, NIGHT, EVENING, ROTATING }

enum class WorkOrderStatus { 
    CREATED, SCHEDULED, IN_PROGRESS, PAUSED, COMPLETED, CANCELLED 
}

enum class MaterialCategory { 
    ELECTRICAL, PLUMBING, TOOLS, HARDWARE, PAINT, CLEANING, 
    SAFETY, CONSUMABLES, OTHER 
}

enum class EscalationLevel { L1, L2, L3, MANAGEMENT }
