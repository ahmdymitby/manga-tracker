package uk.dominikdias.manga.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.ui.tooling.preview.Preview
import uk.dominikdias.manga.theme.AppTheme

@Composable
fun DateInputField(
    label: String,
    selectedDate: LocalDate?,
    error: String?,
    onClick: () -> Unit
) {
    OutlinedTextField(
        value = selectedDate?.toString() ?: "",
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        trailingIcon = {
            Icon(
                Icons.Filled.CalendarMonth,
                contentDescription = "Select Date",
                modifier = Modifier.clickable(onClick = onClick)
            )
        },
        isError = error != null
    )
    if (error != null) {
        Text(
            error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
@Preview
private fun DateInputFieldPreview() {
    AppTheme {
        Surface {
            DateInputField(
                label = "Date",
                selectedDate = LocalDate(2023, 1, 1),
                error = null,
                onClick = {},
            )
        }
    }
}