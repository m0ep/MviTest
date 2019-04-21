package de.florianm.android.mvitest

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import de.florianm.android.mvitest.Mvi.stateReducer
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
abstract class MviViewModel<StateT : Any, EventT : MviEvent>(application: Application, initalState: StateT) :
    AndroidViewModel(application) {

    val stateObservable: Observable<StateT>
    val effectObservable: Observable<MviResult.MviEffect>

    private val log = LoggerFactory.getLogger(MviViewModel::class.java)
    private val processorMap: MutableMap<KClass<EventT>, MviEventProcessor> = mutableMapOf()
    private val eventEmitter = PublishSubject.create<MviEvent>()

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
        eventType: KClass<PEventT>,
        eventProcessor: MviEventProcessor
    ) {
        if (processorMap.containsKey(eventType as KClass<EventT>)) {
            throw IllegalArgumentException("Processor for event $eventType already registered")
        }

        processorMap[eventType] = eventProcessor
    }

    fun sendEvent(event: EventT) {
        eventEmitter.onNext(event)
    }

    private fun Observable<MviEvent>.processEvents(): Observable<MviResult> {
        return flatMap { processorMap[it::class]?.process(it) ?: Observable.empty() }
    }
}