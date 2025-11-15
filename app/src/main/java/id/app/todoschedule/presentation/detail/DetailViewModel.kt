package id.app.todoschedule.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.app.todoschedule.data.repository.EntryRepository
import id.app.todoschedule.domain.model.Entry
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel untuk Detail Screen
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: EntryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val entryId: Long = savedStateHandle.get<Long>("entryId") ?: 0L

    // Entry detail
    val entry: StateFlow<Entry?> = repository.observeEntryById(entryId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    /**
     * Toggle entry status
     */
    fun toggleStatus() {
        entry.value?.let { currentEntry ->
            viewModelScope.launch {
                repository.toggleStatus(
                    currentEntry.id,
                    currentEntry.status != id.app.todoschedule.core.model.Status.DONE
                )
            }
        }
    }

    /**
     * Delete entry
     */
    fun deleteEntry(onDeleted: () -> Unit) {
        entry.value?.let { currentEntry ->
            viewModelScope.launch {
                repository.deleteEntry(currentEntry)
                onDeleted()
            }
        }
    }
}
