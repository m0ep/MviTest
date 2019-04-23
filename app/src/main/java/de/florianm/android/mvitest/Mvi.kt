package de.florianm.android.mvitest

import io.reactivex.Observable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface MviState

/**
 * Interface that all event for MVI must implement.
 */
interface MviEvent


@FunctionalInterface
interface MviEventProcessor<EventT : MviEvent> {
    fun process(event: EventT): Observable<MviResult>
}

/**
 * Base class for all MVI results.
 */
sealed class MviResult {
    /**
     * Base class to apply changes to a view state.
     * This is used in MVI for simpler state reducers.
     *
     * @param StateT Type of the state that should be changed.
     */
    abstract class MviStateChange<StateT : MviState> : MviResult() {

        /**
         * Apply the change to the supplied state.
         *
         * @param state The state to change
         *
         * @return The changed state.
         */
        abstract fun applyChange(state: StateT): StateT
    }

    /**
     * Base class for result who don't affect the state.
     * E.g. toast messages or navigation instructions.
     */
    abstract class MviEffect : MviResult()
}

object Mvi {
    private val LOG: Logger = LoggerFactory.getLogger(Mvi::class.java)

    /**
     * Extension function of a simple MVI state reducer.
     *
     *
     * @param initialState The initial state for the reducer.
     * @param logger Optional SLF4J [Logger] to log state changes and final states.
     *
     * @return An [Observable] that reduces all incoming state changes to a single state object.
     *
     * @see [Observable.scan]
     */
    @JvmStatic
    @JvmOverloads
    fun <StateT : MviState, StateChangeableT : MviResult.MviStateChange<StateT>> Observable<StateChangeableT>.stateReducer(
        initialState: StateT,
        logger: Logger = LOG
    ): Observable<StateT> {
        return doOnNext { logger.debug("#StateReducer - stateChange -> {}", it) }
            .scan(initialState) { state, stateChange -> stateChange.applyChange(state) }
            .distinctUntilChanged()
            .doOnNext { logger.debug("#StateReducer - state -> {}", it) }
    }
}