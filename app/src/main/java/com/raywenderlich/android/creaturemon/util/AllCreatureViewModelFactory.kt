package com.raywenderlich.android.creaturemon.util

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raywenderlich.android.creaturemon.allcreatures.AllCreatureProcessHolder
import com.raywenderlich.android.creaturemon.allcreatures.AllCreatureViewModel
import com.raywenderlich.android.creaturemon.app.Injector

class AllCreatureViewModelFactory(private var ourApplicaton: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AllCreatureViewModel(ourApplicaton, AllCreatureProcessHolder(Injector.provideCreatureRepository(ourApplicaton.applicationContext) ,
        Injector.provideSchedulerProvider())) as T
    }

    companion object : SingletonHolderSingleArg<AllCreatureViewModelFactory , Application>(::AllCreatureViewModelFactory)

}