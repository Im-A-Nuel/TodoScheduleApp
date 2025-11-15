package id.app.todoschedule.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.app.todoschedule.core.model.Priority
import id.app.todoschedule.ui.theme.PriorityHigh
import id.app.todoschedule.ui.theme.PriorityLow
import id.app.todoschedule.ui.theme.PriorityMedium

/**
 * Badge untuk menampilkan prioritas
 */
@Composable
fun PriorityBadge(
    priority: Priority,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, text) = when (priority) {
        Priority.HIGH -> PriorityHigh to "TINGGI"
        Priority.MED -> PriorityMedium to "SEDANG"
        Priority.LOW -> PriorityLow to "RENDAH"
    }

    Text(
        text = text,
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        style = MaterialTheme.typography.labelSmall
    )
}
