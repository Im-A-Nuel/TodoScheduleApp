package id.app.todoschedule.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.app.todoschedule.core.model.Status
import id.app.todoschedule.data.repository.EntryRepository
import id.app.todoschedule.domain.model.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel untuk Search Screen
 * Menangani search logic dengan debounce
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: EntryRepository
) : ViewModel() {

    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Search results with debounce
    val searchResults: StateFlow<List<Entry>> = _searchQuery
        .debounce(300) // 300ms debounce untuk mengurangi database queries
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                // Return empty list jika query kosong
                flowOf(emptyList())
            } else {
                // Search dari repository
                repository.searchEntries(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    /**
     * Set search query
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Toggle entry status dari search results
     */
    fun toggleEntryStatus(entryId: Long) {
        viewModelScope.launch {
            try {
                val entry = repository.getEntryById(entryId) ?: return@launch
                val newStatus = if (entry.status == Status.OPEN) Status.DONE else Status.OPEN
                repository.upsertEntry(entry.copy(status = newStatus))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Clear search
     */
    fun clearSearch() {
        _searchQuery.value = ""
    }
}
