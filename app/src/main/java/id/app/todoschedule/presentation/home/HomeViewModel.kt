package id.app.todoschedule.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.app.todoschedule.core.model.EntryType
import id.app.todoschedule.core.model.Priority
import id.app.todoschedule.core.model.Status
import id.app.todoschedule.data.repository.EntryRepository
import id.app.todoschedule.domain.model.Entry
import id.app.todoschedule.presentation.filter.FilterState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel untuk Home Screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: EntryRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Selected filter type (quick filter)
    private val _selectedType = MutableStateFlow<EntryType?>(null)
    val selectedType: StateFlow<EntryType?> = _selectedType.asStateFlow()

    // Advanced filter state
    private val _filterState = MutableStateFlow<FilterState?>(null)
    val filterState: StateFlow<FilterState?> = _filterState.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Entries based on filter
    val entries: StateFlow<List<Entry>> = combine(
        _selectedType,
        _filterState,
        _searchQuery
    ) { type, filterState, query ->
        Triple(type, filterState, query)
    }.flatMapLatest { (type, filterState, query) ->
        if (query.isNotBlank()) {
            // Search mode
            repository.searchEntries(query)
        } else if (filterState != null) {
            // Advanced filter mode
            repository.observeEntries(
                type = filterState.type,
                status = filterState.status,
                priority = filterState.priority
            ).map { entries ->
                // Apply date range filter in memory
                entries.filter { entry ->
                    val dueTimestamp = entry.dueAt ?: entry.startAt
                    if (dueTimestamp == null) return@filter true

                    val afterStart = filterState.startDate?.let { dueTimestamp >= it } ?: true
                    val beforeEnd = filterState.endDate?.let { dueTimestamp <= it } ?: true

                    afterStart && beforeEnd
                }
            }
        } else {
            // Quick filter mode (type only)
            repository.observeEntries(
                type = type,
                status = Status.OPEN // Default hanya tampilkan yang belum selesai
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    /**
     * Set filter by type (quick filter)
     */
    fun setFilterType(type: EntryType?) {
        _selectedType.value = type
        // Clear advanced filter when using quick filter
        _filterState.value = null
    }

    /**
     * Apply advanced filter
     */
    fun applyAdvancedFilter(filterState: FilterState) {
        _filterState.value = filterState
        // Clear quick filter when using advanced filter
        _selectedType.value = null
    }

    /**
     * Clear all filters
     */
    fun clearFilters() {
        _selectedType.value = null
        _filterState.value = null
    }

    /**
     * Set search query
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Toggle entry status (done/open)
     */
    fun toggleEntryStatus(entryId: Long, done: Boolean) {
        viewModelScope.launch {
            repository.toggleStatus(entryId, done)
        }
    }

    /**
     * Delete entry
     */
    fun deleteEntry(entry: Entry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }
}

/**
 * UI State untuk Home Screen
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)
