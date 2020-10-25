package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.data.model.Creature
import com.raywenderlich.android.creaturemon.mvibase.MviResult

sealed class AllCreatureResult : MviResult {

    sealed class LoadAllCreatureResult : AllCreatureResult() {

        object Loading : LoadAllCreatureResult()

        data class Success(val data: ArrayList<Creature>) : LoadAllCreatureResult()

        data class Error(val error: Throwable) : LoadAllCreatureResult()
    }

    sealed class ClearAllCreatureResult : AllCreatureResult () {

        object Clearing : ClearAllCreatureResult()

        object Success : ClearAllCreatureResult()

        data class Error(val error: Throwable) : ClearAllCreatureResult()
    }

}