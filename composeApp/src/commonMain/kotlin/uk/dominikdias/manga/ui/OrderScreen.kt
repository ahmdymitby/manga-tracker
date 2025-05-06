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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import uk.dominikdias.manga.viewmodel.OrderedViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OrderedScreen(
    setTopBar: (TopBarContent) -> Unit,
    onMangaClick: (mangaId: Long) -> Unit,
    viewModel: OrderedViewModel = koinViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val orderedManga by viewModel.orderedManga.collectAsState()
    val shippedManga by viewModel.shippedManga.collectAsState()

    setTopBar {
        TopAppBar(title = { Text(Ordered.title) })
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        else if (error != null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(onClick = { viewModel.clearError() }) {
                    Text("Dismiss")
                }
            }
        }
        else {
            if (orderedManga.isEmpty() && shippedManga.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No ordered or shipped manga found.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (shippedManga.isNotEmpty()) {
                        stickyHeader { ListSectionHeader("Shipped") }
                        items(shippedManga, key = { "shipped-${it.id}" }) { manga ->
                            MangaListItem(manga = manga, modifier = Modifier.padding(vertical = 4.dp)) {
                                onMangaClick(manga.id)
                            }
                        }
                    }

                    if (orderedManga.isNotEmpty()) {
                        stickyHeader { ListSectionHeader("Ordered (Not Shipped)") }
                        items(orderedManga, key = { "ordered-${it.id}" }) { manga ->
                            MangaListItem(manga = manga, modifier = Modifier.padding(vertical = 4.dp)) {
                                onMangaClick(manga.id)
                            }
                        }
                    }
                }
            }
        }
    }
}
