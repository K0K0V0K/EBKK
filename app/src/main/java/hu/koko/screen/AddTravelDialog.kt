package hu.koko.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import hu.koko.model.Travel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import hu.koko.model.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AddTravelDialog(
    onAdd: (Travel) -> Unit,
    onCancel: () -> Unit,
    user: User
) {
    var originCity by remember { mutableStateOf("") }
    var destinationCity by remember { mutableStateOf("") }
    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(null) }

    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }

    if (showDatePicker.value) {
        val now = LocalDateTime.now()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val current = selectedDateTime ?: LocalDateTime.now()
                selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, current.hour, current.minute)
                showDatePicker.value = false
                showTimePicker.value = true
            },
            now.year, now.monthValue - 1, now.dayOfMonth
        ).show()
    }

    if (showTimePicker.value) {
        val current = selectedDateTime ?: LocalDateTime.now()
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                selectedDateTime = selectedDateTime?.withHour(hourOfDay)?.withMinute(minute)
                    ?: LocalDateTime.now().withHour(hourOfDay).withMinute(minute)
                showTimePicker.value = false
            },
            current.hour,
            current.minute,
            true
        ).show()
    }

    Dialog(onDismissRequest = onCancel) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Title text with modern typography
                Text(
                    "Új utazás hozzáadása",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(12.dp))

                // Origin City TextField with custom shape and padding
                TextField(
                    value = originCity,
                    onValueChange = { originCity = it },
                    label = { Text("Honnan") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                // Destination City TextField with custom shape
                TextField(
                    value = destinationCity,
                    onValueChange = { destinationCity = it },
                    label = { Text("Hova") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                // Button to select date and time, rounded corners
                OutlinedButton(
                    onClick = { showDatePicker.value = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = selectedDateTime?.format(formatter) ?: "Időpont kiválasztása",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Row to align buttons at the bottom
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Cancel Button with modern design
                    TextButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Mégse")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Add Button with enhanced design
                    Button(
                        onClick = {
                            if (originCity.isNotBlank() && destinationCity.isNotBlank() && selectedDateTime != null) {
                                onAdd(
                                    Travel(
                                        null,
                                        originCity,
                                        destinationCity,
                                        user.driverName,
                                        selectedDateTime!!,
                                        System.currentTimeMillis()
                                    )
                                )
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Hozzáadás")
                    }
                }
            }
        }
    }
}
