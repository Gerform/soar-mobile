package tech.soc.soar.push

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object PushEventBus {

    private val _events = MutableSharedFlow<PushEvent>(
        extraBufferCapacity = 16
    )

    val events = _events.asSharedFlow()

    fun emit(event: PushEvent) {
        _events.tryEmit(event)
    }
}

sealed interface PushEvent {

    data class SpaceShouldRefresh(
        val spaceName: String,
        val scrollToTop: Boolean = true
    ) : PushEvent

    data class ResponsesShouldRefresh(
        val alertId: Long,
        val scrollToTop: Boolean = true
    ) : PushEvent
}