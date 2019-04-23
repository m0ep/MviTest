package de.florianm.android.mvitest.home

import android.app.Application
import de.florianm.android.mvitest.MviEventProcessor
import de.florianm.android.mvitest.MviResult
import de.florianm.android.mvitest.MviViewModel
import de.florianm.android.mvitest.home.HomeEvent.PlusOneEvent
import de.florianm.android.mvitest.home.HomeEvent.ResetEvent
import de.florianm.android.mvitest.home.HomeStateChange.IncrementCounterSC
import de.florianm.android.mvitest.home.HomeStateChange.ResetSC
import io.reactivex.Observable

class HomeViewModel(application: Application)
    : MviViewModel<HomeState, HomeEvent>(application, HomeState()) {

    init {
        registerEventProcessor(PlusOneEvent::class.java, object : MviEventProcessor<PlusOneEvent> {
            override fun process(event: PlusOneEvent) =
                    Observable.just<MviResult>(IncrementCounterSC)
        })

        registerEventProcessor(ResetEvent::class.java, object : MviEventProcessor<ResetEvent> {
            override fun process(event: ResetEvent) =
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
