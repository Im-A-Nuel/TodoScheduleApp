package id.app.todoschedule.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Tag Input Dialog
 * Allows user to add custom tags
 */
@Composable
fun TagInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var tagText by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Text(
                    text = "Tambah Tag",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Input Field
                OutlinedTextField(
                    value = tagText,
                    onValueChange = {
                        tagText = it
                        error = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nama Tag") },
                    placeholder = { Text("Contoh: Kuliah, Penting") },
                    singleLine = true,
                    isError = error != null,
                    supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
                )

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val trimmedTag = tagText.trim()
                            when {
                                trimmedTag.isEmpty() -> {
                                    error = "Tag tidak boleh kosong"
                                }
                                trimmedTag.length < 2 -> {
                                    error = "Tag minimal 2 karakter"
                                }
                                trimmedTag.length > 20 -> {
                                    error = "Tag maksimal 20 karakter"
                                }
                                else -> {
                                    onConfirm(trimmedTag)
                                }
                            }
                        }
                    ) {
                        Text("Tambah")
                    }
                }
            }
        }
    }
}
