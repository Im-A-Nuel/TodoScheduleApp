package id.app.todoschedule.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.app.todoschedule.core.model.EntryType
import id.app.todoschedule.ui.theme.TextSecondary

/**
 * Badge untuk menampilkan tipe entry
 */
@Composable
fun TypeBadge(
    type: EntryType,
    modifier: Modifier = Modifier
) {
    val text = when (type) {
        EntryType.TODO -> "To-Do"
        EntryType.TASK -> "Tugas Kuliah"
        EntryType.SHIFT -> "Jadwal Kerja"
        EntryType.EVENT -> "Event/Agenda"
    }

    Text(
        text = text,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        color = TextSecondary,
        fontSize = 11.sp,
        style = MaterialTheme.typography.labelSmall
    )
}
