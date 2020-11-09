package com.raywenderlich.android.creaturemon.util

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raywenderlich.android.creaturemon.addcreature.AddCreatureProcessorHolder
import com.raywenderlich.android.creaturemon.addcreature.AddCreatureViewModel
import com.raywenderlich.android.creaturemon.allcreatures.AllCreatureProcessHolder
import com.raywenderlich.android.creaturemon.allcreatures.AllCreatureViewModel
import com.raywenderlich.android.creaturemon.app.Injector

class AddCreatureViewModelFactory(private var ourApplicaton: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddCreatureViewModel(ourApplicaton, AddCreatureProcessorHolder(
                Injector.provideCreatureRepository(ourApplicaton.applicationContext),
                Injector.provideSchedulerProvider(),
                Injector.provideCreatureGenerator()
                )) as T
    }

    companion object : SingletonHolderSingleArg<AddCreatureViewModelFactory, Application>(::AddCreatureViewModelFactory)

}