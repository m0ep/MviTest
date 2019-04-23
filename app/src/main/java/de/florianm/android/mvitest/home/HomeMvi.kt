package de.florianm.android.mvitest.home

import android.content.Context
import android.widget.Toast
import de.florianm.android.mvitest.MviEvent
import de.florianm.android.mvitest.MviResult
import de.florianm.android.mvitest.MviResult.MviEffect
import de.florianm.android.mvitest.MviState

sealed class HomeEvent : MviEvent {
    object PlusOneEvent : HomeEvent()

    object ResetEvent : HomeEvent()
}

data class HomeState(
    val counter: Int = 0
) : MviState

sealed class HomeStateChange : MviResult.MviStateChange<HomeState>() {
    object IncrementCounterSC : HomeStateChange() {
        override fun applyChange(state: HomeState) = HomeState(state.counter + 1)

    }

    object ResetSC : HomeStateChange() {
        override fun applyChange(state: HomeState) = HomeState(0)
    }

}

data class ToastEffect(
    private val context: Context,
    private val message: String
) : MviEffect() {

    fun show() {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}
