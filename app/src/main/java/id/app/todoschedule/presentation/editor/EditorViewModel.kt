package id.app.todoschedule.presentation.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.app.todoschedule.core.model.EntryType
import id.app.todoschedule.core.model.Priority
import id.app.todoschedule.core.model.Status
import id.app.todoschedule.data.repository.EntryRepository
import id.app.todoschedule.data.worker.ReminderScheduler
import id.app.todoschedule.domain.model.Entry
import id.app.todoschedule.domain.model.Reminder
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel untuk Editor Screen (Create/Edit)
 */
@HiltViewModel
class EditorViewModel @Inject constructor(
    private val repository: EntryRepository,
    private val reminderScheduler: ReminderScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val entryId: Long? = savedStateHandle.get<String>("entryId")?.let {
        if (it == "new") null else it.toLongOrNull()
    }

    // UI State
    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init {
        entryId?.let { id ->
            viewModelScope.launch {
                repository.observeEntryById(id).collect { entry ->
                    entry?.let { updateStateFromEntry(it) }
                }
            }
        }
    }

    private fun updateStateFromEntry(entry: Entry) {
        _uiState.update {
            it.copy(
                title = entry.title,
                description = entry.description ?: "",
                type = entry.type,
                priority = entry.priority,
                startAt = entry.startAt,
                endAt = entry.endAt,
                dueAt = entry.dueAt,
                locationText = entry.locationText ?: "",
                tags = entry.tags,
                reminderOffsets = entry.reminders.mapNotNull { it.offsetMinutes },
                isEditMode = true
            )
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onTypeChange(type: EntryType) {
        _uiState.update { it.copy(type = type) }
    }

    fun onPriorityChange(priority: Priority) {
        _uiState.update { it.copy(priority = priority) }
    }

    fun onStartAtChange(timestamp: Long?) {
        _uiState.update { it.copy(startAt = timestamp) }
    }

    fun onEndAtChange(timestamp: Long?) {
        _uiState.update { it.copy(endAt = timestamp) }
    }

    fun onDueAtChange(timestamp: Long?) {
        _uiState.update { it.copy(dueAt = timestamp) }
    }

    fun onLocationChange(location: String) {
        _uiState.update { it.copy(locationText = location) }
    }

    fun onAddTag(tag: String) {
        _uiState.update { state ->
            if (tag.isNotBlank() && !state.tags.contains(tag)) {
                state.copy(tags = state.tags + tag)
            } else {
                state
            }
        }
    }

    fun onRemoveTag(tag: String) {
        _uiState.update { state ->
            state.copy(tags = state.tags - tag)
        }
    }

    fun onToggleReminderOffset(offset: Int) {
        _uiState.update { state ->
            val newOffsets = if (state.reminderOffsets.contains(offset)) {
                state.reminderOffsets - offset
            } else {
                state.reminderOffsets + offset
            }
            state.copy(reminderOffsets = newOffsets)
        }
    }

    fun saveEntry(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.title.isBlank()) {
            _uiState.update { it.copy(error = "Judul tidak boleh kosong") }
            return
        }

        viewModelScope.launch {
            try {
                val entry = Entry(
                    id = entryId ?: 0,
                    type = state.type,
                    title = state.title,
                    description = state.description.ifBlank { null },
                    priority = state.priority,
                    status = Status.OPEN,
                    startAt = state.startAt,
                    endAt = state.endAt,
                    dueAt = state.dueAt,
                    locationText = state.locationText.ifBlank { null },
                    tags = state.tags,
                    reminders = createReminders(state),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                val savedEntry = repository.upsertEntry(entry)

                // Schedule reminders menggunakan WorkManager
                if (savedEntry.reminders.isNotEmpty()) {
                    reminderScheduler.scheduleReminders(savedEntry)
                }

                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Terjadi kesalahan") }
            }
        }
    }

    private fun createReminders(state: EditorUiState): List<Reminder> {
        val baseTime = state.dueAt ?: state.startAt ?: return emptyList()

        return state.reminderOffsets.map { offset ->
            Reminder(
                id = 0,
                entryId = entryId ?: 0,
                remindAt = baseTime - (offset * 60 * 1000), // Convert minutes to millis
                offsetMinutes = offset
            )
        }
    }

    fun deleteEntry(onDeleted: () -> Unit) {
        entryId?.let { id ->
            viewModelScope.launch {
                try {
                    // Cancel reminders sebelum delete entry
                    reminderScheduler.cancelReminders(id)
                    repository.deleteEntry(id)
                    onDeleted()
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = e.message ?: "Gagal menghapus entry") }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * UI State untuk Editor
 */
data class EditorUiState(
    val title: String = "",
    val description: String = "",
    val type: EntryType = EntryType.TODO,
    val priority: Priority = Priority.MED,
    val startAt: Long? = null,
    val endAt: Long? = null,
    val dueAt: Long? = null,
    val locationText: String = "",
    val tags: List<String> = emptyList(),
    val reminderOffsets: List<Int> = emptyList(), // in minutes: 15, 30, 60
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
