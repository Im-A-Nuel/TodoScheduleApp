package id.app.todoschedule.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Custom Calendar Component with smooth animations
 */
@Composable
fun CustomCalendar(
    selectedDate: Calendar,
    onDateSelected: (Calendar) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(selectedDate.clone() as Calendar) }
    var animationDirection by remember { mutableStateOf(1) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Month/Year Header with navigation
        CalendarHeader(
            currentMonth = currentMonth,
            onPreviousMonth = {
                animationDirection = -1
                currentMonth = (currentMonth.clone() as Calendar).apply {
                    add(Calendar.MONTH, -1)
                }
            },
            onNextMonth = {
                animationDirection = 1
                currentMonth = (currentMonth.clone() as Calendar).apply {
                    add(Calendar.MONTH, 1)
                }
            }
        )

        Divider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            thickness = 1.dp
        )

        // Day labels (M, S, S, R, K, J, S)
        DayLabelsRow()

        // Calendar grid with animation
        AnimatedContent(
            targetState = currentMonth.get(Calendar.MONTH),
            transitionSpec = {
                val direction = if (animationDirection > 0) 1 else -1
                slideInHorizontally(
                    initialOffsetX = { it * direction },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeIn(
                    animationSpec = tween(300)
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { -it * direction },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeOut(
                    animationSpec = tween(300)
                )
            },
            label = "calendar_month_transition"
        ) { month ->
            CalendarGrid(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                onDateSelected = onDateSelected
            )
        }
    }
}

@Composable
private fun CalendarHeader(
    currentMonth: Calendar,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous month button
        IconButton(
            onClick = onPreviousMonth,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Previous month",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        // Month and Year
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
        Text(
            text = monthYearFormat.format(currentMonth.time),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Next month button
        IconButton(
            onClick = onNextMonth,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Next month",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun DayLabelsRow() {
    val dayLabels = listOf("M", "S", "S", "R", "K", "J", "S")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        dayLabels.forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: Calendar,
    selectedDate: Calendar,
    onDateSelected: (Calendar) -> Unit
) {
    val daysInMonth = getDaysInMonth(currentMonth)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        daysInMonth.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Always display 7 cells per week
                for (i in 0 until 7) {
                    val day = week.getOrNull(i)
                    if (day != null) {
                        DayCell(
                            day = day,
                            isSelected = isSameDay(day, selectedDate),
                            isToday = isSameDay(day, Calendar.getInstance()),
                            onDateSelected = onDateSelected
                        )
                    } else {
                        // Empty cell for days from previous/next month
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.DayCell(
    day: Calendar,
    isSelected: Boolean,
    isToday: Boolean,
    onDateSelected: (Calendar) -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "day_cell_scale"
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .scale(scale)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    else -> Color.Transparent
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onDateSelected(day) }
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.get(Calendar.DAY_OF_MONTH).toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                isToday -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurface
            },
            fontSize = 14.sp
        )
    }
}

/**
 * Get all days in the month with proper week alignment
 */
private fun getDaysInMonth(month: Calendar): List<Calendar?> {
    val days = mutableListOf<Calendar?>()
    val firstDay = (month.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }

    // Add empty cells for days before the first day of the month
    val firstDayOfWeek = firstDay.get(Calendar.DAY_OF_WEEK)
    // Adjust for Indonesian week (Monday = 1)
    val emptyDays = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2
    repeat(emptyDays) {
        days.add(null)
    }

    // Add all days in the month
    val daysInMonth = month.getActualMaximum(Calendar.DAY_OF_MONTH)
    for (day in 1..daysInMonth) {
        days.add((month.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, day)
        })
    }

    return days
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
