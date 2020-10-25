package com.raywenderlich.android.creaturemon.addcreature

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.raywenderlich.android.creaturemon.addcreature.avatars.AddCreatureState
import com.raywenderlich.android.creaturemon.allcreatures.AllCreatureIntents
import com.raywenderlich.android.creaturemon.allcreatures.AllCreatureProcessHolder
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesActions
import com.raywenderlich.android.creaturemon.data.model.Creature
import com.raywenderlich.android.creaturemon.data.model.CreatureAttributes
import com.raywenderlich.android.creaturemon.mvibase.MviViewModel
import com.raywenderlich.android.creaturemon.util.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.subjects.PublishSubject
import java.util.function.BiFunction

class AddCreatureViewModel(private var ourApplicaton: Application,
                           private var actionProcessorHolder: AddCreatureProcessorHolder) :
        AndroidViewModel(ourApplicaton), MviViewModel<AddCreatureIntents, AddCreatureState> {

    public var addCreatureIntentsSubject: PublishSubject<AddCreatureIntents> = PublishSubject.create()
    public var addCreatureState: Observable<AddCreatureState> = compose()


    private fun compose(): Observable<AddCreatureState> {
        return addCreatureIntentsSubject
                .map(this::actionFromIntent)
                .compose(actionProcessorHolder.actionProcessor)
                .scan(AddCreatureState.defaultState(), reducer)
                .distinctUntilChanged()
                .replay(1)
                .autoConnect(0)
    }

    private fun actionFromIntent(intent: AddCreatureIntents) = when (intent) {
        is AddCreatureIntents.EnterCreatureNameIntent -> AddCreatureActions.EnterCreatureNameAction(intent?.name)
        is AddCreatureIntents.SaveCreatureAttributesIntent -> AddCreatureActions.SaveCreatureAttributesAction(drawable = intent?.drawable, name = intent?.name, endurance = intent?.endurance, strength = intent?.strength, intelligance = intent?.intelligance)
        is AddCreatureIntents.SelectAvatarCreatureIntent -> AddCreatureActions.SelectAvatarCreatureAction(intent?.drawable)
        is AddCreatureIntents.SelectCreatureEnduranceIntent -> AddCreatureActions.SelectCreatureEnduranceAction(intent?.endurance)
        is AddCreatureIntents.SelectCreatureIntelliganceIntent -> AddCreatureActions.SelectCreatureIntelliganceAction(intent?.intelligance)
        is AddCreatureIntents.SelectCreatureStrengthIntent -> AddCreatureActions.SelectCreatureStrengthAction(intent?.strength)
    }

    override fun processIntents(intents: Observable<AddCreatureIntents>) {
        intents?.subscribe(addCreatureIntentsSubject)
    }

    override fun getStates(): Observable<AddCreatureState> {

        return addCreatureState
    }

    companion object {
        val reducer = io.reactivex.functions.BiFunction { previousState: AddCreatureState, currentResult: AddCreatureResult ->
            return@BiFunction  when (currentResult) {
                is AddCreatureResult.AvatarDrawableResult -> reducerAvatar(previousState, currentResult)!!
                is AddCreatureResult.NameResult -> reducerName(previousState, currentResult)!!
                is AddCreatureResult.StrengthResult -> reducerStrength(previousState, currentResult)!!
                is AddCreatureResult.IntelliganceResult -> reducerIntelligence(previousState, currentResult)!!
                is AddCreatureResult.EnduranceResult -> reducerEnduranceResult(previousState, currentResult)!!
                is AddCreatureResult.SaveResult -> reducerSaveResult(previousState, currentResult)!!
            }
        }

        public fun reducerAvatar(previousState: AddCreatureState, currentResult: AddCreatureResult.AvatarDrawableResult) = when (currentResult) {
            is AddCreatureResult.AvatarDrawableResult.Processing -> {
                previousState?.copy(isProcessing = true, creature = previousState.creature, isDrawableSelected = false, isSaveCompleted = false, error = null)
            }
            is AddCreatureResult.AvatarDrawableResult.Fail -> {
                previousState?.copy(isProcessing = false, creature = previousState.creature, isDrawableSelected = false, isSaveCompleted = false, error = currentResult?.exception)
            }
            is AddCreatureResult.AvatarDrawableResult.Success -> {
                previousState?.copy(isProcessing = false, creature = Creature(
                        attributes = previousState.creature.attributes,
                        name = previousState.creature.name,
                        drawable = currentResult?.drawable,
                        hitPoints = previousState?.creature.hitPoints
                ), isDrawableSelected = false, isSaveCompleted = false, error = null)
            }
        }

        public fun reducerName(previousState: AddCreatureState, currentResult: AddCreatureResult.NameResult) = when (currentResult) {
            is AddCreatureResult.NameResult.Processing -> {
                previousState?.copy(isProcessing = true, creature = previousState.creature, isDrawableSelected = false, isSaveCompleted = false, error = null)
            }
            is AddCreatureResult.NameResult.Fail -> {
                previousState?.copy(isProcessing = false, creature = previousState.creature, isDrawableSelected = false, isSaveCompleted = false, error = currentResult?.exception)
            }
            is AddCreatureResult.NameResult.Success -> {
                previousState?.copy(isProcessing = false, creature = Creature(
                        name = currentResult.name
                ), isDrawableSelected = false, isSaveCompleted = false, error = null)
            }
        }

        public fun reducerStrength(previousState: AddCreatureState, currentResult: AddCreatureResult.StrengthResult) = when (currentResult) {
            is AddCreatureResult.StrengthResult.Processing -> {
                previousState?.copy(isProcessing = true, creature = previousState.creature, isDrawableSelected = false, isSaveCompleted = false, error = null)
            }
            is AddCreatureResult.StrengthResult.Fail -> {
                previousState?.copy(isProcessing = false, creature = previousState.creature, isDrawableSelected = false, isSaveCompleted = false, error = currentResult?.exception)
            }
            is AddCreatureResult.StrengthResult.Success -> {
                val attributes = CreatureAttributes(
                        strength = currentResult.strength,
                        intelligence = previousState.creature.attributes.intelligence,
                        endurance = previousState.creature.attributes.endurance
                )
                previousState?.copy(isProcessing = false, creature = Creature(
                        attributes = attributes,
                        name = previousState.creature.name,
                        drawable = previousState.creature.drawable

                ), isDrawableSelected = false, isSaveCompleted = false, error = null)
            }
        }

        public fun reducerIntelligence(previousState: AddCreatureState, currentResult: AddCreatureResult.IntelliganceResult) = when (currentResult) {
            is AddCreatureResult.IntelliganceResult.Processing -> {
                previousState?.copy(isProcessing = true, creature = previousState.creature, isDrawableSelected = false, isSaveCompleted = false, error = null)
            }
            is AddCreatureResult.IntelliganceResult.Fail -> {
                previousState?.copy(isProcessing = false, creature = previousState.creature, isDrawableSelected = false, isSaveCompleted = false, error = currentResult?.exception)
            }
            is AddCreatureResult.IntelliganceResult.Success -> {
                val attributes = CreatureAttributes(
                        strength = previousState.creature.attributes.strength,
                        intelligence = currentResult.intelligance,
                        endurance = previousState.creature.attributes.endurance
                )
                previousState?.copy(isProcessing = false, creature = Creature(
                        attributes = attributes,
                        name = previousState.creature.name,
                        drawable = previousState.creature.drawable

                ), isDrawableSelected = false, isSaveCompleted = false, error = null)
            }
        }

        public fun reducerEnduranceResult(previousState: AddCreatureState, currentResult: AddCreatureResult.EnduranceResult) = when (currentResult) {
            is AddCreatureResult.EnduranceResult.Processing -> {
                previousState?.copy(isProcessing = true, creature = previousState.creature, isDrawableSelected = false, isSaveCompleted = false, error = null)
            }
            is AddCreatureResult.EnduranceResult.Fail -> {
                previousState?.copy(isProcessing = false, creature = previousState.creature, isDrawableSelected = false, isSaveCompleted = false, error = currentResult?.exception)
            }
            is AddCreatureResult.EnduranceResult.Success -> {
                val attributes = CreatureAttributes(
                        strength = previousState.creature.attributes.strength,
                        intelligence = previousState.creature.attributes.intelligence,
                        endurance = currentResult.endurance
                )
                previousState?.copy(isProcessing = false, creature = Creature(
                        attributes = attributes,
                        name = previousState.creature.name,
                        drawable = previousState.creature.drawable

                ), isDrawableSelected = false, isSaveCompleted = false, error = null)
            }
        }

        public fun reducerSaveResult(previousState: AddCreatureState, currentResult: AddCreatureResult.SaveResult) = when (currentResult) {
            is AddCreatureResult.SaveResult.Processing -> {
                previousState?.copy(isProcessing = true, error = null)
            }
            is AddCreatureResult.SaveResult.Fail -> {
                previousState?.copy(isSaveCompleted = false, error = currentResult?.exception)
            }
            is AddCreatureResult.SaveResult.Success -> {

                previousState?.copy(isProcessing = false, isSaveCompleted = true, error = null)
            }
        }


    }

}