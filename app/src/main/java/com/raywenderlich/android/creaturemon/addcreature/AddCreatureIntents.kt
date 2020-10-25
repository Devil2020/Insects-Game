package com.raywenderlich.android.creaturemon.addcreature

import com.raywenderlich.android.creaturemon.data.model.CreatureAttributes
import com.raywenderlich.android.creaturemon.mvibase.MviIntent

sealed class AddCreatureIntents : MviIntent{

    data class SelectAvatarCreatureIntent (val drawable : Int ) : AddCreatureIntents()

    data class EnterCreatureNameIntent (val name : String) : AddCreatureIntents()

    data class SelectCreatureStrengthIntent (val strength : Int) : AddCreatureIntents()

    data class SelectCreatureIntelliganceIntent (val intelligance : Int) : AddCreatureIntents()

    data class SelectCreatureEnduranceIntent (val endurance : Int) : AddCreatureIntents()

    data class SaveCreatureAttributesIntent (val drawable : Int ,
                                             val name : String ,
                                             val strength : Int ,
                                             val intelligance : Int ,
                                             val endurance : Int) : AddCreatureIntents()

}