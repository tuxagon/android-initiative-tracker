package com.wobblycobbler.initiativetracker

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*

sealed class AbstractState
open class State : AbstractState()

sealed class AbstractMsg
open class Msg : AbstractMsg()
class Idle : Msg()
class Init : Msg()
class ErrorMsg(val err: Throwable, val cmd: Cmd) : Msg()


sealed class AbstractCmd
open class Cmd : AbstractCmd()
class None : Cmd()

interface Component {

    fun update(msg: Msg, state: State): Pair<State, Cmd>

    fun render(state: State)

    fun call(cmd: Cmd): Single<Msg>

}

class Program(val outputScheduler: Scheduler) {

    private val msgRelay: BehaviorRelay<Pair<Msg, State>> = BehaviorRelay.create()
    private var msgQueue = ArrayDeque<Msg>()
    private lateinit var state: State
    private lateinit var component: Component

    fun init(initialState: State, component: Component): Disposable {
        this.component = component
        this.state = initialState
        return msgRelay
            .map { (msg, state) ->
                //update program state and return the new state and command
                component.update(msg, state)
            }
            .observeOn(outputScheduler)
            .doOnNext { (state, cmd) ->
                //draw UI
                component.render(state)
            }
            .doOnNext{ (state, cmd) ->
                this.state = state
                //remove current message from queue
                if (msgQueue.size > 0) {
                    msgQueue.removeFirst()
                }
                //and send a new msg to relay if any
                loop()
            }
            .filter { (_, cmd) -> cmd !is None }
            .observeOn(Schedulers.io())
            .flatMap { (state, cmd) ->
                //execute side effect with command
                return@flatMap component.call(cmd)
                    //if there is an error in side effect, send Error msg with failed command,
                    //which we can handle in Update function
                    .onErrorResumeNext { err -> Single.just(ErrorMsg(err, cmd)) }
                    .toObservable()
            }
            .observeOn(outputScheduler)
            .subscribe { msg ->
                when (msg) {
                    is Idle -> {} //if the message is idle, then do nothing
                    else -> msgQueue.addLast(msg)
                }

                loop()
            }
    }

    fun getState(): State {
        return state
    }

    private fun loop() {
        if (msgQueue.size > 0) {
            msgRelay.accept(Pair(msgQueue.first, this.state))
        }
    }

    fun accept(msg: Msg) {
        msgQueue.addLast(msg)
        if (msgQueue.size == 1) {
            msgRelay.accept(Pair(msgQueue.first, state))
        }
    }

}