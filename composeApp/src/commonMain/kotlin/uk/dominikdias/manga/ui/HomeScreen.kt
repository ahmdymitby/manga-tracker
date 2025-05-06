package uk.dominikdias.manga.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import uk.dominikdias.manga.data.Manga
import uk.dominikdias.manga.viewmodel.HomeViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onMangaClick: (mangaId: Long) -> Unit,
    setTopBar: (TopBarContent) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val upcomingManga by viewModel.upcomingManga.collectAsState()
    val potentiallyDelayedManga by viewModel.potentiallyDelayedManga.collectAsState()

    setTopBar {
        TopAppBar(title = { Text(Home.title) })
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    Text(
                        "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Button(onClick = { viewModel.clearError() }) {
                        Text("Dismiss")
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (potentiallyDelayedManga.isNotEmpty()) {
                        stickyHeader {
                            ListSectionHeader("Potentially Delayed")
                        }
                        items(potentiallyDelayedManga, key = { it.id }) { manga ->
                            MangaListItem(manga = manga, modifier = Modifier.padding(vertical = 4.dp)) {
                                onMangaClick(manga.id)
                            }
                        }
                    }

                    if (upcomingManga.isNotEmpty()) {
                        stickyHeader {
                            ListSectionHeader("Upcoming Releases")
                        }
                        items(upcomingManga, key = { it.id }) { manga ->
                            MangaListItem(manga = manga, modifier = Modifier.padding(vertical = 4.dp)) {
                                onMangaClick(manga.id)
                            }
                        }
                    }

                    if (potentiallyDelayedManga.isEmpty() && upcomingManga.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxSize()
                                    .padding(top = 150.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No upcoming or delayed manga found.")
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable for displaying a section header, often used with stickyHeader.
 */
@Composable
fun ListSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f))
            .padding(vertical = 8.dp, horizontal = 16.dp)
    )
}


/**
 * Composable for displaying a single Manga item in a list.
 * Reusable component for list items.
 * Uses correct Material 3 parameter names.
 *
 * @param manga The Manga data object to display.
 * @param modifier Modifier to be applied to the ListItem.
 * @param onClick Lambda function to execute when the item is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaListItem(
    manga: Manga,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text("${manga.title} Vol. ${manga.volume}", fontWeight = FontWeight.SemiBold) },
        supportingContent = {
            Column {
                Text("Publisher: ${manga.publisher}", maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Expected: ${manga.expectedPublicationDate}", fontSize = 12.sp)
                Text("Status: ${manga.status.name}", fontSize = 12.sp)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
}
