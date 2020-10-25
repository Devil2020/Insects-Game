package com.raywenderlich.android.creaturemon.mvibase

import io.reactivex.Observable

interface MviViewModel <I : MviIntent , O : MviState> {

    public fun processIntents (intents : Observable<I>)

    public fun getStates () : Observable<O>

}