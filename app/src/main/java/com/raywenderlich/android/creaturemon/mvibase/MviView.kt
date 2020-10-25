package com.raywenderlich.android.creaturemon.mvibase

import io.reactivex.Observable

interface MviView <I : MviIntent , O : MviState> {

    public fun render (states :O)

    public fun intents () : Observable<I>

}