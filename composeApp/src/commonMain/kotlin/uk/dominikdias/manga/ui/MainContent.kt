package uk.dominikdias.manga.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import uk.dominikdias.manga.di.appModule
import uk.dominikdias.manga.di.previewDatabaseModule
import uk.dominikdias.manga.theme.AppTheme
import uk.dominikdias.manga.viewmodel.MainContentViewModel
import uk.dominikdias.manga.viewmodel.TopBarViewModel

typealias TopBarContent = @Composable (() -> Unit)?

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    mainContentViewModel: MainContentViewModel = koinViewModel(),
    topBarViewModel: TopBarViewModel = koinViewModel()
) {
    KoinContext {
        val navController = rememberNavController()
        val showFab by mainContentViewModel.showFab.collectAsState()
        val currentTopBar by topBarViewModel.topBarContent.collectAsState()

        LaunchedEffect(navController) {
            navController.currentBackStackEntryFlow.collect { backStackEntry ->
                mainContentViewModel.updateUiStateForRoute(backStackEntry.destination.route)
            }
        }
        AppTheme {
            Scaffold(
                topBar = {
                    currentTopBar?.invoke()
                },
                bottomBar = {
                    NavigationBar {
                        val navBackStackEntry = navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry.value?.destination

                        bottomNavItems.forEach { screen ->
                            val itemRouteName = screen.route::class.qualifiedName.orEmpty()
                            NavigationBarItem(
                                icon = { Icon(screen.icon, contentDescription = screen.label) },
                                label = { Text(screen.label) },
                                selected = currentDestination?.route?.startsWith(itemRouteName) == true,
                                onClick = {
                                    val routeObject = when (screen) {
                                        is HomeNavItem -> Home
                                        is OrderedNavItem -> Ordered
                                        is ReceivedNavItem -> Received
                                        else -> throw IllegalArgumentException("Invalid route")
                                    }
                                    navController.navigate(routeObject) {
                                        popUpTo(navController.graph.findStartDestination().id)
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                },
                floatingActionButton = {
                    if (showFab) {
                        FloatingActionButton(onClick = {
                            navController.navigate(AddManga)
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Manga")
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Home,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable<Home> {
                        var topAppBar by remember { mutableStateOf<TopBarContent>({}) }
                        HomeScreen(
                            onMangaClick = { mangaId ->
                                navController.navigate(EditManga(mangaId))
                            },
                            setTopBar = { topBarContent ->
                                topBarViewModel.updateTopBar(topBarContent)
                                topAppBar = topBarContent
                            },
                        )
                        topBarViewModel.updateTopBar(topAppBar)
                    }
                    composable<Ordered> {
                        var topAppBar by remember { mutableStateOf<TopBarContent>({}) }
                        OrderedScreen(
                            setTopBar = { topBarContent ->
                                topBarViewModel.updateTopBar(topBarContent)
                                topAppBar = topBarContent
                            },
                            onMangaClick = { mangaId ->
                                navController.navigate(EditManga(mangaId))
                            }
                        )
                        topBarViewModel.updateTopBar(topAppBar)
                    }
                    composable<Received> {
                        var topAppBar by remember { mutableStateOf<TopBarContent>({}) }
                        ReceivedScreen(
                            setTopBar = { topBarContent ->
                                topBarViewModel.updateTopBar(topBarContent)
                                topAppBar = topBarContent
                            },
                            onMangaClick = { mangaId -> navController.navigate(EditManga(mangaId)) }
                        )
                        topBarViewModel.updateTopBar(topAppBar)
                    }
                    composable<AddManga> {
                        var topAppBar by remember { mutableStateOf<TopBarContent>({}) }
                        AddMangaScreen(
                            onPopBackStack = navController::popBackStack,
                            setTopBar = { topBarContent ->
                                topBarViewModel.updateTopBar(topBarContent)
                                topAppBar = topBarContent
                            },
                        )
                        topBarViewModel.updateTopBar(topAppBar)
                    }
                    composable<EditManga> { backStackEntry ->
                        val args = backStackEntry.toRoute<EditManga>()
                        var topAppBar by remember { mutableStateOf<TopBarContent>({}) }
                        EditMangaScreen(
                            mangaId = args.mangaId,
                            onPopBackStack = navController::popBackStack,
                            setTopBar = { topBarContent ->
                                topBarViewModel.updateTopBar(topBarContent)
                                topAppBar = topBarContent
                            },
                        )
                        topBarViewModel.updateTopBar(topAppBar)
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun PreviewMainContent() {
    KoinApplication(
        application = {
            modules(previewDatabaseModule(), appModule())
        }
    ) {
        MainContent()
    }
}