package com.wobblycobbler.initiativetracker

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wobblycobbler.initiativetracker.db.AppDatabase
import com.wobblycobbler.initiativetracker.db.entities.Character
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.disposables.Disposable
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerAdapter: CharacterRecyclerAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var charactersSubscription: Disposable
    private var currentPosition: Int = 0
    private var currentRound: Int = 0
    private lateinit var characters: ArrayList<Character>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            currentPosition = getNextPosition()
            Log.d("CURRENT_POSITION", String.format("%s:%s", currentRound.toString(), currentPosition.toString()))
            recyclerAdapter.notifyDataSetChanged()
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        linearLayoutManager = LinearLayoutManager(this)
        characterRecyclerView.layoutManager = linearLayoutManager

        loadCharacters()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (charactersSubscription != null && !charactersSubscription.isDisposed) {
            charactersSubscription.dispose();
        }
    }

    private fun loadCharacters() {
        val charactersObservable = Observable.fromCallable {
            AppDatabase.getDatabase(this@MainActivity).characterDao().getAll()
        }
        charactersSubscription = charactersObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    characters = ArrayList(it)
                    recyclerAdapter = CharacterRecyclerAdapter(characters, currentPosition)
                    characterRecyclerView.adapter = recyclerAdapter
                },
                onError = { },
                onComplete = { }
            )
    }

    private fun getNextPosition(): Int {
        if (this.currentPosition + 1 == this.characters.size) {
            this.currentRound += 1
        }

        recyclerAdapter.notifyItemChanged(currentPosition)
        return (this.currentPosition + 1) % this.characters.size
    }
}
