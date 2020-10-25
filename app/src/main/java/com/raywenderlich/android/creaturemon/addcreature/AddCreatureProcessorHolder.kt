package com.raywenderlich.android.creaturemon.addcreature

import com.raywenderlich.android.creaturemon.data.model.*
import com.raywenderlich.android.creaturemon.data.repository.CreatureRepository
import com.raywenderlich.android.creaturemon.util.schedulers.BaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler

class AddCreatureProcessorHolder (private var creatureRepository: CreatureRepository ,
                                  private var schedulerProvider: BaseSchedulerProvider ,
                                  private var creatureGenerator: CreatureGenerator

) {

    val avatarProcessor = ObservableTransformer < AddCreatureActions.SelectAvatarCreatureAction , AddCreatureResult.AvatarDrawableResult >{
                it?.map { AddCreatureResult.AvatarDrawableResult.Success(it?.drawable) }
                ?.cast(AddCreatureResult.AvatarDrawableResult::class.java)
                ?.onErrorReturn { AddCreatureResult.AvatarDrawableResult.Fail(it) }
                ?.subscribeOn(schedulerProvider.io())
                ?.observeOn(schedulerProvider.ui())
                ?.startWith(AddCreatureResult.AvatarDrawableResult.Processing)!!
    }

    val nameProcessor = ObservableTransformer < AddCreatureActions.EnterCreatureNameAction , AddCreatureResult.NameResult >{
        it?.map { AddCreatureResult.NameResult.Success(it?.name) }
                ?.cast(AddCreatureResult.NameResult::class.java)
                ?.onErrorReturn { AddCreatureResult.NameResult.Fail(it) }
                ?.subscribeOn(schedulerProvider.io())
                ?.observeOn(schedulerProvider.ui())
                ?.startWith(AddCreatureResult.NameResult.Processing)!!
    }

    val strengthProcessor = ObservableTransformer < AddCreatureActions.SelectCreatureStrengthAction , AddCreatureResult.StrengthResult >{
        it?.map { AddCreatureResult.StrengthResult.Success( AttributeStore.STRENGTH.get(it?.strength).value) }
                ?.cast(AddCreatureResult.StrengthResult::class.java)
                ?.onErrorReturn { AddCreatureResult.StrengthResult.Fail(it) }
                ?.subscribeOn(schedulerProvider.io())
                ?.observeOn(schedulerProvider.ui())
                ?.startWith(AddCreatureResult.StrengthResult.Processing)!!
    }

    val intelliganceProcessor = ObservableTransformer < AddCreatureActions.SelectCreatureIntelliganceAction , AddCreatureResult.IntelliganceResult >{
        it?.map { AddCreatureResult.IntelliganceResult.Success(AttributeStore.INTELLIGENCE.get(it?.intelligance).value) }
                ?.cast(AddCreatureResult.IntelliganceResult::class.java)
                ?.onErrorReturn { AddCreatureResult.IntelliganceResult.Fail(it) }
                ?.subscribeOn(schedulerProvider.io())
                ?.observeOn(schedulerProvider.ui())
                ?.startWith(AddCreatureResult.IntelliganceResult.Processing)!!
    }

    val enduranceProcessor = ObservableTransformer < AddCreatureActions.SelectCreatureEnduranceAction , AddCreatureResult.EnduranceResult >{
        it?.map { AddCreatureResult.EnduranceResult.Success(AttributeStore.ENDURANCE.get(it?.endurance).value) }
                ?.cast(AddCreatureResult.EnduranceResult::class.java)
                ?.onErrorReturn { AddCreatureResult.EnduranceResult.Fail(it) }
                ?.subscribeOn(schedulerProvider.io())
                ?.observeOn(schedulerProvider.ui())
                ?.startWith(AddCreatureResult.EnduranceResult.Processing)!!
    }

    val saveProcessor = ObservableTransformer<AddCreatureActions.SaveCreatureAttributesAction , AddCreatureResult.SaveResult> {
        it?.flatMap {
            val attributesOfCreature =  CreatureAttributes(
                    AttributeStore.INTELLIGENCE.get(it?.intelligance).value ,
                    AttributeStore.STRENGTH.get(it?.strength).value ,
                    AttributeStore.ENDURANCE.get(it?.endurance).value
            )
            var creature = creatureGenerator.generateCreature(attributesOfCreature , it?.name , it?.drawable)
            creatureRepository?.saveCreature(creature)?.map {
                AddCreatureResult.SaveResult.Success
            }?.cast(AddCreatureResult.SaveResult::class.java)
                    ?.onErrorReturn{AddCreatureResult.SaveResult.Fail(it)}
                    ?.subscribeOn(schedulerProvider.io())
                    ?.observeOn(schedulerProvider.ui())
                    ?.startWith(AddCreatureResult.SaveResult.Processing)!!

        }
    }


    val actionProcessor = ObservableTransformer<AddCreatureActions , AddCreatureResult> {
        it?.publish{
            Observable.merge(
                    it?.ofType(AddCreatureActions.SelectAvatarCreatureAction::class.java).compose(avatarProcessor) ,
                    it?.ofType(AddCreatureActions.EnterCreatureNameAction::class.java).compose(nameProcessor) ,
                    it?.ofType(AddCreatureActions.SelectCreatureStrengthAction::class.java).compose(strengthProcessor) ,
                    it?.ofType(AddCreatureActions.SelectCreatureIntelliganceAction::class.java).compose(intelliganceProcessor))?.mergeWith(
                            it?.ofType(AddCreatureActions.SelectCreatureEnduranceAction::class.java).compose(enduranceProcessor)
                    )?.mergeWith(
                    it?.ofType(AddCreatureActions.SaveCreatureAttributesAction::class.java).compose(saveProcessor)
                    )?.mergeWith(
                        it?.filter {
                                    it !is AddCreatureActions.SaveCreatureAttributesAction &&
                                    it !is AddCreatureActions.SelectCreatureEnduranceAction &&
                                    it !is AddCreatureActions.SelectCreatureIntelliganceAction &&
                                    it !is AddCreatureActions.SelectCreatureStrengthAction &&
                                    it !is AddCreatureActions.SelectAvatarCreatureAction &&
                                    it !is AddCreatureActions.EnterCreatureNameAction
                        }?.flatMap {
                            Observable.error<AddCreatureResult>(IllegalAccessException("Not Exist"))
                        }
            )
        }

    }





}