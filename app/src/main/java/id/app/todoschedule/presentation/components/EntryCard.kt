package id.app.todoschedule.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.app.todoschedule.core.model.Status
import id.app.todoschedule.domain.model.Entry
import id.app.todoschedule.ui.theme.ErrorColor
import id.app.todoschedule.ui.theme.PriorityMedium
import id.app.todoschedule.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.*

/**
 * Card untuk menampilkan entry di list
 */
@Composable
fun EntryCard(
    entry: Entry,
    onCardClick: () -> Unit,
    onToggleStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onCardClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox
        IconButton(
            onClick = onToggleStatus,
            modifier = Modifier.size(32.dp)
        ) {
            if (entry.status == Status.DONE) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Selesai",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .border(
                            width = 2.dp,
                            color = TextSecondary,
                            shape = CircleShape
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            // Title
            Text(
                text = entry.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (entry.status == Status.DONE) {
                    TextDecoration.LineThrough
                } else {
                    null
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Badges and Time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PriorityBadge(priority = entry.priority)

                // Time/Date with overdue indicator
                val dueTimestamp = entry.dueAt ?: entry.startAt
                if (dueTimestamp != null) {
                    val timeText = formatDateTime(dueTimestamp)
                    val timeColor = getTimeColor(dueTimestamp, entry.status)

                    Text(
                        text = timeText,
                        style = MaterialTheme.typography.bodySmall,
                        color = timeColor,
                        fontSize = 12.sp,
                        fontWeight = if (timeColor != TextSecondary) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

/**
 * Get color based on due date status
 */
@Composable
private fun getTimeColor(timestamp: Long, status: Status): androidx.compose.ui.graphics.Color {
    if (status == Status.DONE) return TextSecondary

    val now = System.currentTimeMillis()
    val diff = timestamp - now
    val hoursUntilDue = diff / (1000 * 60 * 60)

    return when {
        diff < 0 -> ErrorColor // Overdue
        hoursUntilDue < 24 -> PriorityMedium // Due within 24 hours
        else -> TextSecondary // Normal
    }
}

/**
 * Format timestamp ke string yang user-friendly
 */
private fun formatDateTime(timestamp: Long): String {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply { timeInMillis = timestamp }

    val dateFormat = when {
        isSameDay(now, target) -> SimpleDateFormat("'Hari ini' HH:mm", Locale("id", "ID"))
        isYesterday(now, target) -> SimpleDateFormat("'Kemarin' HH:mm", Locale("id", "ID"))
        isTomorrow(now, target) -> SimpleDateFormat("'Besok' HH:mm", Locale("id", "ID"))
        else -> SimpleDateFormat("d MMM, HH:mm", Locale("id", "ID"))
    }

    return dateFormat.format(Date(timestamp))
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun isYesterday(now: Calendar, target: Calendar): Boolean {
    val yesterday = now.clone() as Calendar
    yesterday.add(Calendar.DAY_OF_YEAR, -1)
    return isSameDay(yesterday, target)
}

private fun isTomorrow(now: Calendar, target: Calendar): Boolean {
    val tomorrow = now.clone() as Calendar
    tomorrow.add(Calendar.DAY_OF_YEAR, 1)
    return isSameDay(tomorrow, target)
}
