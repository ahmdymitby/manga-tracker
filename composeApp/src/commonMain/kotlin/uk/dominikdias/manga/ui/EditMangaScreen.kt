package uk.dominikdias.manga.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.Clock
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import uk.dominikdias.manga.data.MangaStatus
import uk.dominikdias.manga.di.appModule
import uk.dominikdias.manga.di.previewDatabaseModule
import uk.dominikdias.manga.extensions.toEpochMillis
import uk.dominikdias.manga.extensions.toLocalDate
import uk.dominikdias.manga.viewmodel.EditMangaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMangaScreen(
    mangaId: Long,
    onPopBackStack: () -> Unit,
    setTopBar: (TopBarContent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditMangaViewModel = koinViewModel(parameters = { parametersOf(mangaId) })
) {
    val formState by viewModel.formState.collectAsState()
    val showOrderDatePicker by viewModel.showOrderDatePicker.collectAsState()
    val showExpectedDatePicker by viewModel.showExpectedDatePicker.collectAsState()
    val showActualDatePicker by viewModel.showActualDatePicker.collectAsState()
    val showShippingDatePicker by viewModel.showShippingDatePicker.collectAsState()
    val statusMenuExpanded by viewModel.statusMenuExpanded.collectAsState()
    val showDeleteConfirmDialog by viewModel.showDeleteConfirmDialog.collectAsState()

    val updatedOnPopBackStack by rememberUpdatedState(onPopBackStack)

    setTopBar {
        TopAppBar(
            title = { Text(EditManga.title) },
            navigationIcon = {
                IconButton(onClick = updatedOnPopBackStack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { viewModel.setShowDeleteConfirmDialog(true) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete Manga")
                }
            }
        )
    }

    LaunchedEffect(viewModel.navigationEvent) {
        viewModel.navigationEvent.collectLatest {
            updatedOnPopBackStack()
        }
    }

    val orderDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = formState.orderDate.toEpochMillis()
            ?: Clock.System.now().toEpochMilliseconds()
    )
    val expectedDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = formState.expectedPublicationDate.toEpochMillis()
            ?: Clock.System.now().toEpochMilliseconds()
    )
    val actualDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = formState.actualPublicationDate.toEpochMillis()
    )
    val shippingDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = formState.shippingDate.toEpochMillis()
    )

    if (showOrderDatePicker) {
        DatePickerDialog(
            onDismissRequest = { viewModel.showOrderDatePicker(false) },
            confirmButton = { TextButton(onClick = { viewModel.onOrderDateChange(orderDatePickerState.selectedDateMillis.toLocalDate()) }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { viewModel.showOrderDatePicker(false) }) { Text("Cancel") } }
        ) { DatePicker(state = orderDatePickerState) }
    }
    if (showExpectedDatePicker) {
        DatePickerDialog(
            onDismissRequest = { viewModel.showExpectedDatePicker(false) },
            confirmButton = { TextButton(onClick = { viewModel.onExpectedPublicationDateChange(expectedDatePickerState.selectedDateMillis.toLocalDate()) }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { viewModel.showExpectedDatePicker(false) }) { Text("Cancel") } }
        ) { DatePicker(state = expectedDatePickerState) }
    }
    if (showActualDatePicker) {
        DatePickerDialog(
            onDismissRequest = { viewModel.showActualDatePicker(false) },
            confirmButton = { TextButton(onClick = { viewModel.onActualPublicationDateChange(actualDatePickerState.selectedDateMillis.toLocalDate()) }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { viewModel.showActualDatePicker(false) }) { Text("Cancel") } }
        ) { DatePicker(state = actualDatePickerState) }
    }
    if (showShippingDatePicker) {
        DatePickerDialog(
            onDismissRequest = { viewModel.showShippingDatePicker(false) },
            confirmButton = { TextButton(onClick = { viewModel.onShippingDateChange(shippingDatePickerState.selectedDateMillis.toLocalDate()) }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { viewModel.showShippingDatePicker(false) }) { Text("Cancel") } }
        ) { DatePicker(state = shippingDatePickerState) }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowDeleteConfirmDialog(false) },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete '${formState.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.confirmDeleteManga() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    enabled = !formState.isDeleting
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (formState.isDeleting) {
                            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                        }
                        Text("Delete")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowDeleteConfirmDialog(false) }) { Text("Cancel") }
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            formState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            formState.initialLoadError != null -> {
                Text(
                    text = "Error loading manga: ${formState.initialLoadError}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            }
            else -> {
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
                    if (formState.titleError != null) { Text(formState.titleError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
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
                    if (formState.volumeError != null) { Text(formState.volumeError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = formState.publisher,
                        onValueChange = viewModel::onPublisherChange,
                        label = { Text("Publisher *") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = formState.publisherError != null,
                        singleLine = true
                    )
                    if (formState.publisherError != null) { Text(formState.publisherError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = formState.store,
                        onValueChange = viewModel::onStoreChange,
                        label = { Text("Store *") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = formState.storeError != null,
                        singleLine = true
                    )
                    if (formState.storeError != null) { Text(formState.storeError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
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
                    if (formState.priceError != null) { Text(formState.priceError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                    Spacer(Modifier.height(16.dp))

                    ExposedDropdownMenuBox(
                        expanded = statusMenuExpanded,
                        onExpandedChange = { viewModel.setStatusMenuExpanded(!statusMenuExpanded) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = formState.status.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Status") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusMenuExpanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = statusMenuExpanded,
                            onDismissRequest = { viewModel.setStatusMenuExpanded(false) }
                        ) {
                            MangaStatus.entries.forEach { statusOption ->
                                DropdownMenuItem(
                                    text = { Text(statusOption.name) },
                                    onClick = {
                                        viewModel.onStatusChange(statusOption)
                                    }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    DateInputField(label = "Order Date *", selectedDate = formState.orderDate, error = formState.orderDateError, onClick = { viewModel.showOrderDatePicker(true) })
                    Spacer(Modifier.height(8.dp))
                    DateInputField(label = "Expected Publication Date *", selectedDate = formState.expectedPublicationDate, error = formState.expectedPublicationDateError, onClick = { viewModel.showExpectedDatePicker(true) })
                    Spacer(Modifier.height(8.dp))
                    DateInputField(label = "Actual Publication Date", selectedDate = formState.actualPublicationDate, error = null, onClick = { viewModel.showActualDatePicker(true) })
                    Spacer(Modifier.height(8.dp))
                    DateInputField(label = "Shipping Date", selectedDate = formState.shippingDate, error = null, onClick = { viewModel.showShippingDatePicker(true) })
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = viewModel::saveChanges,
                            enabled = !formState.isSaving && !formState.isDeleting,
                        ) {
                            if (formState.isSaving) {
                                CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                                Spacer(Modifier.width(8.dp))
                            }
                            Text("Save Changes")
                        }
                    }

                    if (formState.saveError != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(formState.saveError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                    if (formState.deleteError != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(formState.deleteError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
@Preview
private fun EditMangaScreenPreview() {
    var topBar by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }

    KoinApplication(application = {
        modules(previewDatabaseModule(), appModule())
    }) {
        Scaffold(
            topBar = { topBar?.invoke() }
        ) {
            EditMangaScreen(
                mangaId = 1,
                onPopBackStack = {},
                setTopBar = { topBar = it },
                modifier = Modifier.padding(it)
            )
        }
    }
}
