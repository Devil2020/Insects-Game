package com.raywenderlich.android.creaturemon.addcreature.avatars

import com.raywenderlich.android.creaturemon.allcreatures.AllCreatureState
import com.raywenderlich.android.creaturemon.data.model.Creature
import com.raywenderlich.android.creaturemon.mvibase.MviState

data class AddCreatureState (
        public val isProcessing : Boolean ,
        public val creature: Creature ,
        public val isDrawableSelected : Boolean ,
        public val isSaveCompleted : Boolean ,
        public val error  :Throwable?
) : MviState {
    companion object {

        fun defaultState() = AddCreatureState(
                isProcessing = false ,
                creature = Creature() ,
                isDrawableSelected = false ,
                isSaveCompleted = false ,
                error = null
        )
    }
}