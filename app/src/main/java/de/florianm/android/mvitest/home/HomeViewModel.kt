package de.florianm.android.mvitest.home

import android.app.Application
import android.util.Log
import de.florianm.android.mvitest.MviEvent
import de.florianm.android.mvitest.MviEventProcessor
import de.florianm.android.mvitest.MviResult
import de.florianm.android.mvitest.MviViewModel
import de.florianm.android.mvitest.home.HomeEvent.PlusOneEvent
import de.florianm.android.mvitest.home.HomeEvent.ResetEvent
import de.florianm.android.mvitest.home.HomeStateChange.*
import io.reactivex.Observable

class HomeViewModel(application: Application)
    : MviViewModel<HomeState, HomeEvent>(application, HomeState()) {

    init {
        Log.d("Home", "init")

        registerEventProcessor(PlusOneEvent::class, object : MviEventProcessor {
            override fun process(event: MviEvent) =
                    Observable.just<MviResult>(IncrementCounterSC)
        })

        registerEventProcessor(ResetEvent::class, object : MviEventProcessor {
            override fun process(event: MviEvent) =
                    Observable.just<MviResult>(ResetSC)
                            .startWith(ToastEffect(application, "Reset"))
        })
    }

    fun onPlusOneClicked() {
        sendEvent(PlusOneEvent)
    }

    fun onResetClicked() {
        sendEvent(ResetEvent)
    }
}
