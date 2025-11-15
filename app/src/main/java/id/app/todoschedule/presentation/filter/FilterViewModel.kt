package id.app.todoschedule.presentation.filter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.app.todoschedule.core.model.EntryType
import id.app.todoschedule.core.model.Priority
import id.app.todoschedule.core.model.Status
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel untuk Filter Screen
 */
@HiltViewModel
class FilterViewModel @Inject constructor() : ViewModel() {

    // Filter state
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    /**
     * Set filter by type
     */
    fun setType(type: EntryType?) {
        _filterState.value = _filterState.value.copy(type = type)
    }

    /**
     * Set filter by status
     */
    fun setStatus(status: Status?) {
        _filterState.value = _filterState.value.copy(status = status)
    }

    /**
     * Set filter by priority
     */
    fun setPriority(priority: Priority?) {
        _filterState.value = _filterState.value.copy(priority = priority)
    }

    /**
     * Set date range filter
     */
    fun setDateRange(startDate: Long?, endDate: Long?) {
        _filterState.value = _filterState.value.copy(
            startDate = startDate,
            endDate = endDate
        )
    }

    /**
     * Set start date
     */
    fun setStartDate(date: Long?) {
        _filterState.value = _filterState.value.copy(startDate = date)
    }

    /**
     * Set end date
     */
    fun setEndDate(date: Long?) {
        _filterState.value = _filterState.value.copy(endDate = date)
    }

    /**
     * Reset all filters
     */
    fun resetFilters() {
        _filterState.value = FilterState()
    }

    /**
     * Check if any filter is active
     */
    fun hasActiveFilters(): Boolean {
        val state = _filterState.value
        return state.type != null ||
                state.status != null ||
                state.priority != null ||
                state.startDate != null ||
                state.endDate != null
    }
}

/**
 * Filter state data class
 */
data class FilterState(
    val type: EntryType? = null,
    val status: Status? = null,
    val priority: Priority? = null,
    val startDate: Long? = null,
    val endDate: Long? = null
)
