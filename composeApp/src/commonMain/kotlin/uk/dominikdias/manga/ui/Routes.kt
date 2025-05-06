package uk.dominikdias.manga.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
object Home {
    val label = "Upcoming"
    val title = "Manga Tracker"
    val icon = Icons.Filled.Home
}

@Serializable
object Ordered {
    val label = "Ordered"
    val title = "Ordered Manga"
    val icon = Icons.Filled.Book
}

@Serializable
object AddManga {
    val title = "Add New Manga"
    val label = "Add Manga"
}

@Serializable
object Received {
    val title = "Received Manga"
    val label = "Received"
    val icon = Icons.Filled.CheckCircle
}

@Serializable
data class EditManga(val mangaId: Long) {
    val label = "Edit Manga"
    companion object {
        const val title = "Edit Manga"
    }
}

interface BottomNavItem {
    val label: String
    val icon: ImageVector
    val route: Any
}

object HomeNavItem : BottomNavItem {
    override val label = Home.label
    override val icon = Home.icon
    override val route = Home
}
object OrderedNavItem : BottomNavItem {
    override val label = Ordered.label
    override val icon = Ordered.icon
    override val route = Ordered
}
object ReceivedNavItem : BottomNavItem {
    override val label = Received.label
    override val icon = Received.icon
    override val route = Received
}


val bottomNavItems = listOf(
    HomeNavItem,
    OrderedNavItem,
    ReceivedNavItem,
)