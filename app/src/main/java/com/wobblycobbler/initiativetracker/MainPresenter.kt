package com.wobblycobbler.initiativetracker

import com.wobblycobbler.initiativetracker.db.AppDatabase
import com.wobblycobbler.initiativetracker.db.entities.Character
import io.reactivex.Single
import io.reactivex.disposables.Disposable

class MainPresenter(
    private val view: MainActivity,
    private val program: Program
) : Component {

    data class MainState(val characters: List<Character> = emptyList()) : State()

    data class CharactersRetrievedMsg(val characters: List<Character>) : Msg()

    class GetCharactersCmd : Cmd()

    private var programDisposable: Disposable

    init {
        programDisposable = program.init(MainState(), this)
    }

    fun init() {
        program.accept(Init())
    }


    override fun update(msg: Msg, state: State): Pair<State, Cmd> {
        val state = state as MainState
        return when (msg) {
            is Init -> {
                Pair(state, GetCharactersCmd())
            }
            is CharactersRetrievedMsg -> {
                Pair(state.copy(characters = msg.characters), None())
            }
            else -> Pair(state, None())
        }
    }

    override fun render(state: State) {
        (state as MainState).apply {

        }
    }

    override fun call(cmd: Cmd): Single<Msg> {
        val db =
        return when (cmd) {
            is GetCharactersCmd -> Single.just(
                CharactersRetrievedMsg(AppDatabase.getDatabase(view).characterDao().getAll()))
            else -> Single.just(Idle())
        }
        return Single.just(Idle())
    }

    fun destroy() {
        if (!programDisposable.isDisposed) {
            programDisposable.dispose()
        }
    }

}