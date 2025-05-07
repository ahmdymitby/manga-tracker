package uk.dominikdias.manga.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import uk.dominikdias.manga.di.appModule
import uk.dominikdias.manga.di.previewDatabaseModule
import uk.dominikdias.manga.theme.AppTheme
import uk.dominikdias.manga.viewmodel.ReceivedViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReceivedScreen(
    setTopBar: (TopBarContent) -> Unit,
    onMangaClick: (mangaId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReceivedViewModel = koinViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val receivedManga by viewModel.receivedManga.collectAsState()

    setTopBar {
        TopAppBar(title = { Text(Received.title) })
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            error != null -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
                    Button(onClick = { viewModel.clearError() }) { Text("Dismiss") }
                }
            }
            receivedManga.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No received manga found.")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(receivedManga, key = { "received-${it.id}" }) { manga ->
                        MangaListItem(manga = manga) {
                            onMangaClick(manga.id)
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun PreviewReceivedScreen() {
    var topBar by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }

    KoinApplication(
        application = {
            modules(previewDatabaseModule(), appModule())
        }
    ) {
        AppTheme {
            Scaffold(
                topBar = { topBar?.invoke() }
            ) {
                ReceivedScreen(
                    setTopBar = { topBar = it },
                    onMangaClick = {},
                    modifier = Modifier.padding(it),
                )
            }
        }
    }
}
