package id.app.todoschedule.presentation.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import id.app.todoschedule.core.model.EntryType
import id.app.todoschedule.core.model.Priority
import id.app.todoschedule.presentation.components.DateTimePickerDialog
import id.app.todoschedule.presentation.components.TagInputDialog
import id.app.todoschedule.ui.theme.PrimaryBlue
import id.app.todoschedule.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.*

/**
 * Editor Screen - Create/Edit Entry
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Tugas") },
            text = { Text("Apakah Anda yakin ingin menghapus tugas ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteEntry(onDeleted = onNavigateBack)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Show error dialog if any
    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEditMode) "Edit Item" else "Buat Baru",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    if (uiState.isEditMode) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    TextButton(
                        onClick = {
                            viewModel.saveEntry(onSuccess = onNavigateBack)
                        }
                    ) {
                        Text(
                            text = "Simpan",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Judul (Required)
            TitleSection(
                title = uiState.title,
                onTitleChange = viewModel::onTitleChange
            )

            // Tipe
            TypeSection(
                selectedType = uiState.type,
                onTypeChange = viewModel::onTypeChange
            )

            // Prioritas
            PrioritySection(
                selectedPriority = uiState.priority,
                onPriorityChange = viewModel::onPriorityChange
            )

            // Tanggal & Waktu
            DateTimeSection(
                startAt = uiState.startAt,
                endAt = uiState.endAt,
                onStartAtChange = viewModel::onStartAtChange,
                onEndAtChange = viewModel::onEndAtChange
            )

            // Deskripsi
            DescriptionSection(
                description = uiState.description,
                onDescriptionChange = viewModel::onDescriptionChange
            )

            // Tag
            TagSection(
                tags = uiState.tags,
                onAddTag = viewModel::onAddTag,
                onRemoveTag = viewModel::onRemoveTag
            )

            // Pengingat
            ReminderSection(
                selectedOffsets = uiState.reminderOffsets,
                onToggleOffset = viewModel::onToggleReminderOffset
            )
        }
    }
}

@Composable
private fun TitleSection(
    title: String,
    onTitleChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Judul *",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Masukkan judul", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun TypeSection(
    selectedType: EntryType,
    onTypeChange: (EntryType) -> Unit
) {
    Column {
        Text(
            text = "Tipe",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TypeChip(
                label = "TODO",
                selected = selectedType == EntryType.TODO,
                onClick = { onTypeChange(EntryType.TODO) }
            )
            TypeChip(
                label = "TUGAS",
                selected = selectedType == EntryType.TASK,
                onClick = { onTypeChange(EntryType.TASK) }
            )
            TypeChip(
                label = "SHIFT",
                selected = selectedType == EntryType.SHIFT,
                onClick = { onTypeChange(EntryType.SHIFT) }
            )
            TypeChip(
                label = "EVENT",
                selected = selectedType == EntryType.EVENT,
                onClick = { onTypeChange(EntryType.EVENT) }
            )
        }
    }
}

@Composable
private fun TypeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = if (selected) MaterialTheme.colorScheme.onPrimary
                   else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun PrioritySection(
    selectedPriority: Priority,
    onPriorityChange: (Priority) -> Unit
) {
    Column {
        Text(
            text = "Prioritas",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PriorityChip(
                label = "Rendah",
                selected = selectedPriority == Priority.LOW,
                onClick = { onPriorityChange(Priority.LOW) }
            )
            PriorityChip(
                label = "Sedang",
                selected = selectedPriority == Priority.MED,
                onClick = { onPriorityChange(Priority.MED) }
            )
            PriorityChip(
                label = "Tinggi",
                selected = selectedPriority == Priority.HIGH,
                onClick = { onPriorityChange(Priority.HIGH) }
            )
        }
    }
}

@Composable
private fun PriorityChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = label,
                color = if (selected) MaterialTheme.colorScheme.onPrimary
                       else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun DateTimeSection(
    startAt: Long?,
    endAt: Long?,
    onStartAtChange: (Long?) -> Unit,
    onEndAtChange: (Long?) -> Unit
) {
    Column {
        Text(
            text = "Tanggal & Waktu",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Mulai
        DateTimePickerField(
            label = "Mulai",
            timestamp = startAt,
            onTimestampChange = onStartAtChange
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Selesai
        DateTimePickerField(
            label = "Selesai",
            timestamp = endAt,
            onTimestampChange = onEndAtChange
        )
    }
}

@Composable
private fun DateTimePickerField(
    label: String,
    timestamp: Long?,
    onTimestampChange: (Long?) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    if (showPicker) {
        DateTimePickerDialog(
            initialTimestamp = timestamp,
            onDismiss = { showPicker = false },
            onConfirm = { selectedTimestamp ->
                onTimestampChange(selectedTimestamp)
                showPicker = false
            }
        )
    }

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable { showPicker = true }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = timestamp?.let { formatDateTime(it) } ?: "Pilih tanggal & waktu",
                    color = if (timestamp != null) MaterialTheme.colorScheme.onSurface
                           else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun DescriptionSection(
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Deskripsi",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text("Tambahkan catatan...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            maxLines = 6,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun TagSection(
    tags: List<String>,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit
) {
    var showTagDialog by remember { mutableStateOf(false) }

    if (showTagDialog) {
        TagInputDialog(
            onDismiss = { showTagDialog = false },
            onConfirm = { tag ->
                onAddTag(tag)
                showTagDialog = false
            }
        )
    }

    Column {
        Text(
            text = "Tag",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Tag input field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable { showTagDialog = true }
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display existing tags as chips
                if (tags.isNotEmpty()) {
                    tags.forEach { tag ->
                        TagChip(
                            label = tag,
                            onRemove = { onRemoveTag(tag) }
                        )
                    }
                }

                // Placeholder or add more text
                Text(
                    text = if (tags.isEmpty()) "Tambah tag..." else "",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun TagChip(
    label: String,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 12.sp
            )
            Text(
                text = "×",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.clickable(onClick = onRemove)
            )
        }
    }
}

@Composable
private fun ReminderSection(
    selectedOffsets: List<Int>,
    onToggleOffset: (Int) -> Unit
) {
    Column {
        Text(
            text = "Pengingat",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReminderChip(
                label = "15 menit",
                selected = selectedOffsets.contains(15),
                onClick = { onToggleOffset(15) }
            )
            ReminderChip(
                label = "30 menit",
                selected = selectedOffsets.contains(30),
                onClick = { onToggleOffset(30) }
            )
            ReminderChip(
                label = "60 menit",
                selected = selectedOffsets.contains(60),
                onClick = { onToggleOffset(60) }
            )
        }
    }
}

@Composable
private fun ReminderChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = label,
                color = if (selected) MaterialTheme.colorScheme.onPrimary
                       else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

private fun formatDateTime(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("d MMM yyyy, HH:mm", Locale("id", "ID"))
    return dateFormat.format(Date(timestamp))
}
