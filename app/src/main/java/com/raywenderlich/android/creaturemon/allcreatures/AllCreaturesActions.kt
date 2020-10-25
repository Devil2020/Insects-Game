package com.raywenderlich.android.creaturemon.allcreatures

sealed class AllCreaturesActions {

    object LoadAllCreatureAction : AllCreaturesActions()

    object ClearAllCreatureAction : AllCreaturesActions()

}