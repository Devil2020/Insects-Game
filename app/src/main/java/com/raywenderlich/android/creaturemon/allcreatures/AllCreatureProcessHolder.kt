package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.data.model.Creature
import com.raywenderlich.android.creaturemon.data.repository.CreatureRepository
import com.raywenderlich.android.creaturemon.util.schedulers.BaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

class AllCreatureProcessHolder(
        private val creatureRepository: CreatureRepository,
        private val schedulerProvider: BaseSchedulerProvider
) {

    private val loadAllCreatureProcessor = ObservableTransformer<AllCreaturesActions.LoadAllCreatureAction,
            AllCreatureResult.LoadAllCreatureResult> {
        it?.flatMap {
            creatureRepository?.getAllCreatures()
                    ?.map { AllCreatureResult.LoadAllCreatureResult.Success(it as ArrayList<Creature>) }
                    ?.cast(AllCreatureResult.LoadAllCreatureResult::class.java)
                    ?.onErrorReturn { AllCreatureResult.LoadAllCreatureResult.Error(it) }
                    ?.observeOn(schedulerProvider.computation())
                    ?.subscribeOn(schedulerProvider.ui())
                    ?.startWith(AllCreatureResult.LoadAllCreatureResult.Loading)
        }
    }


    private val clearAllCreatureProcessor = ObservableTransformer<AllCreaturesActions.ClearAllCreatureAction,
            AllCreatureResult.ClearAllCreatureResult> {
        it?.flatMap {
            creatureRepository?.clearAllCreatures()
                    ?.map { AllCreatureResult.ClearAllCreatureResult.Success }
                    ?.cast(AllCreatureResult.ClearAllCreatureResult::class.java)
                    ?.onErrorReturn { AllCreatureResult.ClearAllCreatureResult.Error(it) }
                    ?.observeOn(schedulerProvider.computation())
                    ?.subscribeOn(schedulerProvider.ui())
                    ?.startWith(AllCreatureResult.ClearAllCreatureResult.Clearing)

        }
    }

    internal val actionProcessor = ObservableTransformer<AllCreaturesActions , AllCreatureResult> {
        it?.publish {
            Observable.merge(
                    it?.ofType(AllCreaturesActions.LoadAllCreatureAction::class.java).compose(loadAllCreatureProcessor) ,
                    it?.ofType(AllCreaturesActions.ClearAllCreatureAction::class.java).compose(clearAllCreatureProcessor)
            )?.mergeWith  (
                    it?.filter {
                        it !is AllCreaturesActions.LoadAllCreatureAction
                        && it !is AllCreaturesActions.ClearAllCreatureAction
            }?.flatMap {
                        Observable.error<AllCreatureResult>(IllegalAccessException("Not Exist"))
                    }
            )
        }
    }

}