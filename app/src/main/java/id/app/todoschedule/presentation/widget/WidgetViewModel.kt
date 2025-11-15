package id.app.todoschedule.presentation.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.app.todoschedule.core.model.Status
import id.app.todoschedule.data.repository.EntryRepository
import id.app.todoschedule.domain.model.Entry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel untuk widget logic
 * Handle upcoming entries, mark done, dsb
 */
@HiltViewModel
class WidgetViewModel @Inject constructor(
    private val repository: EntryRepository
) : ViewModel() {

    /**
     * Observe upcoming entries untuk widget
     * Hanya menampilkan entries dengan status OPEN atau IN_PROGRESS
     */
    val upcomingEntries: StateFlow<List<Entry>> = repository.observeUpcoming(limit = 10)
        .map { entries ->
            entries.filter { it.status != Status.DONE }
                .take(5) // Limit to 5 items untuk widget
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    /**
     * Mark entry as done
     */
    fun markEntryAsDone(entryId: Long) {
        viewModelScope.launch {
            try {
                val entry = repository.getEntryById(entryId)
                if (entry != null) {
                    repository.upsertEntry(entry.copy(status = Status.DONE))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Get single entry untuk widget click
     */
    fun getEntryById(entryId: Long): Flow<Entry?> {
        return repository.observeEntryById(entryId)
    }
}
