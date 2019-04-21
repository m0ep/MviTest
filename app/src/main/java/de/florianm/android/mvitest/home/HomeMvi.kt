package de.florianm.android.mvitest.home

import android.content.Context
import android.widget.Toast
import de.florianm.android.mvitest.MviEvent
import de.florianm.android.mvitest.MviResult

data class HomeState(
    val counter: Int = 0
)

class ToastEffect(
    private val context: Context,
    private val message: String
) : MviResult.MviEffect() {
    override fun apply() {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

sealed class HomeEvent : MviEvent {
    object PlusOneEvent : HomeEvent()

    object ResetEvent : HomeEvent()
}

sealed class HomeStateChange : MviResult.MviStateChange<HomeState>() {

    object IncrementCounterSC : HomeStateChange() {
        override fun applyChange(state: HomeState) = HomeState(state.counter + 1)
    }

    object ResetSC : HomeStateChange() {
        override fun applyChange(state: HomeState) = HomeState(0)
    }
}