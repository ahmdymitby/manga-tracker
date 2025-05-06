package uk.dominikdias.manga.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import uk.dominikdias.manga.extensions.toEpochMillis
import uk.dominikdias.manga.extensions.toLocalDate
import uk.dominikdias.manga.interfaces.IAddMangaViewModel
import uk.dominikdias.manga.model.AddMangaFormState
import uk.dominikdias.manga.viewmodel.AddMangaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMangaScreen(
    onPopBackStack: () -> Unit,
    setTopBar: (TopBarContent) -> Unit,
    viewModel: IAddMangaViewModel = koinViewModel<AddMangaViewModel>()
) {
    val formState by viewModel.formState.collectAsState()
    val showOrderDatePicker by viewModel.showOrderDatePicker.collectAsState()
    val showExpectedDatePicker by viewModel.showExpectedDatePicker.collectAsState()

    val orderDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = formState.orderDate.toEpochMillis()
            ?: Clock.System.now().toEpochMilliseconds()
    )
    val expectedDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = formState.expectedPublicationDate.toEpochMillis()
            ?: Clock.System.now().toEpochMilliseconds()
    )

    val updatedOnPopBackStack by rememberUpdatedState(onPopBackStack)

    setTopBar {
        TopAppBar(
            title = { Text(AddManga.title) },
            navigationIcon = {
                IconButton(onClick = updatedOnPopBackStack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
    }

    LaunchedEffect(viewModel.navigationEvent) {
        viewModel.navigationEvent.collectLatest {
            updatedOnPopBackStack()
        }
    }

    if (showOrderDatePicker) {
        DatePickerDialog(
            onDismissRequest = viewModel::dismissShowOrderDatePicker,
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onOrderDateChange(orderDatePickerState.selectedDateMillis.toLocalDate())
                },
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = viewModel::dismissShowOrderDatePicker) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = orderDatePickerState)
        }
    }

    if (showExpectedDatePicker) {
        DatePickerDialog(
            onDismissRequest = viewModel::dismissShowExpectedDatePicker,
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onExpectedPublicationDateChange(expectedDatePickerState.selectedDateMillis.toLocalDate())
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissShowExpectedDatePicker) { Text("Cancel") }
            }
        ) {
            DatePicker(state = expectedDatePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        OutlinedTextField(
            value = formState.title,
            onValueChange = viewModel::onTitleChange,
            label = { Text("Title *") },
            modifier = Modifier.fillMaxWidth(),
            isError = formState.titleError != null,
            singleLine = true
        )
        if (formState.titleError != null) {
            Text(
                formState.titleError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.volume,
            onValueChange = viewModel::onVolumeChange,
            label = { Text("Volume *") },
            modifier = Modifier.fillMaxWidth(),
            isError = formState.volumeError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        if (formState.volumeError != null) {
            Text(
                formState.volumeError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.publisher,
            onValueChange = viewModel::onPublisherChange,
            label = { Text("Publisher *") },
            modifier = Modifier.fillMaxWidth(),
            isError = formState.publisherError != null,
            singleLine = true
        )
        if (formState.publisherError != null) {
            Text(
                formState.publisherError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.store,
            onValueChange = viewModel::onStoreChange,
            label = { Text("Store *") },
            modifier = Modifier.fillMaxWidth(),
            isError = formState.storeError != null,
            singleLine = true
        )
        if (formState.storeError != null) {
            Text(
                formState.storeError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.price,
            onValueChange = viewModel::onPriceChange,
            label = { Text("Price (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            isError = formState.priceError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            leadingIcon = { Text("€") }
        )
        if (formState.priceError != null) {
            Text(
                formState.priceError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(Modifier.height(16.dp))

        DateInputField(
            label = "Order Date *",
            selectedDate = formState.orderDate,
            error = formState.orderDateError,
            onClick = viewModel::showOrderDatePicker
        )
        Spacer(Modifier.height(8.dp))

        DateInputField(
            label = "Expected Publication Date *",
            selectedDate = formState.expectedPublicationDate,
            error = formState.expectedPublicationDateError,
            onClick = viewModel::showExpectedDatePicker
        )
        Spacer(Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = viewModel::saveManga,
                enabled = !formState.isSaving,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                if (formState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text("Save Manga")
            }
        }

        if (formState.saveError != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                formState.saveError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

private class AddMangaScreenDummy() : IAddMangaViewModel {
    override val formState = MutableStateFlow(AddMangaFormState())
    override val showOrderDatePicker = MutableStateFlow(false)
    override val showExpectedDatePicker = MutableStateFlow(false)
    override val navigationEvent = MutableSharedFlow<Unit>()
    override fun onTitleChange(newTitle: String) {
    }

    override fun onVolumeChange(newVolume: String) {
    }

    override fun onPublisherChange(newPublisher: String) {
    }

    override fun onStoreChange(newStore: String) {
    }

    override fun onPriceChange(newPrice: String) {
    }

    override fun showOrderDatePicker() {
    }

    override fun dismissShowOrderDatePicker() {
    }

    override fun showExpectedDatePicker() {
    }

    override fun dismissShowExpectedDatePicker() {
    }

    override fun onOrderDateChange(newDate: LocalDate?) {
    }

    override fun onExpectedPublicationDateChange(newDate: LocalDate?) {
    }

    override fun saveManga() {
    }
}

@Composable
@Preview
private fun AddMangaPreview() {
    AddMangaScreen(
        onPopBackStack = {},
        setTopBar = {},
        viewModel = AddMangaScreenDummy(),
    )
}