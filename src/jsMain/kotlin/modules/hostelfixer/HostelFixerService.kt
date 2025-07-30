package modules.hostelfixer

import modules.hostelfixer.*
import kotlin.js.Promise
import kotlin.math.*
import kotlin.collections.*
import kotlin.random.Random

/**
 * 🔧 HostelFixer Service - Advanced Issue Management & Resolution
 * 
 * DSA Features:
 * - Priority queue for intelligent issue scheduling
 * - Assignment optimization using Hungarian algorithm principles
 * - Decision tree for automatic priority classification
 * - Graph algorithms for staff location optimization
 * - Predictive analytics for maintenance forecasting
 */

class HostelFixerService {
    
    // 🎯 Priority Classification Engine using Decision Tree Algorithm
    class PriorityClassifier {
        
        data class ClassificationRule(
            val condition: (Issue) -> Boolean,
            val priority: IssuePriority,
            val severity: IssueSeverity,
            val urgency: IssueUrgency,
            val impact: ImpactLevel,
            val weight: Double = 1.0
        )
        
        private val classificationRules = listOf(
            // Critical Infrastructure Issues
            ClassificationRule(
                condition = { issue ->
                    issue.category == IssueCategory.ELECTRICAL &&
                    (issue.description.contains("power outage", true) ||
                     issue.description.contains("short circuit", true) ||
                     issue.description.contains("fire", true))
                },
                priority = IssuePriority.CRITICAL,
                severity = IssueSeverity.CRITICAL,
                urgency = IssueUrgency.IMMEDIATE,
                impact = ImpactLevel.BUILDING,
                weight = 10.0
            ),
            
            // Water/Plumbing Emergencies
            ClassificationRule(
                condition = { issue ->
                    issue.category == IssueCategory.PLUMBING &&
                    (issue.description.contains("flooding", true) ||
                     issue.description.contains("burst pipe", true) ||
                     issue.description.contains("no water", true))
                },
                priority = IssuePriority.CRITICAL,
                severity = IssueSeverity.HIGH,
                urgency = IssueUrgency.IMMEDIATE,
                impact = ImpactLevel.FLOOR,
                weight = 9.0
            ),
            
            // Security Issues
            ClassificationRule(
                condition = { issue ->
                    issue.category == IssueCategory.SECURITY &&
                    (issue.description.contains("broken lock", true) ||
                     issue.description.contains("door won't close", true) ||
                     issue.description.contains("window broken", true))
                },
                priority = IssuePriority.HIGH,
                severity = IssueSeverity.HIGH,
                urgency = IssueUrgency.HIGH,
                impact = ImpactLevel.SINGLE_ROOM,
                weight = 8.0
            ),
            
            // HVAC Issues
            ClassificationRule(
                condition = { issue ->
                    issue.category == IssueCategory.HVAC &&
                    (issue.description.contains("ac not working", true) ||
                     issue.description.contains("heating", true) ||
                     issue.description.contains("ventilation", true))
                },
                priority = IssuePriority.MEDIUM,
                severity = IssueSeverity.MEDIUM,
                urgency = IssueUrgency.MEDIUM,
                impact = ImpactLevel.SINGLE_ROOM,
                weight = 6.0
            ),
            
            // Multiple Affected Users
            ClassificationRule(
                condition = { issue -> issue.affectedUsers.size > 5 },
                priority = IssuePriority.HIGH,
                severity = IssueSeverity.HIGH,
                urgency = IssueUrgency.HIGH,
                impact = ImpactLevel.MULTIPLE_ROOMS,
                weight = 8.0
            ),
            
            // Repeat Issues (likely systemic)
            ClassificationRule(
                condition = { issue -> issue.isRecurring },
                priority = IssuePriority.HIGH,
                severity = IssueSeverity.MEDIUM,
                urgency = IssueUrgency.MEDIUM,
                impact = ImpactLevel.MULTIPLE_ROOMS,
                weight = 7.0
            ),
            
            // Basic Maintenance
            ClassificationRule(
                condition = { issue ->
                    issue.category in listOf(IssueCategory.CLEANING, IssueCategory.PAINTING, IssueCategory.FURNITURE)
                },
                priority = IssuePriority.LOW,
                severity = IssueSeverity.LOW,
                urgency = IssueUrgency.LOW,
                impact = ImpactLevel.SINGLE_ROOM,
                weight = 2.0
            )
        )
        
        fun classifyIssue(issue: Issue): ClassifiedIssue {
            val applicableRules = classificationRules.filter { it.condition(issue) }
            
            return if (applicableRules.isNotEmpty()) {
                // Apply weighted classification
                val totalWeight = applicableRules.sumOf { it.weight }
                val weightedPriority = calculateWeightedPriority(applicableRules, totalWeight)
                val weightedSeverity = calculateWeightedSeverity(applicableRules, totalWeight)
                val weightedUrgency = calculateWeightedUrgency(applicableRules, totalWeight)
                val weightedImpact = calculateWeightedImpact(applicableRules, totalWeight)
                
                ClassifiedIssue(
                    originalIssue = issue,
                    computedPriority = weightedPriority,
                    computedSeverity = weightedSeverity,
                    computedUrgency = weightedUrgency,
                    computedImpact = weightedImpact,
                    confidenceScore = min(100.0, totalWeight * 10),
                    appliedRules = applicableRules.map { "${it.priority}-${it.weight}" },
                    estimatedResolutionTime = estimateResolutionTime(weightedPriority, issue.category),
                    requiredSkills = determineRequiredSkills(issue.category, weightedSeverity)
                )
            } else {
                // Default classification for unmatched issues
                ClassifiedIssue(
                    originalIssue = issue,
                    computedPriority = IssuePriority.MEDIUM,
                    computedSeverity = IssueSeverity.MEDIUM,
                    computedUrgency = IssueUrgency.MEDIUM,
                    computedImpact = ImpactLevel.SINGLE_ROOM,
                    confidenceScore = 50.0,
                    appliedRules = listOf("default"),
                    estimatedResolutionTime = 240, // 4 hours default
                    requiredSkills = listOf("general")
                )
            }
        }
        
        private fun calculateWeightedPriority(rules: List<ClassificationRule>, totalWeight: Double): IssuePriority {
            val priorityScores = mapOf(
                IssuePriority.CRITICAL to 10,
                IssuePriority.HIGH to 7,
                IssuePriority.MEDIUM to 5,
                IssuePriority.LOW to 2
            )
            
            var weightedScore = 0.0
            rules.forEach { rule ->
                weightedScore += (priorityScores[rule.priority] ?: 5) * rule.weight
            }
            
            val averageScore = weightedScore / totalWeight
            
            return when {
                averageScore >= 9 -> IssuePriority.CRITICAL
                averageScore >= 6 -> IssuePriority.HIGH
                averageScore >= 4 -> IssuePriority.MEDIUM
                else -> IssuePriority.LOW
            }
        }
        
        private fun calculateWeightedSeverity(rules: List<ClassificationRule>, totalWeight: Double): IssueSeverity {
            val severityScores = mapOf(
                IssueSeverity.CRITICAL to 10,
                IssueSeverity.HIGH to 7,
                IssueSeverity.MEDIUM to 5,
                IssueSeverity.LOW to 2
            )
            
            var weightedScore = 0.0
            rules.forEach { rule ->
                weightedScore += (severityScores[rule.severity] ?: 5) * rule.weight
            }
            
            val averageScore = weightedScore / totalWeight
            
            return when {
                averageScore >= 9 -> IssueSeverity.CRITICAL
                averageScore >= 6 -> IssueSeverity.HIGH
                averageScore >= 4 -> IssueSeverity.MEDIUM
                else -> IssueSeverity.LOW
            }
        }
        
        private fun calculateWeightedUrgency(rules: List<ClassificationRule>, totalWeight: Double): IssueUrgency {
            val urgencyScores = mapOf(
                IssueUrgency.IMMEDIATE to 10,
                IssueUrgency.HIGH to 7,
                IssueUrgency.MEDIUM to 5,
                IssueUrgency.LOW to 2
            )
            
            var weightedScore = 0.0
            rules.forEach { rule ->
                weightedScore += (urgencyScores[rule.urgency] ?: 5) * rule.weight
            }
            
            val averageScore = weightedScore / totalWeight
            
            return when {
                averageScore >= 9 -> IssueUrgency.IMMEDIATE
                averageScore >= 6 -> IssueUrgency.HIGH
                averageScore >= 4 -> IssueUrgency.MEDIUM
                else -> IssueUrgency.LOW
            }
        }
        
        private fun calculateWeightedImpact(rules: List<ClassificationRule>, totalWeight: Double): ImpactLevel {
            val impactScores = mapOf(
                ImpactLevel.BUILDING to 10,
                ImpactLevel.FLOOR to 8,
                ImpactLevel.MULTIPLE_ROOMS to 6,
                ImpactLevel.SINGLE_ROOM to 4,
                ImpactLevel.INDIVIDUAL to 2
            )
            
            var weightedScore = 0.0
            rules.forEach { rule ->
                weightedScore += (impactScores[rule.impact] ?: 4) * rule.weight
            }
            
            val averageScore = weightedScore / totalWeight
            
            return when {
                averageScore >= 9 -> ImpactLevel.BUILDING
                averageScore >= 7 -> ImpactLevel.FLOOR
                averageScore >= 5 -> ImpactLevel.MULTIPLE_ROOMS
                averageScore >= 3 -> ImpactLevel.SINGLE_ROOM
                else -> ImpactLevel.INDIVIDUAL
            }
        }
        
        private fun estimateResolutionTime(priority: IssuePriority, category: IssueCategory): Int {
            val baseTimes = mapOf(
                IssueCategory.ELECTRICAL to 120,
                IssueCategory.PLUMBING to 90,
                IssueCategory.HVAC to 180,
                IssueCategory.CARPENTRY to 240,
                IssueCategory.PAINTING to 300,
                IssueCategory.CLEANING to 60,
                IssueCategory.SECURITY to 90,
                IssueCategory.TECHNOLOGY to 120,
                IssueCategory.FURNITURE to 60,
                IssueCategory.APPLIANCE to 150,
                IssueCategory.STRUCTURAL to 480,
                IssueCategory.OTHER to 120
            )
            
            val baseTime = baseTimes[category] ?: 120
            
            return when (priority) {
                IssuePriority.CRITICAL -> (baseTime * 0.5).toInt()
                IssuePriority.HIGH -> (baseTime * 0.7).toInt()
                IssuePriority.MEDIUM -> baseTime
                IssuePriority.LOW -> (baseTime * 1.5).toInt()
            }
        }
        
        private fun determineRequiredSkills(category: IssueCategory, severity: IssueSeverity): List<String> {
            val skillMap = mapOf(
                IssueCategory.ELECTRICAL to listOf("electrical", "safety"),
                IssueCategory.PLUMBING to listOf("plumbing", "water_systems"),
                IssueCategory.HVAC to listOf("hvac", "cooling", "heating"),
                IssueCategory.CARPENTRY to listOf("carpentry", "woodworking", "tools"),
                IssueCategory.PAINTING to listOf("painting", "surface_prep"),
                IssueCategory.CLEANING to listOf("cleaning", "sanitation"),
                IssueCategory.SECURITY to listOf("security", "locks", "access_control"),
                IssueCategory.TECHNOLOGY to listOf("technology", "networking", "it_support"),
                IssueCategory.FURNITURE to listOf("furniture", "assembly"),
                IssueCategory.APPLIANCE to listOf("appliance_repair", "troubleshooting"),
                IssueCategory.STRUCTURAL to listOf("construction", "structural", "safety"),
                IssueCategory.OTHER to listOf("general")
            )
            
            val baseSkills = skillMap[category] ?: listOf("general")
            
            return if (severity in listOf(IssueSeverity.CRITICAL, IssueSeverity.HIGH)) {
                baseSkills + listOf("emergency_response", "safety")
            } else {
                baseSkills
            }
        }
        
        data class ClassifiedIssue(
            val originalIssue: Issue,
            val computedPriority: IssuePriority,
            val computedSeverity: IssueSeverity,
            val computedUrgency: IssueUrgency,
            val computedImpact: ImpactLevel,
            val confidenceScore: Double,
            val appliedRules: List<String>,
            val estimatedResolutionTime: Int, // minutes
            val requiredSkills: List<String>
        )
    }
    
    // 📋 Issue Priority Queue with Smart Scheduling
    class IssuePriorityQueue {
        private val issueHeap = mutableListOf<PriorityIssue>()
        
        data class PriorityIssue(
            val issue: Issue,
            val priorityScore: Double,
            val insertTime: Long = System.currentTimeMillis()
        )
        
        fun enqueue(issue: Issue): Int {
            val priorityScore = calculatePriorityScore(issue)
            val priorityIssue = PriorityIssue(issue, priorityScore)
            
            issueHeap.add(priorityIssue)
            heapifyUp(issueHeap.size - 1)
            
            return getPosition(issue.issueId)
        }
        
        fun dequeue(): Issue? {
            if (issueHeap.isEmpty()) return null
            
            val highestPriority = issueHeap[0]
            val lastItem = issueHeap.removeLastOrNull() ?: return highestPriority.issue
            
            if (issueHeap.isNotEmpty()) {
                issueHeap[0] = lastItem
                heapifyDown(0)
            }
            
            return highestPriority.issue
        }
        
        fun peek(): Issue? = issueHeap.firstOrNull()?.issue
        
        fun remove(issueId: String): Boolean {
            val index = issueHeap.indexOfFirst { it.issue.issueId == issueId }
            if (index == -1) return false
            
            val lastItem = issueHeap.removeLastOrNull() ?: return true
            
            if (index < issueHeap.size) {
                issueHeap[index] = lastItem
                heapifyDown(index)
                heapifyUp(index)
            }
            
            return true
        }
        
        fun updatePriority(issueId: String, newIssue: Issue): Boolean {
            val index = issueHeap.indexOfFirst { it.issue.issueId == issueId }
            if (index == -1) return false
            
            val newPriorityScore = calculatePriorityScore(newIssue)
            issueHeap[index] = issueHeap[index].copy(issue = newIssue, priorityScore = newPriorityScore)
            
            heapifyDown(index)
            heapifyUp(index)
            
            return true
        }
        
        fun getPosition(issueId: String): Int {
            return issueHeap.indexOfFirst { it.issue.issueId == issueId } + 1
        }
        
        fun size(): Int = issueHeap.size
        
        fun isEmpty(): Boolean = issueHeap.isEmpty()
        
        fun getQueueSnapshot(): List<Issue> {
            return issueHeap.map { it.issue }
        }
        
        private fun calculatePriorityScore(issue: Issue): Double {
            var score = issue.calculatePriorityScore()
            
            // Age factor - older issues get higher priority
            val ageInHours = (System.currentTimeMillis() - issue.createdAt) / (1000 * 60 * 60)
            score += min(ageInHours * 0.1, 2.0)
            
            // SLA deadline proximity
            issue.slaDeadline?.let { deadline ->
                val timeToDeadline = deadline - System.currentTimeMillis()
                if (timeToDeadline < 2 * 60 * 60 * 1000) { // Less than 2 hours
                    score += 5.0
                }
            }
            
            // Escalation boost
            when (issue.escalationLevel) {
                EscalationLevel.L2 -> score += 3.0
                EscalationLevel.L3 -> score += 6.0
                EscalationLevel.MANAGEMENT -> score += 10.0
                else -> {}
            }
            
            return score
        }
        
        private fun heapifyUp(index: Int) {
            if (index == 0) return
            
            val parentIndex = (index - 1) / 2
            if (issueHeap[index].priorityScore > issueHeap[parentIndex].priorityScore) {
                swap(index, parentIndex)
                heapifyUp(parentIndex)
            }
        }
        
        private fun heapifyDown(index: Int) {
            val leftChild = 2 * index + 1
            val rightChild = 2 * index + 2
            var largest = index
            
            if (leftChild < issueHeap.size && 
                issueHeap[leftChild].priorityScore > issueHeap[largest].priorityScore) {
                largest = leftChild
            }
            
            if (rightChild < issueHeap.size && 
                issueHeap[rightChild].priorityScore > issueHeap[largest].priorityScore) {
                largest = rightChild
            }
            
            if (largest != index) {
                swap(index, largest)
                heapifyDown(largest)
            }
        }
        
        private fun swap(i: Int, j: Int) {
            val temp = issueHeap[i]
            issueHeap[i] = issueHeap[j]
            issueHeap[j] = temp
        }
    }
    
    // 👥 Staff Assignment Optimizer
    class StaffAssignmentOptimizer {
        
        fun findOptimalAssignment(
            issues: List<Issue>,
            staff: List<MaintenanceStaff>
        ): Map<String, String> { // issueId to staffId mapping
            
            val availableStaff = staff.filter { it.isAvailable() && it.canTakeMoreIssues() }
            if (availableStaff.isEmpty()) return emptyMap()
            
            // Calculate assignment scores using simplified Hungarian algorithm approach
            val assignments = mutableMapOf<String, String>()
            val assignedStaff = mutableSetOf<String>()
            
            // Sort issues by priority for greedy assignment
            val sortedIssues = issues.sortedByDescending { it.calculatePriorityScore() }
            
            for (issue in sortedIssues) {
                val bestStaff = findBestStaffForIssue(issue, availableStaff, assignedStaff)
                bestStaff?.let { staff ->
                    assignments[issue.issueId] = staff.staffId
                    assignedStaff.add(staff.staffId)
                    
                    // Remove staff if they reach max capacity
                    if (staff.assignedIssues.size + 1 >= staff.maxConcurrentIssues) {
                        assignedStaff.add(staff.staffId)
                    }
                }
            }
            
            return assignments
        }
        
        private fun findBestStaffForIssue(
            issue: Issue,
            availableStaff: List<MaintenanceStaff>,
            assignedStaff: Set<String>
        ): MaintenanceStaff? {
            
            val eligibleStaff = availableStaff.filter { staff ->
                staff.staffId !in assignedStaff &&
                hasRequiredSkills(staff, issue.requiredSkills) &&
                staff.canTakeMoreIssues()
            }
            
            if (eligibleStaff.isEmpty()) return null
            
            return eligibleStaff.maxByOrNull { staff ->
                calculateAssignmentScore(staff, issue)
            }
        }
        
        private fun hasRequiredSkills(staff: MaintenanceStaff, requiredSkills: List<String>): Boolean {
            if (requiredSkills.isEmpty() || requiredSkills.contains("general")) return true
            
            return requiredSkills.any { skill ->
                staff.specializations.any { specialization ->
                    specialization.contains(skill, ignoreCase = true) ||
                    skill.contains(specialization, ignoreCase = true)
                }
            }
        }
        
        private fun calculateAssignmentScore(staff: MaintenanceStaff, issue: Issue): Double {
            var score = 0.0
            
            // Skill match score
            val skillMatches = issue.requiredSkills.count { requiredSkill ->
                staff.specializations.any { it.contains(requiredSkill, ignoreCase = true) }
            }
            score += skillMatches * 20.0
            
            // Experience score
            score += staff.experience * 2.0
            
            // Performance rating
            score += staff.performanceRating * 10.0
            
            // Workload consideration (lower workload = higher score)
            score += (1.0 - staff.getWorkload()) * 15.0
            
            // Skill level bonus
            score += when (staff.skillLevel) {
                SkillLevel.EXPERT -> 20.0
                SkillLevel.SENIOR -> 15.0
                SkillLevel.INTERMEDIATE -> 10.0
                SkillLevel.JUNIOR -> 5.0
            }
            
            // Priority matching - expert staff for critical issues
            if (issue.priority == IssuePriority.CRITICAL && staff.skillLevel in listOf(SkillLevel.EXPERT, SkillLevel.SENIOR)) {
                score += 25.0
            }
            
            // Location proximity (simplified - would use actual coordinates in real system)
            val locationBonus = if (staff.currentLocation?.contains(issue.location.substring(0, min(5, issue.location.length)), ignoreCase = true) == true) {
                10.0
            } else 0.0
            score += locationBonus
            
            return score
        }
        
        fun optimizeSchedule(
            workOrders: List<WorkOrder>,
            staff: List<MaintenanceStaff>
        ): Map<String, List<WorkOrder>> { // staffId to ordered work orders
            
            val staffSchedules = mutableMapOf<String, MutableList<WorkOrder>>()
            
            // Initialize schedules
            staff.forEach { staffMember ->
                staffSchedules[staffMember.staffId] = mutableListOf()
            }
            
            // Sort work orders by priority and deadline
            val sortedWorkOrders = workOrders.sortedWith(
                compareByDescending<WorkOrder> { it.priority }
                    .thenBy { it.scheduledStartTime }
            )
            
            // Assign work orders using earliest finish time heuristic
            for (workOrder in sortedWorkOrders) {
                val bestStaff = findBestStaffForWorkOrder(workOrder, staff, staffSchedules)
                bestStaff?.let { staffId ->
                    staffSchedules[staffId]?.add(workOrder)
                }
            }
            
            // Optimize each staff schedule
            staffSchedules.forEach { (staffId, orders) ->
                staffSchedules[staffId] = optimizeStaffSchedule(orders).toMutableList()
            }
            
            return staffSchedules.mapValues { it.value.toList() }
        }
        
        private fun findBestStaffForWorkOrder(
            workOrder: WorkOrder,
            staff: List<MaintenanceStaff>,
            currentSchedules: Map<String, List<WorkOrder>>
        ): String? {
            
            return staff.filter { it.isActive }
                .minByOrNull { staffMember ->
                    val currentSchedule = currentSchedules[staffMember.staffId] ?: emptyList()
                    calculateEarliestFinishTime(workOrder, currentSchedule)
                }?.staffId
        }
        
        private fun calculateEarliestFinishTime(
            newWorkOrder: WorkOrder,
            currentSchedule: List<WorkOrder>
        ): Long {
            val lastWorkOrder = currentSchedule.maxByOrNull { it.scheduledStartTime + (it.estimatedDuration * 60 * 1000) }
            
            val earliestStart = lastWorkOrder?.let { 
                it.scheduledStartTime + (it.estimatedDuration * 60 * 1000)
            } ?: System.currentTimeMillis()
            
            return max(earliestStart, newWorkOrder.scheduledStartTime) + (newWorkOrder.estimatedDuration * 60 * 1000)
        }
        
        private fun optimizeStaffSchedule(workOrders: List<WorkOrder>): List<WorkOrder> {
            // Sort by scheduled start time for basic optimization
            return workOrders.sortedBy { it.scheduledStartTime }
        }
    }
    
    // 📈 Predictive Maintenance Analyzer
    class PredictiveMaintenanceAnalyzer {
        private val maintenancePatterns = mutableMapOf<String, List<MaintenancePattern>>()
        
        data class MaintenancePattern(
            val category: IssueCategory,
            val location: String,
            val frequency: Double, // issues per month
            val averageCost: Double,
            val seasonality: Map<Int, Double>, // month -> frequency multiplier
            val deteriorationRate: Double // 0.0 to 1.0
        )
        
        fun analyzeMaintenanceNeeds(
            hostelId: String,
            historicalIssues: List<Issue>,
            timeHorizonDays: Int = 30
        ): MaintenanceForecast {
            
            val patterns = extractPatterns(historicalIssues)
            maintenancePatterns[hostelId] = patterns
            
            val predictions = patterns.map { pattern ->
                PredictedMaintenance(
                    category = pattern.category,
                    location = pattern.location,
                    probability = calculateProbability(pattern, timeHorizonDays),
                    estimatedCost = pattern.averageCost,
                    recommendedAction = getRecommendedAction(pattern),
                    urgency = calculateUrgency(pattern),
                    timeframe = calculateTimeframe(pattern)
                )
            }.sortedByDescending { it.probability }
            
            return MaintenanceForecast(
                hostelId = hostelId,
                forecastDate = System.currentTimeMillis(),
                timeHorizonDays = timeHorizonDays,
                predictions = predictions,
                totalEstimatedCost = predictions.sumOf { it.estimatedCost },
                recommendedBudget = calculateRecommendedBudget(predictions),
                criticalActions = predictions.filter { it.urgency == MaintenanceUrgency.HIGH }.take(5)
            )
        }
        
        private fun extractPatterns(issues: List<Issue>): List<MaintenancePattern> {
            val groupedIssues = issues.filter { it.status == IssueStatus.RESOLVED }
                .groupBy { "${it.category}-${it.location}" }
            
            return groupedIssues.map { (key, issueList) ->
                val category = issueList.first().category
                val location = issueList.first().location
                
                val monthlyFrequency = calculateMonthlyFrequency(issueList)
                val averageCost = issueList.mapNotNull { it.actualCost }.average()
                val seasonality = calculateSeasonality(issueList)
                val deteriorationRate = calculateDeteriorationRate(issueList)
                
                MaintenancePattern(
                    category = category,
                    location = location,
                    frequency = monthlyFrequency,
                    averageCost = averageCost,
                    seasonality = seasonality,
                    deteriorationRate = deteriorationRate
                )
            }
        }
        
        private fun calculateMonthlyFrequency(issues: List<Issue>): Double {
            if (issues.isEmpty()) return 0.0
            
            val firstIssue = issues.minByOrNull { it.createdAt }?.createdAt ?: System.currentTimeMillis()
            val lastIssue = issues.maxByOrNull { it.createdAt }?.createdAt ?: System.currentTimeMillis()
            
            val monthsSpanned = max(1, (lastIssue - firstIssue) / (1000 * 60 * 60 * 24 * 30))
            
            return issues.size.toDouble() / monthsSpanned
        }
        
        private fun calculateSeasonality(issues: List<Issue>): Map<Int, Double> {
            val monthlyCount = mutableMapOf<Int, Int>()
            
            issues.forEach { issue ->
                // Simplified month extraction
                val month = ((issue.createdAt / (1000 * 60 * 60 * 24 * 30)) % 12).toInt() + 1
                monthlyCount[month] = (monthlyCount[month] ?: 0) + 1
            }
            
            val averageCount = monthlyCount.values.average()
            
            return monthlyCount.mapValues { (_, count) ->
                if (averageCount > 0) count / averageCount else 1.0
            }
        }
        
        private fun calculateDeteriorationRate(issues: List<Issue>): Double {
            if (issues.size < 2) return 0.1
            
            val sortedIssues = issues.sortedBy { it.createdAt }
            val intervals = mutableListOf<Long>()
            
            for (i in 1 until sortedIssues.size) {
                intervals.add(sortedIssues[i].createdAt - sortedIssues[i-1].createdAt)
            }
            
            // Calculate if intervals are decreasing (indicating deterioration)
            val trendSlope = calculateTrendSlope(intervals)
            
            return when {
                trendSlope < -0.1 -> 0.8 // High deterioration
                trendSlope < 0 -> 0.5 // Moderate deterioration
                else -> 0.2 // Low deterioration
            }
        }
        
        private fun calculateTrendSlope(intervals: List<Long>): Double {
            if (intervals.size < 2) return 0.0
            
            val n = intervals.size
            val sumX = (0 until n).sum().toDouble()
            val sumY = intervals.sum().toDouble()
            val sumXY = intervals.mapIndexed { index, value -> index * value }.sum().toDouble()
            val sumX2 = (0 until n).map { it * it }.sum().toDouble()
            
            return (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX)
        }
        
        private fun calculateProbability(pattern: MaintenancePattern, timeHorizonDays: Int): Double {
            val baseRate = pattern.frequency * (timeHorizonDays / 30.0)
            val deteriorationFactor = 1.0 + pattern.deteriorationRate
            val currentMonth = ((System.currentTimeMillis() / (1000 * 60 * 60 * 24 * 30)) % 12).toInt() + 1
            val seasonalFactor = pattern.seasonality[currentMonth] ?: 1.0
            
            return min(0.95, baseRate * deteriorationFactor * seasonalFactor)
        }
        
        private fun getRecommendedAction(pattern: MaintenancePattern): String {
            return when {
                pattern.frequency > 2.0 -> "Schedule preventive maintenance"
                pattern.deteriorationRate > 0.6 -> "Investigate root cause and plan replacement"
                pattern.category in listOf(IssueCategory.ELECTRICAL, IssueCategory.PLUMBING) -> 
                    "Conduct safety inspection"
                else -> "Monitor and maintain as needed"
            }
        }
        
        private fun calculateUrgency(pattern: MaintenancePattern): MaintenanceUrgency {
            return when {
                pattern.frequency > 3.0 || pattern.deteriorationRate > 0.7 -> MaintenanceUrgency.HIGH
                pattern.frequency > 1.5 || pattern.deteriorationRate > 0.4 -> MaintenanceUrgency.MEDIUM
                else -> MaintenanceUrgency.LOW
            }
        }
        
        private fun calculateTimeframe(pattern: MaintenancePattern): String {
            val nextIssueEstimate = 30.0 / pattern.frequency // days until next issue
            
            return when {
                nextIssueEstimate < 7 -> "Within 1 week"
                nextIssueEstimate < 14 -> "Within 2 weeks"
                nextIssueEstimate < 30 -> "Within 1 month"
                else -> "Within 3 months"
            }
        }
        
        private fun calculateRecommendedBudget(predictions: List<PredictedMaintenance>): Double {
            val baseBudget = predictions.sumOf { it.estimatedCost * it.probability }
            val contingencyFactor = 1.2 // 20% contingency
            
            return baseBudget * contingencyFactor
        }
        
        data class MaintenanceForecast(
            val hostelId: String,
            val forecastDate: Long,
            val timeHorizonDays: Int,
            val predictions: List<PredictedMaintenance>,
            val totalEstimatedCost: Double,
            val recommendedBudget: Double,
            val criticalActions: List<PredictedMaintenance>
        )
        
        data class PredictedMaintenance(
            val category: IssueCategory,
            val location: String,
            val probability: Double,
            val estimatedCost: Double,
            val recommendedAction: String,
            val urgency: MaintenanceUrgency,
            val timeframe: String
        )
        
        enum class MaintenanceUrgency { HIGH, MEDIUM, LOW }
    }
    
    // 📱 Main Service Implementation
    private val priorityClassifier = PriorityClassifier()
    private val priorityQueue = IssuePriorityQueue()
    private val assignmentOptimizer = StaffAssignmentOptimizer()
    private val predictiveAnalyzer = PredictiveMaintenanceAnalyzer()
    
    // Cache for frequently accessed data
    private val issuesCache = mutableMapOf<String, Issue>()
    private val staffCache = mutableMapOf<String, MaintenanceStaff>()
    private val workOrdersCache = mutableMapOf<String, WorkOrder>()
    private val slaConfig = mutableMapOf<IssuePriority, SLAConfig>()
    
    /**
     * 🎯 Submit new issue with automatic classification
     */
    fun submitIssue(issue: Issue): Promise<PriorityClassifier.ClassifiedIssue> {
        return Promise { resolve, reject ->
            try {
                // Classify the issue automatically
                val classifiedIssue = priorityClassifier.classifyIssue(issue)
                
                // Update issue with computed values
                val updatedIssue = issue.copy(
                    priority = classifiedIssue.computedPriority,
                    severity = classifiedIssue.computedSeverity,
                    urgency = classifiedIssue.computedUrgency,
                    impact = classifiedIssue.computedImpact,
                    requiredSkills = classifiedIssue.requiredSkills,
                    estimatedResolutionTime = System.currentTimeMillis() + (classifiedIssue.estimatedResolutionTime * 60 * 1000),
                    slaDeadline = calculateSLADeadline(classifiedIssue.computedPriority)
                )
                
                // Add to cache and priority queue
                issuesCache[updatedIssue.issueId] = updatedIssue
                priorityQueue.enqueue(updatedIssue)
                
                resolve(classifiedIssue.copy(originalIssue = updatedIssue))
                
            } catch (e: Exception) {
                reject(e)
            }
        }
    }
    
    /**
     * 👥 Find optimal staff assignment for issues
     */
    fun assignIssues(issueIds: List<String>): Promise<Map<String, String>> {
        return Promise { resolve, reject ->
            try {
                val issues = issueIds.mapNotNull { issuesCache[it] }
                val availableStaff = staffCache.values.toList()
                
                val assignments = assignmentOptimizer.findOptimalAssignment(issues, availableStaff)
                
                // Update issue assignments
                assignments.forEach { (issueId, staffId) ->
                    val issue = issuesCache[issueId]
                    if (issue != null) {
                        val updatedIssue = issue.copy(
                            assignedTo = staffId,
                            status = IssueStatus.ASSIGNED,
                            updatedAt = System.currentTimeMillis()
                        )
                        issuesCache[issueId] = updatedIssue
                        priorityQueue.updatePriority(issueId, updatedIssue)
                    }
                    
                    // Update staff workload
                    val staff = staffCache[staffId]
                    if (staff != null) {
                        val updatedStaff = staff.copy(
                            assignedIssues = staff.assignedIssues + issueId,
                            lastActiveAt = System.currentTimeMillis()
                        )
                        staffCache[staffId] = updatedStaff
                    }
                }
                
                resolve(assignments)
                
            } catch (e: Exception) {
                reject(e)
            }
        }
    }
    
    /**
     * 📊 Get predictive maintenance forecast
     */
    fun getMaintenanceForecast(hostelId: String, timeHorizonDays: Int = 30): Promise<PredictiveMaintenanceAnalyzer.MaintenanceForecast> {
        return Promise { resolve, reject ->
            try {
                val hostelIssues = issuesCache.values.filter { it.hostelId == hostelId }
                val forecast = predictiveAnalyzer.analyzeMaintenanceNeeds(hostelId, hostelIssues, timeHorizonDays)
                resolve(forecast)
                
            } catch (e: Exception) {
                reject(e)
            }
        }
    }
    
    /**
     * 📋 Get next priority issue from queue
     */
    fun getNextPriorityIssue(): Promise<Issue?> {
        return Promise { resolve, reject ->
            try {
                val nextIssue = priorityQueue.dequeue()
                resolve(nextIssue)
                
            } catch (e: Exception) {
                reject(e)
            }
        }
    }
    
    /**
     * 📈 Get comprehensive analytics
     */
    fun getAnalytics(hostelId: String): Promise<Map<String, Any>> {
        return Promise { resolve, reject ->
            try {
                val hostelIssues = issuesCache.values.filter { it.hostelId == hostelId }
                val hostelStaff = staffCache.values.filter { 
                    // Simplified - in real system would have hostel assignment
                    true 
                }
                
                val analytics = mapOf(
                    "totalIssues" to hostelIssues.size,
                    "openIssues" to hostelIssues.count { it.status !in listOf(IssueStatus.RESOLVED, IssueStatus.CLOSED) },
                    "criticalIssues" to hostelIssues.count { it.priority == IssuePriority.CRITICAL },
                    "averageResolutionTime" to hostelIssues.mapNotNull { it.getResolutionTime() }.average(),
                    "categoryDistribution" to hostelIssues.groupingBy { it.category }.eachCount(),
                    "priorityDistribution" to hostelIssues.groupingBy { it.priority }.eachCount(),
                    "statusDistribution" to hostelIssues.groupingBy { it.status }.eachCount(),
                    "staffPerformance" to hostelStaff.map { staff ->
                        mapOf(
                            "staffId" to staff.staffId,
                            "name" to staff.name,
                            "performanceRating" to staff.performanceRating,
                            "completedIssues" to staff.completedIssues,
                            "averageResolutionTime" to staff.averageResolutionTime,
                            "currentWorkload" to staff.getWorkload()
                        )
                    },
                    "slaCompliance" to calculateSLACompliance(hostelIssues),
                    "costAnalysis" to mapOf(
                        "totalCost" to hostelIssues.mapNotNull { it.actualCost }.sum(),
                        "averageCostPerIssue" to hostelIssues.mapNotNull { it.actualCost }.average(),
                        "costByCategory" to hostelIssues.filter { it.actualCost != null }
                            .groupBy { it.category }
                            .mapValues { (_, issues) -> issues.mapNotNull { it.actualCost }.sum() }
                    ),
                    "queueStatus" to mapOf(
                        "queueLength" to priorityQueue.size(),
                        "nextIssue" to priorityQueue.peek()?.let { mapOf(
                            "issueId" to it.issueId,
                            "title" to it.title,
                            "priority" to it.priority.name
                        )}
                    )
                )
                
                resolve(analytics)
                
            } catch (e: Exception) {
                reject(e)
            }
        }
    }
    
    // 🛠️ Helper Methods
    
    private fun calculateSLADeadline(priority: IssuePriority): Long {
        val config = slaConfig[priority] ?: return System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 24 hours default
        return System.currentTimeMillis() + (config.resolutionTime * 60 * 60 * 1000)
    }
    
    private fun calculateSLACompliance(issues: List<Issue>): Double {
        val resolvedIssues = issues.filter { it.status == IssueStatus.RESOLVED }
        if (resolvedIssues.isEmpty()) return 100.0
        
        val compliantIssues = resolvedIssues.count { issue ->
            issue.slaDeadline?.let { deadline ->
                (issue.resolvedAt ?: System.currentTimeMillis()) <= deadline
            } ?: true
        }
        
        return (compliantIssues.toDouble() / resolvedIssues.size) * 100
    }
    
    private fun generateIssueId(): String {
        return "ISS${System.currentTimeMillis()}${Random.nextInt(1000, 9999)}"
    }
    
    private fun generateWorkOrderId(): String {
        return "WO${System.currentTimeMillis()}${Random.nextInt(1000, 9999)}"
    }
    
    // 📊 Cache Management
    fun updateIssueCache(issue: Issue) {
        issuesCache[issue.issueId] = issue
    }
    
    fun updateStaffCache(staff: MaintenanceStaff) {
        staffCache[staff.staffId] = staff
    }
    
    fun updateWorkOrderCache(workOrder: WorkOrder) {
        workOrdersCache[workOrder.workOrderId] = workOrder
    }
    
    fun clearCache() {
        issuesCache.clear()
        staffCache.clear()
        workOrdersCache.clear()
    }
}
