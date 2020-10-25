package com.raywenderlich.android.creaturemon.allcreatures

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.raywenderlich.android.creaturemon.mvibase.MviViewModel
import com.raywenderlich.android.creaturemon.util.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

class AllCreatureViewModel(
        private var ourApplicaton: Application,
        private var actionProcessorHolder: AllCreatureProcessHolder
) : AndroidViewModel(ourApplicaton), MviViewModel<AllCreatureIntents, AllCreatureState> {

    public var intentsSubjects: PublishSubject<AllCreatureIntents> = PublishSubject.create()
    public var stateObservable: Observable<AllCreatureState> = compose()
    public var intentFilter = ObservableTransformer<AllCreatureIntents, AllCreatureIntents> {
        it?.publish {
            Observable.merge(
                    it?.ofType(AllCreatureIntents.LoadAllCreatureIntent::class.java).take(1),
                    it?.notOfType(AllCreatureIntents.LoadAllCreatureIntent::class.java)
            )
        }
    }

    private fun compose() : Observable<AllCreatureState> {
        return intentsSubjects
                .compose(intentFilter)
                .map{actionFromIntent(it)}
                .compose(actionProcessorHolder.actionProcessor)
                .scan(AllCreatureState.defaultState(), reducer)
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