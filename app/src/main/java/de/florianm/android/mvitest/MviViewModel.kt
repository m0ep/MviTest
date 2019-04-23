package de.florianm.android.mvitest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.florianm.android.mvitest.Mvi.stateReducer
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.slf4j.LoggerFactory

@Suppress("UNCHECKED_CAST")
abstract class MviViewModel<StateT : MviState, EventT : MviEvent>(application: Application, initalState: StateT) :
    AndroidViewModel(application) {

    val stateObservable: Observable<StateT>
    val effectObservable: Observable<MviResult.MviEffect>

    private val log = LoggerFactory.getLogger(MviViewModel::class.java)
    private val processorMap: MutableMap<Class<EventT>, MviEventProcessor<EventT>> = mutableMapOf()
    private val eventEmitter = PublishSubject.create<EventT>()

    private lateinit var disposable: Disposable

    init {
        log.debug("init")

        eventEmitter
            .doOnNext { log.debug("Event - {}", it) }
            .processEvents()
            .doOnNext { log.debug("Result - {}", it) }
            .share()
            .also { result ->
                stateObservable = result
                    .ofType(MviResult.MviStateChange::class.java)
                    .map { it as MviResult.MviStateChange<StateT> }
                    .stateReducer(initalState)
                    .replay(1)
                    .autoConnect(1) { disposable = it }

                effectObservable = result
                    .ofType(MviResult.MviEffect::class.java)
            }

    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    internal fun <PEventT : EventT> registerEventProcessor(
        eventType: Class<PEventT>,
        eventProcessor: MviEventProcessor<PEventT>
    ) {
        val baseType = eventType as Class<EventT>
        if (processorMap.containsKey(eventType as Class<EventT>)) {
            throw IllegalArgumentException("Processor for event $eventType already registered")
        }

        processorMap[baseType] = eventProcessor as MviEventProcessor<EventT>
    }

    fun <PEventT : EventT> sendEvent(event: PEventT) {
        eventEmitter.onNext(event)
    }

    private fun Observable<EventT>.processEvents(): Observable<MviResult> {
        return flatMap { processorMap[it.javaClass]?.process(it) ?: Observable.empty<MviResult>() }
    }
}