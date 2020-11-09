package com.raywenderlich.android.creaturemon.allcreatures

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.raywenderlich.android.creaturemon.mvibase.MviViewModel
import com.raywenderlich.android.creaturemon.util.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

class AllCreatureViewModel(
        private var ourApplicaton: Application,
        private var actionProcessorHolder: AllCreatureProcessHolder
) : ViewModel(), MviViewModel<AllCreatureIntents, AllCreatureState> {

    public var intentsSubjects: PublishSubject<AllCreatureIntents> = PublishSubject.create()
    public var stateObservable: Observable<AllCreatureState> = viewModelExecutor()
    public val intentFilter: ObservableTransformer<AllCreatureIntents, AllCreatureIntents>
        get() = ObservableTransformer {
            it?.publish {
                Observable.merge(
                        it?.ofType(AllCreatureIntents.LoadAllCreatureIntent::class.java).take(1),
                        it?.notOfType(AllCreatureIntents.LoadAllCreatureIntent::class.java)
                )
            }
        }

    // This function that get All Data
    private fun viewModelExecutor(): Observable<AllCreatureState> {
        return intentsSubjects
                .compose(intentFilter) // 1 - Must make a filter for which type of Intents i must work on it
                .map { actionFromIntent(it) } // 2 - Convert Intent to Action
                .compose(actionProcessorHolder.actionProcessor) // 3- Excute our Repository And Convert Action To Result
                .scan(AllCreatureState.defaultState(), reducer) // 4- Reduce our result And Convert it To State
                .distinctUntilChanged()
                .replay(1)
                .autoConnect(0)
    }


    private fun actionFromIntent(intent: AllCreatureIntents) = when (intent) {
        is AllCreatureIntents.LoadAllCreatureIntent -> AllCreaturesActions.LoadAllCreatureAction
        is AllCreatureIntents.ClearAllCreatureIntent -> AllCreaturesActions.ClearAllCreatureAction
    }

    override fun processIntents(intents: Observable<AllCreatureIntents>) {
        intents?.subscribe(intentsSubjects)
    }

    override fun getStates(): Observable<AllCreatureState> {
        return stateObservable
    }

    companion object {
        val reducer = BiFunction { previousState: AllCreatureState, currentResult: AllCreatureResult ->
            return@BiFunction when (currentResult) {

                is AllCreatureResult.LoadAllCreatureResult -> {

                    when (currentResult) {

                        is AllCreatureResult.LoadAllCreatureResult.Success -> previousState?.copy(false, currentResult?.data, null)

                        is AllCreatureResult.LoadAllCreatureResult.Error -> previousState?.copy(false, arrayListOf(), currentResult?.error)

                        is AllCreatureResult.LoadAllCreatureResult.Loading -> previousState?.copy(true, arrayListOf(), null)

                    }

                }

                is AllCreatureResult.ClearAllCreatureResult -> {

                    when (currentResult) {

                        is AllCreatureResult.ClearAllCreatureResult.Success -> previousState?.copy(false, arrayListOf(), null)

                        is AllCreatureResult.ClearAllCreatureResult.Error -> previousState?.copy(false, arrayListOf(), currentResult?.error)

                        is AllCreatureResult.ClearAllCreatureResult.Clearing -> previousState?.copy(true, arrayListOf(), null)

                    }

                }

            }

        }
    }


}