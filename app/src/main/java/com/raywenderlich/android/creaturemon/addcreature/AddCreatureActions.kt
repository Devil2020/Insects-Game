package com.raywenderlich.android.creaturemon.addcreature

sealed class AddCreatureActions {

    data class SelectAvatarCreatureAction (val drawable : Int ) : AddCreatureActions()

    data class EnterCreatureNameAction (val name : String) : AddCreatureActions()

    data class SelectCreatureStrengthAction (val strength : Int) : AddCreatureActions()

    data class SelectCreatureIntelliganceAction (val intelligance : Int) : AddCreatureActions()

    data class SelectCreatureEnduranceAction (val endurance : Int) : AddCreatureActions()

    data class SaveCreatureAttributesAction (val drawable : Int ,
                                             val name : String ,
                                             val strength : Int ,
                                             val intelligance : Int ,
                                             val endurance : Int) : AddCreatureActions()

}