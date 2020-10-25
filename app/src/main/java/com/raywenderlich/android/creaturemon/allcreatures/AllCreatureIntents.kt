package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.mvibase.MviIntent

// AllCreature Screen Intents have only 2 Functions [ One for LoadAllCreature from Database and Second For clearAllCreatures]

sealed class AllCreatureIntents  : MviIntent{

    object LoadAllCreatureIntent : AllCreatureIntents()

    object ClearAllCreatureIntent : AllCreatureIntents()

}