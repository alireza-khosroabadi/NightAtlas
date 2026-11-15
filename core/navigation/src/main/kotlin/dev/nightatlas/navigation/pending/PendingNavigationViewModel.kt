package dev.nightatlas.navigation.pending

import androidx.lifecycle.ViewModel
import dev.nightatlas.navigation.logging.NavigationLogger
import dev.nightatlas.navigation.model.ActionEntity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

open class PendingNavigationViewModel : ViewModel() {

    private val pendingRequests = Channel<ActionEntity>(Channel.BUFFERED)
    val pendingNavigation = pendingRequests.receiveAsFlow()

    fun setPendingAction(action: ActionEntity) {
        if (pendingRequests.trySend(action).isFailure) {
            NavigationLogger.w(TAG, "Unable to enqueue pending navigation request")
        }
    }

    override fun onCleared() {
        pendingRequests.close()
        super.onCleared()
    }

    private companion object {
        const val TAG = "PendingNavigationVM"
    }
}
