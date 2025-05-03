package hu.koko.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import hu.koko.model.Travel
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
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 8.dp,
            shadowElevation = 12.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Új utazás hozzáadása",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(16.dp))

                TextField(
                    value = originCity,
                    onValueChange = { originCity = it },
                    label = { Text("Honnan") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                TextField(
                    value = destinationCity,
                    onValueChange = { destinationCity = it },
                    label = { Text("Hova") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { showDatePicker.value = true },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = selectedDateTime?.format(formatter) ?: "Időpont kiválasztása",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Mégse")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

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
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Hozzáadás")
                    }
                }
            }
        }
    }
}
