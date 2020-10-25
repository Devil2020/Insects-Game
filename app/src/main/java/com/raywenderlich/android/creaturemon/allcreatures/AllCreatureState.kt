package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.data.model.Creature
import com.raywenderlich.android.creaturemon.mvibase.MviResult
import com.raywenderlich.android.creaturemon.mvibase.MviState

data class AllCreatureState(

        val isLoading: Boolean,

        val success: ArrayList<Creature>,

        val error: Throwable?

) : MviState {

    // this is the Default Screen State

    companion object {
        fun defaultState() = AllCreatureState(isLoading = true,
                success = arrayListOf(),
                error = null)
    }


}