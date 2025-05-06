package uk.dominikdias.manga.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.dominikdias.manga.ui.TopBarContent

class TopBarViewModel : ViewModel() {
    private val _topBarContent = MutableStateFlow<TopBarContent>(null)
    val topBarContent: StateFlow<TopBarContent> = _topBarContent.asStateFlow()

    fun updateTopBar(content: TopBarContent) {
        _topBarContent.value = content
    }
}