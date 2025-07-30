package modules.laundrybalancer

import kotlinx.serialization.Serializable
import kotlin.math.*

/**
 * 👕 LaundryBalancer Module - Data Models
 * 
 * Advanced laundry scheduling with DSA optimization:
 * - Load balancing algorithms for machine usage
 * - Queue management for peak hours
 * - Predictive scheduling based on patterns
 * - Energy optimization for eco-friendly operations
 */

/**
 * 🏠 Laundry Facility Information
 */
@Serializable
data class LaundryFacility(
    val facilityId: String,
    val hostelId: String,
    val name: String,
    val location: String,
    val floor: Int,
    val operatingHours: OperatingHours,
    val washingMachines: List<LaundryMachine>,
    val dryingMachines: List<LaundryMachine>,
    val additionalServices: List<String> = emptyList(), // ironing, dry cleaning
    val amenities: List<String> = emptyList(), // seating, vending machines
    val capacity: Int,
    val currentOccupancy: Int = 0,
    val averageWaitTime: Int = 0, // minutes
    val peakHours: List<TimeSlot> = emptyList(),
    val maintenanceSchedule: List<MaintenanceWindow> = emptyList(),
    val isActive: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromMap(map: Map<String, Any>): LaundryFacility {
            return LaundryFacility(
                facilityId = map["facilityId"] as String,
                hostelId = map["hostelId"] as String,
                name = map["name"] as String,
                location = map["location"] as String,
                floor = (map["floor"] as Number).toInt(),
                operatingHours = OperatingHours.fromMap(map["operatingHours"] as Map<String, Any>),
                washingMachines = (map["washingMachines"] as List<Map<String, Any>>).map { 
                    LaundryMachine.fromMap(it) 
                },
                dryingMachines = (map["dryingMachines"] as List<Map<String, Any>>).map { 
                    LaundryMachine.fromMap(it) 
                },
                additionalServices = (map["additionalServices"] as? List<String>) ?: emptyList(),
                amenities = (map["amenities"] as? List<String>) ?: emptyList(),
                capacity = (map["capacity"] as Number).toInt(),
                currentOccupancy = (map["currentOccupancy"] as? Number)?.toInt() ?: 0,
                averageWaitTime = (map["averageWaitTime"] as? Number)?.toInt() ?: 0,
                peakHours = (map["peakHours"] as? List<Map<String, Any>>)?.map { 
                    TimeSlot.fromMap(it) 
                } ?: emptyList(),
                maintenanceSchedule = (map["maintenanceSchedule"] as? List<Map<String, Any>>)?.map { 
                    MaintenanceWindow.fromMap(it) 
                } ?: emptyList(),
                isActive = map["isActive"] as? Boolean ?: true,
                lastUpdated = (map["lastUpdated"] as Number).toLong()
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("facilityId", facilityId)
            put("hostelId", hostelId)
            put("name", name)
            put("location", location)
            put("floor", floor)
            put("operatingHours", operatingHours.toMap())
            put("washingMachines", washingMachines.map { it.toMap() })
            put("dryingMachines", dryingMachines.map { it.toMap() })
            put("additionalServices", additionalServices)
            put("amenities", amenities)
            put("capacity", capacity)
            put("currentOccupancy", currentOccupancy)
            put("averageWaitTime", averageWaitTime)
            put("peakHours", peakHours.map { it.toMap() })
            put("maintenanceSchedule", maintenanceSchedule.map { it.toMap() })
            put("isActive", isActive)
            put("lastUpdated", lastUpdated)
        }
    }
    
    fun getAvailableWashingMachines(): List<LaundryMachine> {
        return washingMachines.filter { it.status == MachineStatus.AVAILABLE }
    }
    
    fun getAvailableDryingMachines(): List<LaundryMachine> {
        return dryingMachines.filter { it.status == MachineStatus.AVAILABLE }
    }
    
    fun getTotalMachines(): Int = washingMachines.size + dryingMachines.size
    fun getOccupancyPercentage(): Double = (currentOccupancy.toDouble() / capacity) * 100
}

/**
 * 🔧 Laundry Machine
 */
@Serializable
data class LaundryMachine(
    val machineId: String,
    val type: MachineType,
    val model: String,
    val capacity: Double, // kg
    val status: MachineStatus,
    val currentUser: String? = null,
    val estimatedTimeRemaining: Int = 0, // minutes
    val cycleStartTime: Long? = null,
    val cycleEndTime: Long? = null,
    val energyRating: String, // A++, A+, A, B, C
    val waterConsumption: Double, // liters per cycle
    val powerConsumption: Double, // kWh per cycle
    val averageCycleTime: Int, // minutes
    val maintenanceScore: Int, // 1-100
    val lastMaintenance: Long? = null,
    val cycleCount: Int = 0,
    val features: List<String> = emptyList(), // quick wash, delicate, etc.
    val isEcoFriendly: Boolean = false,
    val installationDate: Long,
    val warrantyExpiry: Long? = null
) {
    companion object {
        fun fromMap(map: Map<String, Any>): LaundryMachine {
            return LaundryMachine(
                machineId = map["machineId"] as String,
                type = MachineType.valueOf(map["type"] as String),
                model = map["model"] as String,
                capacity = (map["capacity"] as Number).toDouble(),
                status = MachineStatus.valueOf(map["status"] as String),
                currentUser = map["currentUser"] as? String,
                estimatedTimeRemaining = (map["estimatedTimeRemaining"] as? Number)?.toInt() ?: 0,
                cycleStartTime = (map["cycleStartTime"] as? Number)?.toLong(),
                cycleEndTime = (map["cycleEndTime"] as? Number)?.toLong(),
                energyRating = map["energyRating"] as String,
                waterConsumption = (map["waterConsumption"] as Number).toDouble(),
                powerConsumption = (map["powerConsumption"] as Number).toDouble(),
                averageCycleTime = (map["averageCycleTime"] as Number).toInt(),
                maintenanceScore = (map["maintenanceScore"] as? Number)?.toInt() ?: 100,
                lastMaintenance = (map["lastMaintenance"] as? Number)?.toLong(),
                cycleCount = (map["cycleCount"] as? Number)?.toInt() ?: 0,
                features = (map["features"] as? List<String>) ?: emptyList(),
                isEcoFriendly = map["isEcoFriendly"] as? Boolean ?: false,
                installationDate = (map["installationDate"] as Number).toLong(),
                warrantyExpiry = (map["warrantyExpiry"] as? Number)?.toLong()
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("machineId", machineId)
            put("type", type.name)
            put("model", model)
            put("capacity", capacity)
            put("status", status.name)
            currentUser?.let { put("currentUser", it) }
            put("estimatedTimeRemaining", estimatedTimeRemaining)
            cycleStartTime?.let { put("cycleStartTime", it) }
            cycleEndTime?.let { put("cycleEndTime", it) }
            put("energyRating", energyRating)
            put("waterConsumption", waterConsumption)
            put("powerConsumption", powerConsumption)
            put("averageCycleTime", averageCycleTime)
            put("maintenanceScore", maintenanceScore)
            lastMaintenance?.let { put("lastMaintenance", it) }
            put("cycleCount", cycleCount)
            put("features", features)
            put("isEcoFriendly", isEcoFriendly)
            put("installationDate", installationDate)
            warrantyExpiry?.let { put("warrantyExpiry", it) }
        }
    }
    
    fun isAvailable(): Boolean = status == MachineStatus.AVAILABLE
    fun isInUse(): Boolean = status == MachineStatus.IN_USE
    fun needsMaintenance(): Boolean = maintenanceScore < 30
    fun getEfficiencyScore(): Double {
        val energyScore = when (energyRating) {
            "A++" -> 100.0
            "A+" -> 90.0
            "A" -> 80.0
            "B" -> 70.0
            "C" -> 60.0
            else -> 50.0
        }
        return (energyScore + maintenanceScore) / 2.0
    }
}

/**
 * 📅 Laundry Booking
 */
@Serializable
data class LaundryBooking(
    val bookingId: String,
    val userId: String,
    val hostelId: String,
    val facilityId: String,
    val machineId: String,
    val machineType: MachineType,
    val slotTime: TimeSlot,
    val estimatedDuration: Int, // minutes
    val washType: WashType,
    val loadSize: LoadSize,
    val specialInstructions: String? = null,
    val status: BookingStatus,
    val priority: BookingPriority,
    val createdAt: Long,
    val scheduledStartTime: Long,
    val actualStartTime: Long? = null,
    val completedTime: Long? = null,
    val cost: Double,
    val paymentStatus: PaymentStatus,
    val remindersSent: Int = 0,
    val queuePosition: Int? = null,
    val estimatedWaitTime: Int = 0, // minutes
    val energyConsumption: Double? = null,
    val waterConsumption: Double? = null,
    val rating: Int? = null,
    val feedback: String? = null,
    val isEcoMode: Boolean = false,
    val cancellationReason: String? = null
) {
    companion object {
        fun fromMap(map: Map<String, Any>): LaundryBooking {
            return LaundryBooking(
                bookingId = map["bookingId"] as String,
                userId = map["userId"] as String,
                hostelId = map["hostelId"] as String,
                facilityId = map["facilityId"] as String,
                machineId = map["machineId"] as String,
                machineType = MachineType.valueOf(map["machineType"] as String),
                slotTime = TimeSlot.fromMap(map["slotTime"] as Map<String, Any>),
                estimatedDuration = (map["estimatedDuration"] as Number).toInt(),
                washType = WashType.valueOf(map["washType"] as String),
                loadSize = LoadSize.valueOf(map["loadSize"] as String),
                specialInstructions = map["specialInstructions"] as? String,
                status = BookingStatus.valueOf(map["status"] as String),
                priority = BookingPriority.valueOf(map["priority"] as? String ?: "NORMAL"),
                createdAt = (map["createdAt"] as Number).toLong(),
                scheduledStartTime = (map["scheduledStartTime"] as Number).toLong(),
                actualStartTime = (map["actualStartTime"] as? Number)?.toLong(),
                completedTime = (map["completedTime"] as? Number)?.toLong(),
                cost = (map["cost"] as Number).toDouble(),
                paymentStatus = PaymentStatus.valueOf(map["paymentStatus"] as String),
                remindersSent = (map["remindersSent"] as? Number)?.toInt() ?: 0,
                queuePosition = (map["queuePosition"] as? Number)?.toInt(),
                estimatedWaitTime = (map["estimatedWaitTime"] as? Number)?.toInt() ?: 0,
                energyConsumption = (map["energyConsumption"] as? Number)?.toDouble(),
                waterConsumption = (map["waterConsumption"] as? Number)?.toDouble(),
                rating = (map["rating"] as? Number)?.toInt(),
                feedback = map["feedback"] as? String,
                isEcoMode = map["isEcoMode"] as? Boolean ?: false,
                cancellationReason = map["cancellationReason"] as? String
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("bookingId", bookingId)
            put("userId", userId)
            put("hostelId", hostelId)
            put("facilityId", facilityId)
            put("machineId", machineId)
            put("machineType", machineType.name)
            put("slotTime", slotTime.toMap())
            put("estimatedDuration", estimatedDuration)
            put("washType", washType.name)
            put("loadSize", loadSize.name)
            specialInstructions?.let { put("specialInstructions", it) }
            put("status", status.name)
            put("priority", priority.name)
            put("createdAt", createdAt)
            put("scheduledStartTime", scheduledStartTime)
            actualStartTime?.let { put("actualStartTime", it) }
            completedTime?.let { put("completedTime", it) }
            put("cost", cost)
            put("paymentStatus", paymentStatus.name)
            put("remindersSent", remindersSent)
            queuePosition?.let { put("queuePosition", it) }
            put("estimatedWaitTime", estimatedWaitTime)
            energyConsumption?.let { put("energyConsumption", it) }
            waterConsumption?.let { put("waterConsumption", it) }
            rating?.let { put("rating", it) }
            feedback?.let { put("feedback", it) }
            put("isEcoMode", isEcoMode)
            cancellationReason?.let { put("cancellationReason", it) }
        }
    }
    
    fun isUpcoming(): Boolean = status in listOf(BookingStatus.CONFIRMED, BookingStatus.QUEUED)
    fun isActive(): Boolean = status == BookingStatus.IN_PROGRESS
    fun isCompleted(): Boolean = status == BookingStatus.COMPLETED
    fun canBeCancelled(): Boolean = status in listOf(BookingStatus.CONFIRMED, BookingStatus.QUEUED)
    
    fun getActualDuration(): Int? {
        return if (actualStartTime != null && completedTime != null) {
            ((completedTime - actualStartTime) / (1000 * 60)).toInt()
        } else null
    }
}

/**
 * ⏰ Operating Hours
 */
@Serializable
data class OperatingHours(
    val monday: DayHours,
    val tuesday: DayHours,
    val wednesday: DayHours,
    val thursday: DayHours,
    val friday: DayHours,
    val saturday: DayHours,
    val sunday: DayHours,
    val holidays: DayHours? = null
) {
    companion object {
        fun fromMap(map: Map<String, Any>): OperatingHours {
            return OperatingHours(
                monday = DayHours.fromMap(map["monday"] as Map<String, Any>),
                tuesday = DayHours.fromMap(map["tuesday"] as Map<String, Any>),
                wednesday = DayHours.fromMap(map["wednesday"] as Map<String, Any>),
                thursday = DayHours.fromMap(map["thursday"] as Map<String, Any>),
                friday = DayHours.fromMap(map["friday"] as Map<String, Any>),
                saturday = DayHours.fromMap(map["saturday"] as Map<String, Any>),
                sunday = DayHours.fromMap(map["sunday"] as Map<String, Any>),
                holidays = (map["holidays"] as? Map<String, Any>)?.let { DayHours.fromMap(it) }
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("monday", monday.toMap())
            put("tuesday", tuesday.toMap())
            put("wednesday", wednesday.toMap())
            put("thursday", thursday.toMap())
            put("friday", friday.toMap())
            put("saturday", saturday.toMap())
            put("sunday", sunday.toMap())
            holidays?.let { put("holidays", it.toMap()) }
        }
    }
    
    fun getHoursForDay(dayOfWeek: Int): DayHours {
        return when (dayOfWeek) {
            1 -> monday
            2 -> tuesday
            3 -> wednesday
            4 -> thursday
            5 -> friday
            6 -> saturday
            7 -> sunday
            else -> monday
        }
    }
}

/**
 * 🕐 Day Hours
 */
@Serializable
data class DayHours(
    val isOpen: Boolean,
    val openTime: String, // HH:mm format
    val closeTime: String, // HH:mm format
    val breakStart: String? = null,
    val breakEnd: String? = null
) {
    companion object {
        fun fromMap(map: Map<String, Any>): DayHours {
            return DayHours(
                isOpen = map["isOpen"] as Boolean,
                openTime = map["openTime"] as String,
                closeTime = map["closeTime"] as String,
                breakStart = map["breakStart"] as? String,
                breakEnd = map["breakEnd"] as? String
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("isOpen", isOpen)
            put("openTime", openTime)
            put("closeTime", closeTime)
            breakStart?.let { put("breakStart", it) }
            breakEnd?.let { put("breakEnd", it) }
        }
    }
}

/**
 * ⏰ Time Slot
 */
@Serializable
data class TimeSlot(
    val startTime: Long,
    val endTime: Long,
    val date: String, // YYYY-MM-DD format
    val slotType: SlotType = SlotType.REGULAR
) {
    companion object {
        fun fromMap(map: Map<String, Any>): TimeSlot {
            return TimeSlot(
                startTime = (map["startTime"] as Number).toLong(),
                endTime = (map["endTime"] as Number).toLong(),
                date = map["date"] as String,
                slotType = SlotType.valueOf(map["slotType"] as? String ?: "REGULAR")
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "startTime" to startTime,
            "endTime" to endTime,
            "date" to date,
            "slotType" to slotType.name
        )
    }
    
    fun getDurationInMinutes(): Int {
        return ((endTime - startTime) / (1000 * 60)).toInt()
    }
    
    fun overlaps(other: TimeSlot): Boolean {
        return startTime < other.endTime && endTime > other.startTime
    }
}

/**
 * 🔧 Maintenance Window
 */
@Serializable
data class MaintenanceWindow(
    val startTime: Long,
    val endTime: Long,
    val machineIds: List<String> = emptyList(), // empty means all machines
    val type: MaintenanceType,
    val description: String,
    val technician: String? = null,
    val isRecurring: Boolean = false,
    val recurringPattern: String? = null // cron expression for recurring maintenance
) {
    companion object {
        fun fromMap(map: Map<String, Any>): MaintenanceWindow {
            return MaintenanceWindow(
                startTime = (map["startTime"] as Number).toLong(),
                endTime = (map["endTime"] as Number).toLong(),
                machineIds = (map["machineIds"] as? List<String>) ?: emptyList(),
                type = MaintenanceType.valueOf(map["type"] as String),
                description = map["description"] as String,
                technician = map["technician"] as? String,
                isRecurring = map["isRecurring"] as? Boolean ?: false,
                recurringPattern = map["recurringPattern"] as? String
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("startTime", startTime)
            put("endTime", endTime)
            put("machineIds", machineIds)
            put("type", type.name)
            put("description", description)
            technician?.let { put("technician", it) }
            put("isRecurring", isRecurring)
            recurringPattern?.let { put("recurringPattern", it) }
        }
    }
}

/**
 * 📊 Usage Statistics
 */
@Serializable
data class UsageStatistics(
    val facilityId: String,
    val date: String, // YYYY-MM-DD
    val hourlyUsage: Map<Int, Int>, // hour -> number of bookings
    val totalBookings: Int,
    val totalRevenue: Double,
    val averageWaitTime: Double,
    val peakHour: Int,
    val machineUtilization: Map<String, Double>, // machineId -> utilization percentage
    val energyConsumption: Double,
    val waterConsumption: Double,
    val customerSatisfaction: Double, // average rating
    val cancellationRate: Double
) {
    companion object {
        fun fromMap(map: Map<String, Any>): UsageStatistics {
            return UsageStatistics(
                facilityId = map["facilityId"] as String,
                date = map["date"] as String,
                hourlyUsage = (map["hourlyUsage"] as Map<String, Number>).mapKeys { it.key.toInt() }.mapValues { it.value.toInt() },
                totalBookings = (map["totalBookings"] as Number).toInt(),
                totalRevenue = (map["totalRevenue"] as Number).toDouble(),
                averageWaitTime = (map["averageWaitTime"] as Number).toDouble(),
                peakHour = (map["peakHour"] as Number).toInt(),
                machineUtilization = (map["machineUtilization"] as Map<String, Number>).mapValues { it.value.toDouble() },
                energyConsumption = (map["energyConsumption"] as Number).toDouble(),
                waterConsumption = (map["waterConsumption"] as Number).toDouble(),
                customerSatisfaction = (map["customerSatisfaction"] as Number).toDouble(),
                cancellationRate = (map["cancellationRate"] as Number).toDouble()
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "facilityId" to facilityId,
            "date" to date,
            "hourlyUsage" to hourlyUsage,
            "totalBookings" to totalBookings,
            "totalRevenue" to totalRevenue,
            "averageWaitTime" to averageWaitTime,
            "peakHour" to peakHour,
            "machineUtilization" to machineUtilization,
            "energyConsumption" to energyConsumption,
            "waterConsumption" to waterConsumption,
            "customerSatisfaction" to customerSatisfaction,
            "cancellationRate" to cancellationRate
        )
    }
}

/**
 * 🏷️ Enums for Laundry System
 */
enum class MachineType { WASHING_MACHINE, DRYER, WASHER_DRYER_COMBO }
enum class MachineStatus { AVAILABLE, IN_USE, OUT_OF_ORDER, MAINTENANCE, RESERVED }
enum class BookingStatus { 
    CONFIRMED, QUEUED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW, EXPIRED 
}
enum class BookingPriority { LOW, NORMAL, HIGH, URGENT }
enum class PaymentStatus { PENDING, PAID, FAILED, REFUNDED }
enum class WashType { 
    REGULAR, DELICATE, QUICK_WASH, HEAVY_DUTY, ECO_WASH, COLD_WASH, HOT_WASH 
}
enum class LoadSize { SMALL, MEDIUM, LARGE, EXTRA_LARGE }
enum class SlotType { REGULAR, PEAK, OFF_PEAK, MAINTENANCE }
enum class MaintenanceType { 
    ROUTINE, PREVENTIVE, EMERGENCY, DEEP_CLEAN, SOFTWARE_UPDATE 
}
