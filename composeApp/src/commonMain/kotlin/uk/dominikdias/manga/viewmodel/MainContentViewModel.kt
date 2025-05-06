package uk.dominikdias.manga.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.dominikdias.manga.ui.Home
import uk.dominikdias.manga.ui.Ordered
import uk.dominikdias.manga.ui.Received

class MainContentViewModel : ViewModel() {
    private val _showFab = MutableStateFlow(true)
    val showFab: StateFlow<Boolean> = _showFab.asStateFlow()

    fun updateUiStateForRoute(route: String?) {
        _showFab.value = when (route) {
            Home::class.qualifiedName,
            Ordered::class.qualifiedName,
            Received::class.qualifiedName -> true
            else -> false
        }
    }
}