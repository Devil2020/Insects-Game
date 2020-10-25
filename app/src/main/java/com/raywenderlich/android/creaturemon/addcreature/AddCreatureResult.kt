package com.raywenderlich.android.creaturemon.addcreature

import com.raywenderlich.android.creaturemon.mvibase.MviResult

sealed class AddCreatureResult : MviResult {

    sealed class AvatarDrawableResult : AddCreatureResult(){

        object Processing : AvatarDrawableResult()

        data class Success (val drawable : Int) : AvatarDrawableResult()

        data class Fail (val exception : Throwable) : AvatarDrawableResult()

    }

    sealed class NameResult : AddCreatureResult(){

        object Processing : NameResult()

        data class Success (val name : String) : NameResult()

        data class Fail (val exception : Throwable) : NameResult()

    }

    sealed class IntelliganceResult : AddCreatureResult(){

        object Processing : IntelliganceResult()

        data class Success (val intelligance : Int) : IntelliganceResult()

        data class Fail (val exception : Throwable) : IntelliganceResult()

    }

    sealed class StrengthResult : AddCreatureResult(){

        object Processing : StrengthResult()

        data class Success (val strength : Int) : StrengthResult()

        data class Fail (val exception : Throwable) : StrengthResult()

    }

    sealed class EnduranceResult : AddCreatureResult(){

        object Processing : EnduranceResult()

        data class Success (val endurance : Int) : EnduranceResult()

        data class Fail (val exception : Throwable) : EnduranceResult()
    }


    sealed class SaveResult : AddCreatureResult(){

        object Processing : SaveResult()

        object Success  : SaveResult()

        data class Fail (val exception : Throwable) : SaveResult()

    }

}