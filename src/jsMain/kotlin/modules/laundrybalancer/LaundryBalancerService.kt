package modules.laundrybalancer

import modules.laundrybalancer.*
import kotlin.js.Promise
import kotlin.math.*
import kotlin.collections.*
import kotlin.random.Random

/**
 * 👕 LaundryBalancer Service - Advanced Load Balancing & Queue Management
 * 
 * DSA Features:
 * - Min-Heap for machine allocation optimization
 * - Queue management with priority algorithms
 * - Load balancing across multiple facilities
 * - Predictive scheduling using time-series analysis
 * - Energy optimization algorithms
 */

class LaundryBalancerService {
    
    // 🔄 Load Balancing Priority Queue (Min-Heap based on machine efficiency)
    class MachineLoadBalancer {
        private val machineHeap = mutableListOf<MachineLoad>()
        
        data class MachineLoad(
            val machine: LaundryMachine,
            val currentLoad: Double, // 0.0 to 1.0
            val efficiency: Double,
            val estimatedAvailable: Long,
            val priority: Double = calculatePriority(currentLoad, efficiency)
        ) {
            companion object {
                fun calculatePriority(load: Double, efficiency: Double): Double {
                    // Lower priority = better choice (min-heap)
                    return (load * 0.7) + ((100.0 - efficiency) * 0.3) / 100.0
                }
            }
        }
        
        fun addMachine(machine: LaundryMachine) {
            val load = calculateCurrentLoad(machine)
            val efficiency = machine.getEfficiencyScore()
            val availableTime = machine.cycleEndTime ?: System.currentTimeMillis()
            
            val machineLoad = MachineLoad(machine, load, efficiency, availableTime)
            machineHeap.add(machineLoad)
            heapifyUp(machineHeap.size - 1)
        }
        
        fun getBestAvailableMachine(machineType: MachineType): LaundryMachine? {
            val availableMachines = machineHeap.filter { 
                it.machine.type == machineType && it.machine.isAvailable() 
            }
            
            return if (availableMachines.isNotEmpty()) {
                availableMachines.minByOrNull { it.priority }?.machine
            } else {
                // Get machine with earliest availability
                machineHeap.filter { it.machine.type == machineType }
                    .minByOrNull { it.estimatedAvailable }?.machine
            }
        }
        
        fun updateMachineLoad(machineId: String, newStatus: MachineStatus) {
            val index = machineHeap.indexOfFirst { it.machine.machineId == machineId }
            if (index != -1) {
                val oldMachine = machineHeap[index]
                val updatedMachine = oldMachine.machine.copy(status = newStatus)
                val newLoad = calculateCurrentLoad(updatedMachine)
                val newMachineLoad = oldMachine.copy(
                    machine = updatedMachine,
                    currentLoad = newLoad,
                    priority = MachineLoad.calculatePriority(newLoad, oldMachine.efficiency)
                )
                
                machineHeap[index] = newMachineLoad
                heapifyDown(index)
                heapifyUp(index)
            }
        }
        
        private fun calculateCurrentLoad(machine: LaundryMachine): Double {
            return when (machine.status) {
                MachineStatus.AVAILABLE -> 0.0
                MachineStatus.IN_USE -> 1.0
                MachineStatus.RESERVED -> 0.8
                MachineStatus.MAINTENANCE -> 1.0
                MachineStatus.OUT_OF_ORDER -> 1.0
            }
        }
        
        private fun heapifyUp(index: Int) {
            if (index == 0) return
            val parentIndex = (index - 1) / 2
            if (machineHeap[index].priority < machineHeap[parentIndex].priority) {
                swap(index, parentIndex)
                heapifyUp(parentIndex)
            }
        }
        
        private fun heapifyDown(index: Int) {
            val leftChild = 2 * index + 1
            val rightChild = 2 * index + 2
            var smallest = index
            
            if (leftChild < machineHeap.size && 
                machineHeap[leftChild].priority < machineHeap[smallest].priority) {
                smallest = leftChild
            }
            
            if (rightChild < machineHeap.size && 
                machineHeap[rightChild].priority < machineHeap[smallest].priority) {
                smallest = rightChild
            }
            
            if (smallest != index) {
                swap(index, smallest)
                heapifyDown(smallest)
            }
        }
        
        private fun swap(i: Int, j: Int) {
            val temp = machineHeap[i]
            machineHeap[i] = machineHeap[j]
            machineHeap[j] = temp
        }
        
        fun getLoadDistribution(): Map<String, Double> {
            return machineHeap.associate { it.machine.machineId to it.currentLoad }
        }
        
        fun getAverageLoad(): Double {
            return if (machineHeap.isNotEmpty()) {
                machineHeap.sumOf { it.currentLoad } / machineHeap.size
            } else 0.0
        }
    }
    
    // 📊 Queue Management System with Priority Handling
    class LaundryQueue {
        private val priorityQueues = mutableMapOf<BookingPriority, MutableList<LaundryBooking>>()
        private val waitTimeCache = mutableMapOf<String, Int>()
        
        init {
            BookingPriority.values().forEach { priority ->
                priorityQueues[priority] = mutableListOf()
            }
        }
        
        fun addToQueue(booking: LaundryBooking): Int {
            val queue = priorityQueues[booking.priority] ?: priorityQueues[BookingPriority.NORMAL]!!
            queue.add(booking)
            
            // Sort by creation time within same priority
            queue.sortBy { it.createdAt }
            
            val position = calculateQueuePosition(booking)
            updateWaitTimes()
            return position
        }
        
        fun getNextBooking(): LaundryBooking? {
            // Process in priority order: URGENT -> HIGH -> NORMAL -> LOW
            for (priority in listOf(BookingPriority.URGENT, BookingPriority.HIGH, 
                                   BookingPriority.NORMAL, BookingPriority.LOW)) {
                val queue = priorityQueues[priority]!!
                if (queue.isNotEmpty()) {
                    return queue.removeFirst()
                }
            }
            return null
        }
        
        fun removeFromQueue(bookingId: String): Boolean {
            priorityQueues.values.forEach { queue ->
                val booking = queue.find { it.bookingId == bookingId }
                if (booking != null) {
                    queue.remove(booking)
                    updateWaitTimes()
                    return true
                }
            }
            return false
        }
        
        fun getQueuePosition(bookingId: String): Int? {
            var position = 1
            
            // Check higher priority queues first
            for (priority in listOf(BookingPriority.URGENT, BookingPriority.HIGH, 
                                   BookingPriority.NORMAL, BookingPriority.LOW)) {
                val queue = priorityQueues[priority]!!
                val index = queue.indexOfFirst { it.bookingId == bookingId }
                if (index != -1) {
                    return position + index
                }
                position += queue.size
            }
            return null
        }
        
        fun getEstimatedWaitTime(bookingId: String): Int {
            return waitTimeCache[bookingId] ?: 0
        }
        
        private fun calculateQueuePosition(booking: LaundryBooking): Int {
            var position = 1
            
            for (priority in listOf(BookingPriority.URGENT, BookingPriority.HIGH, 
                                   BookingPriority.NORMAL, BookingPriority.LOW)) {
                val queue = priorityQueues[priority]!!
                if (priority == booking.priority) {
                    return position + queue.size - 1 // -1 because booking is already added
                }
                position += queue.size
            }
            return position
        }
        
        private fun updateWaitTimes() {
            var cumulativeWaitTime = 0
            
            for (priority in listOf(BookingPriority.URGENT, BookingPriority.HIGH, 
                                   BookingPriority.NORMAL, BookingPriority.LOW)) {
                val queue = priorityQueues[priority]!!
                for (booking in queue) {
                    waitTimeCache[booking.bookingId] = cumulativeWaitTime
                    cumulativeWaitTime += booking.estimatedDuration
                }
            }
        }
        
        fun getTotalQueueLength(): Int {
            return priorityQueues.values.sumOf { it.size }
        }
        
        fun getQueueStats(): Map<String, Any> {
            return mapOf(
                "totalBookings" to getTotalQueueLength(),
                "urgentBookings" to priorityQueues[BookingPriority.URGENT]!!.size,
                "highPriorityBookings" to priorityQueues[BookingPriority.HIGH]!!.size,
                "normalBookings" to priorityQueues[BookingPriority.NORMAL]!!.size,
                "lowPriorityBookings" to priorityQueues[BookingPriority.LOW]!!.size,
                "averageWaitTime" to if (waitTimeCache.isNotEmpty()) {
                    waitTimeCache.values.average()
                } else 0.0
            )
        }
    }
    
    // 🧠 Predictive Scheduling Algorithm
    class PredictiveScheduler {
        private val historicalData = mutableMapOf<String, List<UsagePattern>>()
        
        data class UsagePattern(
            val hour: Int,
            val dayOfWeek: Int,
            val usage: Double,
            val averageWaitTime: Double
        )
        
        fun analyzeUsagePatterns(statistics: List<UsageStatistics>): Map<String, Double> {
            val hourlyPatterns = mutableMapOf<Int, MutableList<Double>>()
            
            statistics.forEach { stat ->
                stat.hourlyUsage.forEach { (hour, bookings) ->
                    hourlyPatterns.getOrPut(hour) { mutableListOf() }.add(bookings.toDouble())
                }
            }
            
            return hourlyPatterns.mapValues { (_, values) ->
                values.average()
            }
        }
        
        fun predictOptimalSlots(
            facilityId: String, 
            targetDate: String,
            requestedDuration: Int
        ): List<TimeSlot> {
            val patterns = historicalData[facilityId] ?: emptyList()
            val hourlyScores = mutableMapOf<Int, Double>()
            
            // Calculate scores for each hour (lower = better)
            for (hour in 6..23) { // Typical operating hours
                val avgUsage = patterns.filter { it.hour == hour }.map { it.usage }.average()
                val avgWaitTime = patterns.filter { it.hour == hour }.map { it.averageWaitTime }.average()
                
                // Score based on usage and wait time
                val score = (avgUsage * 0.6) + (avgWaitTime * 0.4)
                hourlyScores[hour] = score
            }
            
            // Sort hours by score and return top slots
            val bestHours = hourlyScores.toList().sortedBy { it.second }.take(5)
            
            return bestHours.map { (hour, _) ->
                val startTime = System.currentTimeMillis() + (hour * 60 * 60 * 1000)
                TimeSlot(
                    startTime = startTime,
                    endTime = startTime + (requestedDuration * 60 * 1000),
                    date = targetDate,
                    slotType = if (hour in listOf(18, 19, 20)) SlotType.PEAK else SlotType.REGULAR
                )
            }
        }
        
        fun addUsageData(facilityId: String, pattern: UsagePattern) {
            val patterns = historicalData.getOrPut(facilityId) { mutableListOf() }
            (patterns as MutableList).add(pattern)
            
            // Keep only recent data (last 1000 patterns)
            if (patterns.size > 1000) {
                (patterns as MutableList).removeFirst()
            }
        }
        
        fun predictPeakHours(facilityId: String): List<Int> {
            val patterns = historicalData[facilityId] ?: return emptyList()
            
            val hourlyAverage = patterns.groupBy { it.hour }
                .mapValues { (_, patterns) -> patterns.map { it.usage }.average() }
            
            val threshold = hourlyAverage.values.average() * 1.2 // 20% above average
            
            return hourlyAverage.filter { it.value >= threshold }
                .keys.sorted()
        }
    }
    
    // 🌱 Energy Optimization System
    class EnergyOptimizer {
        
        fun optimizeEnergyUsage(
            facilities: List<LaundryFacility>,
            bookings: List<LaundryBooking>
        ): Map<String, Any> {
            val optimizations = mutableMapOf<String, Any>()
            
            facilities.forEach { facility ->
                val facilityBookings = bookings.filter { it.facilityId == facility.facilityId }
                val energyProfile = analyzeEnergyProfile(facility, facilityBookings)
                
                optimizations[facility.facilityId] = mapOf(
                    "currentConsumption" to energyProfile.totalConsumption,
                    "optimizedConsumption" to energyProfile.optimizedConsumption,
                    "savings" to energyProfile.potentialSavings,
                    "recommendations" to energyProfile.recommendations,
                    "ecoSlots" to identifyEcoFriendlySlots(facility),
                    "loadDistribution" to optimizeLoadDistribution(facility, facilityBookings)
                )
            }
            
            return optimizations
        }
        
        private fun analyzeEnergyProfile(
            facility: LaundryFacility,
            bookings: List<LaundryBooking>
        ): EnergyProfile {
            val totalMachines = facility.washingMachines + facility.dryingMachines
            val currentConsumption = totalMachines.sumOf { it.powerConsumption }
            
            // Calculate optimized consumption using eco-friendly machines
            val ecoMachines = totalMachines.filter { it.isEcoFriendly }
            val optimizedConsumption = if (ecoMachines.isNotEmpty()) {
                ecoMachines.sumOf { it.powerConsumption } * 
                (totalMachines.size.toDouble() / ecoMachines.size)
            } else {
                currentConsumption * 0.85 // Assume 15% savings with optimization
            }
            
            val potentialSavings = currentConsumption - optimizedConsumption
            
            val recommendations = buildList {
                if (ecoMachines.isEmpty()) {
                    add("Install energy-efficient machines")
                }
                if (bookings.any { !it.isEcoMode }) {
                    add("Promote eco-mode usage with incentives")
                }
                add("Schedule high-energy operations during off-peak hours")
                add("Implement demand response programs")
            }
            
            return EnergyProfile(
                totalConsumption = currentConsumption,
                optimizedConsumption = optimizedConsumption,
                potentialSavings = potentialSavings,
                recommendations = recommendations
            )
        }
        
        private fun identifyEcoFriendlySlots(facility: LaundryFacility): List<TimeSlot> {
            val ecoSlots = mutableListOf<TimeSlot>()
            val currentTime = System.currentTimeMillis()
            
            // Off-peak hours (typically 10 PM to 6 AM and 10 AM to 2 PM)
            val offPeakHours = listOf(22, 23, 0, 1, 2, 3, 4, 5, 6, 10, 11, 12, 13, 14)
            
            offPeakHours.forEach { hour ->
                val slotStart = currentTime + (hour * 60 * 60 * 1000)
                ecoSlots.add(
                    TimeSlot(
                        startTime = slotStart,
                        endTime = slotStart + (60 * 60 * 1000), // 1 hour slots
                        date = getCurrentDate(),
                        slotType = SlotType.OFF_PEAK
                    )
                )
            }
            
            return ecoSlots
        }
        
        private fun optimizeLoadDistribution(
            facility: LaundryFacility,
            bookings: List<LaundryBooking>
        ): Map<String, Double> {
            val hourlyLoad = mutableMapOf<Int, Double>()
            
            bookings.forEach { booking ->
                val hour = getHourFromTimestamp(booking.scheduledStartTime)
                val machineLoad = when (booking.loadSize) {
                    LoadSize.SMALL -> 0.3
                    LoadSize.MEDIUM -> 0.6
                    LoadSize.LARGE -> 0.8
                    LoadSize.EXTRA_LARGE -> 1.0
                }
                hourlyLoad[hour] = (hourlyLoad[hour] ?: 0.0) + machineLoad
            }
            
            // Normalize load distribution
            val maxLoad = hourlyLoad.values.maxOrNull() ?: 1.0
            return hourlyLoad.mapValues { (_, load) -> load / maxLoad }
        }
        
        private fun getCurrentDate(): String {
            // Simplified date formatting
            return "2024-01-15" // This would be actual current date in real implementation
        }
        
        private fun getHourFromTimestamp(timestamp: Long): Int {
            // Simplified hour extraction
            return ((timestamp / (1000 * 60 * 60)) % 24).toInt()
        }
        
        data class EnergyProfile(
            val totalConsumption: Double,
            val optimizedConsumption: Double,
            val potentialSavings: Double,
            val recommendations: List<String>
        )
    }
    
    // 📱 Main Service Implementation
    private val loadBalancer = MachineLoadBalancer()
    private val queueManager = LaundryQueue()
    private val predictiveScheduler = PredictiveScheduler()
    private val energyOptimizer = EnergyOptimizer()
    
    // Cache for frequently accessed data
    private val facilitiesCache = mutableMapOf<String, LaundryFacility>()
    private val bookingsCache = mutableMapOf<String, LaundryBooking>()
    
    /**
     * 🎯 Find optimal machine for booking
     */
    fun findOptimalMachine(
        facilityId: String,
        machineType: MachineType,
        preferredTime: TimeSlot
    ): Promise<LaundryMachine?> {
        return Promise { resolve, reject ->
            try {
                val facility = facilitiesCache[facilityId]
                if (facility == null) {
                    resolve(null)
                    return@Promise
                }
                
                // Initialize load balancer with current machines
                facility.washingMachines.forEach { loadBalancer.addMachine(it) }
                facility.dryingMachines.forEach { loadBalancer.addMachine(it) }
                
                val optimalMachine = loadBalancer.getBestAvailableMachine(machineType)
                resolve(optimalMachine)
                
            } catch (e: Exception) {
                reject(e)
            }
        }
    }
    
    /**
     * 📊 Create booking with intelligent scheduling
     */
    fun createSmartBooking(
        userId: String,
        facilityId: String,
        machineType: MachineType,
        washType: WashType,
        loadSize: LoadSize,
        preferredTime: TimeSlot?,
        isEcoMode: Boolean = false
    ): Promise<LaundryBooking> {
        return Promise { resolve, reject ->
            try {
                val machine = loadBalancer.getBestAvailableMachine(machineType)
                if (machine == null) {
                    reject(Exception("No available machines of type $machineType"))
                    return@Promise
                }
                
                val estimatedDuration = calculateDuration(washType, loadSize)
                val cost = calculateCost(washType, loadSize, isEcoMode)
                
                val booking = LaundryBooking(
                    bookingId = generateBookingId(),
                    userId = userId,
                    hostelId = facilitiesCache[facilityId]?.hostelId ?: "",
                    facilityId = facilityId,
                    machineId = machine.machineId,
                    machineType = machineType,
                    slotTime = preferredTime ?: predictOptimalSlot(facilityId, estimatedDuration),
                    estimatedDuration = estimatedDuration,
                    washType = washType,
                    loadSize = loadSize,
                    status = BookingStatus.CONFIRMED,
                    priority = BookingPriority.NORMAL,
                    createdAt = System.currentTimeMillis(),
                    scheduledStartTime = preferredTime?.startTime ?: System.currentTimeMillis() + (30 * 60 * 1000),
                    cost = cost,
                    paymentStatus = PaymentStatus.PENDING,
                    isEcoMode = isEcoMode
                )
                
                // Add to queue if machine not immediately available
                if (!machine.isAvailable()) {
                    val queuePosition = queueManager.addToQueue(booking)
                    val updatedBooking = booking.copy(
                        status = BookingStatus.QUEUED,
                        queuePosition = queuePosition,
                        estimatedWaitTime = queueManager.getEstimatedWaitTime(booking.bookingId)
                    )
                    bookingsCache[booking.bookingId] = updatedBooking
                    resolve(updatedBooking)
                } else {
                    bookingsCache[booking.bookingId] = booking
                    resolve(booking)
                }
                
            } catch (e: Exception) {
                reject(e)
            }
        }
    }
    
    /**
     * ⚡ Get energy optimization recommendations
     */
    fun getEnergyOptimizations(hostelId: String): Promise<Map<String, Any>> {
        return Promise { resolve, reject ->
            try {
                val facilities = facilitiesCache.values.filter { it.hostelId == hostelId }
                val bookings = bookingsCache.values.filter { it.hostelId == hostelId }
                
                val optimizations = energyOptimizer.optimizeEnergyUsage(facilities, bookings)
                resolve(optimizations)
                
            } catch (e: Exception) {
                reject(e)
            }
        }
    }
    
    /**
     * 📈 Get facility analytics
     */
    fun getFacilityAnalytics(facilityId: String): Promise<Map<String, Any>> {
        return Promise { resolve, reject ->
            try {
                val facility = facilitiesCache[facilityId]
                if (facility == null) {
                    reject(Exception("Facility not found"))
                    return@Promise
                }
                
                val facilityBookings = bookingsCache.values.filter { it.facilityId == facilityId }
                val queueStats = queueManager.getQueueStats()
                val loadDistribution = loadBalancer.getLoadDistribution()
                val averageLoad = loadBalancer.getAverageLoad()
                
                val analytics = mapOf(
                    "facilityInfo" to facility.toMap(),
                    "totalBookings" to facilityBookings.size,
                    "activeBookings" to facilityBookings.count { it.isActive() },
                    "queueStats" to queueStats,
                    "loadDistribution" to loadDistribution,
                    "averageLoad" to averageLoad,
                    "occupancyRate" to facility.getOccupancyPercentage(),
                    "availableMachines" to mapOf(
                        "washing" to facility.getAvailableWashingMachines().size,
                        "drying" to facility.getAvailableDryingMachines().size
                    ),
                    "revenue" to facilityBookings.filter { it.paymentStatus == PaymentStatus.PAID }
                        .sumOf { it.cost },
                    "averageRating" to facilityBookings.mapNotNull { it.rating }.average()
                )
                
                resolve(analytics)
                
            } catch (e: Exception) {
                reject(e)
            }
        }
    }
    
    /**
     * 🔄 Update machine status and rebalance load
     */
    fun updateMachineStatus(machineId: String, newStatus: MachineStatus): Promise<Boolean> {
        return Promise { resolve, reject ->
            try {
                loadBalancer.updateMachineLoad(machineId, newStatus)
                
                // Process queue if machine becomes available
                if (newStatus == MachineStatus.AVAILABLE) {
                    val nextBooking = queueManager.getNextBooking()
                    nextBooking?.let { booking ->
                        val updatedBooking = booking.copy(
                            status = BookingStatus.CONFIRMED,
                            machineId = machineId,
                            queuePosition = null,
                            estimatedWaitTime = 0
                        )
                        bookingsCache[booking.bookingId] = updatedBooking
                    }
                }
                
                resolve(true)
                
            } catch (e: Exception) {
                reject(e)
            }
        }
    }
    
    // 🛠️ Helper Methods
    
    private fun calculateDuration(washType: WashType, loadSize: LoadSize): Int {
        val baseTime = when (washType) {
            WashType.QUICK_WASH -> 30
            WashType.DELICATE -> 45
            WashType.REGULAR -> 60
            WashType.HEAVY_DUTY -> 90
            WashType.ECO_WASH -> 75
            WashType.COLD_WASH -> 55
            WashType.HOT_WASH -> 70
        }
        
        val multiplier = when (loadSize) {
            LoadSize.SMALL -> 0.8
            LoadSize.MEDIUM -> 1.0
            LoadSize.LARGE -> 1.3
            LoadSize.EXTRA_LARGE -> 1.6
        }
        
        return (baseTime * multiplier).toInt()
    }
    
    private fun calculateCost(washType: WashType, loadSize: LoadSize, isEcoMode: Boolean): Double {
        val baseCost = when (washType) {
            WashType.QUICK_WASH -> 25.0
            WashType.DELICATE -> 35.0
            WashType.REGULAR -> 30.0
            WashType.HEAVY_DUTY -> 45.0
            WashType.ECO_WASH -> 25.0
            WashType.COLD_WASH -> 20.0
            WashType.HOT_WASH -> 40.0
        }
        
        val sizeMultiplier = when (loadSize) {
            LoadSize.SMALL -> 0.8
            LoadSize.MEDIUM -> 1.0
            LoadSize.LARGE -> 1.4
            LoadSize.EXTRA_LARGE -> 1.8
        }
        
        val ecoDiscount = if (isEcoMode) 0.9 else 1.0
        
        return baseCost * sizeMultiplier * ecoDiscount
    }
    
    private fun predictOptimalSlot(facilityId: String, duration: Int): TimeSlot {
        val optimalSlots = predictiveScheduler.predictOptimalSlots(
            facilityId, getCurrentDate(), duration
        )
        
        return optimalSlots.firstOrNull() ?: TimeSlot(
            startTime = System.currentTimeMillis() + (30 * 60 * 1000),
            endTime = System.currentTimeMillis() + (30 * 60 * 1000) + (duration * 60 * 1000),
            date = getCurrentDate()
        )
    }
    
    private fun generateBookingId(): String {
        return "LB${System.currentTimeMillis()}${Random.nextInt(1000, 9999)}"
    }
    
    private fun getCurrentDate(): String {
        return "2024-01-15" // Simplified for demo
    }
    
    // 📊 Cache Management
    fun updateFacilityCache(facility: LaundryFacility) {
        facilitiesCache[facility.facilityId] = facility
    }
    
    fun updateBookingCache(booking: LaundryBooking) {
        bookingsCache[booking.bookingId] = booking
    }
    
    fun clearCache() {
        facilitiesCache.clear()
        bookingsCache.clear()
    }
}
