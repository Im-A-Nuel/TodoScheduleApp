package id.app.todoschedule.domain.model

/**
 * Domain model untuk Reminder/Pengingat
 */
data class Reminder(
    val id: Long = 0,
    val entryId: Long,
    val remindAt: Long,              // Waktu pengingat (epoch millis)
    val offsetMinutes: Int? = null   // Offset dalam menit (15, 30, 60, dll)
)
