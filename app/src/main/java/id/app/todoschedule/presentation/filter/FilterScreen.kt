package id.app.todoschedule.presentation.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
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
import id.app.todoschedule.core.model.Status
import id.app.todoschedule.presentation.components.DatePickerDialog
import id.app.todoschedule.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.*

/**
 * Filter Screen - Advanced filtering
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    onNavigateBack: () -> Unit,
    onApplyFilter: (FilterState) -> Unit,
    viewModel: FilterViewModel = hiltViewModel()
) {
    val filterState by viewModel.filterState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filter", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Reset button
                    if (viewModel.hasActiveFilters()) {
                        TextButton(onClick = { viewModel.resetFilters() }) {
                            Text("Reset")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            // Apply button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = {
                        onApplyFilter(filterState)
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Terapkan Filter", fontSize = 16.sp)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Entry Type Filter
            FilterSection(title = "Tipe") {
                TypeFilterOptions(
                    selectedType = filterState.type,
                    onTypeSelected = { viewModel.setType(it) }
                )
            }

            Divider()

            // Status Filter
            FilterSection(title = "Status") {
                StatusFilterOptions(
                    selectedStatus = filterState.status,
                    onStatusSelected = { viewModel.setStatus(it) }
                )
            }

            Divider()

            // Priority Filter
            FilterSection(title = "Prioritas") {
                PriorityFilterOptions(
                    selectedPriority = filterState.priority,
                    onPrioritySelected = { viewModel.setPriority(it) }
                )
            }

            Divider()

            // Date Range Filter
            FilterSection(title = "Rentang Waktu") {
                DateRangeFilter(
                    startDate = filterState.startDate,
                    endDate = filterState.endDate,
                    onStartDateSelected = { viewModel.setStartDate(it) },
                    onEndDateSelected = { viewModel.setEndDate(it) }
                )
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        content()
    }
}

@Composable
private fun TypeFilterOptions(
    selectedType: EntryType?,
    onTypeSelected: (EntryType?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterOption(
            label = "Semua Tipe",
            isSelected = selectedType == null,
            onClick = { onTypeSelected(null) }
        )
        FilterOption(
            label = "To-Do",
            isSelected = selectedType == EntryType.TODO,
            onClick = { onTypeSelected(EntryType.TODO) }
        )
        FilterOption(
            label = "Tugas",
            isSelected = selectedType == EntryType.TASK,
            onClick = { onTypeSelected(EntryType.TASK) }
        )
        FilterOption(
            label = "Shift",
            isSelected = selectedType == EntryType.SHIFT,
            onClick = { onTypeSelected(EntryType.SHIFT) }
        )
        FilterOption(
            label = "Event",
            isSelected = selectedType == EntryType.EVENT,
            onClick = { onTypeSelected(EntryType.EVENT) }
        )
    }
}

@Composable
private fun StatusFilterOptions(
    selectedStatus: Status?,
    onStatusSelected: (Status?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterOption(
            label = "Semua Status",
            isSelected = selectedStatus == null,
            onClick = { onStatusSelected(null) }
        )
        FilterOption(
            label = "Belum Selesai",
            isSelected = selectedStatus == Status.OPEN,
            onClick = { onStatusSelected(Status.OPEN) }
        )
        FilterOption(
            label = "Selesai",
            isSelected = selectedStatus == Status.DONE,
            onClick = { onStatusSelected(Status.DONE) }
        )
    }
}

@Composable
private fun PriorityFilterOptions(
    selectedPriority: Priority?,
    onPrioritySelected: (Priority?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterOption(
            label = "Semua Prioritas",
            isSelected = selectedPriority == null,
            onClick = { onPrioritySelected(null) }
        )
        FilterOption(
            label = "Tinggi",
            isSelected = selectedPriority == Priority.HIGH,
            onClick = { onPrioritySelected(Priority.HIGH) }
        )
        FilterOption(
            label = "Sedang",
            isSelected = selectedPriority == Priority.MED,
            onClick = { onPrioritySelected(Priority.MED) }
        )
        FilterOption(
            label = "Rendah",
            isSelected = selectedPriority == Priority.LOW,
            onClick = { onPrioritySelected(Priority.LOW) }
        )
    }
}

@Composable
private fun DateRangeFilter(
    startDate: Long?,
    endDate: Long?,
    onStartDateSelected: (Long?) -> Unit,
    onEndDateSelected: (Long?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Start Date
        DatePickerField(
            label = "Dari Tanggal",
            date = startDate,
            onDateSelected = onStartDateSelected
        )

        // End Date
        DatePickerField(
            label = "Sampai Tanggal",
            date = endDate,
            onDateSelected = onEndDateSelected
        )

        // Quick date range options
        Text(
            text = "Pilihan Cepat:",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickDateChip(
                label = "Hari ini",
                onClick = {
                    val today = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val endOfDay = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        set(Calendar.MILLISECOND, 999)
                    }
                    onStartDateSelected(today.timeInMillis)
                    onEndDateSelected(endOfDay.timeInMillis)
                }
            )

            QuickDateChip(
                label = "Minggu ini",
                onClick = {
                    val startOfWeek = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val endOfWeek = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        set(Calendar.MILLISECOND, 999)
                    }
                    onStartDateSelected(startOfWeek.timeInMillis)
                    onEndDateSelected(endOfWeek.timeInMillis)
                }
            )

            QuickDateChip(
                label = "Bulan ini",
                onClick = {
                    val startOfMonth = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val endOfMonth = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        set(Calendar.MILLISECOND, 999)
                    }
                    onStartDateSelected(startOfMonth.timeInMillis)
                    onEndDateSelected(endOfMonth.timeInMillis)
                }
            )
        }

        // Clear dates button
        if (startDate != null || endDate != null) {
            TextButton(
                onClick = {
                    onStartDateSelected(null)
                    onEndDateSelected(null)
                }
            ) {
                Text("Hapus Rentang Waktu")
            }
        }
    }
}

@Composable
private fun DatePickerField(
    label: String,
    date: Long?,
    onDateSelected: (Long?) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    if (showPicker) {
        DatePickerDialog(
            initialTimestamp = date,
            onDismiss = { showPicker = false },
            onConfirm = { selectedDate ->
                onDateSelected(selectedDate)
                showPicker = false
            }
        )
    }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPicker = true }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = date?.let { formatDate(it) } ?: "Pilih tanggal",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (date != null) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        TextSecondary
                    }
                )
            }

            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun QuickDateChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun FilterOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
    return dateFormat.format(Date(timestamp))
}
