package id.app.todoschedule.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
import id.app.todoschedule.core.model.Status
import id.app.todoschedule.presentation.components.EntryCard
import id.app.todoschedule.ui.theme.TextSecondary
import kotlinx.coroutines.launch

/**
 * Home Screen - Halaman utama menampilkan list entries
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToEditor: (Long?) -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToFilter: () -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val entries by viewModel.entries.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TodoSchedule",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                    IconButton(onClick = onNavigateToFilter) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Filter"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToEditor(null) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah"
                    )
                    Text(text = "Tambah")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab Filter
            TypeFilterTabs(
                selectedType = selectedType,
                onTypeSelected = { viewModel.setFilterType(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Entry List
            if (entries.isEmpty()) {
                EmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = entries,
                        key = { it.id }
                    ) { entry ->
                        EntryCard(
                            entry = entry,
                            onCardClick = { onNavigateToDetail(entry.id) },
                            onToggleStatus = {
                                val newStatus = entry.status != Status.DONE
                                viewModel.toggleEntryStatus(entry.id, newStatus)

                                // Show snackbar with undo
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = if (newStatus) "Tugas ditandai selesai" else "Tugas dibuka kembali",
                                        actionLabel = "URUNGKAN",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        // Undo the action
                                        viewModel.toggleEntryStatus(entry.id, !newStatus)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tab filter by type
 */
@Composable
private fun TypeFilterTabs(
    selectedType: EntryType?,
    onTypeSelected: (EntryType?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // All
        item {
            FilterChip(
                label = "Semua",
                selected = selectedType == null,
                onClick = { onTypeSelected(null) }
            )
        }

        // Entry Types
        item {
            FilterChip(
                label = "To-Do",
                selected = selectedType == EntryType.TODO,
                onClick = { onTypeSelected(EntryType.TODO) }
            )
        }

        item {
            FilterChip(
                label = "Tugas",
                selected = selectedType == EntryType.TASK,
                onClick = { onTypeSelected(EntryType.TASK) }
            )
        }

        item {
            FilterChip(
                label = "Shift",
                selected = selectedType == EntryType.SHIFT,
                onClick = { onTypeSelected(EntryType.SHIFT) }
            )
        }

        item {
            FilterChip(
                label = "Event",
                selected = selectedType == EntryType.EVENT,
                onClick = { onTypeSelected(EntryType.EVENT) }
            )
        }
    }
}

/**
 * Filter chip component
 */
@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else TextSecondary,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

/**
 * Empty state
 */
@Composable
private fun EmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon placeholder (simplified)
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Belum ada tugas.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tekan '+' untuk menambahkan.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}
