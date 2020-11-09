/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.creaturemon.allcreatures

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.raywenderlich.android.creaturemon.R
import com.raywenderlich.android.creaturemon.addcreature.CreatureActivity
import com.raywenderlich.android.creaturemon.mvibase.MviView
import com.raywenderlich.android.creaturemon.util.AllCreatureViewModelFactory
import com.raywenderlich.android.creaturemon.util.visible
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_all_creatures.*
import kotlinx.android.synthetic.main.content_all_creatures.*

class AllCreaturesActivity : AppCompatActivity(), MviView<AllCreatureIntents, AllCreatureState> {

    //--------------------------------------Test----------------------------------
    private var intentObservable : PublishSubject<String> = PublishSubject.create()

    private var sendIntentObservable : PublishSubject<String> = PublishSubject.create()
    private var stateObservable : Observable<String> = initObs ()

    private val adapter = CreatureAdapter(mutableListOf())

    private val clearAllFromMenuPublisher = PublishSubject.create<AllCreatureIntents.ClearAllCreatureIntent>()

    private val viewModel: AllCreatureViewModel by lazy(LazyThreadSafetyMode.NONE) {

        ViewModelProviders.of(this, AllCreatureViewModelFactory.getInstance(application)).get(AllCreatureViewModel::class.java)

    }

    public fun initObs () : Observable<String> {
        return intentObservable?.filter {
            if (TextUtils.equals(it , "Yes")) {
                Toast.makeText(this , "Yes" , Toast.LENGTH_SHORT).show()
                return@filter true
            }
            else {
                Toast.makeText(this , "No" , Toast.LENGTH_SHORT).show()
                return@filter false
            }
        }
    }

    private var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_creatures)
        setSupportActionBar(toolbar)

        creaturesRecyclerView.layoutManager = LinearLayoutManager(this)
        creaturesRecyclerView.adapter = adapter

        fab.setOnClickListener {
            startActivity(Intent(this, CreatureActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        bind()
//        sendIntentObservable?.onNext("Yes")
//        sendIntentObservable?.onNext("No")
    }

    private fun renderNothing (data :String){

    }

    private fun bind() {
        compositeDisposable?.add(stateObservable?.subscribe(this::renderNothing))
        compositeDisposable?.add(viewModel?.getStates()?.subscribe(this::render))
        sendIntentObservable?.subscribe(intentObservable)
        viewModel?.processIntents(intents())
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable?.dispose()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear_all -> {
                clearAllFromMenuPublisher?.onNext(AllCreatureIntents.ClearAllCreatureIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun render(states: AllCreatureState) {
        allCreatureProgressBar?.visible = states?.isLoading!!
        if (states?.success?.size != 0) {
            creaturesRecyclerView?.visible = true
            youDidnotHaveData?.visible = false
            adapter?.updateCreatures(states?.success)
        }
        if (states?.success?.size == 0) {
            creaturesRecyclerView?.visible = false
            youDidnotHaveData?.visible = true
            youDidnotHaveData?.setText("Empty")
        } else if (states?.error != null) {
            youDidnotHaveData?.visible = true
            creaturesRecyclerView?.visible = false
            Toast.makeText(this, "You have an Error happended !", Toast.LENGTH_SHORT).show()
            youDidnotHaveData?.setText("You have an Error happended !")
        }

    }

    private fun loadIntentObservable(): Observable<AllCreatureIntents.LoadAllCreatureIntent> {
        return Observable.just(AllCreatureIntents.LoadAllCreatureIntent)
    }

    // He put it as this way because it is not the default but load is Defult so It will Send Data with just
    private fun clearIntentObservable(): Observable<AllCreatureIntents.ClearAllCreatureIntent> {
        return clearAllFromMenuPublisher
    }

    override fun intents(): Observable<AllCreatureIntents> {
        return Observable.merge(loadIntentObservable(), clearIntentObservable())
    }
}
